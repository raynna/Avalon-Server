package com.rs.java.game.player.actions.combat;

import com.rs.kotlin.game.player.combat.special.SpecialAttack;
import com.rs.kotlin.game.player.combat.special.CombatContext;

public class QueuedInstantCombat<T extends SpecialAttack> {
    public final CombatContext context;
    public final T special;

    public QueuedInstantCombat(CombatContext context, T special) {
        this.context = context;
        this.special = special;
    }

    public void execute() {

        switch (special) {
            case SpecialAttack.Instant instant -> instant.getExecute();
            case SpecialAttack.InstantCombat instantCombat -> instantCombat.getExecute().invoke(context);
            case SpecialAttack.InstantRangeCombat instantRangeCombat -> instantRangeCombat.getExecute().invoke(context);
            case SpecialAttack.Combat combat -> combat.getExecute().invoke(context);
            case null, default -> throw new IllegalStateException("Unsupported special type for QueuedInstantCombat");
        }
    }
}
