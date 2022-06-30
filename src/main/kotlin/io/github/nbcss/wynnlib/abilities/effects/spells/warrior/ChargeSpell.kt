package io.github.nbcss.wynnlib.abilities.effects.spells.warrior

import com.google.gson.JsonObject
import io.github.nbcss.wynnlib.abilities.display.*
import io.github.nbcss.wynnlib.abilities.effects.AbilityEffect
import io.github.nbcss.wynnlib.abilities.effects.spells.SpellUnlock
import io.github.nbcss.wynnlib.abilities.properties.DamageProperty

class ChargeSpell(json: JsonObject): SpellUnlock(json), DamageProperty {
    companion object: AbilityEffect.Factory {
        override fun create(properties: JsonObject): ChargeSpell {
            return ChargeSpell(properties)
        }
    }
    private val damage: DamageProperty.Damage = DamageProperty.read(json)

    override fun getDamage(): DamageProperty.Damage = damage

    override fun getTooltipItems(): List<EffectTooltip> {
        return listOf(ManaCostTooltip, DamageTooltip)
    }
}