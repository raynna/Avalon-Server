package com.rs.java.game.npc.dungeonnering;

import com.rs.java.game.Animation;
import com.rs.java.game.World;
import com.rs.java.game.WorldTile;
import com.rs.java.game.player.CombatDefinitions;
import com.rs.java.game.player.actions.combat.Combat;
import com.rs.java.game.player.content.dungeoneering.DungeonManager;
import com.rs.java.utils.WeaponTypesLoader.WeaponType;

@SuppressWarnings("serial")
public class YkLagorMage extends DungeonNPC {

	private YkLagorThunderous boss;
	private int cycle;

	public YkLagorMage(YkLagorThunderous ykLagorThunderous, int id, WorldTile tile, DungeonManager manager, double multiplier) {
		super(id, tile, manager, multiplier);
		this.boss = ykLagorThunderous;
		setNextFaceEntity(boss);
		setCantFollowUnderCombat(true);
	}

	@Override
	public void processNPC() {
		if (isDead() || boss == null)
			return;
		if (isUnderCombat()) {
			super.processNPC();
			return;
		}
		if (cycle > 0) {
			cycle--;
			return;
		}
		cycle = 5;
		setNextFaceEntity(boss);
		animate(new Animation(3645));
		World.sendElementalProjectile(this, boss, 2704);
	}
	
	private static final WeaponType[][] WEAKNESS =
		{
		{ new WeaponType(Combat.MELEE_TYPE, CombatDefinitions.SLASH_ATTACK), new WeaponType(Combat.MAGIC_TYPE, -1), new WeaponType(Combat.MAGIC_TYPE, -1) },};

	public WeaponType[][] getWeaknessStyle() {
		return WEAKNESS;
	}

	@Override
	public void drop() {

	}

	@Override
	public int getMaxHitpoints() {
		return 650;
	}

	@Override
	public int getCombatLevel() {
		return 65;
	}

	/*@Override
	public void sendDeath(Entity source) {
	super.sendDeath(source);
	}*/
}
