package com.rs.kotlin.game.npc.drops

sealed class DropTableSource {
    data class Npc(val id: Int) : DropTableSource()
    data class Named(val key: String) : DropTableSource()
    data class Item(val id: Int) : DropTableSource()
    data class Object(val id: Int) : DropTableSource()
}
