package com.rs.java.game.npc.dungeonnering;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.rs.java.game.Entity;
import com.rs.java.game.Graphics;
import com.rs.java.game.World;
import com.rs.java.game.WorldTile;
import com.rs.java.game.player.actions.combat.Combat;
import com.rs.java.game.player.content.dungeoneering.DungeonManager;
import com.rs.java.game.player.content.dungeoneering.RoomReference;
import com.rs.java.utils.WeaponTypesLoader.WeaponType;

@SuppressWarnings("serial")
public class NecroLord extends DungeonBoss {

	private int resetTicks;
	private List<SkeletalMinion> skeletons;

	public NecroLord(int id, WorldTile tile, DungeonManager manager, RoomReference reference) {
		super(id, tile, manager, reference, 0.6D);
		setCantFollowUnderCombat(true); //force can't walk
		setLureDelay(Integer.MAX_VALUE);//doesn't stop focusing on target
		skeletons = new CopyOnWriteArrayList<SkeletalMinion>();
	}

	@Override
	public void processNPC() {
		super.processNPC();
		if (!isUnderCombat() && skeletons != null && skeletons.size() > 0) {
			resetTicks++;
			if (resetTicks == 50) {
				resetSkeletons();
				resetTicks = 0;
				return;
			}
		}
	}
	
	private static final WeaponType[][] WEAKNESS =
		{
		{ new WeaponType(Combat.RANGE_TYPE, -1), new WeaponType(Combat.MAGIC_TYPE, 2) },};

	public WeaponType[][] getWeaknessStyle() {
		return WEAKNESS;
	}

	public void addSkeleton(WorldTile tile) {
		SkeletalMinion npc = new SkeletalMinion(this, 11722, tile, getManager(), getMultiplier() / 2);
		npc.setForceAgressive(true);
		skeletons.add(npc);
		World.sendGraphics(npc, new Graphics(2399), tile);
	}

	public void resetSkeletons() {
		for (SkeletalMinion skeleton : skeletons)
			skeleton.sendDeath(this);
		skeletons.clear();
	}

	public void removeSkeleton(DungeonNPC sk) {
		skeletons.remove(sk);
	}

	/*
	 * because necrolord room has a safespot which shouldnt
	 */
	@Override
	public boolean clipedProjectile(WorldTile tile, boolean checkClose, int size) {
		//because npc is under cliped data
		return getManager().isAtBossRoom(tile);
	}

	@Override
	public void sendDeath(Entity source) {
		super.sendDeath(source);
		resetSkeletons();
	}

	/*@Override
	public Item sendDrop(Player player, Drop drop) {
		Item item = new Item(drop.getItemId());
		player.getInventory().addItemDrop(item.getId(), item.getAmount());
		return item;
	}*/
}
