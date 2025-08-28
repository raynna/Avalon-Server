package com.rs.discord

import com.rs.Settings

object DiscordRoutes {
    var eventsUrl: String? = null
    var levelupsUrl: String? = null

    fun init() {
        eventsUrl = Settings.eventsWebhook
        levelupsUrl = Settings.levelupsWebhook
    }

    fun toEvents(payload: WebhookPayload) {
        DiscordWebhook.webhookUrl = eventsUrl
        DiscordWebhook.enqueue(payload)
    }

    fun toLevelUps(payload: WebhookPayload) {
        DiscordWebhook.webhookUrl = levelupsUrl
        DiscordWebhook.enqueue(payload)
    }
}