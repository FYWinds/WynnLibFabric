package io.github.nbcss.wynnlib.gui.dicts

import io.github.nbcss.wynnlib.gui.DictionaryScreen
import io.github.nbcss.wynnlib.gui.HandbookTabScreen
import io.github.nbcss.wynnlib.gui.TabFactory
import io.github.nbcss.wynnlib.i18n.Translations.UI_INGREDIENTS
import io.github.nbcss.wynnlib.items.Ingredient
import io.github.nbcss.wynnlib.registry.IngredientRegistry
import io.github.nbcss.wynnlib.utils.ItemFactory
import net.minecraft.client.gui.screen.Screen
import net.minecraft.item.ItemStack
import net.minecraft.text.Text

/**
 * The Dictionary UI for display all ingredients.
 */
class IngredientDictScreen(parent: Screen?) : DictionaryScreen<Ingredient>(parent, TITLE) {
    companion object {
        val ICON: ItemStack = ItemFactory.fromEncoding("minecraft:diamond_axe#94")
        val TITLE: Text = UI_INGREDIENTS.translate()
        val FACTORY = object : TabFactory {
            override fun getTabIcon(): ItemStack = ICON
            override fun getTabTitle(): Text = TITLE
            override fun createScreen(parent: Screen?): HandbookTabScreen = IngredientDictScreen(parent)
            override fun isInstance(screen: HandbookTabScreen): Boolean = screen is IngredientDictScreen
            override fun shouldDisplay(): Boolean = true
        }
    }

    override fun fetchItems(): Collection<Ingredient> {
        return IngredientRegistry.getAll()
    }
}