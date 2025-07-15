package com.rs.java.game.npc.dungeonnering;

import com.rs.java.game.WorldTile;
import com.rs.java.game.player.CombatDefinitions;
import com.rs.java.game.player.actions.combat.Combat;
import com.rs.java.game.player.content.dungeoneering.DungeonManager;
import com.rs.java.utils.Utils;
import com.rs.java.utils.WeaponTypesLoader.WeaponType;

@SuppressWarnings("serial")
public class Bat extends DungeonNPC {

	public Bat(int id, WorldTile tile, DungeonManager manager, double multiplier) {
		super(id, tile, manager, multiplier);
		setCombatLevel(Utils.random(10) == 0 ? 28 : 3);
	}

	@Override
	public int getMaxHitpoints() {
		return getCombatLevel() * 10;
	}

	@Override
	public int getMaxHit() {
		if (getCombatLevel() == 3)
			return 18;
		return 70;
	}

	private static final WeaponType[][] WEAKNESS =
	{
	{ new WeaponType(Combat.RANGE_TYPE, -1) }, // Range (all)     
		{ new WeaponType(Combat.MELEE_TYPE, CombatDefinitions.STAB_ATTACK) } };

	public WeaponType[][] getWeaknessStyle() {
		return WEAKNESS;
	}
}