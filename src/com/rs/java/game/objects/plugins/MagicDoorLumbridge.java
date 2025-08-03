package com.rs.java.game.objects.plugins;

import java.util.TimerTask;

import com.rs.core.thread.CoresManager;
import com.rs.java.game.WorldObject;
import com.rs.java.game.WorldTile;
import com.rs.java.game.objects.ObjectPlugin;
import com.rs.java.game.player.Player;
import com.rs.java.game.player.actions.combat.Magic;

public class MagicDoorLumbridge extends ObjectPlugin {

	@Override
	public Object[] getKeys() {
		return new Object[] { 2406 };
	}

	@Override
	public int getDistance() {
		return 0;
	}

	private static final int DRAMEN_STAFF = 772;

	@Override
	public boolean processObject(Player player, WorldObject object) {
		DoorsAndGates.handleDoorTemporary(player, object, 1200);
		CoresManager.getFastExecutor().schedule(new TimerTask() {

			@Override
			public void run() {
				if (player.getEquipment().getWeaponId() == DRAMEN_STAFF)
					Magic.sendObjectTeleportSpell(player, false, new WorldTile(2452, 4473, 0));
			}

		}, 2000);
		return true;
	}

}
