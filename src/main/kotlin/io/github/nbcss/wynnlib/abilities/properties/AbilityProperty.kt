package io.github.nbcss.wynnlib.abilities.properties

import com.google.gson.JsonElement
import io.github.nbcss.wynnlib.abilities.Ability
import io.github.nbcss.wynnlib.abilities.PlaceholderContainer
import io.github.nbcss.wynnlib.abilities.PropertyProvider
import io.github.nbcss.wynnlib.abilities.builder.EntryContainer
import io.github.nbcss.wynnlib.abilities.properties.archer.*
import io.github.nbcss.wynnlib.abilities.properties.assassin.*
import io.github.nbcss.wynnlib.abilities.properties.general.*
import io.github.nbcss.wynnlib.abilities.properties.info.*
import io.github.nbcss.wynnlib.abilities.properties.mage.*
import io.github.nbcss.wynnlib.abilities.properties.shaman.*
import io.github.nbcss.wynnlib.abilities.properties.warrior.*
import io.github.nbcss.wynnlib.utils.Keyed
import net.minecraft.text.Text

@Suppress("UNCHECKED_CAST")
abstract class AbilityProperty(private val ability: Ability) {
    companion object {
        private val typeMap: Map<String, Type<out AbilityProperty>> = mapOf(
            pairs = listOf(
                ValuesProperty,
                UpgradeProperty,
                ModifyProperty,
                ExtendProperty,
                ReplaceAbilityProperty,
                BoundSpellProperty,
                TotalHealProperty,
                PulseHealProperty,
                SelfDamageProperty,
                TimeDilationProperty,
                CriticalDamageProperty,
                ElementMasteryProperty,
                ResistantBonusProperty,
                ElementConversionProperty,
                DamageBonusProperty,
                DamageBonusProperty.Raw,
                DamageBonusProperty.PerFocus,
                DamageBonusProperty.PerMarked,
                AbilityDamageBonusProperty,
                DamageIntervalProperty,
                DamageIntervalProperty.Modifier,
                AbilityHealModifier,
                BonusEffectProperty,
                IDModifierProperty,
                IDConvertorProperty,
                ChanceProperty,
                ChanceProperty.Modifier,
                ManaCostProperty,
                ManaCostModifierProperty,
                SpellCostModifierProperty,
                DamageProperty,
                DamageModifierProperty,
                AbilityDamageModifierProperty,
                TeleportSuccessionProperty,
                TimelockProperty,
                FluidHealProperty,
                MainAttackDamageProperty,
                MainAttackDamageProperty.Modifier,
                MainAttackDamageRawProperty,
                MainAttackRangeProperty,
                MainAttackRangeProperty.Modifier,
                MainAttackRangeProperty.Clear,
                RangeProperty,
                RangeProperty.Modifier,
                RangeProperty.Clear,
                CooldownProperty,
                CooldownProperty.Modifier,
                DurationProperty,
                DurationProperty.Modifier,
                AreaOfEffectProperty,
                AreaOfEffectProperty.Modifier,
                AreaOfEffectProperty.Clear,
                ChargeProperty,
                ChargeProperty.Modifier,
                MassiveBashProperty,
                MassacreProperty,
                DiscombobulateProperty,
                BrinkMadnessProperty,
                ArcherStreamProperty,
                ArcherStreamProperty.Modifier,
                ArcherSentientBowsProperty,
                ArcherSentientBowsProperty.Modifier,
                DecimatorProperty,
                MaxTrapProperty,
                MaxTrapProperty.Modifier,
                MaxFocusProperty,
                MaxFocusProperty.Modifier,
                PatientHunterProperty,
                PatientHunterProperty.Modifier,
                MageManaBankProperty,
                MageManaBankProperty.Modifier,
                MageOphanimProperty,
                MageOphanimProperty.Modifier,
                MageLightweaverProperty,
                MageLightweaverProperty.Modifier,
                MageManaStoreProperty,
                MageManaStoreProperty.Modifier,
                WindedBoosterProperty,
                MaxWindedProperty,
                MaxWindedProperty.Modifier,
                SmokeBombProperty,
                SmokeBombProperty.Modifier,
                AssassinClonesProperty,
                AssassinClonesProperty.Modifier,
                ShurikensBounceProperty,
                ShurikensBounceProperty.Modifier,
                MaxMarkedProperty,
                MaxMarkedProperty.Modifier,
                SatsujinProperty,
                BloomAoEProperty,
                SacrificialShrineProperty,
                RegenerationProperty,
                BloodTransferProperty,
                HymnHateProperty,
                MantleResistanceProperty,
                CorruptedProperty,
                CorruptedProperty.Modifier,
                EnragedBlowProperty,
                MaxTotemProperty,
                MaxTotemProperty.Modifier,
                MaxPuppetProperty,
                MaxPuppetProperty.Modifier,
                MaxEffigyProperty,
                MaxEffigyProperty.Modifier,
                ShepherdProperty,
                ShamanBeamProperty,
                ShamanBeamProperty.Modifier,
                ShamanBloodPoolProperty,
                ShamanBloodPoolProperty.Modifier,
                TetherProperty,
                TetherProperty.Modifier,
            ).map { it.getKey() to it }.toTypedArray()
        )

        fun getType(key: String): Type<out AbilityProperty>? {
            return typeMap[key.lowercase()]
        }

        fun fromData(ability: Ability, key: String, data: JsonElement): AbilityProperty? {
            return (getType(key) ?: return null).create(ability, data)
        }
    }

    open fun writePlaceholder(container: PlaceholderContainer) = Unit

    open fun updateEntries(container: EntryContainer): Boolean = true

    open fun copy(other: Ability): AbilityProperty? = null

    fun getAbility(): Ability = ability

    open fun getTooltip(provider: PropertyProvider = getAbility()): List<Text> = emptyList()

    interface Type<T : AbilityProperty> : Keyed {
        fun create(ability: Ability, data: JsonElement): T?
        fun from(provider: PropertyProvider): T? {
            return provider.getProperty(getKey()) as? T
        }
    }
}