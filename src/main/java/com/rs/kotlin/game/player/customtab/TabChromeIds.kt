// core/dsl/CustomTab.kt
package com.rs.kotlin.game.player.customtab

import com.rs.java.game.player.Player

/**
 * External component IDs (like your star/back/forward) live in one place.
 * You can keep these configurable per-interface layout if you have variants.
 */
data class TabChromeIds(
    val ifaceId: Int,
    val firstSlot: Int = 3,
    val lastSlot: Int = 22,
    val backButton: Int = 58,
    val forwardButton: Int = 27,
    val blueStar: Int = 62,
    val greenStar: Int = 61,
    val redStar: Int = 60,
    val purpleStar: Int = 59,
    val yellowStar: Int = 26,
    val blueHighlighted: Int = 12184,
    val greenHighlighted: Int = 12182,
    val redHighlighted: Int = 12186,
    val purpleHighlighted: Int = 12185,
    val yellowHighlighted: Int = 12187,
)

/**
 * Declarative model for a tab page.
 * Each entry gets assigned a component id automatically from available slots.
 */

class TabPage(
    val key: String,
    val title: String? = null,
    val chrome: TabChromeIds,
    val items: MutableList<TabItem> = mutableListOf(),
    var onBack: ((Player) -> Unit)? = null,
    var onForward: ((Player) -> Unit)? = null,
    // NEW: let pages paint/hide chrome as they wish before items render
    var beforeOpen: ((Player) -> Unit)? = null,
    // NEW: allow raw handlers for arbitrary component IDs (e.g., star buttons)
    val extraHandlers: MutableMap<Int, (Player) -> Unit> = mutableMapOf()
) {
    internal val idMap: MutableMap<TabItem, Int> = mutableMapOf()
}

/** UI primitives **/
sealed interface TabItem {
    /** Optional dynamic text recomputation when reopened */
    fun text(p: Player): String? = null
    /** Click behavior */
    fun click(p: Player) {}
}

/** Section header with underline */
class Section(private val label: String) : TabItem {
    override fun text(p: Player) = "<u>$label"
}

/** Static label (non-clickable) */
class Label(private val supplier: (Player) -> String) : TabItem {
    override fun text(p: Player) = supplier(p)
}

/** Clickable action item with dynamic text */
class Action(
    private val supplier: (Player) -> String,
    private val onClick: (Player) -> Unit
) : TabItem {
    override fun text(p: Player) = supplier(p)
    override fun click(p: Player) = onClick(p)
}

/** Spacer (hidden or blank line). We can just hide it client-side */
object Spacer : TabItem {
    override fun text(p: Player) = null
}

/** DSL builders */
fun tabPage(key: String, chrome: TabChromeIds, title: String? = null, block: TabPage.() -> Unit): TabPage {
    val page = TabPage(key, title, chrome)
    page.block()
    return page
}

fun TabPage.onBeforeOpen(block: (Player) -> Unit) { this.beforeOpen = block }
fun TabPage.handle(compId: Int, block: (Player) -> Unit) { extraHandlers[compId] = block }
fun TabPage.section(text: String) = items.add(Section(text))
fun TabPage.label(block: (Player) -> String) = items.add(Label(block))
fun TabPage.action(text: (Player) -> String, onClick: (Player) -> Unit) = items.add(Action(text, onClick))
fun TabPage.spacer() = items.add(Spacer)

/** Registry so button routing is centralized */
object TabRegistry {
    private val pages = mutableMapOf<String, TabPage>()
    fun register(page: TabPage) { pages[page.key] = page }
    fun page(key: String) = pages[key]
}
