package raynna.game.npc.dungeoneering;

import raynna.game.WorldTile;
import raynna.game.player.CombatDefinitions;
import raynna.game.player.actions.combat.Combat;
import raynna.game.player.content.dungeoneering.DungeonManager;
import raynna.util.WeaponTypesLoader.WeaponType;

@SuppressWarnings("serial")
public class GreenDragon extends DungeonNPC {

	public GreenDragon(int id, WorldTile tile, DungeonManager manager, double multiplier) {
		super(id, tile, manager, multiplier);
	}

	private static final WeaponType[][] WEAKNESS =
	{
	{ new WeaponType(Combat.MELEE_TYPE, CombatDefinitions.STAB_ATTACK) },
	{ new WeaponType(Combat.RANGE_TYPE, -1) }, // Range (all) 
		{ new WeaponType(Combat.MAGIC_TYPE, 3) } }; // Fire

	public WeaponType[][] getWeaknessStyle() {
		return WEAKNESS;
	}
}