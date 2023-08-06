package io.github.nbcss.wynnlib.items

import io.github.nbcss.wynnlib.items.identity.TooltipProvider
import io.github.nbcss.wynnlib.utils.Color
import net.minecraft.item.ItemStack
import net.minecraft.text.Text

interface BaseItem : TooltipProvider {
    fun getDisplayText(): Text
    fun getDisplayName(): String
    fun getIcon(): ItemStack
    fun getIconText(): String? = null
    fun getRarityColor(): Color
    override fun getTooltip(): List<Text> = listOf(getDisplayText())
}