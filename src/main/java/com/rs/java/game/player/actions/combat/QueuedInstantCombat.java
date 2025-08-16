package com.rs.java.game.player.actions.combat;// QueuedInstantCombat.java
import com.rs.java.game.Entity;
import com.rs.kotlin.game.player.combat.special.SpecialAttack;
import com.rs.kotlin.game.player.combat.special.CombatContext;

public class QueuedInstantCombat {
    public final CombatContext context;
    public final SpecialAttack.InstantCombat special;

    public QueuedInstantCombat(CombatContext context, SpecialAttack.InstantCombat special) {
        this.context = context;
        this.special = special;
    }
}
