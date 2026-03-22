package raynna.game.player.shop

/**
 * Parses k/m price shorthand strings into integer values.
 *
 * ### Supported formats
 * ```
 * "3m"        → 3_000_000
 * "75m"       → 75_000_000
 * "3.5m"      → 3_500_000
 * "100k"      → 100_000
 * "3300k"     → 3_300_000
 * "1_000_000" → 1_000_000
 * "50000"     → 50_000
 * ```
 */
object ShopPriceParser {
    fun parsePrice(raw: String): Int? {
        val cleaned = raw.replace("_", "").lowercase()
        return when {
            cleaned.endsWith("m") -> cleaned.dropLast(1).toDoubleOrNull()?.let { (it * 1_000_000).toInt() }
            cleaned.endsWith("k") -> cleaned.dropLast(1).toDoubleOrNull()?.let { (it * 1_000).toInt() }
            else -> cleaned.toIntOrNull()
        }
    }
}
