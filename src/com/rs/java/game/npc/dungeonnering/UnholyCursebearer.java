package com.rs.java.game.npc.dungeonnering;

import com.rs.java.game.WorldTile;
import com.rs.java.game.player.CombatDefinitions;
import com.rs.java.game.player.actions.combat.Combat;
import com.rs.java.game.player.content.dungeoneering.DungeonManager;
import com.rs.java.game.player.content.dungeoneering.RoomReference;
import com.rs.java.utils.WeaponTypesLoader.WeaponType;

@SuppressWarnings("serial")
public class UnholyCursebearer extends DungeonBoss {

	public UnholyCursebearer(int id, WorldTile tile, DungeonManager manager, RoomReference reference) {
		super(id, tile, manager, reference);
	}
	
	private static final WeaponType[][] WEAKNESS =
		{
		{ new WeaponType(Combat.MELEE_TYPE, CombatDefinitions.SLASH_ATTACK), new WeaponType(Combat.MELEE_TYPE, CombatDefinitions.STAB_ATTACK), new WeaponType(Combat.MELEE_TYPE, CombatDefinitions.CRUSH_ATTACK), new WeaponType(Combat.RANGE_TYPE, -1)},};

	public WeaponType[][] getWeaknessStyle() {
		return WEAKNESS;
	}
}
