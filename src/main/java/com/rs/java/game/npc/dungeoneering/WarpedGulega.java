package com.rs.java.game.npc.dungeoneering;

import com.rs.java.game.Hit;
import com.rs.java.game.WorldTile;
import com.rs.java.game.npc.familiar.Familiar;
import com.rs.java.game.player.CombatDefinitions;
import com.rs.java.game.player.actions.combat.Combat;
import com.rs.java.game.player.content.dungeoneering.DungeonManager;
import com.rs.java.game.player.content.dungeoneering.RoomReference;
import com.rs.java.utils.WeaponTypesLoader.WeaponType;

@SuppressWarnings("serial")
public class WarpedGulega extends DungeonBoss {

	public WarpedGulega(int id, WorldTile tile, DungeonManager manager, RoomReference reference) {
		super(id, tile, manager, reference);
	}

	//thats default lol
	/* @Override
	 public double getMeleePrayerMultiplier() {
	return 0.0;//Fully block it.
	 }
	 
	 @Override
	 public double getRangePrayerMultiplier() {
	return 0.0;//Fully block it.
	 }
	 
	 @Override
	 public double getMagePrayerMultiplier() {
	return 0.0;//Fully block it.
	 }*/
	
	private static final WeaponType[][] WEAKNESS =
		{{ new WeaponType(Combat.MELEE_TYPE, CombatDefinitions.SLASH_ATTACK)}};

	public WeaponType[][] getWeaknessStyle() {
		return WEAKNESS;
	}

	@Override
	public void processHit(Hit hit) {
		if (!(hit.getSource() instanceof Familiar))
			hit.setDamage((int) (hit.getDamage() * 0.45D));
		super.processHit(hit);
	}
}
