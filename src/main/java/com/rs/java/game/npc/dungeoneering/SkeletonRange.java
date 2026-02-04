package com.rs.java.game.npc.dungeoneering;

import com.rs.java.game.WorldTile;
import com.rs.java.game.player.CombatDefinitions;
import com.rs.java.game.player.actions.combat.Combat;
import com.rs.java.game.player.content.dungeoneering.DungeonManager;
import com.rs.java.utils.WeaponTypesLoader.WeaponType;

@SuppressWarnings("serial")
public class SkeletonRange extends DungeonNPC {

	public SkeletonRange(int id, WorldTile tile, DungeonManager manager, double multiplier) {
		super(id, tile, manager, multiplier);
	}

	private static final WeaponType[][] WEAKNESS =
	{
	{ new WeaponType(Combat.MELEE_TYPE, CombatDefinitions.CRUSH_ATTACK) },
	{ new WeaponType(Combat.MELEE_TYPE, CombatDefinitions.SLASH_ATTACK) },
	{ new WeaponType(Combat.MAGIC_TYPE, -1) } };

	public WeaponType[][] getWeaknessStyle() {
		return WEAKNESS;
	}
}