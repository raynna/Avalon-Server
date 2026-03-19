package com.rs.kotlin.game.world.task

internal interface CoroutineWaitCondition<T> {
    fun tick(): Boolean

    fun value(): T
}
