package com.rs.java.game.npc.dungeoneering;

import com.rs.java.game.WorldTile;
import com.rs.java.game.player.CombatDefinitions;
import com.rs.java.game.player.actions.combat.Combat;
import com.rs.java.game.player.content.dungeoneering.DungeonManager;
import com.rs.java.game.player.content.dungeoneering.RoomReference;
import com.rs.java.utils.WeaponTypesLoader.WeaponType;

@SuppressWarnings("serial")
public class ToKashBloodChiller extends DungeonBoss {

	private boolean specialAttack;

	public ToKashBloodChiller(int id, WorldTile tile, DungeonManager manager, RoomReference reference) {
		super(id, tile, manager, reference);
	}
	
	private static final WeaponType[][] WEAKNESS =
		{
		{ new WeaponType(Combat.MELEE_TYPE, CombatDefinitions.CRUSH_ATTACK), new WeaponType(Combat.RANGE_TYPE, -1), new WeaponType(Combat.MAGIC_TYPE, -1) },};

	public WeaponType[][] getWeaknessStyle() {
		return WEAKNESS;
	}

	public void setSpecialAttack(boolean specialAttack) {
		this.specialAttack = specialAttack;
	}

	public boolean canSpecialAttack() {
		return !specialAttack;
	}
}
