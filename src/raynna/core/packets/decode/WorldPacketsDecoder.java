package raynna.core.packets.decode;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import raynna.app.Settings;
import raynna.game.Animation;
import raynna.game.Graphics;
import raynna.game.World;
import raynna.game.WorldObject;
import raynna.game.WorldTile;
import raynna.game.item.FloorItem;
import raynna.game.item.Item;
import raynna.game.item.ground.AutomaticGroundItem;
import raynna.game.item.ground.GroundItems;
import raynna.game.item.meta.GreaterRunicStaffMetaData;
import raynna.game.minigames.clanwars.ClanWars;
import raynna.game.minigames.duel.DuelArena;
import raynna.game.npc.NPC;
import raynna.game.npc.familiar.Familiar;
import raynna.game.npc.familiar.Familiar.SpecialAttack;
import raynna.game.player.content.*;
import raynna.util.*;
import raynna.game.npc.drops.DropTableSource;
import raynna.game.player.AccountCreation;
import raynna.game.player.Inventory;
import raynna.game.player.Player;
import raynna.game.player.PublicChatMessage;
import raynna.game.player.QueuedWorldPacket;
import raynna.game.player.QuickChatMessage;
import raynna.game.player.RouteEvent;
import raynna.game.player.Skills;
import raynna.game.player.actions.PlayerFollow;
import raynna.game.player.bot.PlayerBotManager;
import raynna.game.player.actions.skills.construction.House;
import raynna.game.player.actions.skills.construction.Sawmill;
import raynna.game.player.actions.skills.construction.Sawmill.Plank;
import raynna.game.player.actions.skills.smithing.DungeoneeringSmithing;
import raynna.game.player.actions.skills.summoning.Summoning;
import raynna.game.player.content.clans.ClansManager;
import raynna.game.player.content.customtab.GearTab;
import raynna.game.player.content.customtab.JournalTab;
import raynna.game.player.content.customtab.SettingsTab;
import raynna.game.player.content.friendschat.FriendChatsManager;
import raynna.game.player.content.pet.Pets;
import raynna.game.player.content.presets.Preset;
import raynna.game.player.content.randomevent.AntiBot;
import raynna.game.player.content.unlockables.UnlockableManager;
import raynna.game.player.controllers.construction.SawmillController;
import raynna.game.player.dialogues.Report;
import raynna.game.route.RouteFinder;
import raynna.game.route.strategy.FixedTileStrategy;
import raynna.core.packets.InputStream;
import raynna.core.packets.OutputStream;
import raynna.core.networking.Session;
import raynna.core.packets.handlers.ButtonHandler;
import raynna.core.packets.handlers.InventoryOptionsHandler;
import raynna.core.packets.handlers.NPCHandler;
import raynna.core.packets.handlers.ObjectHandler;
import raynna.core.packets.decode.world.dispatch.WorldPacketDispatcher;
import raynna.core.packets.decode.world.dispatch.WorldQueuedPacketDispatcher;
import raynna.core.packets.decode.world.support.WorldPacketType;
import raynna.util.huffman.Huffman;
import raynna.game.player.combat.CombatAction;
import raynna.game.player.combat.magic.*;
import raynna.game.player.command.CommandRegistry;
import raynna.game.player.grandexchange.GrandExchange;
import raynna.game.player.interfaces.DropInterface;
import raynna.game.player.interfaces.DropSearch;
import raynna.game.player.interfaces.PresetInterface;
import raynna.game.world.pvp.PvpManager;

import static raynna.game.world.util.Msg.warn;

/**
 * Refactored for readability & maintainability while preserving public API,
 * method names, constants and behavior.
 */
public final class WorldPacketsDecoder extends Decoder {

    public static final int EQUIPMENT_REMOVE_PACKET = WorldPacketType.EQUIPMENT_REMOVE;
    public static final int WALKING_PACKET = WorldPacketType.WALKING;
    public static final int MINI_WALKING_PACKET = WorldPacketType.MINI_WALKING;
    public static final int AFK_PACKET = WorldPacketType.AFK;
    public static final int ACTION_BUTTON1_PACKET = WorldPacketType.ACTION_BUTTON1;
    public static final int ACTION_BUTTON2_PACKET = WorldPacketType.ACTION_BUTTON2;
    public static final int ACTION_BUTTON3_PACKET = WorldPacketType.ACTION_BUTTON3;
    public static final int ACTION_BUTTON4_PACKET = WorldPacketType.ACTION_BUTTON4;
    public static final int ACTION_BUTTON5_PACKET = WorldPacketType.ACTION_BUTTON5;
    public static final int ACTION_BUTTON6_PACKET = WorldPacketType.ACTION_BUTTON6;
    public static final int ACTION_BUTTON7_PACKET = WorldPacketType.ACTION_BUTTON7;
    public static final int ACTION_BUTTON8_PACKET = WorldPacketType.ACTION_BUTTON8;
    public static final int ACTION_BUTTON9_PACKET = WorldPacketType.ACTION_BUTTON9;
    public static final int WORLD_MAP_CLICK = WorldPacketType.WORLD_MAP_CLICK;
    public static final int ACTION_BUTTON10_PACKET = WorldPacketType.ACTION_BUTTON10;
    public static final int RECEIVE_PACKET_COUNT_PACKET = WorldPacketType.RECEIVE_PACKET_COUNT;
    public static final int EQUIPMENT_EXAMINE_PACKET = WorldPacketType.EQUIPMENT_EXAMINE;
    public static final int MOVE_CAMERA_PACKET = WorldPacketType.MOVE_CAMERA;
    public static final int INTERFACE_ON_OBJECT = WorldPacketType.INTERFACE_ON_OBJECT;
    private static final int CLICK_PACKET = -1;
    private static final int MOVE_MOUSE_PACKET = -1;
    public static final int KEY_TYPED_PACKET = WorldPacketType.KEY_TYPED;
    public static final int CLOSE_INTERFACE_PACKET = WorldPacketType.CLOSE_INTERFACE;
    public static final int COMMANDS_PACKET = WorldPacketType.COMMANDS;
    public static final int ITEM_ON_ITEM_PACKET = WorldPacketType.ITEM_ON_ITEM;
    private static final int IN_OUT_SCREEN_PACKET = -1;
    public static final int DONE_LOADING_REGION_PACKET = WorldPacketType.DONE_LOADING_REGION;
    public static final int PING_PACKET = WorldPacketType.PING;
    public static final int SCREEN_PACKET = WorldPacketType.SCREEN;
    public static final int CHAT_TYPE_PACKET = WorldPacketType.CHAT_TYPE;
    public static final int CHAT_PACKET = WorldPacketType.CHAT;
    public static final int PUBLIC_QUICK_CHAT_PACKET = WorldPacketType.PUBLIC_QUICK_CHAT;
    public static final int ADD_FRIEND_PACKET = WorldPacketType.ADD_FRIEND;
    public static final int ADD_IGNORE_PACKET = WorldPacketType.ADD_IGNORE;
    public static final int REMOVE_IGNORE_PACKET = WorldPacketType.REMOVE_IGNORE;
    public static final int JOIN_FRIEND_CHAT_PACKET = WorldPacketType.JOIN_FRIEND_CHAT;
    public static final int CHANGE_FRIEND_CHAT_PACKET = WorldPacketType.CHANGE_FRIEND_CHAT;
    public static final int KICK_FRIEND_CHAT_PACKET = WorldPacketType.KICK_FRIEND_CHAT;
    public static final int KICK_CLAN_CHAT_PACKET = WorldPacketType.KICK_CLAN_CHAT;
    public static final int REMOVE_FRIEND_PACKET = WorldPacketType.REMOVE_FRIEND;
    public static final int SEND_FRIEND_MESSAGE_PACKET = WorldPacketType.SEND_FRIEND_MESSAGE;
    public static final int SEND_FRIEND_QUICK_CHAT_PACKET = WorldPacketType.SEND_FRIEND_QUICK_CHAT;
    public static final int OBJECT_CLICK1_PACKET = WorldPacketType.OBJECT_CLICK1;
    public static final int OBJECT_CLICK2_PACKET = WorldPacketType.OBJECT_CLICK2;
    public static final int OBJECT_CLICK3_PACKET = WorldPacketType.OBJECT_CLICK3;
    public static final int OBJECT_CLICK4_PACKET = WorldPacketType.OBJECT_CLICK4;
    public static final int OBJECT_CLICK5_PACKET = WorldPacketType.OBJECT_CLICK5;
    public static final int OBJECT_EXAMINE_PACKET = WorldPacketType.OBJECT_EXAMINE;
    public static final int NPC_CLICK1_PACKET = WorldPacketType.NPC_CLICK1;
    public static final int NPC_CLICK2_PACKET = WorldPacketType.NPC_CLICK2;
    public static final int NPC_CLICK3_PACKET = WorldPacketType.NPC_CLICK3;
    public static final int NPC_CLICK4_PACKET = WorldPacketType.NPC_CLICK4;
    public static final int NPC_EXAMINE_PACKET = WorldPacketType.NPC_EXAMINE;
    public static final int ATTACK_NPC = WorldPacketType.ATTACK_NPC;
    public static final int PLAYER_OPTION_1_PACKET = WorldPacketType.PLAYER_OPTION_1;
    public static final int PLAYER_OPTION_2_PACKET = WorldPacketType.PLAYER_OPTION_2;
    public static final int PLAYER_OPTION_3_PACKET = WorldPacketType.PLAYER_OPTION_3;
    public static final int PLAYER_OPTION_4_PACKET = WorldPacketType.PLAYER_OPTION_4;
    public static final int PLAYER_OPTION_5_PACKET = WorldPacketType.PLAYER_OPTION_5;
    public static final int PLAYER_OPTION_6_PACKET = WorldPacketType.PLAYER_OPTION_6;
    public static final int PLAYER_OPTION_7_PACKET = WorldPacketType.PLAYER_OPTION_7;
    public static final int PLAYER_OPTION_8_PACKET = WorldPacketType.PLAYER_OPTION_8;
    public static final int PLAYER_OPTION_9_PACKET = WorldPacketType.PLAYER_OPTION_9;
    public static final int PLAYER_OPTION_10_PACKET = WorldPacketType.PLAYER_OPTION_10;
    public static final int INCOMMING_ASSIST = WorldPacketType.INCOMING_ASSIST;
    public static final int ITEM_TAKE_PACKET = WorldPacketType.ITEM_TAKE;
    public static final int EXAMINE_FLOORITEM_PACKET = WorldPacketType.EXAMINE_FLOORITEM;
    public static final int DIALOGUE_CONTINUE_PACKET = WorldPacketType.DIALOGUE_CONTINUE;
    public static final int ENTER_INTEGER_PACKET = WorldPacketType.ENTER_INTEGER;
    public static final int ENTER_NAME_PACKET = WorldPacketType.ENTER_NAME;
    public static final int ENTER_LONG_TEXT_PACKET = WorldPacketType.ENTER_LONG_TEXT;
    public static final int ENTER_STRING_PACKET = -1;
    public static final int SWITCH_INTERFACE_ITEM_PACKET = WorldPacketType.SWITCH_INTERFACE_ITEM;
    public static final int INTERFACE_ON_PLAYER = WorldPacketType.INTERFACE_ON_PLAYER;
    public static final int INTERFACE_ON_NPC = WorldPacketType.INTERFACE_ON_NPC;
    public static final int COLOR_ID_PACKET = WorldPacketType.COLOR_ID;
    public static final int REPORT_ABUSE_PACKET = WorldPacketType.REPORT_ABUSE;
    public static final int GRAND_EXCHANGE_ITEM_SELECT_PACKET = WorldPacketType.GRAND_EXCHANGE_ITEM_SELECT;
    public static final int INTERFACE_ON_FLOORITEM_PACKET = WorldPacketType.INTERFACE_ON_FLOORITEM;
    public static final int WORLD_LIST_UPDATE = WorldPacketType.WORLD_LIST_UPDATE;
    private static final int CUSTOM_PACKET161 = 161;
    public static final int DEVELOPER_PACKET = WorldPacketType.DEVELOPER;

    static {
        WorldPacketType.validate();
    }

    public static String currentTime(String dateFormat) {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        return sdf.format(cal.getTime());
    }

    public static void archiveMessage(Player player, String message, int chatType) {
        try {
            String location = "data/logs/" + (chatType == 2 ? "clanchat" : chatType == 1 ? "friendchat" : "chat")
                    + "/" + player.getUsername() + ".txt";
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(location, true))) {
                writer.write("[" + currentTime("dd MMMMM yyyy 'at' hh:mm:ss z") + "] - " + player.getUsername()
                        + ": " + message);
                writer.newLine();
                writer.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private final transient Player player;

    public Player getPlayer() {
        return player;
    }

    public int getChatType() {
        return chatType;
    }

    public void setChatType(int chatType) {
        this.chatType = chatType;
    }

    private int chatType;

    public WorldPacketsDecoder(Session session, Player player) {
        super(session);
        this.player = player;
    }

    @Override
    public void decode(InputStream stream) {
        while (stream.getRemaining() > 0 && session.getChannel().isConnected() && !player.hasFinished()) {

            int startOffset = stream.getOffset();

            int packetId = stream.readPacket(player);
            if (!WorldPacketType.isValidId(packetId)) {
                if (Settings.DEBUG)
                    System.out.println("PacketId " + packetId + " has fake packet id.");
                stream.setOffset(startOffset);
                break;
            }

            int length = WorldPacketType.sizeFor(packetId);

            if (!WorldPacketType.hasDefinedSize(packetId)) {
                if (Settings.DEBUG) {
                    System.out.println("Packet " + packetId + " (" + WorldPacketType.nameOf(packetId) + ") has no defined size.");
                }
                stream.setOffset(startOffset);
                break;
            }

            if (length == -1) {
                if (stream.getRemaining() < 1) {
                    stream.setOffset(startOffset);
                    break;
                }
                length = stream.readUnsignedByte();
            } else if (length == -2) {
                if (stream.getRemaining() < 2) {
                    stream.setOffset(startOffset);
                    break;
                }
                length = stream.readUnsignedShort();
            }

            if (length > stream.getRemaining()) {
                stream.setOffset(startOffset);
                break;
            }

            byte[] data = new byte[length];
            stream.readBytes(data);

            if (shouldQueue(packetId)) {
                player.queueWorldPacket(new QueuedWorldPacket(packetId, data, true));
                player.setPacketsDecoderPing(Utils.currentTimeMillis());
                continue;
            }

            InputStream packetStream = new InputStream(data);
            processPackets(packetId, packetStream, length);
        }

    }

    public static void decodeQueuedPacket(final Player player, QueuedWorldPacket packet) {
        final int packetId = packet.getId();
        final InputStream stream = new InputStream(packet.getData());

        WorldQueuedPacketDispatcher.handle(player, packetId, stream);
    }

    public void processPackets(final int packetId, InputStream stream, int length) {
        player.setPacketsDecoderPing(Utils.currentTimeMillis());

        if (WorldPacketDispatcher.handle(this, packetId, stream, length)) {
            return;
        }

        if (WorldPacketType.isIgnored(packetId)) {
            return;
        }

        int pos = stream.getOffset();
        System.out.println("Unhandled packet -> id=" + packetId + " (" + WorldPacketType.nameOf(packetId) + ") length=" + length);
        StringBuilder hex = new StringBuilder();
        for (int i = 0; i < length; i++) {
            hex.append(String.format("%02X ", stream.readUnsignedByte()));
        }
        System.out.println("Data: " + hex);
        stream.setOffset(pos);
    }

    private static boolean shouldQueue(int packetId) {
        return WorldPacketType.isQueued(packetId);
    }

    


}
