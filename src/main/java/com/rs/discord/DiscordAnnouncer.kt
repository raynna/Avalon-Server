package com.rs.discord

import com.rs.java.game.player.Player
import java.time.Instant

object DiscordAnnouncer {

    @JvmStatic
    fun announce(title: String, message: String) {
        announce(title, message, "", 0x57F287)
    }

    @JvmStatic
    fun announce(title: String, message: String, footer: String = "", color: Int = 0x57F287) {
        DiscordWebhook.enqueue(
            WebhookPayload(
                embeds = listOf(
                    Embed(
                        title = title,
                        description = message,
                        color = color,
                        timestamp = Instant.now().toString(),
                        footer = EmbedFooter(footer)
                    )
                )
            )
        )
    }
    @JvmStatic
    fun announceLevelUp(player: String, skill: String, newLevel: Int) {
        DiscordWebhook.enqueue(
            WebhookPayload(
                embeds = listOf(
                    Embed(
                        title = "🎉 Level Up!",
                        description = "**$player** reached level **$newLevel** in **$skill**",
                        color = 0x57F287, // green-ish
                        timestamp = Instant.now().toString(),
                        footer = EmbedFooter("GZ! Keep grinding 🚀")
                    )
                )
            )
        )
    }

    @JvmStatic
    fun announceKillstreak(player: String, killstreak: Int) {
        val playerName = player ?: "Unknown"
        DiscordWebhook.enqueue(
            WebhookPayload(
                embeds = listOf(
                    Embed(
                        title = "Killstreak!",
                        description = "**$playerName** has a total of **$killstreak** killstreak!",
                        color = 0x57F287,
                        timestamp = Instant.now().toString(),
                        footer = EmbedFooter("Will someone ever stop him/her?")
                    )
                )
            )
        )
    }

    @JvmStatic
    fun announceKillstreakEnded(killedPlayer: String, killer: String, killstreak: Int) {
        val killedName = killedPlayer ?: "Unknown"
        val killerName = killer ?: "Unknown"
        DiscordWebhook.enqueue(
            WebhookPayload(
                embeds = listOf(
                    Embed(
                        title = "Killstreak Ended!",
                        description = "**$killerName** has just ended **$killedName's** killstreak of **$killstreak**!",
                        color = 0x57F287,
                        timestamp = Instant.now().toString(),
                        footer = EmbedFooter("Good game")
                    )
                )
            )
        )
    }

    @JvmStatic
    fun announceGlobalEvent(name: String, description: String, minutesUntilStart: Int? = null) {
        val whenTxt = minutesUntilStart?.let { "Starts in **$it** minutes." } ?: ""
        DiscordWebhook.enqueue(
            WebhookPayload(
                embeds = listOf(
                    Embed(
                        title = "📢 Server Event: $name",
                        description = "$description\n$whenTxt",
                        color = 0x5865F2, // blurple
                        timestamp = Instant.now().toString()
                    )
                )
            )
        )
    }

    @JvmStatic
    fun announceDrop(player: String, item: String, qty: Int, npc: String, rarityText: String? = null) {
        val rarity = rarityText?.let { " *( $it )*" } ?: ""
        DiscordWebhook.enqueue(
            WebhookPayload(
                embeds = listOf(
                    Embed(
                        title = "💎 Rare Drop!",
                        description = "**$player** received **$qty × $item** from **$npc**$rarity",
                        color = 0xFEE75C,
                        timestamp = Instant.now().toString()
                    )
                )
            )
        )
    }
}
