package raynna.core.packets.decode.world.handler;

import raynna.core.packets.decode.WorldPacketsDecoder;
import raynna.core.packets.decode.world.support.*;

import raynna.core.packets.InputStream;
import raynna.core.packets.OutputStream;
import raynna.core.packets.handlers.InventoryOptionsHandler;
import raynna.core.packets.handlers.NPCHandler;
import raynna.core.packets.handlers.ObjectHandler;
import raynna.game.player.Player;
import raynna.game.player.content.ReportAbuse;
import raynna.game.player.grandexchange.GrandExchange;

public final class WorldSystemPacketHandler implements WorldPacketHandler {

    @Override
    public boolean handle(WorldPacketsDecoder decoder, int packetId, InputStream stream, int length) {
        Player player = decoder.getPlayer();
        switch (packetId) {
            case WorldPacketsDecoder.PING_PACKET -> {
                OutputStream packet = new OutputStream(0);
                packet.writePacket(player, 153);
                player.getSession().write(packet);
                return true;
            }
            case WorldPacketsDecoder.DEVELOPER_PACKET -> {
                System.out.println("Developer Packet: " + packetId);
                System.out.println("Value" + stream.readByte());
                return true;
            }
            case WorldPacketsDecoder.RECEIVE_PACKET_COUNT_PACKET -> {
                stream.readInt();
                return true;
            }
            case WorldPacketsDecoder.WORLD_LIST_UPDATE -> {
                int updateType = stream.readInt();
                player.getPackets().sendWorldList(updateType == 0);
                return true;
            }
            case WorldPacketsDecoder.ITEM_ON_ITEM_PACKET -> {
                InventoryOptionsHandler.handleItemOnItem(player, stream);
                return true;
            }
            case WorldPacketsDecoder.AFK_PACKET -> {
                return true;
            }
            case WorldPacketsDecoder.CLOSE_INTERFACE_PACKET -> {
                if (player.hasStarted() && !player.hasFinished() && !player.isActive()) {
                    player.run();
                } else {
                    player.closeInterfaces();
                }
                return true;
            }
            case WorldPacketsDecoder.MOVE_CAMERA_PACKET -> {
                stream.readUnsignedShort();
                stream.readUnsignedShort();
                return true;
            }
            case WorldPacketsDecoder.SCREEN_PACKET -> {
                int displayMode = stream.readUnsignedByte();
                player.setScreenWidth(stream.readUnsignedShort());
                player.setScreenHeight(stream.readUnsignedShort());
                stream.readUnsignedByte();
                if (!player.hasStarted() || player.hasFinished() || displayMode == player.getDisplayMode()
                        || !player.getInterfaceManager().containsInterface(742)) {
                    return true;
                }
                player.setDisplayMode(displayMode);
                player.getInterfaceManager().removeAll();
                player.getInterfaceManager().sendInterfaces();
                player.getInterfaceManager().sendInterface(742);
                return true;
            }
            case WorldPacketsDecoder.INCOMMING_ASSIST -> {
                WorldDialogueSupport.handleIncomingAssist(player, stream);
                return true;
            }
            case WorldPacketsDecoder.DIALOGUE_CONTINUE_PACKET -> {
                WorldDialogueSupport.handleDialogueContinue(player, stream);
                return true;
            }
            case WorldPacketsDecoder.WORLD_MAP_CLICK -> {
                WorldDialogueSupport.handleWorldMapClick(player, stream);
                return true;
            }
            case WorldPacketsDecoder.SWITCH_INTERFACE_ITEM_PACKET -> {
                WorldInterfaceSupport.handleSwitchInterfaceItem(player, stream);
                return true;
            }
            case WorldPacketsDecoder.DONE_LOADING_REGION_PACKET -> {
                if (!player.clientHasLoadedMapRegion()) {
                    player.setClientHasLoadedMapRegion();
                }
                player.refreshSpawnedObjects();
                player.refreshSpawnedItems();
                return true;
            }
            case WorldPacketsDecoder.OBJECT_EXAMINE_PACKET -> {
                ObjectHandler.handleOption(player, stream, -1);
                return true;
            }
            case WorldPacketsDecoder.KEY_TYPED_PACKET -> {
                int key = stream.readByte();
                if (key == 13) {
                    player.closeInterfaces();
                }
                return true;
            }
            case WorldPacketsDecoder.NPC_EXAMINE_PACKET -> {
                NPCHandler.handleExamine(player, stream);
                return true;
            }
            case WorldPacketsDecoder.REPORT_ABUSE_PACKET -> {
                if (player.hasStarted()) {
                    ReportAbuse.Report(player, stream.readString(), stream.readUnsignedByte(),
                            stream.readUnsignedByte() == 1);
                }
                return true;
            }
            case WorldPacketsDecoder.GRAND_EXCHANGE_ITEM_SELECT_PACKET -> {
                int itemId = stream.readUnsignedShort();
                if (player.getInterfaceManager().containsInterface(105)) {
                    player.getGeManager().chooseItem(itemId);
                } else {
                    GrandExchange.INSTANCE.priceCheckItem(player, itemId);
                }
                return true;
            }
            default -> {
                return false;
            }
        }
    }
}
