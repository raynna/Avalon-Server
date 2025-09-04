package com.rs.kotlin.game.world.area

import com.rs.java.game.Entity
import com.rs.java.game.WorldObject
import com.rs.java.game.WorldTile
import com.rs.java.game.item.Item
import com.rs.java.game.player.Player
import com.rs.kotlin.game.player.combat.magic.SpellType

abstract class Area {

    private val shapes: List<Shape>
    private val regionIds: List<Int>
    @Transient private val players: MutableList<Player> = mutableListOf()

    /** Construct an area with shapes only */
    constructor(vararg shapes: Shape) {
        this.shapes = shapes.toList()
        this.regionIds = emptyList()
    }

    /** Construct an area with regions only */
    constructor(vararg regionIds: Int) {
        this.shapes = emptyList()
        this.regionIds = regionIds.toList()
    }

    /** Construct an area with both regions and shapes */
    constructor(regionIds: Array<Int>, vararg shapes: Shape) {
        this.shapes = shapes.toList()
        this.regionIds = regionIds.toList()
    }

    abstract fun update(): Area
    abstract fun name(): String
    abstract fun member(): Boolean
    abstract fun environment(): Environment
    open fun onMoved(player: Player) {}
    open fun onEnter(player: Player) {}
    open fun onExit(player: Player) {}
    open fun onObjectClick(player: Player, obj: WorldObject): Boolean = false
    open fun onObjectClick2(player: Player, obj: WorldObject): Boolean = false
    open fun onItemClick(player: Player, item: Item): Boolean = false
    open fun onItemClick2(player: Player, item: Item): Boolean = false
    open fun onNPCKill(player: Player, npcId: Int) {}
    open fun onPlayerKill(player: Player, target: Entity) {}
    open fun onTick(player: Player) {}
    open fun onDeath(player: Player): Boolean = false
    open fun onTeleport(player: Player) {}

    fun shapes(): List<Shape> = shapes
    fun regions(): List<Int> = regionIds
    fun players(): MutableList<Player> = players

    /** Supports shapes AND regions */
    fun contains(tile: WorldTile): Boolean {
        // Region check first (fast O(1))
        if (regionIds.isNotEmpty() && regionIds.contains(tile.regionId)) {
            return true
        }
        // Shape check
        return shapes.any { it.inside(tile) }
    }

    enum class Environment {
        NORMAL, DESERT, SAFEZONE, MULTI, WILDERNESS, WILDERNESS_SAFE, MINIGAME, BOSS
    }
}
