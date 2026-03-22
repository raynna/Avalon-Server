package raynna.game.npc.dungeoneering;

import raynna.game.WorldTile;
import raynna.game.player.CombatDefinitions;
import raynna.game.player.actions.combat.Combat;
import raynna.game.player.content.dungeoneering.DungeonManager;
import raynna.util.WeaponTypesLoader.WeaponType;

@SuppressWarnings("serial")
public class ThrowerTroll extends DungeonNPC {

	public ThrowerTroll(int id, WorldTile tile, DungeonManager manager, double multiplier) {
		super(id, tile, manager, multiplier);
	}
	
	private static final WeaponType[][] WEAKNESS =
		{
		{ new WeaponType(Combat.MELEE_TYPE, CombatDefinitions.STAB_ATTACK) },
		{ new WeaponType(Combat.MELEE_TYPE, CombatDefinitions.SLASH_ATTACK) },
		{ new WeaponType(Combat.RANGE_TYPE, -1) }
		};

	public WeaponType[][] getWeaknessStyle() {
		return WEAKNESS;
	}
}
