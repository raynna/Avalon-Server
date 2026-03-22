package raynna.core.packets.decode.world.dispatch;

import raynna.core.packets.decode.WorldPacketsDecoder;
import raynna.core.packets.decode.world.handler.*;
import raynna.core.packets.decode.world.support.*;

import raynna.core.packets.InputStream;

import java.util.BitSet;

public final class WorldPacketDispatcher {

    private static final WorldPacketHandler[] ROUTES = new WorldPacketHandler[256];

    static {
        register(new WorldSystemPacketHandler(),
                WorldPacketsDecoder.PING_PACKET,
                WorldPacketsDecoder.DEVELOPER_PACKET,
                WorldPacketsDecoder.RECEIVE_PACKET_COUNT_PACKET,
                WorldPacketsDecoder.WORLD_LIST_UPDATE,
                WorldPacketsDecoder.ITEM_ON_ITEM_PACKET,
                WorldPacketsDecoder.AFK_PACKET,
                WorldPacketsDecoder.CLOSE_INTERFACE_PACKET,
                WorldPacketsDecoder.MOVE_CAMERA_PACKET,
                WorldPacketsDecoder.SCREEN_PACKET,
                WorldPacketsDecoder.INCOMMING_ASSIST,
                WorldPacketsDecoder.DIALOGUE_CONTINUE_PACKET,
                WorldPacketsDecoder.WORLD_MAP_CLICK,
                WorldPacketsDecoder.SWITCH_INTERFACE_ITEM_PACKET,
                WorldPacketsDecoder.DONE_LOADING_REGION_PACKET,
                WorldPacketsDecoder.OBJECT_EXAMINE_PACKET,
                WorldPacketsDecoder.KEY_TYPED_PACKET,
                WorldPacketsDecoder.NPC_EXAMINE_PACKET,
                WorldPacketsDecoder.REPORT_ABUSE_PACKET,
                WorldPacketsDecoder.GRAND_EXCHANGE_ITEM_SELECT_PACKET);
        register(new WorldInputPacketHandler(),
                WorldPacketsDecoder.PLAYER_OPTION_3_PACKET,
                WorldPacketsDecoder.PLAYER_OPTION_7_PACKET,
                WorldPacketsDecoder.PLAYER_OPTION_8_PACKET,
                WorldPacketsDecoder.PLAYER_OPTION_10_PACKET,
                WorldPacketsDecoder.ENTER_NAME_PACKET,
                WorldPacketsDecoder.ENTER_LONG_TEXT_PACKET,
                WorldPacketsDecoder.ENTER_INTEGER_PACKET);
        register(new WorldInterfacePacketHandler(),
                WorldPacketsDecoder.ACTION_BUTTON1_PACKET,
                WorldPacketsDecoder.ACTION_BUTTON2_PACKET,
                WorldPacketsDecoder.ACTION_BUTTON3_PACKET,
                WorldPacketsDecoder.ACTION_BUTTON4_PACKET,
                WorldPacketsDecoder.ACTION_BUTTON5_PACKET,
                WorldPacketsDecoder.ACTION_BUTTON6_PACKET,
                WorldPacketsDecoder.ACTION_BUTTON7_PACKET,
                WorldPacketsDecoder.ACTION_BUTTON8_PACKET,
                WorldPacketsDecoder.ACTION_BUTTON9_PACKET,
                WorldPacketsDecoder.ACTION_BUTTON10_PACKET,
                WorldPacketsDecoder.EQUIPMENT_REMOVE_PACKET,
                WorldPacketsDecoder.ENTER_STRING_PACKET);
        register(new WorldSocialPacketHandler(),
                WorldPacketsDecoder.JOIN_FRIEND_CHAT_PACKET,
                WorldPacketsDecoder.KICK_FRIEND_CHAT_PACKET,
                WorldPacketsDecoder.KICK_CLAN_CHAT_PACKET,
                WorldPacketsDecoder.CHANGE_FRIEND_CHAT_PACKET,
                WorldPacketsDecoder.ADD_FRIEND_PACKET,
                WorldPacketsDecoder.REMOVE_FRIEND_PACKET,
                WorldPacketsDecoder.ADD_IGNORE_PACKET,
                WorldPacketsDecoder.REMOVE_IGNORE_PACKET,
                WorldPacketsDecoder.SEND_FRIEND_MESSAGE_PACKET,
                WorldPacketsDecoder.SEND_FRIEND_QUICK_CHAT_PACKET,
                WorldPacketsDecoder.PUBLIC_QUICK_CHAT_PACKET,
                WorldPacketsDecoder.CHAT_TYPE_PACKET,
                WorldPacketsDecoder.CHAT_PACKET,
                WorldPacketsDecoder.COMMANDS_PACKET,
                WorldPacketsDecoder.COLOR_ID_PACKET);
    }

    private WorldPacketDispatcher() {
    }

    public static boolean handle(WorldPacketsDecoder decoder, int packetId, InputStream stream, int length) {
        if (packetId < 0 || packetId >= ROUTES.length) {
            return false;
        }
        WorldPacketHandler handler = ROUTES[packetId];
        if (handler == null) {
            return false;
        }
        return handler.handle(decoder, packetId, stream, length);
    }

    public static BitSet registeredPacketIds() {
        BitSet registered = new BitSet(ROUTES.length);
        for (int packetId = 0; packetId < ROUTES.length; packetId++) {
            if (ROUTES[packetId] != null) {
                registered.set(packetId);
            }
        }
        return registered;
    }

    private static void register(WorldPacketHandler handler, int... packetIds) {
        for (int packetId : packetIds) {
            if (packetId >= 0 && packetId < ROUTES.length && ROUTES[packetId] == null) {
                ROUTES[packetId] = handler;
            }
        }
    }
}
