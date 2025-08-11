package com.rs.kotlin.game.player.equipment;

enum class BonusType(val index: Int, val clientScriptId: Int) {
        StabAttack(0, 0),
        SlashAttack(1, 1),
        CrushAttack(2, 2),
        MagicAttack(3, 3),
        RangeAttack(4, 4),
        StabDefence(5, 5),
        SlashDefence(6, 6),
        CrushDefence(7, 7),
        MagicDefence(8, 8),
        RangeDefence(9, 9),
        SummoningDefence(10, 417),
        AbsorbMelee(11, 967),
        AbsorbMage(12, 969),
        AbsorbRange(13, 968),
        StregthBonus(14, 641),
        RangedStrBonus(15, 643),
        PrayerBonus(16, 11),
        MagicDamage(17, 685);
    }