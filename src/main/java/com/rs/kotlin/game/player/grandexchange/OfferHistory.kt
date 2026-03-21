package com.rs.kotlin.game.player.grandexchange

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.time.Instant

data class OfferHistory(
    @SerializedName("id") val id: Int,
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("price") val price: Int,
    @SerializedName("bought") val isBought: Boolean,
    @SerializedName("time") val timestamp: Long = Instant.now().epochSecond,
) : Serializable {
    val totalValue: Long get() = quantity.toLong() * price.toLong()

    companion object {
        private const val serialVersionUID = 7322642705393018764L
    }
}
