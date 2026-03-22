package raynna.core.packets.decode.world.support;

import raynna.core.packets.decode.world.dispatch.WorldPacketDispatcher;
import raynna.core.packets.decode.world.dispatch.WorldQueuedPacketDispatcher;

import java.util.BitSet;

public final class WorldPacketType {
    public static final int UNDEFINED_SIZE = Byte.MIN_VALUE;
    public static final int EQUIPMENT_REMOVE = 216;
    public static final int WALKING = 8;
    public static final int MINI_WALKING = 58;
    public static final int AFK = 16;
    public static final int ACTION_BUTTON1 = 14;
    public static final int ACTION_BUTTON2 = 67;
    public static final int ACTION_BUTTON3 = 5;
    public static final int ACTION_BUTTON4 = 55;
    public static final int ACTION_BUTTON5 = 68;
    public static final int ACTION_BUTTON6 = 90;
    public static final int ACTION_BUTTON7 = 6;
    public static final int ACTION_BUTTON8 = 32;
    public static final int ACTION_BUTTON9 = 27;
    public static final int WORLD_MAP_CLICK = 38;
    public static final int ACTION_BUTTON10 = 96;
    public static final int RECEIVE_PACKET_COUNT = 33;
    public static final int EQUIPMENT_EXAMINE = 3;
    public static final int MOVE_CAMERA = 103;
    public static final int INTERFACE_ON_OBJECT = 37;
    public static final int KEY_TYPED = 1;
    public static final int CLOSE_INTERFACE = 54;
    public static final int COMMANDS = 60;
    public static final int ITEM_ON_ITEM = 3;
    public static final int DONE_LOADING_REGION = 30;
    public static final int PING = 21;
    public static final int SCREEN = 98;
    public static final int CHAT_TYPE = 83;
    public static final int CHAT = 53;
    public static final int PUBLIC_QUICK_CHAT = 86;
    public static final int ADD_FRIEND = 89;
    public static final int ADD_IGNORE = 4;
    public static final int REMOVE_IGNORE = 73;
    public static final int JOIN_FRIEND_CHAT = 36;
    public static final int CHANGE_FRIEND_CHAT = 22;
    public static final int KICK_FRIEND_CHAT = 74;
    public static final int KICK_CLAN_CHAT = 92;
    public static final int REMOVE_FRIEND = 24;
    public static final int SEND_FRIEND_MESSAGE = 82;
    public static final int SEND_FRIEND_QUICK_CHAT = 0;
    public static final int OBJECT_CLICK1 = 26;
    public static final int OBJECT_CLICK2 = 59;
    public static final int OBJECT_CLICK3 = 40;
    public static final int OBJECT_CLICK4 = 23;
    public static final int OBJECT_CLICK5 = 80;
    public static final int OBJECT_EXAMINE = 25;
    public static final int NPC_CLICK1 = 31;
    public static final int NPC_CLICK2 = 101;
    public static final int NPC_CLICK3 = 34;
    public static final int NPC_CLICK4 = 65;
    public static final int NPC_EXAMINE = 9;
    public static final int ATTACK_NPC = 20;
    public static final int PLAYER_OPTION_1 = 42;
    public static final int PLAYER_OPTION_2 = 46;
    public static final int PLAYER_OPTION_3 = 88;
    public static final int PLAYER_OPTION_4 = 17;
    public static final int PLAYER_OPTION_5 = 77;
    public static final int PLAYER_OPTION_6 = 49;
    public static final int PLAYER_OPTION_7 = 51;
    public static final int PLAYER_OPTION_8 = 85;
    public static final int PLAYER_OPTION_9 = 56;
    public static final int PLAYER_OPTION_10 = 7;
    public static final int INCOMING_ASSIST = 51;
    public static final int ITEM_TAKE = 57;
    public static final int EXAMINE_FLOORITEM = 102;
    public static final int DIALOGUE_CONTINUE = 72;
    public static final int ENTER_INTEGER = 81;
    public static final int ENTER_NAME = 29;
    public static final int ENTER_LONG_TEXT = 48;
    public static final int SWITCH_INTERFACE_ITEM = 76;
    public static final int INTERFACE_ON_PLAYER = 50;
    public static final int INTERFACE_ON_NPC = 66;
    public static final int COLOR_ID = 97;
    public static final int REPORT_ABUSE = 11;
    public static final int GRAND_EXCHANGE_ITEM_SELECT = 71;
    public static final int INTERFACE_ON_FLOORITEM = 69;
    public static final int WORLD_LIST_UPDATE = 87;
    public static final int DEVELOPER = 162;
    public static final int NPC_CLICK2_ALT = 222;

    private static final byte[] PACKET_SIZES = new byte[256];
    private static final boolean[] QUEUED_PACKETS = new boolean[256];
    private static final boolean[] IGNORED_PACKETS = new boolean[256];
    private static final String[] PACKET_NAMES = new String[256];

    static {
        initializePacketSizes();
        loadPacketSizes();
        registerNames();
        registerQueuedPackets();
        registerIgnoredPackets();
    }

    private WorldPacketType() {
    }
    public static int sizeFor(int packetId) {
        return isValidId(packetId) ? PACKET_SIZES[packetId] : Integer.MIN_VALUE;
    }
    public static boolean hasDefinedSize(int packetId) {
        return isValidId(packetId) && PACKET_SIZES[packetId] != UNDEFINED_SIZE;
    }
    public static boolean isQueued(int packetId) {
        return isValidId(packetId) && QUEUED_PACKETS[packetId];
    }
    public static boolean isIgnored(int packetId) {
        return isValidId(packetId) && IGNORED_PACKETS[packetId];
    }
    public static boolean isValidId(int packetId) {
        return packetId >= 0 && packetId < PACKET_SIZES.length;
    }
    public static String nameOf(int packetId) {
        if (!isValidId(packetId)) {
            return "INVALID";
        }
        String name = PACKET_NAMES[packetId];
        return name != null ? name : "UNKNOWN";
    }
    public static void validate() {
        BitSet immediate = WorldPacketDispatcher.registeredPacketIds();
        BitSet queued = WorldQueuedPacketDispatcher.registeredPacketIds();
        BitSet overlap = (BitSet) immediate.clone();
        overlap.and(queued);
        if (!overlap.isEmpty()) {
            int packetId = overlap.nextSetBit(0);
            throw new IllegalStateException("Packet " + nameOf(packetId) + " (" + packetId + ") is registered as both immediate and queued");
        }
        for (int packetId = immediate.nextSetBit(0); packetId >= 0; packetId = immediate.nextSetBit(packetId + 1)) {
            validateRegisteredPacket(packetId, false);
        }
        for (int packetId = queued.nextSetBit(0); packetId >= 0; packetId = queued.nextSetBit(packetId + 1)) {
            validateRegisteredPacket(packetId, true);
        }
    }

    private static void validateRegisteredPacket(int packetId, boolean queued) {
        if (!isValidId(packetId)) {
            throw new IllegalStateException("Packet handler registered invalid id " + packetId);
        }
        if (!hasDefinedSize(packetId)) {
            throw new IllegalStateException("Packet " + nameOf(packetId) + " (" + packetId + ") has no defined size");
        }
        int size = sizeFor(packetId);
        if (size == 0
                && packetId != PING
                && packetId != DONE_LOADING_REGION
                && packetId != CLOSE_INTERFACE
                && packetId != DEVELOPER) {
            String name = nameOf(packetId);
            throw new IllegalStateException("Packet " + name + " (" + packetId + ") has suspicious fixed size 0");
        }
        if (queued != isQueued(packetId)) {
            String name = nameOf(packetId);
            throw new IllegalStateException("Packet " + name + " (" + packetId + ") queued registration mismatch");
        }
    }

    private static void initializePacketSizes() {
        for (int i = 0; i < PACKET_SIZES.length; i++) {
            PACKET_SIZES[i] = (byte) UNDEFINED_SIZE;
        }
    }

    private static void registerNames() {
        name(SEND_FRIEND_QUICK_CHAT, "SEND_FRIEND_QUICK_CHAT");
        name(KEY_TYPED, "KEY_TYPED");
        name(EQUIPMENT_EXAMINE, "EQUIPMENT_EXAMINE");
        name(ADD_IGNORE, "ADD_IGNORE");
        name(ACTION_BUTTON3, "ACTION_BUTTON3");
        name(ACTION_BUTTON7, "ACTION_BUTTON7");
        name(PLAYER_OPTION_10, "PLAYER_OPTION_10");
        name(WALKING, "WALKING");
        name(NPC_EXAMINE, "NPC_EXAMINE");
        name(REPORT_ABUSE, "REPORT_ABUSE");
        name(ACTION_BUTTON1, "ACTION_BUTTON1");
        name(AFK, "AFK");
        name(PLAYER_OPTION_4, "PLAYER_OPTION_4");
        name(ATTACK_NPC, "ATTACK_NPC");
        name(PING, "PING");
        name(CHANGE_FRIEND_CHAT, "CHANGE_FRIEND_CHAT");
        name(OBJECT_CLICK4, "OBJECT_CLICK4");
        name(REMOVE_FRIEND, "REMOVE_FRIEND");
        name(OBJECT_EXAMINE, "OBJECT_EXAMINE");
        name(OBJECT_CLICK1, "OBJECT_CLICK1");
        name(ACTION_BUTTON9, "ACTION_BUTTON9");
        name(ENTER_NAME, "ENTER_NAME");
        name(DONE_LOADING_REGION, "DONE_LOADING_REGION");
        name(NPC_CLICK1, "NPC_CLICK1");
        name(RECEIVE_PACKET_COUNT, "RECEIVE_PACKET_COUNT");
        name(NPC_CLICK3, "NPC_CLICK3");
        name(JOIN_FRIEND_CHAT, "JOIN_FRIEND_CHAT");
        name(INTERFACE_ON_OBJECT, "INTERFACE_ON_OBJECT");
        name(WORLD_MAP_CLICK, "WORLD_MAP_CLICK");
        name(OBJECT_CLICK3, "OBJECT_CLICK3");
        name(PLAYER_OPTION_1, "PLAYER_OPTION_1");
        name(PLAYER_OPTION_2, "PLAYER_OPTION_2");
        name(ENTER_LONG_TEXT, "ENTER_LONG_TEXT");
        name(PLAYER_OPTION_6, "PLAYER_OPTION_6");
        name(INTERFACE_ON_PLAYER, "INTERFACE_ON_PLAYER");
        name(CHAT, "CHAT");
        name(CLOSE_INTERFACE, "CLOSE_INTERFACE");
        name(ACTION_BUTTON4, "ACTION_BUTTON4");
        name(PLAYER_OPTION_9, "PLAYER_OPTION_9");
        name(ITEM_TAKE, "ITEM_TAKE");
        name(MINI_WALKING, "MINI_WALKING");
        name(OBJECT_CLICK2, "OBJECT_CLICK2");
        name(COMMANDS, "COMMANDS");
        name(NPC_CLICK4, "NPC_CLICK4");
        name(INTERFACE_ON_NPC, "INTERFACE_ON_NPC");
        name(NPC_CLICK2, "NPC_CLICK2");
        name(ACTION_BUTTON2, "ACTION_BUTTON2");
        name(INTERFACE_ON_FLOORITEM, "INTERFACE_ON_FLOORITEM");
        name(DIALOGUE_CONTINUE, "DIALOGUE_CONTINUE");
        name(REMOVE_IGNORE, "REMOVE_IGNORE");
        name(KICK_FRIEND_CHAT, "KICK_FRIEND_CHAT");
        name(SWITCH_INTERFACE_ITEM, "SWITCH_INTERFACE_ITEM");
        name(PLAYER_OPTION_5, "PLAYER_OPTION_5");
        name(ADD_FRIEND, "ADD_FRIEND");
        name(ACTION_BUTTON5, "ACTION_BUTTON5");
        name(ACTION_BUTTON6, "ACTION_BUTTON6");
        name(GRAND_EXCHANGE_ITEM_SELECT, "GRAND_EXCHANGE_ITEM_SELECT");
        name(KICK_CLAN_CHAT, "KICK_CLAN_CHAT");
        name(SEND_FRIEND_MESSAGE, "SEND_FRIEND_MESSAGE");
        name(CHAT_TYPE, "CHAT_TYPE");
        name(OBJECT_CLICK5, "OBJECT_CLICK5");
        name(ENTER_INTEGER, "ENTER_INTEGER");
        name(PLAYER_OPTION_8, "PLAYER_OPTION_8");
        name(PUBLIC_QUICK_CHAT, "PUBLIC_QUICK_CHAT");
        name(WORLD_LIST_UPDATE, "WORLD_LIST_UPDATE");
        name(PLAYER_OPTION_3, "PLAYER_OPTION_3");
        name(ADD_FRIEND, "ADD_FRIEND");
        name(ACTION_BUTTON10, "ACTION_BUTTON10");
        name(COLOR_ID, "COLOR_ID");
        name(SCREEN, "SCREEN");
        name(NPC_CLICK2, "NPC_CLICK2");
        name(KICK_CLAN_CHAT, "KICK_CLAN_CHAT");
        name(MOVE_CAMERA, "MOVE_CAMERA");
        name(EXAMINE_FLOORITEM, "EXAMINE_FLOORITEM");
        name(DEVELOPER, "DEVELOPER");
        name(NPC_CLICK2_ALT, "NPC_CLICK2_ALT");
        name(EQUIPMENT_REMOVE, "EQUIPMENT_REMOVE");
        name(INCOMING_ASSIST, "INCOMING_ASSIST/PLAYER_OPTION_7");
        name(ITEM_ON_ITEM, "ITEM_ON_ITEM/EQUIPMENT_EXAMINE");
    }

    private static void registerIgnoredPackets() {
        ignore(2, "FRIENDS_PACKET");
        ignore(7, "UNKNOWN_7");
        ignore(8, "UNKNOWN_8");
        ignore(10, "SET_MOUSE_PACKET");
        ignore(12, "UNKNOWN_12");
        ignore(13, "UNKNOWN_13");
        ignore(15, "UNKNOWN_15");
        ignore(35, "UNKNOWN_35");
        ignore(39, "GAME_PANE_PACKET");
        ignore(84, "UNKNOWN_84");
        ignore(93, "UNKNOWN_93");
        ignore(101, "UNKNOWN_101");
        ignore(158, "UNKNOWN_158");
        ignore(161, "UNKNOWN_161");
        ignore(221, "UNKNOWN_221");
        ignore(223, "UNKNOWN_223");
        ignore(224, "UNKNOWN_224");
        ignore(217, "UNKNOWN_217");
    }

    private static void registerQueuedPackets() {
        registerQueued(
                WALKING,
                MINI_WALKING,
                ITEM_TAKE,
                EXAMINE_FLOORITEM,
                PLAYER_OPTION_2,
                PLAYER_OPTION_4,
                PLAYER_OPTION_6,
                PLAYER_OPTION_5,
                PLAYER_OPTION_9,
                PLAYER_OPTION_1,
                ATTACK_NPC,
                INTERFACE_ON_PLAYER,
                INTERFACE_ON_NPC,
                NPC_CLICK1,
                NPC_CLICK2,
                NPC_CLICK2_ALT,
                NPC_CLICK3,
                NPC_CLICK4,
                OBJECT_CLICK1,
                OBJECT_CLICK2,
                OBJECT_CLICK3,
                OBJECT_CLICK4,
                OBJECT_CLICK5,
                INTERFACE_ON_OBJECT,
                INTERFACE_ON_FLOORITEM
        );
    }

    private static void loadPacketSizes() {
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
        PACKET_SIZES[33] = 4;
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
        PACKET_SIZES[68] = 8;
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
        PACKET_SIZES[158] = 0;
        PACKET_SIZES[161] = 0;
        PACKET_SIZES[162] = 1;
        PACKET_SIZES[216] = 8;
        PACKET_SIZES[217] = 0;
        PACKET_SIZES[221] = 0;
        PACKET_SIZES[222] = 3;
        PACKET_SIZES[223] = 0;
        PACKET_SIZES[224] = 0;
    }

    private static void registerQueued(int... packetIds) {
        for (int packetId : packetIds) {
            if (isValidId(packetId)) {
                QUEUED_PACKETS[packetId] = true;
            }
        }
    }

    private static void name(int packetId, String name) {
        if (isValidId(packetId) && PACKET_NAMES[packetId] == null) {
            PACKET_NAMES[packetId] = name;
        }
    }

    private static void ignore(int packetId, String name) {
        if (isValidId(packetId)) {
            IGNORED_PACKETS[packetId] = true;
            if (PACKET_NAMES[packetId] == null) {
                PACKET_NAMES[packetId] = name;
            }
        }
    }
}
