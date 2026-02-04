package com.rs.java.game.npc.dungeoneering;

import com.rs.java.game.Entity;
import com.rs.java.game.Graphics;
import com.rs.java.game.Hit;
import com.rs.java.game.Hit.HitLook;
import com.rs.java.game.WorldTile;
import com.rs.java.game.player.CombatDefinitions;
import com.rs.java.game.player.Player;
import com.rs.java.game.player.actions.combat.Combat;
import com.rs.java.game.player.content.dungeoneering.DungeonManager;
import com.rs.java.game.player.content.dungeoneering.RoomReference;
import com.rs.java.utils.Utils;
import com.rs.java.utils.WeaponTypesLoader.WeaponType;

@SuppressWarnings("serial")
public final class ShadowForgerIhlakhizan extends DungeonBoss {

	public ShadowForgerIhlakhizan(int id, WorldTile tile, DungeonManager manager, RoomReference reference) {
		super(id, tile, manager, reference);
		setCantFollowUnderCombat(true); //force cant walk
	}

	@Override
	public void setNextFaceEntity(Entity entity) {
		//this boss doesnt face
	}

	@Override
	public void processNPC() {
		if (isDead())
			return;
		super.processNPC();
		for (Player player : getManager().getParty().getTeam()) {
			if (!getManager().isAtBossRoom(player) || clipedProjectile(player, false) || player.getTemporaryAttributtes().get("SHADOW_FORGER_SHADOW") != null)
				continue;
			player.gfx(new Graphics(2378));
			player.getTemporaryAttributtes().put("SHADOW_FORGER_SHADOW", Boolean.TRUE);
			player.applyHit(new Hit(this, Utils.random((int) (player.getMaxHitpoints() * 0.1)) + 1, HitLook.REGULAR_DAMAGE));
		}
	}

	public void setUsedShadow() {
		for (Player player : getManager().getParty().getTeam()) {
			player.getTemporaryAttributtes().put("SHADOW_FORGER_SHADOW", Boolean.TRUE);
		}
	}

	private static final WeaponType[][] WEAKNESS =
		{
		{ new WeaponType(Combat.MELEE_TYPE, CombatDefinitions.STAB_ATTACK), new WeaponType(Combat.RANGE_TYPE, -1) },};

	public WeaponType[][] getWeaknessStyle() {
		return WEAKNESS;
	}

	/*@Override
	public Item sendDrop(Player player, Drop drop) {
		Item item = new Item(drop.getItemId());
		player.getInventory().addItemDrop(item.getId(), item.getAmount());
		return item;
	}*///TODO 

}
