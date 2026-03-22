package raynna.game.player.combat.melee

import raynna.game.player.combat.*
import raynna.game.player.combat.special.SpecialAttack
import raynna.game.player.combat.special.SpecialEffect

data class MeleeWeapon(
    override val itemId: List<Int>,
    override val name: String,
    override val weaponStyle: WeaponStyle,
    override val attackSpeed: Int? = -1,
    override val soundId: Int? = -1,
    override val attackRange: Int? = null,
    override val attackDelay: Int? = null,
    override val animationId: Int? = null,
    override val blockAnimationId: Int? = null,
    override val special: SpecialAttack? = null,
    override val effect: SpecialEffect? = null,
    val animations: Map<StyleKey, Int> = emptyMap(),
    val sounds: Map<StyleKey, Int> = emptyMap()
) : Weapon

