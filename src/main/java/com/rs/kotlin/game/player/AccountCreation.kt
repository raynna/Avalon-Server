package com.rs.kotlin.game.player

import com.rs.java.game.player.Player
import com.rs.java.utils.Utils
import com.rs.json.GSONParser
import java.io.File

class AccountCreation {

    companion object {

        private val userDir: String =
            System.getProperty("user.dir") + "/data/characters/"

        private fun normalize(username: String): String {
            return username.trim().lowercase()
        }

        @JvmStatic
        fun init() {
            // make sure to create a /data/characters/ folder if it doesn't exist.
            val dir = File(userDir)
            if (!dir.exists()) {
                val created = dir.mkdirs()
                println("Characters directory created: $created")
            }
        }

        @JvmStatic
        fun loadPlayer(username: String): Player? {
            val clean = normalize(username)
            val path = "$userDir$clean.json"
            val file = File(path)

            if (!file.exists() || !file.isFile) {
                println("Player not found: $username")
                return null
            }

            return GSONParser.load(path, Player::class.java)
        }

        @JvmStatic
        fun rename(oldName: String, newName: String) {
            val oldFile = File("$userDir${Utils.formatPlayerNameForProtocol(oldName)}.json")
            val newFile = File("$userDir${Utils.formatPlayerNameForProtocol(newName)}.json")

            if (oldFile.exists()) {
                oldFile.renameTo(newFile)
            }
        }


        @JvmStatic
        fun savePlayer(player: Player) {
            val filename = Utils.formatPlayerNameForProtocol(player.username)

            GSONParser.save(
                player,
                "$userDir$filename.json",
                Player::class.java
            )
        }

        @JvmStatic
        fun exists(username: String): Boolean {
            val clean = Utils.formatPlayerNameForProtocol(username)
            val path = "$userDir$clean.json"
            val file = File(path)

            if (!file.exists() || !file.isFile) {
                println("Player: '$username' doesn't exist")
                return false
            }
            return true
        }
    }
}