package com.rs.java.game.npc.corporeal;

import com.rs.core.cache.defintions.ItemDefinitions;
import com.rs.java.game.Entity;
import com.rs.java.game.Hit;
import com.rs.java.game.WorldTile;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.player.Player;

@SuppressWarnings("serial")
public class CorporealBeast extends NPC {

	private DarkEnergyCore core;

	public CorporealBeast(int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea,
			boolean spawned) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		setCapDamage(1000);
		setLureDelay(3000);
		setForceTargetDistance(64);
		setForceAgressiveDistance(64);
		setIntelligentRouteFinder(true);
	}

	public void spawnDarkEnergyCore() {
		if (core != null)
			return;
		core = new DarkEnergyCore(this);
	}

	public void removeDarkEnergyCore() {
		if (core == null)
			return;
		core.finish();
		core = null;
	}

	@Override
	public void handleHit(Hit hit) {
		if (hit.getSource() instanceof Player) {
		Player target = (Player) hit.getSource();
		if (!ItemDefinitions.getItemDefinitions(target.getEquipment().getWeaponId()).getName().toLowerCase()
				.contains(" spear")) {
			hit.setDamage(hit.getDamage() / 2);
			target.getPackets().sendGameMessage("You cannot deal full damage without a spear weapon.");
		}
		}
		super.handleHit(hit);
	}

	@Override
	public void processNPC() {
		super.processNPC();
		if (isDead())
			return;
		int maxhp = getMaxHitpoints();
		if (maxhp > getHitpoints() && getPossibleTargets().isEmpty())
			setHitpoints(maxhp);
	}

	@Override
	public void sendDeath(Entity source) {
		super.sendDeath(source);
		if (core != null)
			core.sendDeath(source);
	}
}
