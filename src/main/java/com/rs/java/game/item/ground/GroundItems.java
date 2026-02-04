package com.rs.java.game.item.ground;

import com.rs.Settings;
import com.rs.core.tasks.WorldTask;
import com.rs.core.tasks.WorldTasksManager;
import com.rs.java.game.Region;
import com.rs.java.game.World;
import com.rs.java.game.WorldTile;
import com.rs.java.game.item.FloorItem;
import com.rs.java.game.item.Item;
import com.rs.java.game.minigames.clanwars.FfaZone;
import com.rs.java.game.player.Player;
import com.rs.java.game.player.content.ItemConstants;
import com.rs.java.utils.Utils;

/**
 * Consolidated GroundItems handler (merged from World + previous GroundItems).
 *
 * Drop types:
 * 0 = normal
 * 1 = personal
 * 2 = untradeables/hidden for others
 */
public final class GroundItems {

    private GroundItems() {}

    public static int secondsToTicks(int seconds) {
        return Math.max(1, (int) Math.ceil((seconds * 1000d) / Settings.WORLD_CYCLE_TIME));
    }

    public static FloorItem addGroundItem(Item item, WorldTile tile) {
        return addGroundItem(item, tile, null, false, -1, 2, -1);
    }

    public static FloorItem addGroundItem(Item item, WorldTile tile, int publicTime) {
        return addGroundItem(item, tile, null, false, -1, 2, publicTime);
    }

    public static FloorItem addGroundItem(Item item, WorldTile tile, Player owner,
                                          boolean invisible, int hiddenTime) {
        return addGroundItem(item, tile, owner, invisible, hiddenTime, 2, 120);
    }

    public static FloorItem addGroundItem(Item item, WorldTile tile, Player owner,
                                          boolean invisible, int hiddenTime, int type) {
        return addGroundItem(item, tile, owner, invisible, hiddenTime, type, 120);
    }

    public static FloorItem addGroundItem(Item item, WorldTile tile, Player owner,
                                          boolean invisible, int hiddenTime,
                                          int type, int publicTime) {
        return addGroundItemInternal(item, tile, owner, invisible, hiddenTime, type, publicTime);
    }

    public static void addGlobalGroundItem(Item item, WorldTile tile) {
        addGroundItem(item, tile, null, false, -1, 2, -1);
    }

    public static void addGlobalGroundItem(final Item item, final WorldTile tile, final int tick,
                                           final boolean spawned) {

        FloorItem floorItem = null;

        if (item.getDefinitions().isStackable() || item.getDefinitions().isNoted()) {
            floorItem = World.getRegion(tile.getRegionId()).getGroundItem(item.getId(), tile, null);
        }

        if (floorItem != null)
            return;

        floorItem = new FloorItem(item, tile, null, false, tick, spawned);
        final Region region = World.getRegion(tile.getRegionId());

        if (floorItem.isGlobalPicked())
            return;

        region.getGroundItemsSafe().add(floorItem);

        int regionId = tile.getRegionId();
        for (Player player : World.getPlayers()) {
            if (player == null || !player.hasStarted() || player.hasFinished())
                continue;
            if (player.getPlane() != tile.getPlane())
                continue;
            if (!player.getMapRegionsIds().contains(regionId))
                continue;
            if (player.getRegionId() != region.getRegionId())
                continue;

            player.getPackets().sendGroundItem(floorItem);
        }
    }

    public static void addGroundItemForever(Item item, WorldTile tile) {
        FloorItem floorItem = new FloorItem(item, tile, true);
        Region region = World.getRegion(tile.getRegionId());
        region.getGroundItemsSafe().add(floorItem);
        broadcastGroundItem(floorItem, false);
    }

    private static FloorItem addGroundItemInternal(Item item, WorldTile tile, Player owner,
                                                   boolean invisible, int hiddenTime,
                                                   int type, int publicTime) {

        FloorItem floorItem = new FloorItem(item, tile, owner, false, invisible);
        Region region = World.getRegion(tile.getRegionId());

        boolean shouldTrack = type != 2
                || ItemConstants.isTradeable(item)
                || ItemConstants.turnCoins(item);

        if (shouldTrack) {
            region.getGroundItemsSafe().add(floorItem);
        }

        // If invisible, only owner sees it (if it is a tracked item by rules)
        if (invisible && owner != null) {
            if (type != 2 || ItemConstants.isTradeable(item) || ItemConstants.turnCoins(item)) {
                owner.getPackets().sendGroundItem(floorItem);
            }
        }

        // PvP special-case from your World: type 0 in pking area becomes instantly public
        if (type == 0 && owner != null && owner.inPkingArea()) {
            hiddenTime = 0;
        }

        if (!invisible) {
            // Public broadcast
            broadcastGroundItem(floorItem, type != 2);
            if (publicTime != -1) {
                removeGroundItem(floorItem, publicTime);
            }
        } else if (hiddenTime != -1) {
            WorldTasksManager.schedule(new WorldTask() {
                @Override
                public void run() {
                    turnPublic(floorItem, publicTime);
                }
            }, secondsToTicks(hiddenTime));
        }

        return floorItem;
    }

    public static void turnPublic(FloorItem item, int publicTime) {
        if (!item.isInvisible() || item.isRemoved())
            return;

        Region region = World.getRegion(item.getTile().getRegionId());
        if (!region.getGroundItemsSafe().contains(item))
            return;

        Player owner = item.getOwner();

        // Handle attached items (split on public)
        int attachedId1 = ItemConstants.removeAttachedId(item);
        if (attachedId1 != -1) {

            int attachedId2 = ItemConstants.removeAttachedId2(item);
            WorldTile tile = new WorldTile(item.getTile().getX(), item.getTile().getY(), item.getTile().getPlane());

            // Hard remove original
            removeGroundItemInstant(item);

            // Spawn attached items as public (type 0, no timer)
            addGroundItem(new Item(attachedId1, 1), tile, null, false, -1, 0, -1);
            if (attachedId2 != -1) {
                addGroundItem(new Item(attachedId2, 1), tile, null, false, -1, 0, -1);
            }
            return;
        }

        item.setInvisible(false);

        // Remove player beam if applicable (kept parity with your World.turnPublic)
        if (owner != null && owner.getBeam() != null && owner.getBeamItem() != null) {
            if (item.getTile().matches(owner.getBeam()) && owner.getBeamItem().getId() == item.getId()) {
                owner.setBeam(null);
                owner.setBeamItem(null);
            }
        }

        // Send to other players (tradeable-only visibility)
        int regionId = item.getTile().getRegionId();
        for (Player player : World.getPlayers()) {
            if (player == null || (owner != null && owner.getUsername().equalsIgnoreCase(player.getUsername())) || !player.hasStarted() || player.hasFinished())
                continue;
            if (player.getPlane() != item.getTile().getPlane())
                continue;
            if (!player.getMapRegionsIds().contains(regionId))
                continue;
            if (!ItemConstants.isTradeable(item))
                continue;

            player.getPackets().sendGroundItem(item);
        }

        if (publicTime != -1) {
            removeGroundItem(item, publicTime);
        }
    }

    public static void updateGroundItem(Item item, WorldTile tile, Player owner) {
        updateGroundItem(item, tile, owner, 60, 0);
    }

    public static void updateGroundItem(Item item, WorldTile tile, Player owner, int hiddenTime) {
        updateGroundItem(item, tile, owner, hiddenTime, 0);
    }

    public static void updateGroundItem(Item item, WorldTile tile, Player owner, int hiddenTime, int type) {

        FloorItem floorItem = null;

        if (item.getDefinitions().isStackable() || item.getDefinitions().isNoted()) {
            floorItem = World.getRegion(tile.getRegionId()).getGroundItem(item.getId(), tile, owner);
        }

        // droptypes, 0 = normal, 1 = personal, 2 = untradeables/hidden for others
        if (floorItem == null) {
            spawnAsNewGroundItem(item, tile, owner, hiddenTime, type);
            return;
        }

        boolean stackable = floorItem.getDefinitions().isStackable() || floorItem.getDefinitions().isNoted();
        if (stackable) {
            int total = floorItem.getAmount() + item.getAmount();

            if (total < 0) {
                int amountCanAdd = Integer.MAX_VALUE - floorItem.getAmount();
                floorItem.setAmount(Integer.MAX_VALUE);
                item.setAmount(item.getAmount() - amountCanAdd);
                addGroundItem(item, tile, owner, owner != null, hiddenTime, type);
            } else {
                floorItem.setAmount(total);
            }

            for (Player p : World.getPlayers()) {
                if (p == null || !p.hasStarted() || p.hasFinished())
                    continue;
                if (p.getPlane() != tile.getPlane())
                    continue;
                if (!p.withinDistance(tile, 64))
                    continue;
                if (floorItem.isInvisible()) {
                    if (floorItem.getOwner() != null && !floorItem.getOwner().getUsername().equalsIgnoreCase(p.getUsername())) {
                        continue;
                    }
                }
                if (type == 1 && p != floorItem.getOwner())
                    continue;

                p.getPackets().sendRemoveGroundItem(floorItem);
                p.getPackets().sendGroundItem(floorItem);
            }
            return;
        }

        spawnAsNewGroundItem(item, tile, owner, hiddenTime, type);
    }

    private static void spawnAsNewGroundItem(Item item, WorldTile tile, Player owner, int hiddenTime, int type) {
        boolean stackable = item.getDefinitions().isStackable() || item.getDefinitions().isNoted();

        if (!stackable && item.getAmount() > 1) {
            for (int i = 0; i < item.getAmount(); i++) {
                Item single = item.clone();
                single.setAmount(1);
                addGroundItem(single, tile, owner, owner != null, hiddenTime, type);
            }
        } else {
            addGroundItem(item, tile, owner, owner != null, hiddenTime, type);
        }
    }

    private static void broadcastGroundItem(FloorItem item, boolean checkTradeable) {
        if (item.isRemoved())
            return;

        int regionId = item.getTile().getRegionId();
        for (Player player : World.getPlayers()) {
            if (player == null || !player.hasStarted() || player.hasFinished())
                continue;
            if (player.getPlane() != item.getTile().getPlane())
                continue;
            if (!player.getMapRegionsIds().contains(regionId))
                continue;
            if (checkTradeable && !ItemConstants.isTradeable(item))
                continue;

            player.getPackets().sendGroundItem(item);
        }
    }

    private static void broadcastRemoveGroundItem(FloorItem item) {
        int regionId = item.getTile().getRegionId();

        for (Player player : World.getPlayers()) {
            if (player == null || !player.hasStarted() || player.hasFinished())
                continue;
            if (player.getPlane() != item.getTile().getPlane())
                continue;
            if (!player.getMapRegionsIds().contains(regionId))
                continue;

            player.getPackets().sendRemoveGroundItem(item);
        }
    }

    public static boolean pickup(Player player, FloorItem item) {

        if (player == null || item == null || item.isRemoved())
            return false;

        Region region = World.getRegion(item.getTile().getRegionId());
        if (!region.getGroundItemsSafe().contains(item))
            return false;

        if (!canPickupItem(player, item))
            return false;

        // Special handling for coins -> money pouch (only outside pking/risk areas like your World)
        if (item.getId() == 995 && !player.inPkingArea() && !FfaZone.inRiskArea(player)) {
            return handleCoinPickup(player, item, region);
        }

        if (!hasInventorySpace(player, item)) {
            player.getPackets().sendGameMessage("Not enough inventory space.");
            return false;
        }

        if (item.getId() == 7957)
            item.setId(1005);

        player.getInventory().addItem(item);

        // remove from region + notify
        region.getGroundItemsSafe().removeIf(fi -> fi == item);

        handleBeamPickup(player, item);

        if (item.isInvisible()) {
            player.getPackets().sendRemoveGroundItem(item);
        } else {
            broadcastRemoveGroundItem(item);
            if (item.isForever()) {
                scheduleRespawn(item);
            }
        }

        return true;
    }

    private static boolean canPickupItem(Player player, FloorItem item) {

        // Owner / ironman restriction (matches your World)
        if (item.getOwner() != null) {
            if (!item.getOwner().getUsername().equalsIgnoreCase(player.getUsername())
                    && player.getPlayerRank().isIronman()) {
                player.getPackets().sendGameMessage(
                        "You can't pickup other players items as an Iron "
                                + (player.getAppearence().isMale() ? "Man" : "Woman") + "."
                );
                return false;
            }
        }

        // Developer-only item protection (matches your World: owner.isDeveloper())
        if (item.getOwner() != null && item.getOwner().isDeveloper() && !player.isDeveloper()) {
            player.getPackets().sendGameMessage("This item has been dropped by a developer, therefore you can't pick it up.");
            return false;
        }

        // Clue restriction (matches your World behavior using specific IDs)
        return canPickupClueScroll(player, item);
    }

    private static boolean canPickupClueScroll(Player player, FloorItem item) {
        int id = item.getId();
        switch (id) {
            case 2677: // easy
            case 2801: // medium
            case 2722: // hard
            case 19043: // elite (your World used 19043)
                if (player.getInventory().containsOneItem(id) || player.getBank().getItem(id) != null) {
                    player.getPackets().sendGameMessage("You can only have one " + getClueTypeName(id) + " clue scroll at a time.");
                    return false;
                }
                break;
        }
        return true;
    }

    private static String getClueTypeName(int id) {
        switch (id) {
            case 2677: return "easy";
            case 2801: return "medium";
            case 2722: return "hard";
            case 19043: return "elite";
            default: return "";
        }
    }

    private static boolean hasInventorySpace(Player player, FloorItem item) {
        int id = item.getId();
        boolean stackable = item.getDefinitions().isStackable() || item.getDefinitions().isNoted();

        if (player.getInventory().getAmountOf(id) == Integer.MAX_VALUE) {
            return false;
        }

        if (!player.getInventory().hasFreeSlots()) {
            if (stackable) {
                return player.getInventory().containsItem(id, 1);
            }
            return false;
        }
        return true;
    }

    private static boolean handleCoinPickup(Player player, FloorItem item, Region region) {
        int amount = item.getAmount();

        int pouchTotal = player.getMoneyPouch().getTotal();
        int invCoins = player.getInventory().getNumberOf(995);

        if (pouchTotal == Integer.MAX_VALUE && invCoins == Integer.MAX_VALUE) {
            player.getPackets().sendGameMessage("You don't have enough space to hold more coins.");
            return false;
        }

        int canAddToPouch = Integer.MAX_VALUE - pouchTotal;
        int toPouch = Math.min(amount, canAddToPouch);
        int leftover = amount - toPouch;

        if (toPouch > 0) {
            player.getMoneyPouch().setTotal(pouchTotal + toPouch);
            player.getPackets().sendRunScript(5561, 1, toPouch);
            player.getMoneyPouch().refresh();
            player.getPackets().sendGameMessage(
                    toPouch == 1 ? "One coin has been added to your money pouch."
                            : Utils.getFormattedNumber(toPouch, ',') + " coins have been added to your money pouch.");
        }

        if (leftover > 0) {
            if (!player.getInventory().hasFreeSlots() && !player.getInventory().containsOneItem(995)) {
                player.getPackets().sendGameMessage("You don't have enough inventory space.");
                return false;
            }

            if (player.getInventory().getNumberOf(995) + leftover < 0) {
                int invCanAdd = Integer.MAX_VALUE - player.getInventory().getNumberOf(995);
                player.getInventory().addItem(995, invCanAdd);
                item.setAmount(leftover - invCanAdd);
                player.getPackets().sendRemoveGroundItem(item);
                player.getPackets().sendGroundItem(item);
                return false;
            }

            player.getInventory().addItem(995, leftover);
        }

        // remove ground coins
        region.getGroundItemsSafe().removeIf(fi -> fi == item);

        if (item.isInvisible()) {
            player.getPackets().sendRemoveGroundItem(item);
        } else {
            broadcastRemoveGroundItem(item);
            if (item.isForever())
                scheduleRespawn(item);
        }

        return true;
    }

    private static void handleBeamPickup(Player player, FloorItem item) {
        if (player.getBeam() != null && player.getBeamItem() != null) {
            if (item.getTile().matches(player.getBeam()) && player.getBeamItem().getId() == item.getId()) {
                player.setBeam(null);
                player.setBeamItem(null);
            }
        }
    }

    public static void removeGroundItem(FloorItem item, int seconds) {
        WorldTasksManager.schedule(new WorldTask() {
            @Override
            public void run() {
                removeGroundItemInstant(item);
            }
        }, secondsToTicks(seconds));
    }

    public static void removeGroundItemInstant(FloorItem item) {
        if (item == null || item.isRemoved())
            return;

        item.setRemoved(true);

        Region region = World.getRegion(item.getTile().getRegionId());
        boolean removed = region.getGroundItemsSafe().removeIf(fi -> fi == item);
        if (!removed)
            return;

        broadcastRemoveGroundItem(item);

        if (item.isForever()) {
            scheduleRespawn(item);
        }
    }

    public static void removeGroundItem(Player player, FloorItem floorItem) {
        removeGroundItem(player, floorItem, true);
    }

    public static boolean removeGroundItem(Player player, final FloorItem floorItem, boolean addToInventory) {

        if (floorItem == null || floorItem.isRemoved())
            return false;

        final Region region = World.getRegion(floorItem.getTile().getRegionId());

        // server/system removal
        if (player == null) {
            region.getGroundItemsSafe().removeIf(fi -> fi == floorItem);
            broadcastRemoveGroundItem(floorItem);
            if (floorItem.isForever())
                scheduleRespawn(floorItem);
            floorItem.setRemoved(true);
            return false;
        }

        if (!region.getGroundItemsSafe().contains(floorItem))
            return false;

        if (!canPickupItem(player, floorItem))
            return false;

        if (!hasInventorySpace(player, floorItem)) {
            player.getPackets().sendGameMessage("Not enough space in your inventory.");
            return false;
        }

        if (addToInventory) {
            if (floorItem.getId() == 7957)
                floorItem.setId(1005);
            player.getInventory().addItem(floorItem);
        }

        region.getGroundItemsSafe().removeIf(fi -> fi == floorItem);
        handleBeamPickup(player, floorItem);

        if (floorItem.isInvisible()) {
            player.getPackets().sendRemoveGroundItem(floorItem);
        } else {
            broadcastRemoveGroundItem(floorItem);
            if (floorItem.isForever()) {
                scheduleRespawn(floorItem);
            }
        }

        floorItem.setRemoved(true);
        return true;
    }

    private static void scheduleRespawn(FloorItem item) {
        WorldTasksManager.schedule(new WorldTask() {
            @Override
            public void run() {
                addGroundItemForever(item, item.getTile());
            }
        }, secondsToTicks(60));
    }

    @SuppressWarnings("unused")
    private static boolean shouldNotifyPlayer(Player player, FloorItem item) {
        return player != null
                && player.hasStarted()
                && !player.hasFinished()
                && player.getPlane() == item.getTile().getPlane()
                && player.withinDistance(item.getTile(), 64);
    }
}
