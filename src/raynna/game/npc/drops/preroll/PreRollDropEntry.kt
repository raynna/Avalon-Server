package raynna.game.npc.drops.preroll

import raynna.game.npc.drops.Drop
import raynna.game.npc.drops.DropContext
import raynna.game.npc.drops.DropEntry
import raynna.game.npc.drops.DropMetadata
import raynna.game.npc.drops.DropSource
import java.util.concurrent.ThreadLocalRandom

class PreRollDropEntry(
    itemId: Int?,
    amount: IntRange,
    val numerator: Int,
    var denominator: Int,
    private val condition: ((DropContext) -> Boolean)? = null,
    private val dynamicItem: ((DropContext) -> Int?)? = null,
    val displayItems: List<Int>? = null,
    metadata: DropMetadata = DropMetadata(),
) : DropEntry(
        itemId ?: -1,
        amount,
        always = false,
        condition = null, // handled here instead
        metadata = metadata,
    ) {
    init {
        require(numerator in 1..denominator) {
            "Invalid preroll rate: $numerator/$denominator"
        }
    }

    fun roll(
        context: DropContext,
        multiplier: Double,
    ): Drop? {
        if (condition?.invoke(context) == false) {
            return null
        }

        val effectiveDenominator =
            (denominator / multiplier)
                .toInt()
                .coerceAtLeast(1)

        val roll = ThreadLocalRandom.current().nextInt(effectiveDenominator)

        if (roll >= numerator) {
            return null
        }

        val finalItemId =
            dynamicItem?.invoke(context)
                ?: itemId.takeIf { it != -1 }
                ?: return null

        val prerollContext =
            context.copy(dropSource = DropSource.PREROLL)

        return Drop(
            itemId = finalItemId,
            amount = rollAmount(),
            context = prerollContext,
            isAlways = false,
            metadata = metadata,
        )
    }
}
