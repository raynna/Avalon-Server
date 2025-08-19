package com.rs.kotlin.game.player.command

import com.everythingrs.net.packet.impl.Yell
import com.rs.java.game.player.Player
import com.rs.kotlin.game.player.command.commands.*

object CommandRegistry {
    private val commands = mutableMapOf<String, Command>()

    private val primaryNames = mutableMapOf<Command, String>()

    @JvmStatic
    fun registerCommands() {
        register("item", "spawn", command = ItemCommand())
        register("commandlist", "command", command = CommandsCommand())
        register("spellbook", "spells", "book", "switchbook", command = SpellBookCommand())
        register("prayers", "switchprayers", "switchpray", command = PrayerCommand())
        register("curses", "curse", "ancientcurses", command = AncientCursesCommand())
        register("normal", "normals", "regulars", "normalprayers", "regularprayers", command = NormalPrayersCommand())
        register("moderns", "modern", command = ModernsCommand())
        register("ancients", "ancient", command = AncientsCommand())
        register("lunars", "lunar", command = LunarsCommand())
        register("setlevel", "level", command = SetLevelCommand())
        register("master", "max", command = MasterCommand())
        register("special", "spec", command = SpecCommand())
        register("heal", "healme", command = HealCommand())
        register("void", "voidknight", "voids", command = VoidCommand())
        register("dharoks", "dharok", "dh", command = DharoksCommand())
        register("ahrims", "ahrim", command = AhrimsCommand())
        register("runepouch", "rp", "pouch", command = RunePouchCommand())
        register("yell", "worldmessage", command = YellCommand())
        val teleportCommand = TeleportCommand()
        register(*teleportCommand.getAllTriggers().toTypedArray(), command = teleportCommand)
        register("location", "locations", "teleports", command = LocationsCommand(teleportCommand))
    }

    @JvmStatic
    fun register(vararg names: String, command: Command) {
        if (names.isEmpty()) return

        val primary = names[0].lowercase()
        primaryNames[command] = primary

        for (name in names) {
            commands[name.lowercase()] = command
        }
    }

    @JvmStatic
    fun execute(player: Player, input: String): Boolean {
        val parts = input.trim().split("\\s+".toRegex())
        if (parts.isEmpty()) return false

        val name = parts[0].lowercase()        // the trigger used
        val args = parts.drop(1)                // everything after the trigger

        val command = commands[name]
        if (command == null) {
            player.message("Unknown command: $name")
            return false
        }

        if (!player.rank.isAtLeast(command.requiredRank)) {
            player.message("You don't have permission to use this command.")
            return true
        }

        return command.execute(player, args, name)
    }

    fun getAllPrimary(): Map<String, Command> {
        return primaryNames.entries.associate { (command, primaryName) ->
            primaryName to command
        }
    }

    fun getAll(): Map<String, Command> = commands
}
