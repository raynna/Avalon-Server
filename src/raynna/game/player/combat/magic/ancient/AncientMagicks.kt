package raynna.game.player.combat.magic.ancient

import raynna.app.Settings
import raynna.game.Graphics
import raynna.game.WorldTile
import raynna.game.player.combat.magic.ElementType
import raynna.game.player.combat.magic.RuneDefinitions
import raynna.game.player.combat.magic.RuneRequirement
import raynna.game.player.combat.magic.Spell
import raynna.game.player.combat.magic.SpellType
import raynna.game.player.combat.magic.Spellbook
import raynna.game.player.combat.magic.StaffRequirement
import raynna.game.world.projectile.Projectile
import raynna.data.rscm.Rscm

object AncientMagicks : Spellbook(ANCIENT_ID) {
    override val spells =
        listOf(
            Spell(
                id = 48,
                name = "Home Teleport",
                level = 0,
                xp = 0.0,
                type = SpellType.Teleport,
                runes = emptyList(),
                teleportLocation = Settings.HOME_PLAYER_LOCATION,
            ),
            Spell(
                id = 40,
                name = "Paddewwa Teleport",
                level = 54,
                xp = 64.0,
                type = SpellType.Teleport,
                runes =
                    listOf(
                        RuneRequirement(RuneDefinitions.Runes.LAW, 2),
                        RuneRequirement(RuneDefinitions.Runes.FIRE, 1),
                        RuneRequirement(RuneDefinitions.Runes.AIR, 1),
                    ),
                teleportLocation = WorldTile(3099, 9882, 0),
            ),
            Spell(
                id = 41,
                name = "Senntisten Teleport",
                level = 60,
                xp = 70.0,
                type = SpellType.Teleport,
                runes =
                    listOf(
                        RuneRequirement(RuneDefinitions.Runes.LAW, 2),
                        RuneRequirement(RuneDefinitions.Runes.SOUL, 1),
                    ),
                teleportLocation = WorldTile(3222, 3336, 0),
            ),
            Spell(
                id = 42,
                name = "Kharyrll Teleport",
                level = 66,
                xp = 76.0,
                type = SpellType.Teleport,
                runes =
                    listOf(
                        RuneRequirement(RuneDefinitions.Runes.LAW, 2),
                        RuneRequirement(RuneDefinitions.Runes.BLOOD, 1),
                    ),
                teleportLocation = WorldTile(3492, 3471, 0),
            ),
            Spell(
                id = 43,
                name = "Lassar Teleport",
                level = 72,
                xp = 82.0,
                type = SpellType.Teleport,
                runes =
                    listOf(
                        RuneRequirement(RuneDefinitions.Runes.LAW, 2),
                        RuneRequirement(RuneDefinitions.Runes.WATER, 4),
                    ),
                teleportLocation = WorldTile(3006, 3471, 0),
            ),
            Spell(
                id = 44,
                name = "Dareeyak Teleport",
                level = 78,
                xp = 88.0,
                type = SpellType.Teleport,
                runes =
                    listOf(
                        RuneRequirement(RuneDefinitions.Runes.LAW, 2),
                        RuneRequirement(RuneDefinitions.Runes.FIRE, 3),
                        RuneRequirement(RuneDefinitions.Runes.AIR, 2),
                    ),
                teleportLocation = WorldTile(2990, 3696, 0),
            ),
            Spell(
                id = 45,
                name = "Carrallangar Teleport",
                level = 84,
                xp = 94.0,
                type = SpellType.Teleport,
                runes =
                    listOf(
                        RuneRequirement(RuneDefinitions.Runes.LAW, 2),
                        RuneRequirement(RuneDefinitions.Runes.SOUL, 2),
                    ),
                teleportLocation = WorldTile(3217, 3677, 0),
            ),
            Spell(
                id = 46,
                name = "Annakarl Teleport",
                level = 90,
                xp = 100.0,
                type = SpellType.Teleport,
                runes =
                    listOf(
                        RuneRequirement(RuneDefinitions.Runes.LAW, 2),
                        RuneRequirement(RuneDefinitions.Runes.BLOOD, 2),
                    ),
                teleportLocation = WorldTile(3288, 3886, 0),
            ),
            Spell(
                id = 47,
                name = "Ghorrock Teleport",
                level = 96,
                xp = 106.0,
                type = SpellType.Teleport,
                runes =
                    listOf(
                        RuneRequirement(RuneDefinitions.Runes.LAW, 2),
                        RuneRequirement(RuneDefinitions.Runes.BLOOD, 2),
                    ),
                teleportLocation = WorldTile(2977, 3873, 0),
            ),
            Spell(
                id = 28,
                name = "Smoke Rush",
                level = 50,
                xp = 30.0,
                damage = 150,
                attackSound = 183,
                hitSound = 185,
                type = SpellType.Combat,
                runes =
                    listOf(
                        RuneRequirement(RuneDefinitions.Runes.CHAOS, 2),
                        RuneRequirement(RuneDefinitions.Runes.DEATH, 2),
                        RuneRequirement(RuneDefinitions.Runes.FIRE, 1, true),
                        RuneRequirement(RuneDefinitions.Runes.AIR, 1, true),
                    ),
                animationId = 1978,
                projectileId = 384,
                projectileType = Projectile.ANCIENT_SPELL,
                endGraphic = Graphics(385),
            ),
            Spell(
                id = 32,
                name = "Shadow Rush",
                level = 52,
                xp = 31.0,
                damage = 160,
                attackSound = 178,
                hitSound = 179,
                type = SpellType.Combat,
                runes =
                    listOf(
                        RuneRequirement(RuneDefinitions.Runes.CHAOS, 2),
                        RuneRequirement(RuneDefinitions.Runes.DEATH, 2),
                        RuneRequirement(RuneDefinitions.Runes.AIR, 1, true),
                        RuneRequirement(RuneDefinitions.Runes.SOUL, 1),
                    ),
                animationId = 1978,
                graphicId = Graphics(379),
                projectileId = 380,
                projectileType = Projectile.ANCIENT_SPELL,
                endGraphic = Graphics(381),
            ),
            Spell(
                id = 24,
                name = "Blood Rush",
                level = 56,
                xp = 33.0,
                damage = 170,
                attackSound = 106,
                hitSound = 110,
                type = SpellType.Combat,
                element = ElementType.Blood,
                runes =
                    listOf(
                        RuneRequirement(RuneDefinitions.Runes.CHAOS, 2),
                        RuneRequirement(RuneDefinitions.Runes.DEATH, 2),
                        RuneRequirement(RuneDefinitions.Runes.BLOOD, 1),
                    ),
                animationId = 1978,
                graphicId = Graphics(373),
                projectileId = 374,
                projectileType = Projectile.BLOOD_SPELL,
                endGraphic = Graphics(375),
            ),
            Spell(
                id = 20,
                name = "Ice Rush",
                level = 58,
                xp = 34.0,
                damage = 180,
                attackSound = 171,
                hitSound = 173,
                type = SpellType.Combat,
                runes =
                    listOf(
                        RuneRequirement(RuneDefinitions.Runes.CHAOS, 2),
                        RuneRequirement(RuneDefinitions.Runes.DEATH, 2),
                        RuneRequirement(RuneDefinitions.Runes.WATER, 2),
                    ),
                animationId = 1978,
                projectileId = 360,
                projectileType = Projectile.ICE_SPELL,
                endGraphic = Graphics(361),
                bind = 8,
            ),
            Spell(
                id = 36,
                name = "Miasmic Rush",
                level = 61,
                xp = 35.0,
                damage = 200,
                type = SpellType.Combat,
                attackSound = 5368,
                hitSound = 5365,
                runes =
                    listOf(
                        RuneRequirement(RuneDefinitions.Runes.CHAOS, 2),
                        RuneRequirement(RuneDefinitions.Runes.EARTH, 1),
                        RuneRequirement(RuneDefinitions.Runes.SOUL, 1),
                    ),
                staff =
                    StaffRequirement(
                        anyOf =
                            listOf(
                                Rscm.item("item.zuriel_s_staff"),
                                Rscm.item("item.zuriel_s_staff_deg"),
                                Rscm.item("item.corrupt_zuriel_s_staff"),
                                Rscm.item("item.corrupt_zuriel_s_staff_deg"),
                            ),
                    ),
                animationId = 10513,
                graphicId = Graphics(1845),
                projectileId = 1846,
                projectileType = Projectile.MIASMIC_SPELL,
                endGraphic = Graphics(1847),
            ),
            Spell(
                id = 30,
                name = "Smoke Burst",
                level = 62,
                xp = 36.0,
                damage = 190,
                attackSound = 183,
                hitSound = 182,
                type = SpellType.Combat,
                runes =
                    listOf(
                        RuneRequirement(RuneDefinitions.Runes.CHAOS, 4),
                        RuneRequirement(RuneDefinitions.Runes.DEATH, 2),
                        RuneRequirement(RuneDefinitions.Runes.FIRE, 2, true),
                        RuneRequirement(RuneDefinitions.Runes.AIR, 2, true),
                    ),
                animationId = 1979,
                projectileType = Projectile.ANCIENT_SPELL,
                endGraphic = Graphics(389),
                multi = true,
            ),
            Spell(
                id = 34,
                name = "Shadow Burst",
                level = 64,
                xp = 37.0,
                damage = 200,
                attackSound = 178,
                hitSound = 177,
                type = SpellType.Combat,
                runes =
                    listOf(
                        RuneRequirement(RuneDefinitions.Runes.CHAOS, 4),
                        RuneRequirement(RuneDefinitions.Runes.DEATH, 2),
                        RuneRequirement(RuneDefinitions.Runes.AIR, 1, true),
                        RuneRequirement(RuneDefinitions.Runes.SOUL, 1),
                    ),
                animationId = 1979,
                endGraphic = Graphics(382),
                projectileType = Projectile.ANCIENT_SPELL,
                multi = true,
            ),
            Spell(
                id = 26,
                name = "Blood Burst",
                level = 68,
                xp = 39.0,
                damage = 210,
                attackSound = 106,
                hitSound = 105,
                element = ElementType.Blood,
                type = SpellType.Combat,
                runes =
                    listOf(
                        RuneRequirement(RuneDefinitions.Runes.CHAOS, 4),
                        RuneRequirement(RuneDefinitions.Runes.DEATH, 2),
                        RuneRequirement(RuneDefinitions.Runes.BLOOD, 2),
                    ),
                animationId = 1979,
                endGraphic = Graphics(377),
                projectileType = Projectile.BLOOD_SPELL,
                multi = true,
            ),
            Spell(
                id = 22,
                name = "Ice Burst",
                level = 70,
                xp = 46.0,
                damage = 220,
                attackSound = 171,
                hitSound = 170,
                type = SpellType.Combat,
                runes =
                    listOf(
                        RuneRequirement(RuneDefinitions.Runes.CHAOS, 4),
                        RuneRequirement(RuneDefinitions.Runes.DEATH, 2),
                        RuneRequirement(RuneDefinitions.Runes.WATER, 4),
                    ),
                animationId = 1979,
                projectileId = 362,
                endGraphic = Graphics(363),
                projectileType = Projectile.ICE_SPELL,
                multi = true,
                bind = 16,
            ),
            Spell(
                id = 38,
                name = "Miasmic Burst",
                level = 73,
                xp = 42.0,
                damage = 240,
                attackSound = 5366,
                hitSound = 5372,
                type = SpellType.Combat,
                runes =
                    listOf(
                        RuneRequirement(RuneDefinitions.Runes.CHAOS, 4),
                        RuneRequirement(RuneDefinitions.Runes.EARTH, 2),
                        RuneRequirement(RuneDefinitions.Runes.SOUL, 2),
                    ),
                staff =
                    StaffRequirement(
                        anyOf =
                            listOf(
                                Rscm.item("item.zuriel_s_staff"),
                                Rscm.item("item.zuriel_s_staff_deg"),
                                Rscm.item("item.corrupt_zuriel_s_staff"),
                                Rscm.item("item.corrupt_zuriel_s_staff_deg"),
                            ),
                    ),
                animationId = 10516,
                graphicId = Graphics(1848),
                endGraphic = Graphics(1849),
                projectileType = Projectile.MIASMIC_SPELL,
                multi = true,
            ),
            Spell(
                id = 29,
                name = "Smoke Blitz",
                level = 74,
                xp = 42.0,
                damage = 230,
                attackSound = 183,
                hitSound = 181,
                type = SpellType.Combat,
                runes =
                    listOf(
                        RuneRequirement(RuneDefinitions.Runes.DEATH, 2),
                        RuneRequirement(RuneDefinitions.Runes.BLOOD, 2),
                        RuneRequirement(RuneDefinitions.Runes.FIRE, 2, true),
                        RuneRequirement(RuneDefinitions.Runes.AIR, 2, true),
                    ),
                animationId = 1978,
                projectileId = 386,
                projectileType = Projectile.ANCIENT_SPELL,
                endGraphic = Graphics(387),
            ),
            Spell(
                id = 33,
                name = "Shadow Blitz",
                level = 76,
                xp = 43.0,
                damage = 240,
                attackSound = 178,
                hitSound = 176,
                type = SpellType.Combat,
                runes =
                    listOf(
                        RuneRequirement(RuneDefinitions.Runes.DEATH, 2),
                        RuneRequirement(RuneDefinitions.Runes.BLOOD, 2),
                        RuneRequirement(RuneDefinitions.Runes.AIR, 2, true),
                        RuneRequirement(RuneDefinitions.Runes.SOUL, 2),
                    ),
                animationId = 1978,
                graphicId = Graphics(379),
                projectileId = 380,
                projectileType = Projectile.ANCIENT_SPELL,
                endGraphic = Graphics(381),
            ),
            Spell(
                id = 25,
                name = "Blood Blitz",
                level = 80,
                xp = 45.0,
                damage = 250,
                attackSound = 103,
                hitSound = 104,
                type = SpellType.Combat,
                element = ElementType.Blood,
                runes =
                    listOf(
                        RuneRequirement(RuneDefinitions.Runes.DEATH, 2),
                        RuneRequirement(RuneDefinitions.Runes.BLOOD, 4),
                    ),
                animationId = 1978,
                graphicId = Graphics(373),
                projectileId = 374,
                projectileType = Projectile.BLOOD_SPELL,
                endGraphic = Graphics(375),
            ),
            Spell(
                id = 21,
                name = "Ice Blitz",
                level = 82,
                xp = 46.0,
                damage = 260,
                attackSound = 171,
                hitSound = 169,
                type = SpellType.Combat,
                runes =
                    listOf(
                        RuneRequirement(RuneDefinitions.Runes.DEATH, 2),
                        RuneRequirement(RuneDefinitions.Runes.BLOOD, 2),
                        RuneRequirement(RuneDefinitions.Runes.WATER, 3),
                    ),
                animationId = 1978,
                graphicId = Graphics(366),
                endGraphic = Graphics(367),
                projectileType = Projectile.ICE_SPELL,
                bind = 24,
            ),
            Spell(
                id = 37,
                name = "Miasmic Blitz",
                level = 85,
                xp = 48.0,
                damage = 280,
                attackSound = 5370,
                hitSound = 5367,
                type = SpellType.Combat,
                runes =
                    listOf(
                        RuneRequirement(RuneDefinitions.Runes.BLOOD, 2),
                        RuneRequirement(RuneDefinitions.Runes.EARTH, 3),
                        RuneRequirement(RuneDefinitions.Runes.SOUL, 3),
                    ),
                staff =
                    StaffRequirement(
                        anyOf =
                            listOf(
                                Rscm.item("item.zuriel_s_staff"),
                                Rscm.item("item.zuriel_s_staff_deg"),
                                Rscm.item("item.corrupt_zuriel_s_staff"),
                                Rscm.item("item.corrupt_zuriel_s_staff_deg"),
                            ),
                    ),
                animationId = 10524,
                graphicId = Graphics(1850),
                projectileId = 1852,
                projectileType = Projectile.MIASMIC_SPELL,
                endGraphic = Graphics(1851),
            ),
            Spell(
                id = 31,
                name = "Smoke Barrage",
                level = 86,
                xp = 48.0,
                damage = 270,
                attackSound = 183,
                hitSound = 180,
                type = SpellType.Combat,
                runes =
                    listOf(
                        RuneRequirement(RuneDefinitions.Runes.DEATH, 4),
                        RuneRequirement(RuneDefinitions.Runes.BLOOD, 2),
                        RuneRequirement(RuneDefinitions.Runes.FIRE, 4, true),
                        RuneRequirement(RuneDefinitions.Runes.AIR, 4, true),
                    ),
                animationId = 1979,
                projectileType = Projectile.ANCIENT_SPELL,
                endGraphic = Graphics(390),
                multi = true,
            ),
            Spell(
                id = 35,
                name = "Shadow Barrage",
                level = 88,
                xp = 49.0,
                damage = 280,
                attackSound = 178,
                hitSound = 175,
                type = SpellType.Combat,
                runes =
                    listOf(
                        RuneRequirement(RuneDefinitions.Runes.DEATH, 4),
                        RuneRequirement(RuneDefinitions.Runes.BLOOD, 2),
                        RuneRequirement(RuneDefinitions.Runes.AIR, 4, true),
                        RuneRequirement(RuneDefinitions.Runes.SOUL, 3),
                    ),
                animationId = 1979,
                projectileType = Projectile.ANCIENT_SPELL,
                endGraphic = Graphics(383),
                multi = true,
            ),
            Spell(
                id = 27,
                name = "Blood Barrage",
                level = 92,
                xp = 51.0,
                damage = 290,
                type = SpellType.Combat,
                element = ElementType.Blood,
                attackSound = 106,
                hitSound = 102,
                runes =
                    listOf(
                        RuneRequirement(RuneDefinitions.Runes.DEATH, 4),
                        RuneRequirement(RuneDefinitions.Runes.BLOOD, 4),
                        RuneRequirement(RuneDefinitions.Runes.SOUL, 1),
                    ),
                animationId = 1979,
                projectileType = Projectile.BLOOD_SPELL,
                endGraphic = Graphics(377),
                multi = true,
            ),
            Spell(
                id = 23,
                name = "Ice Barrage",
                level = 94,
                xp = 52.0,
                damage = 300,
                attackSound = 171,
                hitSound = 168,
                type = SpellType.Combat,
                runes =
                    listOf(
                        RuneRequirement(RuneDefinitions.Runes.DEATH, 4),
                        RuneRequirement(RuneDefinitions.Runes.BLOOD, 2),
                        RuneRequirement(RuneDefinitions.Runes.WATER, 6),
                    ),
                animationId = 1979,
                projectileType = Projectile.ICE_SPELL,
                endGraphic = Graphics(369),
                multi = true,
                bind = 32,
            ),
            Spell(
                id = 39,
                name = "Miasmic Barrage",
                level = 85,
                xp = 54.0,
                damage = 320,
                attackSound = 5371,
                hitSound = 5369,
                type = SpellType.Combat,
                runes =
                    listOf(
                        RuneRequirement(RuneDefinitions.Runes.BLOOD, 4),
                        RuneRequirement(RuneDefinitions.Runes.EARTH, 4),
                        RuneRequirement(RuneDefinitions.Runes.SOUL, 4),
                    ),
                staff =
                    StaffRequirement(
                        anyOf =
                            listOf(
                                Rscm.item("item.zuriel_s_staff"),
                                Rscm.item("item.zuriel_s_staff_deg"),
                                Rscm.item("item.corrupt_zuriel_s_staff"),
                                Rscm.item("item.corrupt_zuriel_s_staff_deg"),
                            ),
                    ),
                animationId = 10518,
                projectileType = Projectile.MIASMIC_SPELL,
                graphicId = Graphics(1853),
                endGraphic = Graphics(1854),
                multi = true,
            ),
        )
}
