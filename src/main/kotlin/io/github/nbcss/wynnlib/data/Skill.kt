package io.github.nbcss.wynnlib.data

import io.github.nbcss.wynnlib.i18n.Translatable
import io.github.nbcss.wynnlib.utils.Keyed

enum class Skill(
    val id: String,               //The id of the skill; used in equipment req
    val displayName: String,      //display name
    val requirementName: String   //The name used in ingredient req
) : Keyed, Translatable {
    STRENGTH("strength", "Strength", "strengthRequirement"),
    DEXTERITY("dexterity", "Dexterity", "dexterityRequirement"),
    INTELLIGENCE("intelligence", "Intelligence", "intelligenceRequirement"),
    DEFENCE("defense", "Defence", "defenceRequirement"),
    AGILITY("agility", "Agility", "agilityRequirement");

    companion object {
        private val NAME_MAP: Map<String, Skill> = mapOf(
            pairs = values().map { it.displayName to it }.toTypedArray()
        )

        fun fromDisplayName(displayName: String): Skill? = NAME_MAP[displayName]
    }

    override fun getKey(): String = id

    override fun getTranslationKey(label: String?): String {
        return "wynnlib.tooltip.skill_point.${getKey().lowercase()}"
    }
}