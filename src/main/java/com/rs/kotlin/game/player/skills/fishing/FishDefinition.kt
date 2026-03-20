package com.rs.kotlin.game.player.skills.fishing

import com.rs.kotlin.game.player.tasksystem.Task

enum class FishDefinition(
    val level: Int,
    val xp: Double,
    val itemId: Int,
    val successCurve: SuccessCurve,
    val task: Task? = null,
) {
    SHRIMP(
        level = 1,
        xp = 10.0,
        itemId = 317,
        successCurve = SuccessCurve(low = 48, high = 256), // done
    ),
    SARDINE(
        level = 5,
        xp = 20.0,
        itemId = 327,
        successCurve = SuccessCurve(low = 32, high = 192), // done
    ),
    HERRING(
        level = 10,
        xp = 30.0,
        itemId = 345,
        successCurve = SuccessCurve(low = 24, high = 128), // done
    ),
    ANCHOVIES(
        level = 15,
        xp = 40.0,
        itemId = 321,
        successCurve = SuccessCurve(low = 24, high = 128), // done
    ),
    MACKEREL(
        level = 16,
        xp = 20.0,
        itemId = 353,
        successCurve = SuccessCurve(low = 5, high = 65), // done
    ),
    COD(
        level = 23,
        xp = 45.0,
        itemId = 341,
        successCurve = SuccessCurve(low = 4, high = 55), // done
    ),
    PIKE(
        level = 25,
        xp = 60.0,
        itemId = 349,
        successCurve = SuccessCurve(low = 16, high = 96), // done
    ),
    TROUT(
        level = 20,
        xp = 50.0,
        itemId = 335,
        successCurve = SuccessCurve(low = 32, high = 192), // done
    ),
    SALMON(
        level = 30,
        xp = 70.0,
        itemId = 331,
        successCurve = SuccessCurve(low = 16, high = 96), // done
    ),
    TUNA(
        level = 35,
        xp = 80.0,
        itemId = 359,
        successCurve = SuccessCurve(low = 8, high = 64), // done
    ),
    LOBSTER(
        level = 40,
        xp = 90.0,
        itemId = 377,
        successCurve = SuccessCurve(low = 6, high = 95), // done
    ),
    BASS(
        level = 46,
        xp = 100.0,
        itemId = 363,
        successCurve = SuccessCurve(low = 3, high = 40), // done
    ),
    SWORDFISH(
        level = 50,
        xp = 100.0,
        itemId = 371,
        successCurve = SuccessCurve(low = 4, high = 57), // done
    ),
    MONKFISH(
        level = 62,
        xp = 120.0,
        itemId = 7944,
        successCurve = SuccessCurve(low = 48, high = 90), // done
    ),
    SHARK(
        level = 76,
        xp = 110.0,
        itemId = 383,
        successCurve = SuccessCurve(low = 3, high = 40), // done
        task = Task.FISH_SHARK,
    ),
    CAVEFISH(
        level = 85,
        xp = 300.0,
        itemId = 15264,
        successCurve = SuccessCurve(low = 3, high = 40), // done
    ),
    ROCKTAIL(
        level = 90,
        xp = 385.0,
        itemId = 15270,
        successCurve = SuccessCurve(low = 2, high = 40), // done
    ),

    // Secondary / non-fish catches
    SEAWEED(
        level = 16,
        xp = 1.0,
        itemId = 401,
        successCurve = SuccessCurve(low = 16, high = 64),
    ),
    OYSTER(
        level = 16,
        xp = 10.0,
        itemId = 407,
        successCurve = SuccessCurve(low = 16, high = 64),
    ),
    CRAYFISH(
        level = 1,
        xp = 10.0,
        itemId = 13435,
        successCurve = SuccessCurve(low = 48, high = 256),
    ),
}
