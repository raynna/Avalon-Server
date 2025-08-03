package com.rs.java.game.npc.dungeonnering;

import com.rs.java.game.Animation;
import com.rs.java.game.Graphics;
import com.rs.java.game.WorldTile;
import com.rs.java.game.player.CombatDefinitions;
import com.rs.java.game.player.actions.combat.Combat;
import com.rs.java.game.player.content.dungeoneering.DungeonManager;
import com.rs.java.game.player.content.dungeoneering.RoomReference;
import com.rs.core.tasks.WorldTask;
import com.rs.core.tasks.WorldTasksManager;
import com.rs.java.utils.Utils;
import com.rs.java.utils.WeaponTypesLoader.WeaponType;

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
