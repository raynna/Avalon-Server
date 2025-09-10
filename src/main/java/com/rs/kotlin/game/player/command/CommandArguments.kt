package com.rs.kotlin.game.player.command

class CommandArguments(private val args: List<String>) {

    operator fun get(index: Int): String? = args.getOrNull(index)

    private val namedArgs: Map<String, String> by lazy {
        args.mapNotNull { arg ->
            arg.split("=").takeIf { it.size == 2 }?.let { it[0].lowercase() to it[1] }
        }.toMap()
    }

    fun getInt(index: Int, default: Int = 0): Int =
        args.getOrNull(index)?.let {
            if (it.startsWith("+")) it.drop(1).toIntOrNull() else it.toIntOrNull()
        } ?: default

    fun getJoinedString(fromIndex: Int = 0, separator: String = " "): String =
        if (fromIndex in args.indices) args.drop(fromIndex).joinToString(separator) else ""

    fun getString(index: Int, default: String = ""): String =
        args.getOrNull(index) ?: default

    operator fun get(key: String): String? = namedArgs[key.lowercase()]

    fun getInt(key: String, default: Int = 0): Int = get(key)?.toIntOrNull() ?: default

    fun first(): String? = args.firstOrNull()

    fun last(): String? = args.lastOrNull()
}
