package raynna.core.packets.decode.world.handler;

import raynna.core.packets.decode.WorldPacketsDecoder;
import raynna.core.packets.decode.world.support.*;

import raynna.core.packets.InputStream;
import raynna.game.player.Player;
import raynna.game.player.content.friendschat.FriendChatsManager;
import raynna.util.Utils;

public final class WorldSocialPacketHandler implements WorldPacketHandler {

    @Override
    public boolean handle(WorldPacketsDecoder decoder, int packetId, InputStream stream, int length) {
        Player player = decoder.getPlayer();
        switch (packetId) {
            case WorldPacketsDecoder.JOIN_FRIEND_CHAT_PACKET -> {
                if (player.hasStarted()) {
                    FriendChatsManager.joinChat(stream.readString(), player, false);
                }
                return true;
            }
            case WorldPacketsDecoder.KICK_FRIEND_CHAT_PACKET -> {
                if (player.hasStarted()) {
                    player.setLastPublicMessage(Utils.currentTimeMillis() + 1000);
                    player.kickPlayerFromFriendsChannel(stream.readString());
                }
                return true;
            }
            case WorldPacketsDecoder.KICK_CLAN_CHAT_PACKET -> {
                if (!player.hasStarted()) {
                    return true;
                }
                player.setLastPublicMessage(Utils.currentTimeMillis() + 1000);
                boolean guest = stream.readByte() == 1;
                if (!guest) {
                    return true;
                }
                stream.readUnsignedShort();
                player.kickPlayerFromClanChannel(stream.readString());
                return true;
            }
            case WorldPacketsDecoder.CHANGE_FRIEND_CHAT_PACKET -> {
                if (player.hasStarted() && player.getInterfaceManager().containsInterface(1108)) {
                    player.getFriendsIgnores().changeRank(stream.readString(), stream.readUnsignedByte128());
                }
                return true;
            }
            case WorldPacketsDecoder.ADD_FRIEND_PACKET -> {
                if (player.hasStarted()) {
                    player.getFriendsIgnores().addFriend(stream.readString());
                }
                return true;
            }
            case WorldPacketsDecoder.REMOVE_FRIEND_PACKET -> {
                if (player.hasStarted()) {
                    player.getFriendsIgnores().removeFriend(stream.readString());
                }
                return true;
            }
            case WorldPacketsDecoder.ADD_IGNORE_PACKET -> {
                if (player.hasStarted()) {
                    player.getFriendsIgnores().addIgnore(stream.readString(), stream.readUnsignedByte() == 1);
                }
                return true;
            }
            case WorldPacketsDecoder.REMOVE_IGNORE_PACKET -> {
                if (player.hasStarted()) {
                    player.getFriendsIgnores().removeIgnore(stream.readString());
                }
                return true;
            }
            case WorldPacketsDecoder.SEND_FRIEND_MESSAGE_PACKET -> {
                WorldChatSupport.handleSendFriendMessage(player, stream);
                return true;
            }
            case WorldPacketsDecoder.SEND_FRIEND_QUICK_CHAT_PACKET -> {
                WorldChatSupport.handleSendFriendQuickChat(player, stream, length);
                return true;
            }
            case WorldPacketsDecoder.PUBLIC_QUICK_CHAT_PACKET -> {
                WorldChatSupport.handlePublicQuickChat(decoder, player, stream, length);
                return true;
            }
            case WorldPacketsDecoder.CHAT_TYPE_PACKET -> {
                decoder.setChatType(stream.readUnsignedByte());
                return true;
            }
            case WorldPacketsDecoder.CHAT_PACKET -> {
                WorldChatSupport.handlePublicChat(decoder, player, stream);
                return true;
            }
            case WorldPacketsDecoder.COMMANDS_PACKET -> {
                WorldChatSupport.handleCommands(decoder, player, stream);
                return true;
            }
            case WorldPacketsDecoder.COLOR_ID_PACKET -> {
                WorldChatSupport.handleColorId(player, stream);
                return true;
            }
            default -> {
                return false;
            }
        }
    }
}
