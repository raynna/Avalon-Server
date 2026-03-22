package raynna.game.npc.dungeoneering;

import raynna.game.WorldTile;
import raynna.game.player.CombatDefinitions;
import raynna.game.player.actions.combat.Combat;
import raynna.game.player.content.dungeoneering.DungeonManager;
import raynna.util.WeaponTypesLoader.WeaponType;


@SuppressWarnings("serial")
public class Brute extends DungeonNPC {

	public Brute(int id, WorldTile tile, DungeonManager manager, double multiplier) {
		super(id, tile, manager, multiplier);
	}

	private static final WeaponType[][] WEAKNESS =
	{
	{ new WeaponType(Combat.MAGIC_TYPE, -1) }, // Magic (all) 
		{ new WeaponType(Combat.MELEE_TYPE, CombatDefinitions.CRUSH_ATTACK) },
		{ new WeaponType(Combat.MELEE_TYPE, CombatDefinitions.STAB_ATTACK) } };

	public WeaponType[][] getWeaknessStyle() {
		return WEAKNESS;
	}
}