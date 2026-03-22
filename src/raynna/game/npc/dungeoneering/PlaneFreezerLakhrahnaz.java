package raynna.game.npc.dungeoneering;

import raynna.game.WorldTile;
import raynna.game.player.CombatDefinitions;
import raynna.game.player.actions.combat.Combat;
import raynna.game.player.content.dungeoneering.DungeonManager;
import raynna.game.player.content.dungeoneering.RoomReference;
import raynna.util.WeaponTypesLoader.WeaponType;

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
