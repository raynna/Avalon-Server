package com.rs.java.game.npc.dungeoneering;

import com.rs.java.game.WorldTile;
import com.rs.java.game.player.CombatDefinitions;
import com.rs.java.game.player.actions.combat.Combat;
import com.rs.java.game.player.content.dungeoneering.DungeonManager;
import com.rs.java.utils.WeaponTypesLoader.WeaponType;

@SuppressWarnings("serial")
public class Hobgoblin extends DungeonNPC {

	public Hobgoblin(int id, WorldTile tile, DungeonManager manager, double multiplier) {
		super(id, tile, manager, multiplier);
	}

	private static final WeaponType[][] WEAKNESS =
	{
	{ new WeaponType(Combat.MELEE_TYPE, CombatDefinitions.STAB_ATTACK) },
	{ new WeaponType(Combat.MAGIC_TYPE, 1) }, // Water
		{ new WeaponType(Combat.MELEE_TYPE, CombatDefinitions.SLASH_ATTACK) } };

	public WeaponType[][] getWeaknessStyle() {
		return WEAKNESS;
	}
}