package com.rs.kotlin.game.player.combat

import com.rs.java.game.Entity
import com.rs.java.game.WorldTile

object EntityUtils {

    fun WorldTile.distanceSquared(other: WorldTile): Int {
        val dx = x - other.x
        val dy = y - other.y
        return dx * dx + dy * dy
    }
    fun collides(entity: Entity, target: Entity): Boolean {
        return entity.plane == target.plane && 
               colides(entity.x, entity.y, entity.size,
                       target.x, target.y, target.size)
    }

    private fun colides(x1: Int, y1: Int, size1: Int,
                        x2: Int, y2: Int, size2: Int): Boolean {
        val entityLeft = x1
        val entityRight = x1 + size1
        val entityBottom = y1
        val entityTop = y1 + size1
        
        val targetLeft = x2
        val targetRight = x2 + size2
        val targetBottom = y2
        val targetTop = y2 + size2
        
        //under target
        return entityLeft < targetRight && 
               entityRight > targetLeft && 
               entityBottom < targetTop && 
               entityTop > targetBottom
    }
}