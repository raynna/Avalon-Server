package com.rs.kotlin.game.npc;

enum class NpcBonusType(val index: Int) {
        AttackLevel(0),
        StrengthLevel(1),
        DefenceLevel(2),
        MagicLevel(3),
        RangeLevel(4),

        StabAttack(5),
        SlashAttack(6),
        CrushAttack(7),
        MagicAttack(8),
        RangeAttack(9),

        StabDefence(10),
        SlashDefence(11),
        CrushDefence(12),
        MagicDefence(13),
        RangeDefence(14),

        StrengthBonus(15),
        AttackBonus(16);
    }