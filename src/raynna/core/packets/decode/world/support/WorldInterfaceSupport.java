package raynna.core.packets.decode.world.support;

import raynna.core.packets.decode.WorldPacketsDecoder;

import raynna.core.packets.InputStream;
import raynna.game.player.Inventory;
import raynna.game.player.Player;
import raynna.util.Utils;

public final class WorldInterfaceSupport {

    private WorldInterfaceSupport() {
    }
    public static void handleSwitchInterfaceItem(Player player, InputStream stream) {
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

        if (fromInterfaceId == Inventory.INVENTORY_INTERFACE && fromComponentId == 0
                && toInterfaceId == Inventory.INVENTORY_INTERFACE && toComponentId == 0) {
            toSlot -= 28;
            if (toSlot < 0 || toSlot >= player.getInventory().getItemsContainerSize()
                    || fromSlot >= player.getInventory().getItemsContainerSize())
                return;
            player.getInventory().switchItem(fromSlot, toSlot);
            return;
        }

        if (fromInterfaceId == 763 && fromComponentId == 0 && toInterfaceId == 763 && toComponentId == 0) {
            if (toSlot >= player.getInventory().getItemsContainerSize()
                    || fromSlot >= player.getInventory().getItemsContainerSize())
                return;
            player.getInventory().switchItem(fromSlot, toSlot);
            return;
        }

        if (fromInterfaceId == 762 && toInterfaceId == 762) {
            player.getBank().switchItem(fromSlot, toSlot, fromComponentId, toComponentId);
            return;
        }

        if (fromInterfaceId == 34 && toInterfaceId == 34) {
            player.getNotes().switchNotes(fromSlot, toSlot);
        }
    }
}
