package raynna.game.npc.dungeoneering;

import raynna.game.Animation;
import raynna.game.Graphics;
import raynna.game.WorldTile;
import raynna.game.player.CombatDefinitions;
import raynna.game.player.actions.combat.Combat;
import raynna.game.player.content.dungeoneering.DungeonManager;
import raynna.game.player.content.dungeoneering.RoomReference;
import raynna.core.tasks.WorldTask;
import raynna.core.tasks.WorldTasksManager;
import raynna.util.Utils;
import raynna.util.WeaponTypesLoader.WeaponType;

@SuppressWarnings("serial")
public class HobgoblinGeomancer extends DungeonBoss {

	public HobgoblinGeomancer(int id, WorldTile tile, DungeonManager manager, RoomReference reference) {
		super(id, tile, manager, reference);
	}
	
	private static final WeaponType[][] WEAKNESS =
		{
		{ new WeaponType(Combat.MELEE_TYPE, CombatDefinitions.STAB_ATTACK), new WeaponType(Combat.RANGE_TYPE, -1) },};

	public WeaponType[][] getWeaknessStyle() {
		return WEAKNESS;
	}

	public void sendTeleport(final WorldTile tile, final RoomReference room) {
		setCantInteract(true);
		animate(new Animation(12991, 70));
		gfx(new Graphics(1576, 70, 0));
		WorldTasksManager.schedule(new WorldTask() {

			@Override
			public void run() {
				setCantInteract(false);
				animate(new Animation(-1));
				setNextWorldTile(Utils.getFreeTile(getManager().getRoomCenterTile(room), 6));
				resetAllDamage();
			}
		}, 5);
	}
}
