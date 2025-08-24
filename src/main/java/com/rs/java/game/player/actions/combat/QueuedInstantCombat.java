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
        if (special instanceof SpecialAttack.InstantCombat) {
            ((SpecialAttack.InstantCombat) special).getExecute().invoke(context);
        } else if (special instanceof SpecialAttack.InstantRangeCombat) {
            ((SpecialAttack.InstantRangeCombat) special).getExecute().invoke(context);
        } else if (special instanceof SpecialAttack.Combat) {
            ((SpecialAttack.Combat) special).getExecute().invoke(context);
        } else {
            throw new IllegalStateException("Unsupported special type for QueuedInstantCombat");
        }
    }
}
