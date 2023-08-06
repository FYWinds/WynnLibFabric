package io.github.nbcss.wynnlib.abilities.properties.general

import com.google.gson.JsonElement
import io.github.nbcss.wynnlib.abilities.Ability
import io.github.nbcss.wynnlib.abilities.PlaceholderContainer
import io.github.nbcss.wynnlib.abilities.builder.entries.PropertyEntry
import io.github.nbcss.wynnlib.abilities.properties.AbilityProperty
import io.github.nbcss.wynnlib.abilities.properties.SetupProperty
import io.github.nbcss.wynnlib.data.Identification

class IDModifierProperty(
    ability: Ability,
    pairs: List<Pair<String, Int>>
) :
    AbilityProperty(ability), SetupProperty {
    companion object : Type<IDModifierProperty> {
        override fun create(ability: Ability, data: JsonElement): IDModifierProperty {
            val json = data.asJsonObject
            val values: MutableList<Pair<String, Int>> = mutableListOf()
            for (entry in json.entrySet()) {
                val value = entry.value.asInt
                if (value != 0)
                    values.add(entry.key to value)
            }
            return IDModifierProperty(ability, values)
        }

        override fun getKey(): String = "id_modifier"
    }

    private val modifiers: Map<String, Int> = mapOf(pairs = pairs.toTypedArray())

    fun getModifiers(): Map<Identification, Int> = mapOf(pairs = modifiers.mapNotNull {
        Identification.fromName(it.key)?.to(it.value)
    }.toTypedArray())

    override fun writePlaceholder(container: PlaceholderContainer) {
        for (entry in modifiers.entries) {
            container.putPlaceholder("id_modifier.${entry.key}", entry.value.toString())
        }
    }

    override fun setup(entry: PropertyEntry) {
        entry.setProperty(getKey(), this)
    }
}