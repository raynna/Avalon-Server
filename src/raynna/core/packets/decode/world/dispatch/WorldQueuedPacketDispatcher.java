package raynna.core.packets.decode.world.dispatch;

import raynna.core.packets.decode.WorldPacketsDecoder;
import raynna.core.packets.decode.world.handler.*;
import raynna.core.packets.decode.world.support.*;

import raynna.core.packets.InputStream;
import raynna.core.packets.handlers.NPCHandler;
import raynna.core.packets.handlers.ObjectHandler;
import raynna.game.item.FloorItem;
import raynna.game.player.Player;

import java.util.BitSet;

public final class WorldQueuedPacketDispatcher {

    private static final WorldQueuedPacketHandler[] ROUTES = new WorldQueuedPacketHandler[256];

    static {
        register((player, stream) -> {
                    player.queue().clearWeak();
                    WorldInteractionSupport.handleWalking(player, stream);
                },
                WorldPacketsDecoder.WALKING_PACKET,
                WorldPacketsDecoder.MINI_WALKING_PACKET);
        register(WorldInteractionSupport::handleInterfaceOnObject, WorldPacketsDecoder.INTERFACE_ON_OBJECT);
        register(WorldInteractionSupport::handlePlayerOption2, WorldPacketsDecoder.PLAYER_OPTION_2_PACKET);
        register(WorldInteractionSupport::handlePlayerOption5, WorldPacketsDecoder.PLAYER_OPTION_5_PACKET);
        register(WorldInteractionSupport::handlePlayerOption6, WorldPacketsDecoder.PLAYER_OPTION_6_PACKET);
        register(WorldInteractionSupport::handlePlayerOption4, WorldPacketsDecoder.PLAYER_OPTION_4_PACKET);
        register(WorldInteractionSupport::handlePlayerOption1Attack, WorldPacketsDecoder.PLAYER_OPTION_1_PACKET);
        register(WorldInteractionSupport::handlePlayerOption9, WorldPacketsDecoder.PLAYER_OPTION_9_PACKET);
        register(WorldInteractionSupport::handleAttackNpc, WorldPacketsDecoder.ATTACK_NPC);
        register(WorldInteractionSupport::handleSpellOnFloorItem, WorldPacketsDecoder.INTERFACE_ON_FLOORITEM_PACKET);
        register(WorldInteractionSupport::handleInterfaceOnPlayer, WorldPacketsDecoder.INTERFACE_ON_PLAYER);
        register(WorldInteractionSupport::handleInterfaceOnNpc, WorldPacketsDecoder.INTERFACE_ON_NPC);
        register(NPCHandler::handleOption1, WorldPacketsDecoder.NPC_CLICK1_PACKET);
        register(NPCHandler::handleOption2, WorldPacketsDecoder.NPC_CLICK2_PACKET);
        register(NPCHandler::handleOption222, WorldPacketType.NPC_CLICK2_ALT);
        register(NPCHandler::handleOption3, WorldPacketsDecoder.NPC_CLICK3_PACKET);
        register(NPCHandler::handleOption4, WorldPacketsDecoder.NPC_CLICK4_PACKET);
        register((player, stream) -> ObjectHandler.handleOption(player, stream, 1), WorldPacketsDecoder.OBJECT_CLICK1_PACKET);
        register((player, stream) -> ObjectHandler.handleOption(player, stream, 2), WorldPacketsDecoder.OBJECT_CLICK2_PACKET);
        register((player, stream) -> ObjectHandler.handleOption(player, stream, 3), WorldPacketsDecoder.OBJECT_CLICK3_PACKET);
        register((player, stream) -> ObjectHandler.handleOption(player, stream, 4), WorldPacketsDecoder.OBJECT_CLICK4_PACKET);
        register((player, stream) -> ObjectHandler.handleOption(player, stream, 5), WorldPacketsDecoder.OBJECT_CLICK5_PACKET);
        register(WorldInteractionSupport::handleItemTake, WorldPacketsDecoder.ITEM_TAKE_PACKET);
        register(FloorItem::handleExamine, WorldPacketsDecoder.EXAMINE_FLOORITEM_PACKET);
    }

    private WorldQueuedPacketDispatcher() {
    }

    public static void handle(Player player, int packetId, InputStream stream) {
        if (packetId < 0 || packetId >= ROUTES.length) {
            return;
        }
        WorldQueuedPacketHandler handler = ROUTES[packetId];
        if (handler == null) {
            return;
        }
        handler.handle(player, stream);
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

    private static void register(WorldQueuedPacketHandler handler, int... packetIds) {
        for (int packetId : packetIds) {
            if (packetId >= 0 && packetId < ROUTES.length && ROUTES[packetId] == null) {
                ROUTES[packetId] = handler;
            }
        }
    }
}
