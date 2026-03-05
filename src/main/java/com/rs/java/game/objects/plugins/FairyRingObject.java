package com.rs.java.game.objects.plugins;

import com.rs.java.game.WorldObject;
import com.rs.java.game.objects.ObjectPlugin;
import com.rs.java.game.player.Player;
import com.rs.kotlin.game.player.skills.woodcutting.TreeDefinition;
import com.rs.kotlin.game.player.skills.woodcutting.Woodcutting;
import com.rs.kotlin.game.player.transportation.FairyRings;
import com.rs.kotlin.rscm.Rscm;
import com.rs.kotlin.rscm.RscmResolver;

import java.util.List;

//TODO: Not done
public class FairyRingObject extends ObjectPlugin {

	@Override
	public Object[] getKeys() {
		return Rscm.lookupList("object_group.fairy_ring_use").toArray(); }

	@Override
	public boolean processObject(Player player, WorldObject object) {
		FairyRings.Companion.openRingInterface(player, object);
		return true;
	}
}
