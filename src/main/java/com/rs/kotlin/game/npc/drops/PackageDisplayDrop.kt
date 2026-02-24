package com.rs.kotlin.game.npc.drops

data class PackageDisplayDrop(
    val itemId: Int,
    val amount: IntRange,
    val metadata: DropMetadata = DropMetadata(),
)
