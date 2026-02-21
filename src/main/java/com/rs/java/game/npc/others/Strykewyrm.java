package com.rs.java.game.npc.others;

import com.rs.java.game.Animation;
import com.rs.java.game.WorldTile;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.player.Player;
import com.rs.core.tasks.WorldTasksManager;
import com.rs.java.game.player.Skills;

import java.util.Map;

@SuppressWarnings("serial")
public class Strykewyrm extends NPC {

	private static final Map<Integer, Integer> DIG_TO_COMBAT = Map.of(
			9462, 9463,
			9464, 9465,
			9466, 9467
	);

	private static final Map<Integer, Integer> COMBAT_TO_DIG = Map.of(
			9463, 9462,
			9465, 9464,
			9467, 9466
	);

	private final int baseId;

	public Strykewyrm(int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, true);
		this.baseId = id; // original dig form
	}


	@Override
	public void processNPC() {
		super.processNPC();

		if (isDead())
			return;

		if (isCombatForm() && !isCantInteract() && !isUnderCombat() && !isInCombat()) {
			redig();
		}
	}

	private void redig() {
		setCantInteract(true);

		animate(new Animation(12796));

		WorldTasksManager.schedule(1, () -> {

			resetCombat();
			setTarget(null);
			setAttackedBy(null);

			Integer digId = COMBAT_TO_DIG.get(getId());
			if (digId != null) {
				transformIntoNPC(digId);
			}

			setCantInteract(false);
		});
	}

	@Override
	public void reset() {
		transformIntoNPC(baseId);
		resetCombat();
		super.reset();
	}

	@Override
	public boolean handleNPCClick(Player player) {

		if (!isDigForm())
			return true;

		if (isCantInteract())
			return true;

		if (!canAttack(player, this))
			return true;

		if (getId() == 9462) {
			if (player.getSkills().getLevel(Skills.SLAYER) < 93) {
				player.getPackets().sendGameMessage(
						"You need at least a Slayer level of 93 to fight this."
				);
				return true;
			}
		}

		stomp(player, this);
		return true;
	}

	private static void stomp(Player player, Strykewyrm wyrm) {

		wyrm.setCantInteract(true);

		player.animate(new Animation(4278));

		WorldTasksManager.schedule(2, () -> {

			Integer combatId = DIG_TO_COMBAT.get(wyrm.getId());
			if (combatId == null)
				return;

			wyrm.animate(new Animation(12795));
			wyrm.transformIntoNPC(combatId);

			wyrm.setTarget(player);
			wyrm.setAttackedBy(player);

			wyrm.setCantInteract(false);
			wyrm.setBonuses();
			wyrm.getCombat().addAttackDelay(4);
		});
	}

	private static boolean canAttack(Player player, NPC npc) {

		if (!npc.isAtMultiArea() || !player.isAtMultiArea()) {

			if (player.getAttackedBy() != npc && player.isInCombat()) {
				player.getPackets().sendGameMessage("You are already in combat.");
				return false;
			}

			if (npc.getAttackedBy() != player && npc.isInCombat()) {

				if (npc.getAttackedBy() instanceof NPC) {
					npc.setAttackedBy(player);
				} else {
					player.getPackets().sendGameMessage("That npc is already in combat.");
					return false;
				}
			}
		}

		return true;
	}

	private boolean isDigForm() {
		return DIG_TO_COMBAT.containsKey(getId());
	}

	private boolean isCombatForm() {
		return COMBAT_TO_DIG.containsKey(getId());
	}
}