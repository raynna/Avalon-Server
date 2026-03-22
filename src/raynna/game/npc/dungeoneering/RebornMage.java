package raynna.game.npc.dungeoneering;

import raynna.game.WorldTile;
import raynna.game.player.CombatDefinitions;
import raynna.game.player.actions.combat.Combat;
import raynna.game.player.content.dungeoneering.DungeonManager;
import raynna.util.WeaponTypesLoader.WeaponType;


@SuppressWarnings("serial")
public class RebornMage extends DungeonNPC {

	public RebornMage(int id, WorldTile tile, DungeonManager manager, double multiplier) {
		super(id, tile, manager, multiplier);
	}

	private static final WeaponType[][] WEAKNESS =
	{
	{ new WeaponType(Combat.RANGE_TYPE, -1) }, // Range (all)
		{ new WeaponType(Combat.MAGIC_TYPE, 1) }, // Water 
		{ new WeaponType(Combat.MELEE_TYPE, CombatDefinitions.CRUSH_ATTACK) } };

	public WeaponType[][] getWeaknessStyle() {
		return WEAKNESS;
	}
}