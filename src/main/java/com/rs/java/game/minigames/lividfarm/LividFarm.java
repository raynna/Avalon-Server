package com.rs.java.game.minigames.lividfarm;

import java.util.ArrayList;

import com.rs.java.game.minigames.lividfarm.LividStore.Spell;
import com.rs.java.game.player.Player;
import com.rs.java.utils.Utils;

/**
 * @author -Andreas 20 feb. 2020 13:29:52
 * @project source
 * 
 */

public class LividFarm {

	private transient Player player;

	private int produce;
	public int spellAmount;
	private ArrayList<Spell> spellsLearned = new ArrayList<>();

	public LividFarm() {
		produce = 0;
	}

	public ArrayList<Spell> getSpellsLearned() {
		return spellsLearned;
	}

	public int getSpellAmount() {
		return spellAmount;
	}

	public void addSpellAmount() {
		spellAmount++;
		player.getVarsManager().sendVarBit(9067, player.getLivid().getSpellAmount(), true);
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public int getProduce() {
		return produce;
	}

	public void addProduce(int value) {
		this.produce += (value * 20);
		player.getVarsManager().sendVarBit(9065, player.getLivid().getProduce() / 10, true);
		if (player.getControlerManager().getControler() instanceof LividFarmController)
			updateProduce(player);
	}

	public void removeProduce(int value) {
		this.produce -= value;
		player.getVarsManager().sendVarBit(9065, player.getLivid().getProduce() / 10, true);
		if (player.getControlerManager().getControler() instanceof LividFarmController)
			updateProduce(player);
	}

	public static void updateProduce(Player player) {
		player.getPackets().sendTextOnComponent(3046, 3,
				"Produce: " + Utils.getFormattedNumber(player.getLivid().getProduce(), ','));
	}

}
