package com.rs.java.game.npc.dungeoneering;

import com.rs.java.game.WorldTile;
import com.rs.java.game.player.CombatDefinitions;
import com.rs.java.game.player.actions.combat.Combat;
import com.rs.java.game.player.content.dungeoneering.DungeonManager;
import com.rs.java.game.player.content.dungeoneering.RoomReference;
import com.rs.java.utils.WeaponTypesLoader.WeaponType;

@SuppressWarnings("serial")
public class PlaneFreezerLakhrahnaz extends DungeonBoss {

	public PlaneFreezerLakhrahnaz(int id, WorldTile tile, DungeonManager manager, RoomReference reference) {
		super(id, tile, manager, reference);
		//TODO setCantSetTargetAutoRelatio(true);
	}
	
	private static final WeaponType[][] WEAKNESS =
		{{ new WeaponType(Combat.MELEE_TYPE, CombatDefinitions.STAB_ATTACK) }};

	public WeaponType[][] getWeaknessStyle() {
		return WEAKNESS;
	}
}
