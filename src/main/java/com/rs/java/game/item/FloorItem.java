package com.rs.java.game.item;

import com.rs.java.game.World;
import com.rs.java.game.WorldTile;
import com.rs.java.game.player.Player;
import com.rs.java.game.player.content.ItemConstants;
import com.rs.core.packets.InputStream;
import com.rs.java.utils.EconomyPrices;
import com.rs.java.utils.ItemExamines;
import com.rs.java.utils.Utils;

import java.io.Serial;

public class FloorItem extends Item {

	@Serial
	private static final long serialVersionUID = -2287633342490535089L;

	private final WorldTile tile;
	private Player owner;
	// 0 visible, 1 invisible, 2 visible and reappears 30sec after taken
	private int type;
	private int tick;
	private boolean spawned;
	private boolean globalPicked;
	private boolean removed;
	private final long uid = System.nanoTime();
	public long getUid() { return uid; }

	public FloorItem(int id) {
		super(id);
		this.tile = null;
	}

	public FloorItem(Item item, WorldTile tile, Player owner, boolean underGrave, boolean invisible) {
		super(item.getId(), item.getAmount(), item.getMetadata());
		this.tile = tile;
		this.owner = owner;
		this.type = invisible ? 1 : 0;
	}

	public FloorItem(Item item, WorldTile tile, Player owner, boolean invisible, int tick, boolean spawned) {
		super(item.getId(), item.getAmount(), item.getMetadata());
		this.tile = tile;
		this.owner = owner;
		this.type = invisible ? 1 : 0;
		this.tick = tick;
		this.spawned = spawned;
	}

	public FloorItem(Item item, WorldTile tile, boolean appearForever) {
		super(item.getId(), item.getAmount(), item.getMetadata());
		this.tile = tile;
		this.owner = null;
		this.type = appearForever ? 2 : 0;
	}

	@Override
	public void setAmount(int amount) {
		super.setAmount(amount);
	}

	public void setGlobalPicked(boolean picked) {
		this.globalPicked = picked;
	}

	public boolean isGlobalPicked() {
		return globalPicked;
	}

	public WorldTile getTile() {
		return tile;
	}

	public boolean isInvisible() {
		return type == 1;
	}

	public boolean isForever() {
		return type == 2;
	}

	public Player getOwner() {
		return owner;
	}

	public static void handleExamine(final Player player, InputStream stream) {
		if (!player.hasStarted() || !player.clientHasLoadedMapRegion() || player.isDead())
			return;
		final int y = stream.readUnsignedShort();
		final int x = stream.readUnsignedShortLE();
		final int id = stream.readUnsignedShort();
		final WorldTile tile = new WorldTile(x, y, player.getPlane());
		final int regionId = tile.getRegionId();
		if (!player.getMapRegionsIds().contains(regionId))
			return;
		final FloorItem floorItem = World.getRegion(regionId).getVisibleGroundItem(id, tile, player);
		if (floorItem == null)
			return;

		player.stopAll(false);

		if (!ItemConstants.isTradeable(floorItem)) {
			player.getPackets().sendGameMessage(
					"[Price Checker] " + (floorItem.getAmount() > 1 ? floorItem.getAmount() + " x " : "")
							+ floorItem.getDefinitions().getName() + " is untradeable.");
			return;
		}

		if (floorItem.getId() == 995) {
			player.getPackets()
					.sendGameMessage("[Price Checker] " + Utils.getFormattedNumber(floorItem.getAmount(), ',') + " x "
							+ floorItem.getDefinitions().getName() + ".");
			return;
		}

		if ((floorItem.getDefinitions().isNoted() || floorItem.getDefinitions().isStackable())
				&& floorItem.getAmount() > 1) {
			long amount = floorItem.getAmount();
			long price = EconomyPrices.getPrice(floorItem.getId()) * amount;
			player.getPackets()
					.sendGameMessage("[Price Checker] " + Utils.getFormattedNumber(floorItem.getAmount(), ',') + " x "
							+ floorItem.getDefinitions().getName() + ": " + Utils.formatDoubledAmount(price)
							+ " coins.");
		} else {
			player.getPackets().sendGameMessage("[Price Checker] " + floorItem.getDefinitions().getName() + ": "
					+ Utils.getFormattedNumber(EconomyPrices.getPrice(floorItem.getId()), ',') + " coins.");
		}

		if (player.isDeveloper()) {
			player.getPackets()
					.sendGameMessage(floorItem.getDefinitions().getName() + ", ItemId: "
							+ floorItem.getDefinitions().getId() + ", X: " + tile.getX() + ", Y: " + tile.getY()
							+ ", H: " + tile.getPlane() + ", Owner: " + (floorItem.hasOwner() ? floorItem.getOwner().getUsername() : "None"));
		}

		player.getPackets().sendItemMessage(0, 15263739, id, x, y, ItemExamines.getExamine(new Item(id))); // ChatboxMessage
	}

	public boolean hasOwner() {
		return owner != null;
	}

	public void setInvisible(boolean invisible) {
		type = invisible ? 1 : 0;
	}

	public boolean isSpawned() {
		return spawned;
	}

	public int getTick() {
		return tick;
	}

	public boolean isRemoved() {
		return removed;
	}

	public void setRemoved(boolean removed) {
		this.removed = removed;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof FloorItem other)) return false;
		return uid == other.uid;
	}

	@Override
	public int hashCode() {
		return Long.hashCode(uid);
	}

}
