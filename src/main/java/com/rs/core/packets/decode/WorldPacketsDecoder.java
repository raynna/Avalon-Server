package com.rs.core.packets.decode;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

import com.rs.Settings;
import com.rs.core.thread.CoresManager;
import com.rs.java.game.Animation;
import com.rs.java.game.Graphics;
import com.rs.java.game.World;
import com.rs.java.game.WorldObject;
import com.rs.java.game.WorldTile;
import com.rs.java.game.item.FloorItem;
import com.rs.java.game.item.Item;
import com.rs.java.game.item.ground.AutomaticGroundItem;
import com.rs.java.game.item.ground.GroundItems;
import com.rs.java.game.item.meta.GreaterRunicStaffMetaData;
import com.rs.java.game.minigames.clanwars.ClanWars;
import com.rs.java.game.minigames.duel.DuelArena;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.familiar.Familiar;
import com.rs.java.game.npc.familiar.Familiar.SpecialAttack;
import com.rs.java.utils.*;
import com.rs.kotlin.game.npc.drops.DropTableSource;
import com.rs.kotlin.game.player.AccountCreation;
import com.rs.java.game.player.Inventory;
import com.rs.java.game.player.LogicPacket;
import com.rs.java.game.player.Player;
import com.rs.java.game.player.PublicChatMessage;
import com.rs.java.game.player.QuickChatMessage;
import com.rs.java.game.player.RouteEvent;
import com.rs.java.game.player.Skills;
import com.rs.java.game.player.actions.PlayerFollow;
import com.rs.java.game.player.actions.combat.LunarMagicks;
import com.rs.java.game.player.actions.skills.construction.House;
import com.rs.java.game.player.actions.skills.construction.Sawmill;
import com.rs.java.game.player.actions.skills.construction.Sawmill.Plank;
import com.rs.java.game.player.actions.skills.smithing.DungeoneeringSmithing;
import com.rs.java.game.player.actions.skills.summoning.Summoning;
import com.rs.java.game.player.content.Commands;
import com.rs.java.game.player.content.GambleTest;
import com.rs.java.game.player.content.ReferSystem;
import com.rs.java.game.player.content.ReportAbuse;
import com.rs.java.game.player.content.SkillCapeCustomizer;
import com.rs.java.game.player.content.TicketSystem;
import com.rs.java.game.player.content.clans.ClansManager;
import com.rs.java.game.player.content.customtab.GearTab;
import com.rs.java.game.player.content.customtab.JournalTab;
import com.rs.java.game.player.content.customtab.SettingsTab;
import com.rs.java.game.player.content.friendschat.FriendChatsManager;
import com.rs.java.game.player.content.grandexchange.GrandExchange;
import com.rs.java.game.player.content.pet.Pets;
import com.rs.java.game.player.content.presets.Preset;
import com.rs.java.game.player.content.randomevent.AntiBot;
import com.rs.java.game.player.content.unlockables.UnlockableManager;
import com.rs.java.game.player.controllers.construction.SawmillController;
import com.rs.java.game.player.dialogues.Report;
import com.rs.java.game.route.RouteFinder;
import com.rs.java.game.route.strategy.FixedTileStrategy;
import com.rs.core.packets.InputStream;
import com.rs.core.packets.OutputStream;
import com.rs.core.networking.Session;
import com.rs.core.packets.packet.ButtonHandler;
import com.rs.core.packets.packet.InventoryOptionsHandler;
import com.rs.core.packets.packet.NPCHandler;
import com.rs.core.packets.packet.ObjectHandler;
import com.rs.java.utils.huffman.Huffman;
import com.rs.kotlin.game.player.combat.CombatAction;
import com.rs.kotlin.game.player.combat.magic.*;
import com.rs.kotlin.game.player.command.CommandRegistry;
import com.rs.kotlin.game.player.interfaces.DropInterface;
import com.rs.kotlin.game.player.interfaces.DropSearch;
import com.rs.kotlin.game.player.interfaces.PresetInterface;
import com.rs.kotlin.game.world.pvp.PvpManager;

import static com.rs.kotlin.game.world.util.Msg.warn;

/**
 * Refactored for readability & maintainability while preserving public API,
 * method names, constants and behavior.
 */
public final class WorldPacketsDecoder extends Decoder {

    public static final int EQUIPMENT_REMOVE_PACKET = 216;
    private static final byte[] PACKET_SIZES = new byte[104];

    private static final int WALKING_PACKET = 8;
    private static final int MINI_WALKING_PACKET = 58;
    private static final int AFK_PACKET = 16;
    public static final int ACTION_BUTTON1_PACKET = 14;
    public static final int ACTION_BUTTON2_PACKET = 67;
    public static final int ACTION_BUTTON3_PACKET = 5;
    public static final int ACTION_BUTTON4_PACKET = 55;
    public static final int ACTION_BUTTON5_PACKET = 68;
    public static final int ACTION_BUTTON6_PACKET = 90;
    public static final int ACTION_BUTTON7_PACKET = 6;
    public static final int ACTION_BUTTON8_PACKET = 32;
    public static final int ACTION_BUTTON9_PACKET = 27;
    public static final int WORLD_MAP_CLICK = 38;
    public static final int ACTION_BUTTON10_PACKET = 96;
    public static final int RECEIVE_PACKET_COUNT_PACKET = 33;
    public static final int EQUIPMENT_EXAMINE_PACKET = 3;
    private static final int MOVE_CAMERA_PACKET = 103;
    private static final int INTERFACE_ON_OBJECT = 37;
    private static final int CLICK_PACKET = -1;
    private static final int MOVE_MOUSE_PACKET = -1;
    private static final int KEY_TYPED_PACKET = 1;
    private static final int CLOSE_INTERFACE_PACKET = 54;
    private static final int COMMANDS_PACKET = 60;
    private static final int ITEM_ON_ITEM_PACKET = 3;
    private static final int IN_OUT_SCREEN_PACKET = -1;
    private static final int DONE_LOADING_REGION_PACKET = 30;
    private static final int PING_PACKET = 21;
    private static final int SCREEN_PACKET = 98;
    private static final int CHAT_TYPE_PACKET = 83;
    private static final int CHAT_PACKET = 53;
    private static final int PUBLIC_QUICK_CHAT_PACKET = 86;
    private static final int ADD_FRIEND_PACKET = 89;
    private static final int ADD_IGNORE_PACKET = 4;
    private static final int REMOVE_IGNORE_PACKET = 73;
    private static final int JOIN_FRIEND_CHAT_PACKET = 36;
    private static final int CHANGE_FRIEND_CHAT_PACKET = 22;
    private static final int KICK_FRIEND_CHAT_PACKET = 74;
    private static final int KICK_CLAN_CHAT_PACKET = 92;
    private static final int REMOVE_FRIEND_PACKET = 24;
    private static final int SEND_FRIEND_MESSAGE_PACKET = 82;
    private static final int SEND_FRIEND_QUICK_CHAT_PACKET = 0;
    private static final int OBJECT_CLICK1_PACKET = 26;
    private static final int OBJECT_CLICK2_PACKET = 59;
    private static final int OBJECT_CLICK3_PACKET = 40;
    private static final int OBJECT_CLICK4_PACKET = 23;
    private static final int OBJECT_CLICK5_PACKET = 80;
    private static final int OBJECT_EXAMINE_PACKET = 25;
    private static final int NPC_CLICK1_PACKET = 31;
    private static final int NPC_CLICK2_PACKET = 101;
    private static final int NPC_CLICK3_PACKET = 34;
    private static final int NPC_CLICK4_PACKET = 65;
    private static final int NPC_EXAMINE_PACKET = 9;
    private static final int ATTACK_NPC = 20;
    public static final int PLAYER_OPTION_1_PACKET = 42;
    public static final int PLAYER_OPTION_2_PACKET = 46;
    public static final int PLAYER_OPTION_3_PACKET = 88;
    public static final int PLAYER_OPTION_4_PACKET = 17;
    public static final int PLAYER_OPTION_5_PACKET = 77;
    public static final int PLAYER_OPTION_6_PACKET = 49;
    public static final int PLAYER_OPTION_7_PACKET = 51;
    public static final int PLAYER_OPTION_8_PACKET = 85;
    public static final int PLAYER_OPTION_9_PACKET = 56;
    public static final int PLAYER_OPTION_10_PACKET = 7;
    private static final int INCOMMING_ASSIST = 51;
    private static final int ITEM_TAKE_PACKET = 57;
    private static final int EXAMINE_FLOORITEM_PACKET = 102;
    private static final int DIALOGUE_CONTINUE_PACKET = 72;
    private static final int ENTER_INTEGER_PACKET = 81;
    private static final int ENTER_NAME_PACKET = 29;
    private static final int ENTER_LONG_TEXT_PACKET = 48;
    private static final int ENTER_STRING_PACKET = -1;
    private static final int SWITCH_INTERFACE_ITEM_PACKET = 76;
    private static final int INTERFACE_ON_PLAYER = 50;
    private static final int INTERFACE_ON_NPC = 66;
    private static final int COLOR_ID_PACKET = 97;
    private static final int REPORT_ABUSE_PACKET = 11;
    private static final int GRAND_EXCHANGE_ITEM_SELECT_PACKET = 71;
    private static final int TELEKINETIC_GRAB_SPELL_PACKET = 69;
    private static final int WORLD_LIST_UPDATE = 87;
    private static final int CUSTOM_PACKET161 = 161;
    private static final int DEVELOPER_PACKET = 162;

    static {
        loadPacketSizes();
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

    public static void loadPacketSizes() {
        PACKET_SIZES[0] = -1;
        PACKET_SIZES[1] = -2;
        PACKET_SIZES[2] = -1;
        PACKET_SIZES[3] = 16;
        PACKET_SIZES[4] = -1;
        PACKET_SIZES[5] = 8;
        PACKET_SIZES[6] = 8;
        PACKET_SIZES[7] = 3;
        PACKET_SIZES[8] = -1;
        PACKET_SIZES[9] = 3;
        PACKET_SIZES[10] = -1;
        PACKET_SIZES[11] = -1;
        PACKET_SIZES[12] = -1;
        PACKET_SIZES[13] = 7;
        PACKET_SIZES[14] = 8;
        PACKET_SIZES[15] = 6;
        PACKET_SIZES[16] = 2;
        PACKET_SIZES[17] = 3;
        PACKET_SIZES[18] = -1;
        PACKET_SIZES[19] = -2;
        PACKET_SIZES[20] = 3;
        PACKET_SIZES[21] = 0;
        PACKET_SIZES[22] = -1;
        PACKET_SIZES[23] = 9;
        PACKET_SIZES[24] = -1;
        PACKET_SIZES[25] = 9;
        PACKET_SIZES[26] = 9;
        PACKET_SIZES[27] = 8;
        PACKET_SIZES[28] = 4;
        PACKET_SIZES[29] = -1;
        PACKET_SIZES[30] = 0;
        PACKET_SIZES[31] = 3;
        PACKET_SIZES[32] = 8;
        PACKET_SIZES[33] = 4;// 4
        PACKET_SIZES[34] = 3;
        PACKET_SIZES[35] = -1;
        PACKET_SIZES[36] = -1;
        PACKET_SIZES[37] = 17;
        PACKET_SIZES[38] = 4;
        PACKET_SIZES[39] = 4;
        PACKET_SIZES[40] = 9;
        PACKET_SIZES[41] = -1;
        PACKET_SIZES[42] = 3;
        PACKET_SIZES[43] = 7;
        PACKET_SIZES[44] = -2;
        PACKET_SIZES[45] = 7;
        PACKET_SIZES[46] = 3;
        PACKET_SIZES[47] = 4;
        PACKET_SIZES[48] = -1;
        PACKET_SIZES[49] = 3;
        PACKET_SIZES[50] = 11;
        PACKET_SIZES[51] = 3;
        PACKET_SIZES[52] = -1;
        PACKET_SIZES[53] = -1;
        PACKET_SIZES[54] = 0;
        PACKET_SIZES[55] = 8;
        PACKET_SIZES[56] = 3;
        PACKET_SIZES[57] = 7;
        PACKET_SIZES[58] = -1;
        PACKET_SIZES[59] = 9;
        PACKET_SIZES[60] = -1;
        PACKET_SIZES[61] = 7;
        PACKET_SIZES[62] = 7;
        PACKET_SIZES[63] = 12;
        PACKET_SIZES[64] = 4;
        PACKET_SIZES[65] = 3;
        PACKET_SIZES[66] = 11;
        PACKET_SIZES[67] = 8;
        PACKET_SIZES[68] = 8;// 8
        PACKET_SIZES[69] = 15;
        PACKET_SIZES[70] = 1;
        PACKET_SIZES[71] = 2;
        PACKET_SIZES[72] = 6;
        PACKET_SIZES[73] = -1;
        PACKET_SIZES[74] = -1;
        PACKET_SIZES[75] = -2;
        PACKET_SIZES[76] = 16;
        PACKET_SIZES[77] = 3;
        PACKET_SIZES[78] = 1;
        PACKET_SIZES[79] = 3;
        PACKET_SIZES[80] = 9;
        PACKET_SIZES[81] = 4;
        PACKET_SIZES[82] = -2;
        PACKET_SIZES[83] = 1;
        PACKET_SIZES[84] = 1;
        PACKET_SIZES[85] = 3;
        PACKET_SIZES[86] = -1;
        PACKET_SIZES[87] = 4;
        PACKET_SIZES[88] = 3;
        PACKET_SIZES[89] = -1;
        PACKET_SIZES[90] = 8;
        PACKET_SIZES[91] = -2;
        PACKET_SIZES[92] = -1;
        PACKET_SIZES[93] = -1;
        PACKET_SIZES[94] = 9;
        PACKET_SIZES[95] = -2;
        PACKET_SIZES[96] = 8;
        PACKET_SIZES[97] = 2;
        PACKET_SIZES[98] = 6;
        PACKET_SIZES[99] = 2;
        PACKET_SIZES[100] = -2;
        PACKET_SIZES[101] = 3;
        PACKET_SIZES[102] = 7;
        PACKET_SIZES[103] = 4;
    }

    private final transient Player player;

    public Player getPlayer() {
        return player;
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
            if (packetId < 0 || packetId >= PACKET_SIZES.length) {
                if (Settings.DEBUG)
                    System.out.println("PacketId " + packetId + " has fake packet id.");
                stream.setOffset(startOffset);
                return;
            }

            int length = PACKET_SIZES[packetId];

            if (length == -1) {
                if (stream.getRemaining() < 1) {
                    stream.setOffset(startOffset);
                    return;
                }
                length = stream.readUnsignedByte();
            } else if (length == -2) {
                if (stream.getRemaining() < 2) {
                    stream.setOffset(startOffset);
                    return;
                }
                length = stream.readUnsignedShort();
            }

            if (length > stream.getRemaining()) {
                stream.setOffset(startOffset);
                return;
            }

            int startData = stream.getOffset();
            processPackets(packetId, stream, length);
            stream.setOffset(startData + length);
        }

    }

    private static int resolvePacketLength(InputStream stream, int packetId) {
        int length = PACKET_SIZES[packetId];
        if (length == -1)
            length = stream.readUnsignedByte();
        else if (length == -2)
            length = stream.readUnsignedShort();
        else if (length == -3)
            length = stream.readInt();
        else if (length == -4) {
            length = stream.getRemaining();
            if (Settings.DEBUG)
                System.out.println("Invalid size for PacketId " + packetId + ". Size guessed to be " + length);
        }
        return length;
    }

    public static void decodeLogicPacket(final Player player, LogicPacket packet) {
        final int packetId = packet.getId();
        final InputStream stream = new InputStream(packet.getData());

        if (packetId == WALKING_PACKET || packetId == MINI_WALKING_PACKET) {
            handleWalking(player, stream);
            return;
        }

        if (packetId == INTERFACE_ON_OBJECT) {
            handleInterfaceOnObject(player, stream);
            return;
        }

        if (packetId == PLAYER_OPTION_2_PACKET) {
            handlePlayerOption2(player, stream);
            return;
        }

        if (packetId == PLAYER_OPTION_5_PACKET) {
            handlePlayerOption5(player, stream);
            return;
        }

        if (packetId == PLAYER_OPTION_6_PACKET) {
            handlePlayerOption6(player, stream);
            return;
        }

        if (packetId == PLAYER_OPTION_4_PACKET) {
            handlePlayerOption4(player, stream);
            return;
        }

        if (packetId == PLAYER_OPTION_1_PACKET) {
            handlePlayerOption1_Attack(player, stream);
            return;
        }
        if (packetId == ITEM_ON_ITEM_PACKET) {
            InventoryOptionsHandler.handleItemOnItem(player, stream);
        }

        if (packetId == PLAYER_OPTION_9_PACKET) {
            handlePlayerOption9(player, stream);
            return;
        }

        if (packetId == ATTACK_NPC) {
            handleAttackNpc(player, stream);
            return;
        }

        if (packetId == TELEKINETIC_GRAB_SPELL_PACKET) {
            handleTelekineticGrab(player, stream);
            return;
        }

        if (packetId == INTERFACE_ON_PLAYER) {
            handleInterfaceOnPlayer(player, stream);
            return;
        }

        if (packetId == INTERFACE_ON_NPC) {
            handleInterfaceOnNpc(player, stream);
            return;
        }

        if (packetId == NPC_CLICK1_PACKET)
            NPCHandler.handleOption1(player, stream);
        else if (packetId == NPC_CLICK2_PACKET)
            NPCHandler.handleOption2(player, stream);
        else if (packetId == NPC_CLICK3_PACKET)
            NPCHandler.handleOption3(player, stream);
        else if (packetId == NPC_CLICK4_PACKET)
            NPCHandler.handleOption4(player, stream);
        else if (packetId == OBJECT_CLICK1_PACKET)
            ObjectHandler.handleOption(player, stream, 1);
        else if (packetId == OBJECT_CLICK2_PACKET)
            ObjectHandler.handleOption(player, stream, 2);
        else if (packetId == OBJECT_CLICK3_PACKET)
            ObjectHandler.handleOption(player, stream, 3);
        else if (packetId == OBJECT_CLICK4_PACKET)
            ObjectHandler.handleOption(player, stream, 4);
        else if (packetId == OBJECT_CLICK5_PACKET)
            ObjectHandler.handleOption(player, stream, 5);
        else if (packetId == ITEM_TAKE_PACKET) {
            handleItemTake(player, stream);
        } else if (packetId == EXAMINE_FLOORITEM_PACKET) {
            FloorItem.handleExamine(player, stream);
        }
    }

    public void processPackets(final int packetId, InputStream stream, int length) {
        player.setPacketsDecoderPing(Utils.currentTimeMillis());

        switch (packetId) {
            case PING_PACKET:
                OutputStream packet = new OutputStream(0);
                packet.writePacket(player, 153);
                player.getSession().write(packet);
                return;

            case DEVELOPER_PACKET:
                System.out.println("Developer Packet: " + packetId);
                System.out.println("Value" + stream.readByte());
                return;

            case RECEIVE_PACKET_COUNT_PACKET:
                stream.readInt();
                return;

            case WORLD_LIST_UPDATE:
                int updateType = stream.readInt();
                player.getPackets().sendWorldList(updateType == 0);
                return;
            case ITEM_ON_ITEM_PACKET:
                InventoryOptionsHandler.handleItemOnItem(player, stream);
                return;

            case AFK_PACKET:
                return;

            case CLOSE_INTERFACE_PACKET:
                if (player.hasStarted() && !player.hasFinished() && !player.isActive()) {
                    player.run();
                    return;
                }
                player.closeInterfaces();
                return;

            case MOVE_CAMERA_PACKET:
                stream.readUnsignedShort();
                stream.readUnsignedShort();
                return;

            case SCREEN_PACKET: {
                int displayMode = stream.readUnsignedByte();
                player.setScreenWidth(stream.readUnsignedShort());
                player.setScreenHeight(stream.readUnsignedShort());
                @SuppressWarnings("unused")
                boolean switchScreenMode = stream.readUnsignedByte() == 1;
                if (!player.hasStarted() || player.hasFinished() || displayMode == player.getDisplayMode()
                        || !player.getInterfaceManager().containsInterface(742))
                    return;
                player.setDisplayMode(displayMode);
                player.getInterfaceManager().removeAll();
                player.getInterfaceManager().sendInterfaces();
                player.getInterfaceManager().sendInterface(742);
                return;
            }

            case INCOMMING_ASSIST:
                handleIncomingAssist(player, stream);
                return;

            case DIALOGUE_CONTINUE_PACKET:
                handleDialogueContinue(player, stream);
                return;

            case WORLD_MAP_CLICK:
                handleWorldMapClick(player, stream);
                return;

            case ACTION_BUTTON1_PACKET:
            case ACTION_BUTTON2_PACKET:
            case ACTION_BUTTON4_PACKET:
            case ACTION_BUTTON5_PACKET:
            case ACTION_BUTTON6_PACKET:
            case ACTION_BUTTON7_PACKET:
            case ACTION_BUTTON8_PACKET:
            case ACTION_BUTTON3_PACKET:
            case ACTION_BUTTON9_PACKET:
            case ACTION_BUTTON10_PACKET:
                ButtonHandler.handleButtons(player, stream, packetId);
                return;

            case ENTER_NAME_PACKET:
                handleEnterName(player, stream);
                return;

            case ENTER_STRING_PACKET: {
                if (!player.isActive() || player.isDead())
                    return;
                String value = stream.readString();
                if (value.equals(""))
                    return;
                return;
            }

            case ENTER_LONG_TEXT_PACKET:
                handleEnterLongText(player, stream);
                return;

            case ENTER_INTEGER_PACKET:
                handleEnterInteger(player, stream);
                return;

            case SWITCH_INTERFACE_ITEM_PACKET:
                handleSwitchInterfaceItem(player, stream);
                return;

            case DONE_LOADING_REGION_PACKET:
                if (!player.clientHasLoadedMapRegion())
                    player.setClientHasLoadedMapRegion();
                player.refreshSpawnedObjects();
                player.refreshSpawnedItems();
                return;

            case OBJECT_EXAMINE_PACKET:
                ObjectHandler.handleOption(player, stream, -1);
                return;

            case KEY_TYPED_PACKET:
                int key = stream.readByte();


                if (key == 13) {
                    player.closeInterfaces();
                    return;
                }

                return;

            case NPC_EXAMINE_PACKET:
                NPCHandler.handleExamine(player, stream);
                return;

            case JOIN_FRIEND_CHAT_PACKET:
                if (!player.hasStarted())
                    return;
                FriendChatsManager.joinChat(stream.readString(), player, false);
                return;

            case KICK_FRIEND_CHAT_PACKET:
                if (!player.hasStarted())
                    return;
                player.setLastPublicMessage(Utils.currentTimeMillis() + 1000);
                player.kickPlayerFromFriendsChannel(stream.readString());
                return;

            case KICK_CLAN_CHAT_PACKET: {
                if (!player.hasStarted())
                    return;
                player.setLastPublicMessage(Utils.currentTimeMillis() + 1000);
                boolean guest = stream.readByte() == 1;
                if (!guest)
                    return;
                stream.readUnsignedShort();
                player.kickPlayerFromClanChannel(stream.readString());
                return;
            }

            case CHANGE_FRIEND_CHAT_PACKET:
                if (!player.hasStarted() || !player.getInterfaceManager().containsInterface(1108))
                    return;
                player.getFriendsIgnores().changeRank(stream.readString(), stream.readUnsignedByte128());
                return;

            case ADD_FRIEND_PACKET:
                if (!player.hasStarted())
                    return;
                player.getFriendsIgnores().addFriend(stream.readString());
                return;

            case REMOVE_FRIEND_PACKET:
                if (!player.hasStarted())
                    return;
                player.getFriendsIgnores().removeFriend(stream.readString());
                return;

            case ADD_IGNORE_PACKET:
                if (!player.hasStarted())
                    return;
                player.getFriendsIgnores().addIgnore(stream.readString(), stream.readUnsignedByte() == 1);
                return;

            case REMOVE_IGNORE_PACKET:
                if (!player.hasStarted())
                    return;
                player.getFriendsIgnores().removeIgnore(stream.readString());
                return;

            case SEND_FRIEND_MESSAGE_PACKET:
                handleSendFriendMessage(player, stream);
                return;

            case SEND_FRIEND_QUICK_CHAT_PACKET:
                handleSendFriendQuickChat(player, stream, length);
                return;

            case PUBLIC_QUICK_CHAT_PACKET:
                handlePublicQuickChat(player, stream, length);
                return;

            case CHAT_TYPE_PACKET:
                chatType = stream.readUnsignedByte();
                return;

            case CHAT_PACKET:
                handlePublicChat(player, stream);
                return;

            case COMMANDS_PACKET:
                handleCommands(player, stream);
                return;

            case COLOR_ID_PACKET:
                handleColorId(player, stream);
                return;

            case REPORT_ABUSE_PACKET:
                if (!player.hasStarted() || player == null)
                    return;
                ReportAbuse.Report(player, stream.readString(), stream.readUnsignedByte(),
                        stream.readUnsignedByte() == 1);
                return;

            case GRAND_EXCHANGE_ITEM_SELECT_PACKET:
                int itemId = stream.readUnsignedShort();
                if (player.getInterfaceManager().containsInterface(105))
                    player.getGeManager().chooseItem(itemId);
                else
                    GrandExchange.priceCheckItem(player, itemId);
                return;

            default:
                if (packetId == PING_PACKET || packetId == WALKING_PACKET || packetId == MINI_WALKING_PACKET
                        || packetId == ITEM_TAKE_PACKET || packetId == EXAMINE_FLOORITEM_PACKET
                        || packetId == PLAYER_OPTION_2_PACKET || packetId == PLAYER_OPTION_4_PACKET
                        || packetId == PLAYER_OPTION_6_PACKET || packetId == PLAYER_OPTION_5_PACKET
                        || packetId == PLAYER_OPTION_9_PACKET || packetId == PLAYER_OPTION_1_PACKET || packetId == ATTACK_NPC
                        || packetId == INTERFACE_ON_PLAYER || packetId == INTERFACE_ON_NPC || packetId == NPC_CLICK1_PACKET
                        || packetId == NPC_CLICK2_PACKET || packetId == NPC_CLICK3_PACKET || packetId == NPC_CLICK4_PACKET
                        || packetId == OBJECT_CLICK1_PACKET || packetId == SWITCH_INTERFACE_ITEM_PACKET
                        || packetId == OBJECT_CLICK2_PACKET || packetId == OBJECT_CLICK3_PACKET
                        || packetId == OBJECT_CLICK4_PACKET || packetId == OBJECT_CLICK5_PACKET || packetId == KEY_TYPED_PACKET
                        || packetId == INTERFACE_ON_OBJECT || packetId == TELEKINETIC_GRAB_SPELL_PACKET
                        || packetId == DEVELOPER_PACKET || packetId == EQUIPMENT_REMOVE_PACKET) {
                    player.addLogicPacketToQueue(new LogicPacket(packetId, length, stream, true));
                }
        }
    }

    private static boolean basicPlayerActiveAndLoaded(Player p) {
        return p != null && p.hasStarted() && p.clientHasLoadedMapRegion() && !p.isDead();
    }

    private static boolean canUseInput(Player p) {
        long currentTime = Utils.currentTimeMillis();
        return !p.isLocked() && p.getEmotesManager().getNextEmoteEnd() < currentTime && !p.isLocked();
    }

    private static void handleWalking(Player player, InputStream stream) {
        if (!basicPlayerActiveAndLoaded(player))
            return;
        if (player.isDead()) {
            player.resetWalkSteps();
            return;
        }
        if (player.isLocked()) {
            return;
        }
        if (player.isFrozen()) {
            player.getPackets().sendGameMessage("A magical force prevents you from moving.");
            player.stopAll(true, false, true);
            return;
        }
        if (!player.getControlerManager().canMove(0)) {
            player.stopAll(true, false, true);
            return;
        }
        int length = stream.getLength();
        int baseX = stream.readUnsignedShort128();
        boolean forceRun = stream.readUnsigned128Byte() == 1;
        int baseY = stream.readUnsignedShort128();
        int steps = (length - 5) / 2;
        if (steps > 25)
            steps = 25;
        player.stopAll();
        player.setNextFaceEntity(null);
        if (forceRun)
            player.setRun(true);
        if (steps <= 0)
            return;

        int x = 0, y = 0;
        for (int step = 0; step < steps; step++) {
            x = baseX + stream.readUnsignedByte();
            y = baseY + stream.readUnsignedByte();
        }
        steps = RouteFinder.findRoute(RouteFinder.WALK_ROUTEFINDER, player.getX(), player.getY(),
                player.getPlane(), player.getSize(), new FixedTileStrategy(x, y), true);
        int[] bufferX = RouteFinder.getLastPathBufferX();
        int[] bufferY = RouteFinder.getLastPathBufferY();
        int last = -1;
        for (int i = steps - 1; i >= 0; i--) {
            if (!player.addWalkSteps(bufferX[i], bufferY[i], 25, true))
                break;
            last = i;
        }

        if (last != -1) {
            WorldTile tile = new WorldTile(bufferX[last], bufferY[last], player.getPlane());
            player.getPackets().sendMinimapFlag(
                    tile.getLocalX(player.getLastLoadedMapRegionTile(), player.getMapSize()),
                    tile.getLocalY(player.getLastLoadedMapRegionTile(), player.getMapSize()));
        } else {
            player.getPackets().sendResetMinimapFlag();
        }
        if (player.temporaryAttribute().get("Dreaming") == Boolean.TRUE) {
            player.stopAll(true);
            player.animate(new Animation(6297));
            player.temporaryAttribute().remove("Dreaming");
        }
    }

    private static void handleInterfaceOnObject(Player player, InputStream stream) {
        boolean forceRun = stream.readByte128() == 1;
        int itemId = stream.readShortLE128();
        int y = stream.readShortLE128();
        int objectId = stream.readIntV2();
        int interfaceHash = stream.readInt();
        final int interfaceId = interfaceHash >> 16;
        int componentId = interfaceHash - (interfaceId << 16);
        int slot = stream.readShortLE();
        int x = stream.readShort128();

        if (!basicPlayerActiveAndLoaded(player))
            return;
        if (!canUseInput(player))
            return;

        final WorldTile tile = new WorldTile(x, y, player.getPlane());
        int regionId = tile.getRegionId();
        if (!player.getMapRegionsIds().contains(regionId))
            return;

        WorldObject mapObject = World.getObjectWithId(tile, objectId);
        if (mapObject == null || mapObject.getId() != objectId)
            return;
        final WorldObject object = !player.isAtDynamicRegion() ? mapObject
                : new WorldObject(objectId, mapObject.getType(), mapObject.getRotation(), x, y, player.getPlane());
        final Item item = player.getInventory().getItem(slot);
        if (player.isDead() || componentId < 0 || Utils.getInterfaceDefinitionsSize() <= interfaceId)
            return;
        if (player.isLocked())
            return;
        if (!player.getInterfaceManager().containsInterface(interfaceId))
            return;
        player.stopAll(false);
        if (forceRun)
            player.setRun(true);

        switch (interfaceId) {
            case 430: // Lunar spellbook
                player.setRouteEvent(new RouteEvent(object, () -> {
                    player.faceObject(object);
                    LunarMagicks.RSLunarSpellStore s = LunarMagicks.RSLunarSpellStore.getSpell(componentId);
                    if (s != null) {
                        if (s.getSpellId() == 44)
                            return;
                        player.getTemporaryAttributtes().put("spell_objectid", objectId);
                        if (!LunarMagicks.hasRequirement(player, componentId)) {
                            return;
                        }
                    }
                }));
                break;

            case Inventory.INVENTORY_INTERFACE: // inventory on object
                if (item == null || item.getId() != itemId)
                    return;
                ObjectHandler.handleItemOnObject(player, object, interfaceId, item);
                break;
        }
    }

    private static void handlePlayerOption2(Player player, InputStream stream) {
        if (!basicPlayerActiveAndLoaded(player))
            return;
        stream.readByte(); // unknown
        int playerIndex = stream.readUnsignedShortLE128();
        Player p2 = World.getPlayers().get(playerIndex);
        if (p2 == null || p2.isDead() || p2.hasFinished() || !player.getMapRegionsIds().contains(p2.getRegionId()))
            return;
        if (player.isLocked())
            return;

        if (player.getEquipment().getWeaponId() == 10501) {
            player.faceEntity(p2);
            player.getEquipment().deleteItem(10501, 1);
            player.stopAll(true);
            player.animate(new Animation(7530));
            World.sendFastBowProjectile(player, p2, 1281);
            p2.gfx(new Graphics(862, 100, 0));
            return;
        }
        player.stopAll(false);
        player.getActionManager().setAction(new PlayerFollow(p2));
    }

    private static void handlePlayerOption5(Player player, InputStream stream) {
        stream.readByte(); // unknown
        int playerIndex = stream.readUnsignedShortLE128();
        final Player p2 = World.getPlayers().get(playerIndex);
        player.setRouteEvent(new RouteEvent(p2, () -> {
            if (p2 == null || p2.isDead() || p2.hasFinished() || !player.getMapRegionsIds().contains(p2.getRegionId()))
                return;
            if (player.isLocked())
                return;
            player.stopAll(false);
            if (player.getPlayerRank().isIronman()) {
                player.message("You cannot assist as a " + (player.getPlayerRank().isHardcore()
                        ? "Hardcore Iron " + (player.getAppearance().isMale() ? "Man" : "Woman") + "."
                        : "Iron " + (player.getAppearance().isMale() ? "Man" : "Woman") + "."));
                return;
            }
            if (p2.getPlayerRank().isIronman()) {
                player.message("You cannot assist a " + (p2.getPlayerRank().isHardcore() ? "HC ironman." : "Ironman."));
                return;
            }
            if (player.isCantTrade()) {
                player.getPackets().sendGameMessage("You are busy.");
                return;
            }
            if (p2.getInterfaceManager().containsScreenInter() || p2.isCantTrade()) {
                player.getPackets().sendGameMessage("The other player is busy.");
                return;
            }
            if (p2.temporaryAttribute().get("assist") == player) {
                p2.temporaryAttribute().remove("assist");
                player.getAssist().Assist(p2);
                return;
            }
            player.temporaryAttribute().put("assist", p2);
            player.message("Currently Disabled.");
        }));
    }

    private static void handlePlayerOption6(Player player, InputStream stream) {
        stream.readByte(); // unknown
        int playerIndex = stream.readUnsignedShortLE128();
        final Player p2 = World.getPlayers().get(playerIndex);
        player.setRouteEvent(new RouteEvent(p2, () -> {
            if (p2 == null || p2.isDead() || p2.hasFinished() || !player.getMapRegionsIds().contains(p2.getRegionId()))
                return;
            if (player.isLocked())
                return;
            player.message("Currently out of order.");
        }));
    }

    private static void handlePlayerOption4(Player player, InputStream stream) {
        stream.readByte(); // unknown
        int playerIndex = stream.readUnsignedShortLE128();
        final Player p2 = World.getPlayers().get(playerIndex);
        player.setRouteEvent(new RouteEvent(p2, () -> {
            if (p2 == null || p2.isDead() || p2.hasFinished() || !player.getMapRegionsIds().contains(p2.getRegionId()))
                return;
            if (player.isLocked())
                return;
            player.stopAll(false);
            if (player.getPlayerRank().isIronman()) {
                player.message("You cannot trade as a " + (player.getPlayerRank().isHardcore()
                        ? "Hardcore Iron " + (player.getAppearance().isMale() ? "Man" : "Woman") + "."
                        : "Iron " + (player.getAppearance().isMale() ? "Man" : "Woman") + "."));
                return;
            }
            if (p2.getPlayerRank().isIronman()) {
                player.message("You cannot trade a " + (p2.getPlayerRank().isHardcore() ? "HC ironman." : "Ironman."));
                return;
            }
            if (player.isCantTrade()) {
                player.getPackets().sendGameMessage("You are busy.");
                return;
            }
            if (p2.getInterfaceManager().containsScreenInter() || p2.isCantTrade()) {
                player.getPackets().sendGameMessage("The other player is busy.");
                return;
            }
            if (player.getTemporaryAttributtes().remove("claninvite") != null) {
                ClansManager.viewInvite(player, p2);
                return;
            }
            if (p2.temporaryAttribute().get("TradeTarget") == player) {
                p2.temporaryAttribute().remove("TradeTarget");
                player.getTrade().openTrade(p2);
                p2.getTrade().openTrade(player);
                return;
            }
            player.temporaryAttribute().put("TradeTarget", p2);
            player.getPackets().sendGameMessage("Sending trade offer...");
            p2.getPackets().sendTradeRequestMessage(player);
        }));
    }

    private static void handlePlayerOption1_Attack(Player player, InputStream stream) {
        if (!basicPlayerActiveAndLoaded(player))
            return;
        stream.readByte(); // unknown
        int playerIndex = stream.readUnsignedShortLE128();
        final Player p2 = World.getPlayers().get(playerIndex);
        if (p2 == null || p2.isDead() || p2.hasFinished() || !player.getMapRegionsIds().contains(p2.getRegionId()))
            return;
        if (player.isLocked())
            return;

        // Dungeon invite special case
        if (player.getTemporaryAttributtes().get("DUNGEON_INVITE_RECIEVED") != null) {
            Player inviteBy = (Player) player.getTemporaryAttributtes().get("DUNGEON_INVITE_RECIEVED");
            if (inviteBy != null)
                player.getDungManager().acceptInvite(inviteBy.getDisplayName());
            else
                player.message("inviteBy is null");
            return;
        }
        if (!PvpManager.canPlayerAttack(player, p2))
            return;
        if (!player.getControlerManager().canPlayerOption1(p2))
            return;
        if (!player.isCanPvp())
            return;
        if (!player.getControlerManager().canAttack(p2))
            return;
        player.stopAll();
        player.getActionManager().setAction(new CombatAction(p2));
    }

    private static void handlePlayerOption9(Player player, InputStream stream) {
        boolean forceRun = stream.readUnsignedByte() == 1;
        int playerIndex = stream.readUnsignedShortLE128();
        Player p2 = World.getPlayers().get(playerIndex);
        if (p2 == null || p2 == player || p2.isDead() || p2.hasFinished()
                || !player.getMapRegionsIds().contains(p2.getRegionId()))
            return;
        if (player.isLocked())
            return;
        if (forceRun)
            player.setRun(true);
        player.stopAll();

        ClansManager.viewInvite(player, p2);
    }

    private static void handleAttackNpc(Player player, InputStream stream) {
        if (!basicPlayerActiveAndLoaded(player))
            return;
        if (player.isLocked())
            return;

        int npcIndex = stream.readUnsignedShort128();
        boolean forceRun = stream.read128Byte() == 1;
        player.stopAll();
        if (forceRun)
            player.setRun(true);

        NPC npc = World.getNPCs().get(npcIndex);
        if (npc == null || npc.isDead() || npc.hasFinished()
                || !player.getMapRegionsIds().contains(npc.getRegionId())
                || !npc.getDefinitions().hasAttackOption())
            return;

        if (!player.getControlerManager().canAttack(npc))
            return;

        if (npc instanceof Familiar familiar) {
            if (familiar == player.getFamiliar()) {
                player.getPackets().sendGameMessage("You can't attack your own familiar.");
                return;
            }
            if (!familiar.canAttack(player)) {
                player.getPackets().sendGameMessage("You can't attack this npc.");
                return;
            }
        } else if (!npc.isForceMultiAttacked()) {
            // Multi-combat rules
            if (player.isAtMultiArea() && !npc.isAtMultiArea()) {
                if (npc.getAttackedBy() != player && npc.isPjBlocked()) {
                    player.getPackets().sendGameMessage("This npc is already in combat.");
                    return;
                }
            }
        }

        player.stopAll();
        player.getActionManager().setAction(new CombatAction(npc));
    }

    private static void handleTelekineticGrab(Player player, InputStream stream) {
        if (!basicPlayerActiveAndLoaded(player) || player.isLocked())
            return;

        if (player.isLocked())
            return;

        int xCoord = stream.readShort();
        int yCoord = stream.readShort();
        stream.readShortLE128(); // unknown
        stream.readIntV2();      // unknown
        stream.readShortLE();    // unknown
        boolean forceRun = stream.readByte() == 1;
        int itemId = stream.readShortLE();

        if (forceRun) player.setRun(true);

        final WorldTile tile = new WorldTile(xCoord, yCoord, player.getPlane());
        final int regionId = tile.getRegionId();
        if (!player.getMapRegionsIds().contains(regionId))
            return;

        final FloorItem item = World.getRegion(regionId).getGroundItem(itemId, tile, player);
        if (item == null)
            return;

        player.stopAll(false);

        if (player.getSkills().getLevel(Skills.MAGIC) < 33) {
            player.getPackets().sendGameMessage("You do not have the required level to cast this spell.");
            return;
        }

        boolean staffOfAir = player.getEquipment().getWeaponId() == 1381
                || player.getEquipment().getWeaponId() == 1397
                || player.getEquipment().getWeaponId() == 1405;

        // Rune check
        if (staffOfAir) {
            if (!player.getInventory().containsItem(563, 1)) {
                player.getPackets().sendGameMessage("You do not have the required runes to cast this spell.");
                return;
            }
        } else {
            if (!player.getInventory().containsItem(563, 1) || !player.getInventory().containsItem(556, 1)) {
                player.getPackets().sendGameMessage("You do not have the required runes to cast this spell.");
                return;
            }
        }

        player.setNextFaceWorldTile(tile);
        player.animate(new Animation(711));
        player.getSkills().addXp(Skills.MAGIC, 10);
        player.getInventory().deleteItem(563, 1);
        World.sendProjectileToTile(player, new WorldTile(xCoord, yCoord, player.getPlane()), 142);

        CoresManager.getSlowExecutor().schedule(() -> {
            World.sendGraphics(player, new Graphics(144), tile);
            if (World.getRegion(regionId).getGroundItem(itemId, tile, player) == null) {
                player.getPackets().sendGameMessage("Oops! - To late!");
                return;
            }
            if (!player.getInventory().hasFreeSlots()) {
                player.getPackets().sendGameMessage("You don't have enough inventory space.");
                return;
            }
            player.getInventory().addItem(item.getId(), item.getAmount());
            GroundItems.removeGroundItem(player, item);
        }, 2, TimeUnit.SECONDS);
    }

    private static void handleInterfaceOnPlayer(Player player, InputStream stream) {
        if (!basicPlayerActiveAndLoaded(player))
            return;
        if (player.isLocked())
            return;

        stream.readUnsignedShort();              // junk1
        int playerIndex = stream.readUnsignedShort();
        int interfaceHash = stream.readIntV2();
        stream.readUnsignedShortLE128();         // junk2
        boolean unknown = stream.read128Byte() == 1; // not used
        int interfaceId = interfaceHash >> 16;
        int componentId = interfaceHash - (interfaceId << 16);

        if (componentId < 0 || Utils.getInterfaceDefinitionsSize() <= interfaceId)
            return;
        if (!player.getInterfaceManager().containsInterface(interfaceId))
            return;
        if (componentId == 65535) componentId = -1;
        if (componentId != -1 && Utils.getInterfaceDefinitionsComponentsSize(interfaceId) <= componentId)
            return;

        final Player p2 = World.getPlayers().get(playerIndex);
        if (p2 == null || p2.isDead() || p2.hasFinished() || !player.getMapRegionsIds().contains(p2.getRegionId()))
            return;

        switch (interfaceId) {
            case 1110: // Clan invite button
                if (componentId == 87) {
                    player.setRouteEvent(new RouteEvent(p2, () -> ClansManager.invite(player, p2)));
                }
                break;

            case 662:
            case 747: // Familiar specials on player
                if (player.getFamiliar() == null)
                    return;
                if ((interfaceId == 747 && componentId == 15)
                        || (interfaceId == 662 && componentId == 65)
                        || (interfaceId == 662 && componentId == 74)
                        || (interfaceId == 747 && componentId == 18)) {

                    // (entity special checks are left permissive like original)
                    if (!player.isCanPvp() || !p2.isCanPvp()) {
                        player.getPackets().sendGameMessage("You can only attack players in a player-vs-player area.");
                        return;
                    }
                    if (!player.getFamiliar().canAttack(p2)) {
                        player.getPackets().sendGameMessage("You can only use your familiar in a multi-zone area.");
                        return;
                    }
                    player.getFamiliar().setTarget(p2);
                }
                break;

            case 430: { // Lunar spellbook on player
                LunarMagicks.RSLunarSpellStore lunar = LunarMagicks.RSLunarSpellStore.getSpell(componentId);
                if (lunar != null) {
                    player.getTemporaryAttributtes().put("spell_target", p2);
                    if (lunar.getSpellType() == LunarMagicks.NPC) {
                        player.getPackets().sendGameMessage("You can only cast this spell on a npcs.");
                        return;
                    }
                    if (!LunarMagicks.hasRequirement(player, componentId)) {
                        player.getPackets().sendGameMessage("Nothing interesting happens.");
                        return;
                    }
                }
                break;
            }

            case 193: { // Ancient spellbook on player
                Spell spell = Spellbook.getSpellById(player, componentId);
                if (spell == null) return;

                player.setNextFaceEntity(p2);
                if (spell.getType() instanceof SpellType.Combat) {
                    if (!player.getControlerManager().canAttack(p2)) return;
                    if (!player.isCanPvp() || !p2.isCanPvp()) {
                        player.getPackets().sendGameMessage("You can only attack players in a player-vs-player area.");
                        return;
                    }
                    // multi-combat checks
                    if (player.isAtMultiArea() && !p2.isAtMultiArea()) {
                        if (p2.getAttackedBy() != player && p2.isPjBlocked()) {
                            player.getPackets().sendGameMessage("That player is already in combat.");
                            return;
                        }
                    }
                }
                player.getTemporaryAttributtes().put("spell_target", p2);
                SpellHandler.castOnPlayer(player, spell.getId(), p2);
                break;
            }

            case 192: { // Modern spellbook on player
                Spell spell = Spellbook.getSpellById(player, componentId);
                if (spell == null) return;

                player.setNextFaceEntity(p2);
                if (spell.getType() instanceof SpellType.Combat) {
                    if (!player.getControlerManager().canAttack(p2)) return;
                    if (!player.isCanPvp() || !p2.isCanPvp()) {
                        player.getPackets().sendGameMessage("You can only attack players in a player-vs-player area.");
                        return;
                    }
                }
                player.getTemporaryAttributtes().put("spell_target", p2);
                SpellHandler.castOnPlayer(player, spell.getId(), p2);
                break;
            }
        }
    }

    private static void handleInterfaceOnNpc(Player player, InputStream stream) {
        if (!basicPlayerActiveAndLoaded(player))
            return;
        if (player.isLocked())
            return;

        boolean unknown = stream.readByte() == 1; // unused
        int interfaceHash = stream.readInt();
        int npcIndex = stream.readUnsignedShortLE();
        int interfaceSlot = stream.readUnsignedShortLE128();
        stream.readUnsignedShortLE(); // junk
        int interfaceId = interfaceHash >> 16;
        int componentId = interfaceHash - (interfaceId << 16);

        if (Utils.getInterfaceDefinitionsSize() <= interfaceId)
            return;
        if (!player.getInterfaceManager().containsInterface(interfaceId))
            return;
        if (componentId == 65535) componentId = -1;
        if (componentId != -1 && Utils.getInterfaceDefinitionsComponentsSize(interfaceId) <= componentId)
            return;

        NPC npc = World.getNPCs().get(npcIndex);
        if (npc == null || npc.isDead() || npc.hasFinished()
                || !player.getMapRegionsIds().contains(npc.getRegionId()))
            return;

        player.stopAll(false);

        switch (interfaceId) {
            case Inventory.INVENTORY_INTERFACE: {
                Item item = player.getInventory().getItem(interfaceSlot);
                if (item == null) return;
                InventoryOptionsHandler.handleItemOnNPC(player, npc, item, interfaceSlot);
                break;
            }
            case 662:
            case 747: { // familiar special on npc
                if (player.getFamiliar() == null)
                    return;

                player.resetWalkSteps();
                boolean isEntitySpecial =
                        (interfaceId == 662 && componentId == 74) ||
                                (interfaceId == 747 && componentId == 18);

                if ((interfaceId == 747 && componentId == 15)
                        || (interfaceId == 662 && componentId == 65)
                        || isEntitySpecial
                        || (interfaceId == 747 && componentId == 24)) {

                    if (isEntitySpecial && player.getFamiliar().getSpecialAttack() != SpecialAttack.ENTITY)
                        return;

                    if (npc instanceof Familiar) {
                        Familiar fam = (Familiar) npc;
                        if (fam == player.getFamiliar()) {
                            player.getPackets().sendGameMessage("You can't attack your own familiar.");
                            return;
                        }
                        if (!player.getFamiliar().canAttack(fam.getOwner())) {
                            player.getPackets().sendGameMessage("You can only attack players in a player-vs-player area.");
                            return;
                        }
                    }

                    if (!player.getFamiliar().canAttack(npc)) {
                        player.getPackets().sendGameMessage("You can only use your familiar in a multi-zone area.");
                        return;
                    }

                    player.getFamiliar().setTarget(npc);
                }
                break;
            }

            case 430: { // Lunar on npc
                LunarMagicks.RSLunarSpellStore lunar = LunarMagicks.RSLunarSpellStore.getSpell(componentId);
                if (lunar != null) {
                    player.getTemporaryAttributtes().put("spell_target", npc);
                    if (lunar.getSpellType() == LunarMagicks.PLAYER) {
                        player.getPackets().sendGameMessage("You can only cast this spell on players.");
                        return;
                    }
                    if (!LunarMagicks.hasRequirement(player, componentId)) {
                        player.getPackets().sendGameMessage("Nothing interesting happens.");
                        return;
                    }
                }
                break;
            }

            case 193: { // Ancients on npc
                Spell spell = Spellbook.getSpellById(player, componentId);
                if (spell == null) return;

                player.setNextFaceEntity(npc);
                if (spell.getType() instanceof SpellType.Combat) {
                    if (!npc.getDefinitions().hasAttackOption() && !(npc instanceof Familiar))
                        return;

                    if (npc.getId() == 23921) { // dummy
                        player.getPackets().sendGameMessage("You can't use magic on a dummy.");
                        return;
                    }

                    if (npc instanceof Familiar) {
                        Familiar fam = (Familiar) npc;
                        if (fam == player.getFamiliar()) {
                            player.getPackets().sendGameMessage("You can't attack your own familiar.");
                            return;
                        }
                        if (!fam.canAttack(player)) {
                            if (!fam.isAtMultiArea()) {
                                Player owner = fam.getOwner();
                                player.setNextFaceEntity(owner);
                                player.getTemporaryAttributtes().put("spell_target", owner);
                                SpellHandler.castOnPlayer(player, spell.getId(), owner);
                                return;
                            }
                            player.getPackets().sendGameMessage("You can't attack this npc.");
                            return;
                        }
                    } else if (!npc.isForceMultiAttacked()) {
                        if (player.isAtMultiArea() && !npc.isAtMultiArea()) {
                            if (npc.getAttackedBy() != player && npc.isPjBlocked()) {
                                player.getPackets().sendGameMessage("This npc is already in combat.");
                                return;
                            }
                        }
                        if (!npc.isAtMultiArea() && !player.isAtMultiArea()) {
                            if (player.getAttackedBy() != npc && player.isPjBlocked()) {
                                player.getPackets().sendGameMessage("You are already in combat.");
                                return;
                            }
                            if (npc.getAttackedBy() != player && npc.isPjBlocked()) {
                                player.getPackets().sendGameMessage("This npc is already in combat.");
                                return;
                            }
                        }
                    }

                    if (!player.getControlerManager().canAttack(npc))
                        return;
                }

                player.getTemporaryAttributtes().put("spell_target", npc);
                SpellHandler.castOnNpc(player, spell.getId(), npc);
                break;
            }

            case 192: { // Moderns on npc
                Spell spell = Spellbook.getSpellById(player, componentId);
                if (spell == null) return;

                player.setNextFaceEntity(npc);
                if (spell.getType() instanceof SpellType.Combat) {
                    if (!npc.getDefinitions().hasAttackOption())
                        return;

                    if (npc.getId() == 23921) {
                        player.getPackets().sendGameMessage("You can't use magic on a dummy.");
                        return;
                    }

                    if (npc instanceof Familiar) {
                        Familiar fam = (Familiar) npc;
                        if (fam == player.getFamiliar()) {
                            player.getPackets().sendGameMessage("You can't attack your own familiar.");
                            return;
                        }
                        if (!fam.canAttack(player)) {
                            if (!fam.isAtMultiArea()) {
                                Player owner = fam.getOwner();
                                player.setNextFaceEntity(owner);
                                player.getTemporaryAttributtes().put("spell_target", owner);
                                SpellHandler.castOnPlayer(player, spell.getId(), owner);
                                return;
                            }
                            player.getPackets().sendGameMessage("You can't attack this npc.");
                            return;
                        }
                    } else if (!npc.isForceMultiAttacked()) {
                        if (player.isAtMultiArea() && !npc.isAtMultiArea()) {
                            if (npc.getAttackedBy() != player && npc.isPjBlocked()) {
                                player.getPackets().sendGameMessage("This npc is already in combat.");
                                return;
                            }
                        }
                        if (!npc.isAtMultiArea() && !player.isAtMultiArea()) {
                            if (player.getAttackedBy() != npc && player.isPjBlocked()) {
                                player.getPackets().sendGameMessage("You are already in combat.");
                                return;
                            }
                            if (npc.getAttackedBy() != player && npc.isPjBlocked()) {
                                player.getPackets().sendGameMessage("This npc is already in combat.");
                                return;
                            }
                        }
                    }

                    if (!player.getControlerManager().canAttack(npc))
                        return;
                }

                player.getTemporaryAttributtes().put("spell_target", npc);
                SpellHandler.castOnNpc(player, spell.getId(), npc);
                break;
            }
        }
    }

    private static void handleItemTake(Player player, InputStream stream) {
        if (!basicPlayerActiveAndLoaded(player))
            return;

        if (player.isLocked())
            return;

        int y = stream.readUnsignedShort();
        int x = stream.readUnsignedShortLE();
        final int itemId = stream.readUnsignedShort();
        boolean forceRun = stream.read128Byte() == 1;

        final WorldTile tile = new WorldTile(x, y, player.getPlane());
        final int regionId = tile.getRegionId();

        if (!player.getMapRegionsIds().contains(regionId))
            return;

        if (forceRun) player.setRun(true);
        player.stopAll(false);

        final FloorItem item = World.getRegion(regionId).getGroundItem(itemId, tile, player);
        player.setRouteEvent(new RouteEvent(item, () -> {
            if (item == null) {
                player.message("The item has disappeared.");
                return;
            }
            if (!player.getControlerManager().canTakeItem(item))
                return;

            if (item.hasOwner() && item.isInvisible() && !item.getOwner().getUsername().equalsIgnoreCase(player.getUsername())) {
                if (player.getPlayerRank().isIronman()) {
                    player.message("You are not able to pick up other players' items.");
                    return;
                }
            }

            player.setNextFaceWorldTile(tile);
            if (!player.getTile().matches(tile)) {
                player.animate(new Animation(832));
            }

            if (!player.isFrozen())
                player.addWalkSteps(tile.getX(), tile.getY(), 1);

            // Delegates inventory/bank/pouch/ground logic
            AutomaticGroundItem.pickup(tile, item);
            GroundItems.pickup(player, item);
        }));
    }

    private void handlePublicChat(Player player, InputStream stream) {
        if (!player.hasStarted()) return;
        if (player.getLastPublicMessage() > Utils.currentTimeMillis()) return;
        player.setLastPublicMessage(Utils.currentTimeMillis() + 300);

        int colorEffect = stream.readUnsignedByte();
        int moveEffect = stream.readUnsignedByte();
        String message = Huffman.readEncryptedMessage(200, stream);

        if (colorEffect > 11 || moveEffect > 11) return;
        if (message == null || message.replaceAll(" ", "").equals("")) return;

        // dev cheat
        if (message.equalsIgnoreCase("potato") && player.isDeveloper()) {
            player.getInventory().addItem(5733, 1);
            return;
        }

        // AntiBot quick-answer
        if (message.equalsIgnoreCase(AntiBot.getInstance().getCorrectAnswer())) {
            if (AntiBot.getInstance().hasEvent) {
                AntiBot.getInstance().verify(player, message);
                return;
            }
        }

        // TicketSystem chatroom prefix
        if (message.startsWith(">>")) {
            if (player.isInLiveChat) {
                TicketSystem.handleChat(player, message.replace(">>", ""));
            } else {
                player.message("<col=ff0000>You are currently not in a chatroom.");
            }
            return;
        }

        // filter junk / exploit strings
        if (message.contains("0hdr2ufufl9ljlzlyla") || message.startsWith("0hdr")) return;

        // inline command path (:: or ;;)
        if (message.startsWith("::") || message.startsWith(";;")) {
            String rawCommand = message.substring(2);
            if (CommandRegistry.execute(player, rawCommand)) return;
        }

        if (player.isMuted()) {
            player.message("You're currently muted. Time left: " + player.getMuteTime());
            return;
        }

        int effects = (colorEffect << 8) | (moveEffect & 0xff) & ~0x8000;
        archiveMessage(player, message, chatType);

        if (chatType == 1) {
            player.sendFriendsChannelMessage(message);
        } else if (chatType == 2) {
            player.sendClanChannelMessage(message);
        } else if (chatType == 3) {
            player.sendGuestClanChannelMessage(message);
        } else {
            player.sendPublicChatMessage(new PublicChatMessage(Utils.fixChatMessage(message), effects));
        }

        player.setLastMsg(message);
        if (Settings.DEBUG) Logger.log(this, "Chat type: " + chatType);
    }

    private void handleSendFriendMessage(Player player, InputStream stream) {
        if (!player.hasStarted()) return;
        if (player.isMuted()) {
            player.getPackets().sendGameMessage(
                    "You can't send a PM until your mute is lifted. Time left: " + player.getMuteTime());
            return;
        }
        String username = stream.readString();
        Player p2 = World.getPlayerByDisplayName(username);
        if (p2 == null) return;
        player.getFriendsIgnores().sendMessage(p2, Utils.fixChatMessage(Huffman.readEncryptedMessage(150, stream)));
    }

    private void handleSendFriendQuickChat(Player player, InputStream stream, int length) {
        if (!player.hasStarted() && !World.containsLobbyPlayer(player.getUsername())) return;
        String username = stream.readString();
        int fileId = stream.readUnsignedShort();
        if (fileId > 1163) return;

        byte[] data = null;
        if (length > 3 + username.length()) {
            data = new byte[length - (3 + username.length())];
            stream.readBytes(data);
        }
        if (!Utils.isQuickChatValid(fileId)) return;

        data = Utils.completeQuickMessage(player, fileId, data);
        Player p2 = World.getPlayerByDisplayName(username);
        if (p2 == null) return;

        player.getFriendsIgnores().sendQuickChatMessage(p2, new QuickChatMessage(fileId, data));
    }

    private void handlePublicQuickChat(Player player, InputStream stream, int length) {
        if (!player.hasStarted()) return;
        if (player.getLastPublicMessage() > Utils.currentTimeMillis()) return;
        player.setLastPublicMessage(Utils.currentTimeMillis() + 300);

        stream.readByte(); // secondClientScript (unused)
        int fileId = stream.readUnsignedShort();
        if (fileId > 1163) return;

        byte[] data = null;
        if (length > 3) {
            data = new byte[length - 3];
            stream.readBytes(data);
        }
        if (!Utils.isQuickChatValid(fileId)) return;
        data = Utils.completeQuickMessage(player, fileId, data);

        if (chatType == 0) player.sendPublicChatMessage(new QuickChatMessage(fileId, data));
        else if (chatType == 1) player.sendFriendsChannelQuickMessage(new QuickChatMessage(fileId, data));
        else if (chatType == 2) player.sendClanChannelQuickMessage(new QuickChatMessage(fileId, data));
        else if (chatType == 3) player.sendGuestClanChannelQuickMessage(new QuickChatMessage(fileId, data));
    }

    private void handleCommands(Player player, InputStream stream) {
        if (!player.isActive()) return;
        boolean clientCommand = stream.readUnsignedByte() == 1;
        stream.readUnsignedByte(); // unknown flag currently ignored
        String command = stream.readString();

        if (CommandRegistry.execute(player, command)) return;
        if (!Commands.processCommand(player, command, true, clientCommand))
            Logger.log(this, "Command: " + command);
    }

    private void handleColorId(Player player, InputStream stream) {
        if (!player.hasStarted()) return;
        int colorId = stream.readUnsignedShort();
        if (player.temporaryAttribute().get("SkillcapeCustomize") != null)
            SkillCapeCustomizer.handleSkillCapeCustomizerColor(player, colorId);
        else if (player.temporaryAttribute().get("MottifCustomize") != null)
            ClansManager.setMottifColor(player, colorId);
    }

    private static void handleSwitchInterfaceItem(Player player, InputStream stream) {
        stream.readShortLE128();
        int fromInterfaceHash = stream.readIntV1();
        int toInterfaceHash = stream.readInt();
        int fromSlot = stream.readUnsignedShort();
        int toSlot = stream.readUnsignedShortLE128();
        stream.readUnsignedShortLE();

        int toInterfaceId = toInterfaceHash >> 16;
        int toComponentId = toInterfaceHash - (toInterfaceId << 16);
        int fromInterfaceId = fromInterfaceHash >> 16;
        int fromComponentId = fromInterfaceHash - (fromInterfaceId << 16);

        if (Utils.getInterfaceDefinitionsSize() <= fromInterfaceId
                || Utils.getInterfaceDefinitionsSize() <= toInterfaceId)
            return;
        if (!player.getInterfaceManager().containsInterface(fromInterfaceId)
                || !player.getInterfaceManager().containsInterface(toInterfaceId))
            return;
        if (fromComponentId != -1
                && Utils.getInterfaceDefinitionsComponentsSize(fromInterfaceId) <= fromComponentId)
            return;
        if (toComponentId != -1
                && Utils.getInterfaceDefinitionsComponentsSize(toInterfaceId) <= toComponentId)
            return;

        // inv <-> inv
        if (fromInterfaceId == Inventory.INVENTORY_INTERFACE && fromComponentId == 0
                && toInterfaceId == Inventory.INVENTORY_INTERFACE && toComponentId == 0) {
            toSlot -= 28;
            if (toSlot < 0 || toSlot >= player.getInventory().getItemsContainerSize()
                    || fromSlot >= player.getInventory().getItemsContainerSize())
                return;
            player.getInventory().switchItem(fromSlot, toSlot);
            return;
        }

        // inv overlay (763)
        if (fromInterfaceId == 763 && fromComponentId == 0 && toInterfaceId == 763 && toComponentId == 0) {
            if (toSlot >= player.getInventory().getItemsContainerSize()
                    || fromSlot >= player.getInventory().getItemsContainerSize())
                return;
            player.getInventory().switchItem(fromSlot, toSlot);
            return;
        }

        // bank to bank
        if (fromInterfaceId == 762 && toInterfaceId == 762) {
            player.getBank().switchItem(fromSlot, toSlot, fromComponentId, toComponentId);
            return;
        }

        // notes
        if (fromInterfaceId == 34 && toInterfaceId == 34) {
            player.getNotes().switchNotes(fromSlot, toSlot);
        }
    }

    private void handleEnterInteger(Player player, InputStream stream) {
        if (!player.isActive() || player.isDead()) return;
        int value = stream.readInt();

        if (QuestionScript(player, value)) return;

        // bank deposit/withdraw or legacy bank screen
        if ((player.getInterfaceManager().containsInterface(762)
                && player.getInterfaceManager().containsInterface(763))
                || player.getInterfaceManager().containsInterface(11)) {
            if (value < 0) return;
            Integer bank_item_X_Slot = (Integer) player.temporaryAttribute().remove("bank_item_X_Slot");
            if (bank_item_X_Slot == null) return;
            player.getBank().setLastX(value);
            player.getBank().refreshLastX();
            if (player.temporaryAttribute().remove("bank_isWithdraw") != null)
                player.getBank().withdrawItem(bank_item_X_Slot, value);
            else
                player.getBank().depositItem(bank_item_X_Slot, value,
                        !player.getInterfaceManager().containsInterface(11));
            return;
        }
        if (player.getTemporaryAttributtes().get("PRESET_EDIT_SKILL") != null) {

            Integer skillId = (Integer) player.getTemporaryAttributtes().remove("PRESET_EDIT_SKILL");

            int level;

            try {
                level = value;
            } catch (Exception e) {
                player.message("Invalid number.");
                return;
            }
            if (player.inPkingArea()) {
                warn(player, "You can't change levels in player killing areas.");
                return;
            }

            if (level < 1) level = 1;
            if (level > 99) level = 99;

            Preset preset = PresetInterface.INSTANCE.getSelectedPreset(player);
            if (preset == null)
                return;

            int[] levels = preset.getLevels();
            int index = -1;

            switch (skillId) {
                case 0:
                    index = 0;
                    break; // Attack
                case 1:
                    index = 1;
                    break; // Defence
                case 2:
                    index = 2;
                    break; // Strength
                case 3:
                    index = 3;
                    break; // Hitpoints
                case 4:
                    index = 4;
                    break; // Range
                case 5:
                    index = 5;
                    break; // Prayer
                case 6:
                    index = 6;
                    break; // Magic
                case 23:
                    index = 7;
                    break; // Summoning
            }

            if (index != -1) {
                levels[index] = level;
            }

            PresetInterface.INSTANCE.selectPresetByName(player, preset.getName());
        }


        // Dungeoneering smithing (934)
        if (player.getInterfaceManager().containsInterface(934)
                && player.getTemporaryAttributtes().get("FORGE_X") != null) {
            Integer index = (Integer) player.getTemporaryAttributtes().remove("FORGE_X");
            if (index == null) return;
            boolean dungeoneering = false;
            if (index > 100) {
                index -= 100;
                dungeoneering = true;
            }
            player.closeInterfaces();
            player.getActionManager().setAction(new DungeoneeringSmithing(index, value, dungeoneering));
            return;
        }

        // Custom store
        if (player.getTemporaryAttributtes().get("SHOP_BUY_X_ITEM") != null) {
            if (value <= 0) return;
            player.getShopSystem().handleBuyXInput(value);
            return;
        }

        // Duel arena stake / pouch (628/631)
        if ((player.getInterfaceManager().containsInterface(628)
                && player.getInterfaceManager().containsInterface(631))) {
            if (value <= 0) return;

            if (player.temporaryAttribute().get("duel_addingmoney") != null) {
                player.temporaryAttribute().remove("duel_addingmoney");
                if (player.getControlerManager().getControler() instanceof DuelArena) {
                    DuelArena duel = (DuelArena) player.getControlerManager().getControler();
                    Player target = duel.target;
                    if (target != null && target.getControlerManager().getControler() instanceof DuelArena) {
                        duel.addPouch(1, value);
                        duel.refresh(1);
                        return;
                    }
                }
            }

            if (player.temporaryAttribute().get("duel_item_X_Slot") != null) {
                Integer slot = (Integer) player.temporaryAttribute().remove("duel_item_X_Slot");
                if (slot == null) return;
                if (player.getControlerManager().getControler() instanceof DuelArena) {
                    DuelArena duel = (DuelArena) player.getControlerManager().getControler();
                    Player target = duel.target;
                    if (target != null && target.getControlerManager().getControler() instanceof DuelArena) {
                        if (player.temporaryAttribute().remove("duel_isWithdraw") != null)
                            duel.removeItem(slot, value);
                        else
                            duel.addItem(slot, value);
                        duel.refresh(slot);
                        return;
                    }
                }
            }

            player.getDialogueManager().startDialogue("DungExperiencePurchase", value);
            return;
        }

        // Gambling
        if (player.temporaryAttribute().get("gambling") == Boolean.TRUE) {
            player.temporaryAttribute().put("gambling", Boolean.FALSE);
            int money = value;
            if (player.getInventory().getNumberOf(995) < money && player.getMoneyPouch().getTotal() < money) {
                player.message("You do not have the money to do that.");
                return;
            }
            GambleTest.Gamble(player, money);
            return;
        }

        // Unlockable manager
        if (player.temporaryAttribute().get("unlock_item") == Boolean.TRUE) {
            player.temporaryAttribute().put("unlock_item", Boolean.FALSE);
            if (value <= 0) {
                player.message("Invalid itemId");
                return;
            }
            UnlockableManager.unlockItemForPlayer(player, value);
            return;
        }

        // Runic staff charge
        if (player.temporaryAttribute().get("charge_staff") == Boolean.TRUE) {
            player.temporaryAttribute().put("charge_staff", Boolean.FALSE);
            Item item = (Item) player.getTemporaryAttributtes().get("GREATER_RUNIC_STAFF");
            Boolean inventory = (Boolean) player.getTemporaryAttributtes().get("INTERACT_STAFF_FROM_INVENTORY");
            GreaterRunicStaffMetaData data = (GreaterRunicStaffMetaData) item.getMetadata();
            if (value <= 0) value = 1;
            player.getRunicStaff().chargeStaff(value, data.getSpellId(), inventory);
            return;
        }

        // safe restart
        if (player.temporaryAttribute().get("serverupdate") == Boolean.TRUE) {
            player.temporaryAttribute().put("serverupdate", Boolean.FALSE);
            if (value > 30 || value <= 0) {
                player.message("Max is 30 minutes.");
                return;
            }
            World.safeRestart(value * 60);
            return;
        }

        // DXP toggler
        if (player.temporaryAttribute().get("doubleexp") == Boolean.TRUE) {
            player.temporaryAttribute().put("doubleexp", Boolean.FALSE);
            if (value == 0) return;
            if (value > 5) {
                player.message("Max is 5. You can't go above that.");
                return;
            }
            if (value == 1 && Settings.BONUS_EXP_WEEK_MULTIPLIER > 1) {
                Settings.BONUS_EXP_WEEK_MULTIPLIER = 1.0;
                World.sendWorldMessage("<img=7><col=ffc000>DXP is no longer active.", false);
                return;
            }
            Settings.BONUS_EXP_WEEK_MULTIPLIER = (double) value;
            World.sendWorldMessage("<img=7><col=ffc000>DXP is now live with a multiplier of " + ((double) value) + "!", false);
            return;
        }

        if (player.temporaryAttribute().get("bankAmount") != null) {
            Integer itemId = (Integer) player.temporaryAttribute().remove("bankAmount");
            int invAmt = player.getInventory().getAmountOf(itemId);
            Item bankedItem = player.getBank().getItem(itemId);
            if (itemId == null) return;
            if (!player.getBank().hasBankSpace()) {
                player.message("Not enough bank space.");
                return;
            }
            if (bankedItem != null && bankedItem.getDefinitions().isNoted()) {
                player.message("You can't bank this item.");
                return;
            }
            if (bankedItem != null) {
                if (bankedItem.getAmount() + value <= 0 || bankedItem.getAmount() + invAmt <= 0) {
                    player.message("Not enough space for " + bankedItem.getName() + ".");
                    return;
                }
            }
            if (value > invAmt) {
                player.getInventory().deleteItem(itemId, invAmt);
                player.getBank().addItem(itemId, invAmt, true);
                return;
            }
            player.getInventory().deleteItem(itemId, value);
            player.getBank().addItem(itemId, value, true);
            return;
        }

        // GE price/quantity
        if (player.temporaryAttribute().get("GEPRICESET") != null) {
            if (value == 0) return;
            player.temporaryAttribute().remove("GEQUANTITYSET");
            player.temporaryAttribute().remove("GEPRICESET");
            player.getGeManager().setPricePerItem(value);
            return;
        }
        if (player.temporaryAttribute().get("GEQUANTITYSET") != null) {
            player.temporaryAttribute().remove("GEPRICESET");
            player.temporaryAttribute().remove("GEQUANTITYSET");
            player.getGeManager().setAmount(value);
            return;
        }

        // exp lamp via Avalon points
        if (player.temporaryAttribute().get("exp_lamp") != null) {
            player.temporaryAttribute().remove("exp_lamp");
            if (value <= player.getAvalonPoints()) {
                player.setAvalonPoints(player.getAvalonPoints() - value);
                player.getSkills().addXp(Skills.DUNGEONEERING, value);
                player.getInterfaceManager().closeScreenInterface();
            } else {
                player.getInterfaceManager().closeScreenInterface();
                player.getSkills().addXp(Skills.DUNGEONEERING, player.getAvalonPoints());
                player.setAvalonPoints(0);
            }
            return;
        }

        // price checker
        if (player.getInterfaceManager().containsInterface(206)
                && player.getInterfaceManager().containsInterface(207)) {
            if (value < 0) return;
            Integer pc_item_X_Slot = (Integer) player.temporaryAttribute().remove("pc_item_X_Slot");
            if (pc_item_X_Slot == null) return;
            if (player.temporaryAttribute().remove("pc_isRemove") != null)
                player.getPriceCheckManager().removeItem(pc_item_X_Slot, value);
            else
                player.getPriceCheckManager().addItem(pc_item_X_Slot, value);
            return;
        }

        // summoning (672/666)
        if (player.getInterfaceManager().containsInterface(672)
                || player.getInterfaceManager().containsInterface(666)) {
            if (value < 0) return;
            if (player.temporaryAttribute().get("infuse_scroll_x") != null) {
                Integer idx = (Integer) player.temporaryAttribute().remove("infuse_scroll_x");
                if (idx == null) return;
                Summoning.handlePouchInfusion(player, idx, value);
            } else {
                Integer idx = (Integer) player.temporaryAttribute().remove("infuse_pouch_x");
                if (idx == null) return;
                Summoning.handlePouchInfusion(player, idx, value);
            }
            return;
        }

        if (player.getInterfaceManager().containsInterface(671)
                && player.getInterfaceManager().containsInterface(665)) {
            if (player.getFamiliar() == null || player.getFamiliar().getBob() == null) return;
            if (value < 0) return;
            Integer bob_item_X_Slot = (Integer) player.temporaryAttribute().remove("bob_item_X_Slot");
            if (bob_item_X_Slot == null) return;
            if (player.temporaryAttribute().remove("bob_isRemove") != null)
                player.getFamiliar().getBob().removeItem(bob_item_X_Slot, value);
            else
                player.getFamiliar().getBob().addItem(bob_item_X_Slot, value);
            return;
        }

        // Sawmill actions
        if (player.getInterfaceManager().containsInterface(403)
                && player.getTemporaryAttributtes().get("PlanksConvert") != null) {
            Sawmill.convertPlanks(player, (Plank) player.getTemporaryAttributtes().remove("PlanksConvert"), value);
            return;
        }
        if (player.getInterfaceManager().containsInterface(902)
                && player.getTemporaryAttributtes().get("PlankMake") != null) {
            Integer type = (Integer) player.getTemporaryAttributtes().remove("PlankMake");
            if (player.getControlerManager().getControler() instanceof SawmillController)
                ((SawmillController) player.getControlerManager().getControler()).cutPlank(type, value);
            return;
        }
        if (player.getInterfaceManager().containsInterface(903)
                && player.getTemporaryAttributtes().get("PlankWithdraw") != null) {
            Integer type = (Integer) player.getTemporaryAttributtes().remove("PlankWithdraw");
            if (player.getControlerManager().getControler() instanceof SawmillController)
                ((SawmillController) player.getControlerManager().getControler()).withdrawFromCart(type, value);
            return;
        }

        // Servant request in house
        if (player.getControlerManager().getControler() != null
                && player.getTemporaryAttributtes().get("SERVANT_REQUEST_ITEM") != null) {
            Integer type = (Integer) player.getTemporaryAttributtes().remove("SERVANT_REQUEST_TYPE");
            Integer item = (Integer) player.getTemporaryAttributtes().remove("SERVANT_REQUEST_ITEM");
            if (!player.getHouse().isLoaded() || !player.getHouse().getPlayers().contains(player) || type == null || item == null)
                return;
            player.getHouse().getServantInstance().requestType(item, value, type.byteValue());
            return;
        }

        // trade add/remove/pouch
        if (player.temporaryAttribute().get("trade_item_X_Slot") != null) {
            Integer slot = (Integer) player.temporaryAttribute().get("trade_item_X_Slot");
            player.temporaryAttribute().remove("trade_item_X_Slot");
            if (value < 0) return;
            player.getTrade().addItem(slot, value);
            return;
        }
        if (player.temporaryAttribute().get("trade_removeitem_X_Slot") != null) {
            Integer slot = (Integer) player.temporaryAttribute().get("trade_removeitem_X_Slot");
            player.temporaryAttribute().remove("trade_removeitem_X_Slot");
            if (value < 0) return;
            player.getTrade().removeItem(slot, value);
            return;
        }
        if (player.temporaryAttribute().get("trade_moneypouch_X_Slot") != null) {
            Integer slot = (Integer) player.temporaryAttribute().get("trade_moneypouch_X_Slot");
            player.temporaryAttribute().remove("trade_moneypouch_X_Slot");
            if (value < 0) return;
            player.getTrade().addPouch(value);
            return;
        }

        // set skills & targets
        if (player.temporaryAttribute().get("skillId") != null) {
            if (player.getEquipment().wearingArmour()) {
                player.getDialogueManager().finishDialogue();
                player.getDialogueManager().startDialogue("SimpleMessage", "You cannot do this while having armour on!");
                return;
            }
            int skillId = (Integer) player.temporaryAttribute().remove("skillId");
            if (skillId == Skills.HITPOINTS && value <= 9) value = 10;
            else if (value < 1) value = 1;
            else if (value > 99) value = 99;
            player.getSkills().set(skillId, value);
            player.getSkills().setXp(skillId, Skills.getXPForLevel(value));
            player.getAppearance().generateAppearenceData();
            player.getDialogueManager().finishDialogue();
            return;
        }

        if (player.temporaryAttribute().get("setLevel") != null) {
            int skillId = (Integer) player.temporaryAttribute().remove("setLevel");
            if (value <= player.getSkills().getLevelForXp(skillId)) {
                player.getPackets().sendGameMessage("You can't set a level target lower than your current level.");
                return;
            }
            if (skillId == 24 && value > 120) value = 120;
            if (skillId != 24 && value > 99) value = 99;
            if (value < 1) value = 1;
            player.getSkills().setSkillTarget(true, skillId, value);
            return;
        }

        if (player.temporaryAttribute().get("setXp") != null) {
            int skillId = (Integer) player.temporaryAttribute().remove("setXp");
            if (value <= player.getSkills().getLevelForXp(skillId)) {
                player.getPackets().sendGameMessage("You can't set a experience target lower than your current experience.");
                return;
            }
            if (value > 200000000) value = 200000000;
            if (value < 1) value = 1;
            player.getSkills().setSkillTarget(false, skillId, value);
            return;
        }

        // Drop value threshold
        if (player.getTemporaryAttributtes().get("SET_DROPVALUE") == Boolean.TRUE) {
            player.getTemporaryAttributtes().remove("SET_DROPVALUE");
            if (value < 0) value = 0;
            if (value > Integer.MAX_VALUE) value = Integer.MAX_VALUE;
            player.toggles.put("DROPVALUE", value);
            player.getPackets().sendGameMessage("Drop value set to: "
                    + Utils.getFormattedNumber((Integer) player.toggles.get("DROPVALUE"), ',') + " gp.");
            SettingsTab.open(player);
            return;
        }

        // Title id
        if (player.getTemporaryAttributtes().get("SET_TITLE") == Boolean.TRUE) {
            player.getTemporaryAttributtes().remove("SET_TITLE");
            if (value < 1) value = 0;
            if (value == 0) value = -1;
            if (value > 58 && value != 65535) value = 58;
            player.getAppearance().setTitle(value);
            player.getAppearance().generateAppearenceData();
            player.getPackets().sendGameMessage("Title set to: " + player.getAppearance().getTitleName());
            JournalTab.open(player);
            return;
        }

        // Money pouch withdraw via main gameframe
        if (player.temporaryAttribute().get("money_pouch_remove") == Boolean.TRUE) {
            player.message("withdraw cash");
            player.getMoneyPouch().withdrawPouch(value);
            player.temporaryAttribute().put("money_pouch_remove", Boolean.FALSE);
        }
    }

    private void handleEnterLongText(Player player, InputStream stream) {
        if (!player.isActive() || player.isDead()) return;
        String value = stream.readString();
        if (value.equals("")) return;

        if (QuestionScript(player, value)) return;

        if (player.temporaryAttribute().remove("entering_note") == Boolean.TRUE) {
            player.getNotes().add(value);
            return;
        } else if (player.temporaryAttribute().remove("editing_note") == Boolean.TRUE) {
            player.getNotes().edit(value);
            return;
        } else if (player.temporaryAttribute().remove("refer") == Boolean.TRUE) {
            player.getTemporaryAttributtes().put("refer", Boolean.FALSE);
            ReferSystem.SendInvite(player, value);
            return;
        } else if (player.temporaryAttribute().remove("doubledrop") == Boolean.TRUE) {
            if (value.equalsIgnoreCase("enable")) {
                Settings.DOUBLE_DROP = true;
                World.sendWorldMessage("<img=7><col=ff000>Double drop is now enabled!", false);
            } else if (value.equalsIgnoreCase("disable")) {
                Settings.DOUBLE_DROP = false;
                World.sendWorldMessage("<img=7><col=ff000>Double drop is now disabled!", false);
            }
            return;
        } else if (player.temporaryAttribute().remove("servermsg") == Boolean.TRUE) {
            World.sendWorldMessage("<col=ff000>Attention: " + Utils.fixChatMessage(value), false);
            return;
        } else if (player.temporaryAttribute().remove("tp_player") == Boolean.TRUE) {
            Player other = World.getPlayerByDisplayName(value);
            if (other == null) {
                player.message("Target offline, or does not exist.");
                return;
            }
            if (other.getControlerManager().getControler() != null) player.getAppearance().switchHidden();
            player.setNextWorldTile(new WorldTile(other.getX(), other.getY(), other.getPlane()));
            return;
        } else if (player.temporaryAttribute().remove("tp_to_me") == Boolean.TRUE) {
            Player other = World.getPlayerByDisplayName(value);
            if (other == null) {
                player.message("Target offline, or does not exist.");
                return;
            }
            if (other.getControlerManager().getControler() != null) {
                player.message("Target is in a controler, you must teleport to them or they must exit.");
                return;
            }
            other.setNextWorldTile(new WorldTile(player.getX(), player.getY(), player.getPlane()));
            return;
        } else if (player.temporaryAttribute().remove("sendhome") == Boolean.TRUE) {
            Player other = World.getPlayerByDisplayName(value);
            if (other == null) {
                player.message("Offline, or does not exist.");
                return;
            }
            other.message(player.getDisplayName() + " has sent you home.");
            other.setNextWorldTile(new WorldTile(Settings.HOME_PLAYER_LOCATION));
            other.getInterfaceManager().sendTabInterfaces(false);
            if (other.getControlerManager().getControler() != null)
                other.getControlerManager().getControler().removeControler();
            other.unlock();
            return;
        } else if (player.temporaryAttribute().remove("report_category") == Boolean.TRUE) {
            Report.category = value;
            player.getPackets().sendInputLongTextScript("Description of bug:");
            player.temporaryAttribute().put("report_bug", Boolean.TRUE);
            return;
        } else if (player.temporaryAttribute().remove("report_bug") == Boolean.TRUE) {
            Report.bug = value;
            player.message("Thankyou! We will investigate your case further.");
            Report.archiveBug(player);
            return;
        } else if (player.temporaryAttribute().remove("change_troll_name") == Boolean.TRUE) {
            value = Utils.formatPlayerNameForDisplay(value);
            if (value.length() < 3 || value.length() > 14) {
                player.getPackets().sendGameMessage("You can't use a name shorter than 3 or longer than 14 characters.");
                return;
            }
            if (value.equalsIgnoreCase("none")) {
                player.getPetManager().setTrollBabyName(null);
            } else {
                player.getPetManager().setTrollBabyName(value);
                if (player.getPet() != null && player.getPet().getId() == Pets.TROLL_BABY.getBabyNpcId()) {
                    player.getPet().setName(value);
                }
            }
            return;
        } else if (player.temporaryAttribute().remove("setdisplay") == Boolean.TRUE) {
            if (Utils.invalidAccountName(Utils.formatPlayerNameForProtocol(value))) {
                player.getPackets().sendGameMessage("Name contains invalid characters or is too short/long.");
                return;
            }
            if (!DisplayNames.setDisplayName(player, value)) {
                player.getPackets().sendGameMessage("This name is already in use.");
                return;
            }
            player.getPackets().sendGameMessage("Your display name was successfully changed.");
            return;
        }

        if (player.getInterfaceManager().containsInterface(1103))
            ClansManager.setClanMottoInterface(player, value);
    }

    private void handleEnterName(Player player, InputStream stream) {
        if (!player.isActive() || player.isDead()) return;
        String value = stream.readString();
        if (value.equals("")) return;

        // punish workflow
        if (player.temporaryAttribute().get("PUNISH_NAME") == Boolean.TRUE) {
            if (World.getPlayer(value) == null) {
                value = Utils.formatPlayerNameForProtocol(value);
                if (!AccountCreation.exists(value)) {
                    player.getPackets().sendGameMessage("No such account named " + value + " was found in the database.");
                } else {
                    player.getDialogueManager().startDialogue("Punish", value, false);
                }
                player.temporaryAttribute().put("PUNISH_NAME", Boolean.FALSE);
                return;
            }
            Player target = World.getPlayerByDisplayName(value);
            try {
                player.getDialogueManager().startDialogue("Punish", target, true);
            } catch (Exception e) {
                player.getPackets().sendGameMessage(Utils.formatPlayerNameForDisplay(value) + " wasn't found.");
            }
            player.temporaryAttribute().put("PUNISH_NAME", Boolean.FALSE);
            return;
        }

        if (player.getInterfaceManager().containsInterface(1108)) {
            player.getFriendsIgnores().setChatPrefix(value);
            return;
        } else if (player.temporaryAttribute().remove("setclan") != null) {
            ClansManager.createClan(player, value);
            return;
        } else if (player.temporaryAttribute().remove("joinguestclan") != null) {
            ClansManager.connectToClan(player, value, true);
            return;
        } else if (player.temporaryAttribute().remove("banclanplayer") != null) {
            ClansManager.banPlayer(player, value);
            return;
        } else if (player.temporaryAttribute().remove("unbanclanplayer") != null) {
            ClansManager.unbanPlayer(player, value);
            return;
        } else if (player.getTemporaryAttributtes().remove("enterhouse") == Boolean.TRUE) {
            House.enterHouse(player, value);
            return;
        } else if (player.getTemporaryAttributtes().remove("DUNGEON_INVITE") == Boolean.TRUE) {
            player.getDungManager().invite(value);
            return;
        } else if (player.temporaryAttribute().get("TITLE_COLOR_SET") != null) {

            String input = value.toLowerCase().trim();

            String presetHex = Colors.getHexByName(input);
            if (presetHex != null) {

                player.setCustomTitleColour(presetHex.toLowerCase());
                player.setCustomTitle(player.getCustomTitle());
                player.getAppearance().generateAppearenceData();
                JournalTab.open(player);
                player.message("Set your title colour to: <col=" + presetHex + ">COLOUR");
            } else {
                String hex = input;
                if (hex.startsWith("#")) {
                    hex = hex.substring(1);
                }
                if (!hex.matches("[0-9a-fA-F]{6}")) {
                    player.getDialogueManager().startDialogue(
                            "SimpleMessage",
                            "Enter a valid 6-digit HEX or preset (red, blue, darkred, etc)."
                    );
                    return;
                }
                player.setCustomTitleColour(hex.toLowerCase());
                player.setCustomTitle(player.getCustomTitle());
                player.getAppearance().generateAppearenceData();
                JournalTab.open(player);
                player.message("Set your title colour to: <col=" + hex + ">COLOUR");
            }
            player.getPackets().sendRunScript(109, "Would you like the title infront or behind your name? Front/Back");
            player.temporaryAttribute().put("TITLE_ORDER_SET", Boolean.TRUE);

            player.temporaryAttribute().remove("CUSTOM_TITLE_SET");
            player.temporaryAttribute().remove("TITLE_COLOR_SET");
            return;
        } else if (player.temporaryAttribute().get("npc_find") != null) {

            player.temporaryAttribute().remove("npc_find");

            // Rebuild master list then filter by name
            DropInterface.INSTANCE.open(player, true);

            List<DropTableSource> results =
                    DropSearch.INSTANCE.findSourcesByName(value);

            player.temporaryAttribute().put("drop_viewer_found_npcs", results);
            player.temporaryAttribute().put("drop_viewer_npc_page", 0);
            player.temporaryAttribute().put("drop_viewer_in_search", true);

            player.temporaryAttribute().put("drop_viewer_source_filter", value);

            DropInterface.INSTANCE.sendSourceList(player);

            if (!results.isEmpty())
                DropInterface.INSTANCE.selectSource(player, results.get(0));

            return;

        } else if (player.temporaryAttribute().get("drop_find") != null) {

            player.temporaryAttribute().remove("drop_find");

            List<DropTableSource> results =
                    DropSearch.INSTANCE.findSourcesByDrop(value);

            player.temporaryAttribute().put("drop_viewer_found_npcs", results);
            player.temporaryAttribute().put("drop_viewer_npc_page", 0);
            player.temporaryAttribute().put("drop_viewer_in_search", true);
            player.temporaryAttribute().put("drop_viewer_item_filter", value);

            DropInterface.INSTANCE.sendSourceList(player);

            if (!results.isEmpty())
                DropInterface.INSTANCE.selectSource(player, results.getFirst());

            return;
        } else if (player.temporaryAttribute().get("TITLE_ORDER_SET") != null) {
            if (value.toLowerCase().contains("back") || value.equalsIgnoreCase("b")) {
                player.titleIsBehindName = true;
                player.getAppearance().setTitle(901);
                player.message("Set your title order to the back.");
            } else if (value.toLowerCase().contains("front") || value.equalsIgnoreCase("f")) {
                player.titleIsBehindName = false;
                player.getAppearance().setTitle(900);
                player.message("Set your title order to the front.");
            }
            player.getDialogueManager().startDialogue("SimpleMessage", "The process was successfully done!");
            if (player.titleIsBehindName) {
                player.getAppearance().setTitle(901);
            } else {
                player.getAppearance().setTitle(900);
            }
            player.getAppearance().generateAppearenceData();
            JournalTab.open(player);
            player.temporaryAttribute().remove("CUSTOM_TITLE_SET");
            player.temporaryAttribute().remove("TITLE_COLOR_SET");
            player.temporaryAttribute().remove("TITLE_ORDER_SET");
            return;
        } else if (player.temporaryAttribute().get("CUSTOM_TITLE_SET") != null) {
            try {
                int titleId = Integer.parseInt(value);

                if (titleId >= 0 && titleId <= 58) {

                    if (titleId == 0)
                        titleId = -1;

                    player.getAppearance().setTitle(titleId);
                    player.getAppearance().generateAppearenceData();
                    player.getPackets().sendGameMessage(
                            "Title set to: " + player.getAppearance().getTitleName()
                    );
                    JournalTab.open(player);
                    return;
                }
            } catch (NumberFormatException ignored) {
            }
            String[] invalid = {">", "<", "_", "donator", "superdonator", "member", "mod", "admin", "owner", "jagex", "developer", "recruit"};
            if (value.length() > 10) {
                player.getDialogueManager().startDialogue("SimpleMessage", "Titles are limted to ten characters due to spam.");
                return;
            }
            for (String s : invalid) {
                if (value.toLowerCase().contains(s)) {
                    player.getDialogueManager().startDialogue("SimpleMessage", "You cannot use this in your title.");
                    player.temporaryAttribute().remove("CUSTOM_TITLE_SET");
                    return;
                }
            }
            player.setCustomTitle(value);
            player.getAppearance().setTitle(900);
            player.message("Set your title to: <col=" + player.getCustomTitleColour() + ">" + value);
            JournalTab.open(player);
            player.getPackets().sendRunScript(109, "Enter a color (HEX) or preset: " + Colors.getPresetList());
            player.temporaryAttribute().put("TITLE_COLOR_SET", Boolean.TRUE);
            player.temporaryAttribute().remove("CUSTOM_TITLE_SET");
            player.temporaryAttribute().remove("TITLE_ORDER_SET");
            return;
        } else if (player.temporaryAttribute().get("PRESET_SAVE_PROMPT") == Boolean.TRUE) {
            player.temporaryAttribute().remove("PRESET_SAVE_PROMPT");
            String[] invalid = {">", "<"};
            for (String s : invalid) {
                if (value.toLowerCase().contains(s)) {
                    player.getDialogueManager().startDialogue("SimpleMessage", "You cannot use < or > in preset names.");
                    return;
                }
            }
            player.getPresetManager().savePreset(value);
            boolean fromBank = Boolean.TRUE.equals(
                    player.getTemporaryAttributtes().get("preset_opened_from_bank")
            );

            PresetInterface.INSTANCE.open(player, fromBank);
            PresetInterface.INSTANCE.selectPresetByName(player, value);
        } else if (player.temporaryAttribute().get("SAVESETUP") == Boolean.TRUE) {
            player.temporaryAttribute().remove("SAVESETUP");
            player.getPresetManager().savePreset(value);
            GearTab.refresh(player);
            return;
        }
        if (player.getTemporaryAttributtes().get("preset_rename_target") != null) {
            String oldName = (String) player.getTemporaryAttributtes().remove("preset_rename_target");
            String[] invalid = {">", "<"};
            for (String s : invalid) {
                if (value.toLowerCase().contains(s)) {
                    player.getDialogueManager().startDialogue("SimpleMessage", "You cannot use < or > in preset names.");
                    return;
                }
            }
            String newName = value;

            if (newName.trim().isEmpty()) {
                player.message("Invalid name.");
                return;
            }

            newName = newName.toLowerCase();

            if (player.getPresetManager().PRESET_SETUPS.containsKey(newName)) {
                player.message("A preset with that name already exists.");
                return;
            }

            Preset preset = player.getPresetManager().PRESET_SETUPS.remove(oldName.toLowerCase());

            if (preset == null) {
                player.message("Preset not found.");
                return;
            }

            preset.setName(newName);
            player.getPresetManager().PRESET_SETUPS.put(newName, preset);

            player.message("Preset renamed to " + newName + ".");

            boolean fromBank = Boolean.TRUE.equals(
                    player.getTemporaryAttributtes().get("preset_opened_from_bank")
            );

            PresetInterface.INSTANCE.open(player, fromBank);
            PresetInterface.INSTANCE.selectPresetByName(player, newName);
            return;
        } else if (player.temporaryAttribute().get("RENAME_SETUP") == Boolean.TRUE) {
            player.temporaryAttribute().remove("RENAME_SETUP");
            Integer selectedGear = (Integer) player.getTemporaryAttributtes().get("SELECTED_RENAME");
            if (selectedGear != null) {
                String keyToRename = null;
                Preset presetToRename = null;
                for (Map.Entry<String, Preset> entry : player.getPresetManager().PRESET_SETUPS.entrySet()) {
                    if (entry.getValue().getId(player) == selectedGear) {
                        keyToRename = entry.getKey();
                        presetToRename = entry.getValue();
                        break;
                    }
                }
                if (keyToRename != null && presetToRename != null) {
                    player.getPresetManager().PRESET_SETUPS.remove(keyToRename);
                    presetToRename.setName(value);
                    player.getPresetManager().PRESET_SETUPS.put(value, presetToRename);
                    player.getTemporaryAttributtes().remove("SELECTED_RENAME");
                    player.getPackets().sendGameMessage("Preset \"" + keyToRename + "\" renamed to \"" + value + "\".");
                    GearTab.refresh(player);
                    if (player.getInterfaceManager().containsInterface(PresetInterface.INTERFACE_ID)) {
                        PresetInterface.INSTANCE.selectPresetByName(player, value);
                    }
                } else {
                    player.getPackets().sendGameMessage("Could not find the preset to rename.");
                }
            } else {
                player.getPackets().sendGameMessage("No preset selected to rename.");
            }
            return;
        } else if (player.temporaryAttribute().get("OTHERPRESET") == Boolean.TRUE) {
            player.temporaryAttribute().remove("OTHERPRESET");
            String otherName = Utils.formatPlayerNameForDisplay(value);
            Player p2 = World.getPlayerByDisplayName(otherName);
            if (p2 != null) {
                player.getTemporaryAttributtes().put("OTHERPRESET_NAME", otherName);
                GearTab.open(player, otherName);
                player.getPackets().sendGameMessage("Viewing " + otherName + " presets.");
            } else {
                if (!AccountCreation.exists(otherName)) {
                    player.getPackets().sendGameMessage("Account name " + Utils.formatPlayerNameForDisplay(otherName) + " doesn't exist.");
                    GearTab.open(player, null);
                } else {
                    p2 = AccountCreation.loadPlayer(otherName);
                    player.getTemporaryAttributtes().put("OTHERPRESET_NAME", otherName);
                    GearTab.open(player, otherName);
                    player.getPackets().sendGameMessage("Viewing " + otherName + " presets.");
                }
            }
            return;
        } else if (player.temporaryAttribute().get("AdventureLog") == Boolean.TRUE) {
            Player other = AccountCreation.loadPlayer(Utils.formatPlayerNameForProtocol(value));
            if (other == null) {
                player.getDialogueManager().startDialogue("SimpleMessage", "This player was not found in our database.");
                return;
            }
            player.getAdventureLog().OpenAdventureLog(other, value);
            player.temporaryAttribute().put("AdventureLog", false);
            return;
        } else if (player.temporaryAttribute().get("muting_reason") == Boolean.TRUE) {
            player.temporaryAttribute().remove("muting_reason");
            player.message("Value: " + value);
            return;
        } else if (player.temporaryAttribute().remove("entering_note") == Boolean.TRUE) {
            player.getNotes().add(value);
            return;
        } else if (player.temporaryAttribute().remove("editing_note") == Boolean.TRUE) {
            player.getNotes().edit(value);
            return;
        } else if (player.temporaryAttribute().get("view_name") == Boolean.TRUE) {
            player.temporaryAttribute().remove("view_name");
            Player other = World.getPlayerByDisplayName(value);
            if (other == null) {
                player.getPackets().sendGameMessage("Couldn't find player.");
                return;
            }
            ClanWars clan = other.getCurrentFriendChat() != null ? other.getCurrentFriendChat().getClanWars() : null;
            if (clan == null) {
                player.getPackets().sendGameMessage("This player's clan is not in war.");
                return;
            }
            if (clan.getSecondTeam().getOwnerDisplayName() != other.getCurrentFriendChat().getOwnerDisplayName()) {
                player.temporaryAttribute().put("view_prefix", 1);
            }
            player.temporaryAttribute().put("view_clan", clan);
            ClanWars.enter(player);
            return;
        } else if (player.temporaryAttribute().remove("setdisplay") != null) {
            DisplayNames.setDisplayName(player, value);
            return;
        } else if (player.temporaryAttribute().remove("VERIFY_PASSWORD") == Boolean.TRUE) {
            value = value.trim();
            String encrypted = Encrypt.hashPassword(value);

            if (!encrypted.equals(player.getPassword())) {
                player.getPackets().sendGameMessage("Incorrect current password.");
                return;
            }

            player.temporaryAttribute().put("SET_NEW_PASSWORD", Boolean.TRUE);
            player.getPackets().sendInputNameScript("Enter your new password:");
            return;
        } else if (player.temporaryAttribute().remove("SET_NEW_PASSWORD") == Boolean.TRUE) {
            value = value.trim();
            if (value.length() < 5 || value.length() > 15) {
                player.getPackets().sendGameMessage(
                        "Password length is limited to 5-15 characters."
                );
                return;
            }

            player.setPassword(Encrypt.hashPassword(value));
            player.getPackets().sendGameMessage("Your password has been changed successfully.");
            return;
        } else if (player.temporaryAttribute().remove("SETUSERNAME") != null) {
            DisplayNames.queueUsernameChange(player, value);
            JournalTab.open(player);
            return;
        }

        if (player.getInterfaceManager().containsInterface(1103))
            ClansManager.setClanMottoInterface(player, value);
    }

    private static void handleWorldMapClick(Player player, InputStream stream) {
        int coordinateHash = stream.readInt();
        int x = coordinateHash >> 14;
        int y = coordinateHash & 0x3fff;
        int plane = coordinateHash >> 28;

        Integer hash = (Integer) player.temporaryAttribute().get("worldHash");
        if (hash == null || coordinateHash != hash) {
            player.temporaryAttribute().put("worldHash", coordinateHash);
        } else {
            player.temporaryAttribute().remove("worldHash");
            player.getHintIconsManager().addHintIcon(x, y, plane, 20, 0, 2, -1, true);
            player.getPackets().sendVar(1159, coordinateHash);
        }
    }

    private static void handleDialogueContinue(Player player, InputStream stream) {
        int interfaceHash = stream.readInt();
        stream.readShort128(); // junk
        int interfaceId = interfaceHash >> 16;
        if (Utils.getInterfaceDefinitionsSize() <= interfaceId) return;
        if (!player.isActive() || !player.getInterfaceManager().containsInterface(interfaceId)) return;
        int componentId = interfaceHash - (interfaceId << 16);
        player.getDialogueManager().continueDialogue(interfaceId, componentId);
    }

    private static void handleIncomingAssist(Player player, InputStream stream) {
        stream.readByte(); // unknown
        int playerIndex = stream.readUnsignedShortLE128();
        final Player p2 = World.getPlayers().get(playerIndex);
        player.setRouteEvent(new RouteEvent(p2, () -> {
            if (p2 == null || p2.isDead() || p2.hasFinished()
                    || !player.getMapRegionsIds().contains(p2.getRegionId()))
                return;
            if (player.isLocked())
                return;
            player.stopAll(false);
            if (player.getPlayerRank().isIronman()) {
                player.message("You cannot assist as a " + (player.getPlayerRank().isHardcore() ? "HC ironman." : "Ironman."));
                return;
            }
            if (p2.getPlayerRank().isIronman()) {
                player.message("You cannot assist a " + (p2.getPlayerRank().isHardcore() ? "HC ironman." : "Ironman."));
                return;
            }
            if (p2.getInterfaceManager().containsScreenInter()) {
                player.getPackets().sendGameMessage("The other player is busy.");
                return;
            }
            if (p2.temporaryAttribute().get("assist") == player) {
                p2.temporaryAttribute().remove("assist");
                player.getAssist().Assist(p2);
            }
        }));
    }

    private static void handleClickPacket(Player player, InputStream stream) {
        int mouseHash = stream.readShortLE128();
        int mouseButton = mouseHash >> 15;
        int time = mouseHash - (mouseButton << 15);
        int positionHash = stream.readIntV1();
        int y = positionHash >> 16;
        int x = positionHash - (y << 16);
        @SuppressWarnings("unused")
        boolean clicked;
        if (time <= 1 || x < 0 || x > player.getScreenWidth() || y < 0 || y > player.getScreenHeight()) {
            clicked = false;
            return;
        }
        clicked = true;
    }

    /**
     * Handles the multi-step punishment prompt flow (perm/x with optional reason).
     * Returns true if this consumed the input, false if not applicable.
     */
    boolean QuestionScript(Player player, Object in) {
        Object q = player.temporaryAttribute().get("QUESTION_SCRIPT");
        if (q == null) return false;

        Object[] questionScript = (Object[]) q;
        player.temporaryAttribute().remove("QUESTION_SCRIPT");

        File acc = null;
        String question = null;
        Player target = null;
        String name = "null";
        Integer xtime = 0;
        boolean online = true;

        if (questionScript.length == 4) {
            question = (String) questionScript[0];
            target = (Player) questionScript[1];
            online = (boolean) questionScript[2];
            name = (String) questionScript[3];
        } else if (questionScript.length == 5) {
            question = (String) questionScript[0];
            target = (Player) questionScript[1];
            xtime = (Integer) questionScript[2];
            online = (boolean) questionScript[3];
            name = (String) questionScript[4];
        }

        if (in == null || question == null) return false;

        if (question.startsWith("perm") || question.equalsIgnoreCase("blackmark")) {
            if (((String) in).length() < 5) {
                player.temporaryAttribute().put("QUESTION_SCRIPT", questionScript);
                player.getPackets().sendRunScript(110,
                        new Object[]{"Reason to short! Enter a brief reason for this punishment:"});
                return true;
            }

            if (!online) {
                acc = new File("data/characters/" + name.replace(" ", "_") + ".p");
                try {
                    target = (Player) SerializableFilesManager.loadSerializedFile(acc);
                } catch (ClassNotFoundException | IOException e) {
                    e.printStackTrace();
                }
                switch (question.toLowerCase()) {
                    case "permmute":
                        player.getPackets().sendGameMessage("You have permanently muted %s. Reason: %s",
                                Utils.formatPlayerNameForDisplay(name), in);
                        if (target != null) target.mute(player.getDisplayName(), (String) in, -1);
                        break;
                }
                try {
                    if (target != null)
                        SerializableFilesManager.storeSerializableClass(target, acc);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                switch (question.toLowerCase()) {
                    case "permmute":
                        player.getPackets().sendGameMessage("You have permanently jailed %s. Reason: %s",
                                target.getDisplayName(), in);
                        target.getPackets().sendGameMessage("You have been jailed by %s. Reason: %s",
                                player.getDisplayName(), in);
                        target.mute(player.getDisplayName(), (String) in, -1);
                        break;
                }
            }
            return true;
        }

        if (question.equalsIgnoreCase("xreasonjail") || question.equalsIgnoreCase("xreasonmute")) {
            if (xtime == 0) return false;

            if (!online) {
                acc = new File("data/characters/" + name.replace(" ", "_") + ".p");
                try {
                    target = (Player) SerializableFilesManager.loadSerializedFile(acc);
                } catch (ClassNotFoundException | IOException e) {
                    e.printStackTrace();
                }
                switch (question.toLowerCase()) {
                    case "xreasonmute":
                        player.getPackets().sendGameMessage("You have muted %s for %d days. Reason: %s",
                                Utils.formatPlayerNameForDisplay(name), xtime, in);
                        if (target != null) target.mute(player.getDisplayName(), (String) in, xtime);
                        break;
                }
                try {
                    if (target != null)
                        SerializableFilesManager.storeSerializableClass(target, acc);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                switch (question.toLowerCase()) {
                    case "xreasonmute":
                        player.getPackets().sendGameMessage("You have muted %s for %d days. Reason: %s",
                                target.getDisplayName(), xtime, in);
                        target.getPackets().sendGameMessage("You have been muted by %s. Reason: %s",
                                player.getDisplayName(), in);
                        target.mute(player.getDisplayName(), (String) in, xtime);
                        break;
                }
            }
            return true;
        }

        if (question.startsWith("x")) { // xjail / xmute
            if ((Integer) in < 1 || (Integer) in > 15) {
                player.temporaryAttribute().put("QUESTION_SCRIPT", questionScript);
                player.getPackets().sendInputIntegerScript(true, "Number must be between 1 - 15 days:");
                return true;
            }

            switch (question.toLowerCase()) {
                case "xjail":
                    player.temporaryAttribute().put("QUESTION_SCRIPT",
                            new Object[]{"xreasonjail", target, in, online, name});
                    player.getPackets().sendRunScript(110, new Object[]{"Enter a brief reason for this punishment:"});
                    break;

                case "xmute":
                    player.temporaryAttribute().put("QUESTION_SCRIPT",
                            new Object[]{"xreasonmute", target, in, online, name});
                    player.getPackets().sendRunScript(110, new Object[]{"Enter a brief reason for this punishment:"});
                    break;
            }
            return true;
        }

        return false;
    }


}
