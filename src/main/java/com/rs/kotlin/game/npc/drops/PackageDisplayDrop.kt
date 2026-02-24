package com.rs.kotlin.game.npc.drops

import com.rs.kotlin.Rscm

data class PackageDisplayDrop(
    val itemId: Int,
    val amount: IntRange = 1..1,
    val metadata: DropMetadata = DropMetadata(),
)

fun PackageDisplayDrop(
    key: String,
    amount: IntRange = 1..1,
    metadata: DropMetadata = DropMetadata(),
) = PackageDisplayDrop(
    itemId = Rscm.lookup(key),
    amount = amount,
    metadata = metadata,
)
