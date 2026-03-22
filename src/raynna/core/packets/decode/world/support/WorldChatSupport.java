package raynna.core.packets.decode.world.support;

import raynna.core.packets.decode.WorldPacketsDecoder;

import raynna.app.Settings;
import raynna.core.packets.InputStream;
import raynna.game.World;
import raynna.game.player.Player;
import raynna.game.player.PublicChatMessage;
import raynna.game.player.QuickChatMessage;
import raynna.game.player.content.Commands;
import raynna.game.player.content.ReportAbuse;
import raynna.game.player.content.SkillCapeCustomizer;
import raynna.game.player.content.TicketSystem;
import raynna.game.player.bot.PlayerBotManager;
import raynna.game.player.content.clans.ClansManager;
import raynna.game.player.content.randomevent.AntiBot;
import raynna.util.Logger;
import raynna.util.Utils;
import raynna.util.huffman.Huffman;
import raynna.game.player.command.CommandRegistry;

public final class WorldChatSupport {

    private WorldChatSupport() {
    }
    public static void handlePublicChat(WorldPacketsDecoder decoder, Player player, InputStream stream) {
        if (!player.hasStarted()) return;
        if (player.getLastPublicMessage() > Utils.currentTimeMillis()) return;
        player.setLastPublicMessage(Utils.currentTimeMillis() + 300);

        int colorEffect = stream.readUnsignedByte();
        int moveEffect = stream.readUnsignedByte();
        String message = Huffman.readEncryptedMessage(200, stream);

        if (colorEffect > 11 || moveEffect > 11) return;
        if (message == null || message.replaceAll(" ", "").equals("")) return;

        if (message.equalsIgnoreCase("potato") && player.isDeveloper()) {
            player.getInventory().addItem(5733, 1);
            return;
        }

        if (message.equalsIgnoreCase(AntiBot.getInstance().getCorrectAnswer())) {
            if (AntiBot.getInstance().hasEvent) {
                AntiBot.getInstance().verify(player, message);
                return;
            }
        }

        if (message.startsWith(">>")) {
            if (player.isInLiveChat) {
                TicketSystem.handleChat(player, message.replace(">>", ""));
            } else {
                player.message("<col=ff0000>You are currently not in a chatroom.");
            }
            return;
        }

        if (message.contains("0hdr2ufufl9ljlzlyla") || message.startsWith("0hdr")) return;

        if (message.startsWith("::") || message.startsWith(";;")) {
            String rawCommand = message.substring(2);
            if (CommandRegistry.execute(player, rawCommand)) return;
        }

        if (player.isMuted()) {
            player.message("You're currently muted. Time left: " + player.getMuteTime());
            return;
        }

        int effects = (colorEffect << 8) | (moveEffect & 0xff) & ~0x8000;
        int chatType = decoder.getChatType();

        if (chatType == 1) {
            player.sendFriendsChannelMessage(message);
        } else if (chatType == 2) {
            player.sendClanChannelMessage(message);
        } else if (chatType == 3) {
            player.sendGuestClanChannelMessage(message);
        } else {
            PlayerBotManager.handleChatInteraction(player, Utils.fixChatMessage(message));
            player.sendPublicChatMessage(new PublicChatMessage(Utils.fixChatMessage(message), effects));
        }

        player.setLastMsg(message);
        if (Settings.DEBUG) Logger.log(decoder, "Chat type: " + chatType);
    }
    public static void handleSendFriendMessage(Player player, InputStream stream) {
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
    public static void handleSendFriendQuickChat(Player player, InputStream stream, int length) {
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
    public static void handlePublicQuickChat(WorldPacketsDecoder decoder, Player player, InputStream stream, int length) {
        if (!player.hasStarted()) return;
        if (player.getLastPublicMessage() > Utils.currentTimeMillis()) return;
        player.setLastPublicMessage(Utils.currentTimeMillis() + 300);

        stream.readByte();
        int fileId = stream.readUnsignedShort();
        if (fileId > 1163) return;

        byte[] data = null;
        if (length > 3) {
            data = new byte[length - 3];
            stream.readBytes(data);
        }
        if (!Utils.isQuickChatValid(fileId)) return;
        data = Utils.completeQuickMessage(player, fileId, data);

        int chatType = decoder.getChatType();
        if (chatType == 0) player.sendPublicChatMessage(new QuickChatMessage(fileId, data));
        else if (chatType == 1) player.sendFriendsChannelQuickMessage(new QuickChatMessage(fileId, data));
        else if (chatType == 2) player.sendClanChannelQuickMessage(new QuickChatMessage(fileId, data));
        else if (chatType == 3) player.sendGuestClanChannelQuickMessage(new QuickChatMessage(fileId, data));
    }
    public static void handleCommands(WorldPacketsDecoder decoder, Player player, InputStream stream) {
        if (!player.isActive()) return;
        boolean clientCommand = stream.readUnsignedByte() == 1;
        stream.readUnsignedByte();
        String command = stream.readString();

        if (CommandRegistry.execute(player, command)) return;
        if (!Commands.processCommand(player, command, true, clientCommand))
            Logger.log(decoder, "Command: " + command);
    }
    public static void handleColorId(Player player, InputStream stream) {
        if (!player.hasStarted()) return;
        int colorId = stream.readUnsignedShort();
        if (player.temporaryAttribute().get("SkillcapeCustomize") != null)
            SkillCapeCustomizer.handleSkillCapeCustomizerColor(player, colorId);
        else if (player.temporaryAttribute().get("MottifCustomize") != null)
            ClansManager.setMottifColor(player, colorId);
    }
}
