package io.github.nbcss.wynnlib.gui.dicts.filter

import io.github.nbcss.wynnlib.data.Tier
import io.github.nbcss.wynnlib.gui.TooltipScreen
import io.github.nbcss.wynnlib.gui.widgets.scrollable.CheckboxWidget
import io.github.nbcss.wynnlib.gui.widgets.scrollable.LabelWidget
import io.github.nbcss.wynnlib.i18n.Translations.UI_FILTER_RARITY
import io.github.nbcss.wynnlib.items.equipments.Equipment
import net.minecraft.util.Formatting
import java.util.function.Supplier

class RarityFilter(
    memory: CriteriaState<Equipment>,
    private val screen: TooltipScreen
) : FilterGroup<Equipment>(memory) {
    companion object {
        private const val FILTER_KEY = "ITEM_RARITY"
    }

    private val checkboxes: MutableMap<Tier, CheckboxWidget> = linkedMapOf()
    private val contentHeight: Int

    init {
        var index = 0
        val types = (memory.getFilter(FILTER_KEY) as? TierFilter)?.tiers
        addElement(LabelWidget(2, 2, Supplier {
            return@Supplier UI_FILTER_RARITY.formatted(Formatting.GOLD)
        }, mode = LabelWidget.Mode.OUTLINE))
        Tier.values().forEach { tier ->
            val posX = 2
            val posY = 12 + 20 * index
            val name = tier.getDisplayText()
            val checkbox = CheckboxWidget(posX, posY, name, screen,
                types?.let { tier in it } ?: true)
            checkbox.setCallback { updateFilter() }
            checkboxes[tier] = checkbox
            addElement(checkbox)
            addElement(LabelWidget(posX + 20, posY + 4, Supplier {
                return@Supplier tier.getDisplayText()
            }))
            index += 1
        }
        val group = CheckboxWidget.Group(checkboxes.values.toSet()) {
            updateFilter()
        }
        checkboxes.values.forEach { it.setGroup(group) }
        contentHeight = 10 + 20 * index
    }

    private fun updateFilter() {
        memory.putFilter(TierFilter(checkboxes.entries
            .filter { it.value.isChecked() }
            .map { it.key }.toSet()
        )
        )
    }

    override fun getHeight(): Int {
        return contentHeight
    }

    /*override fun renderContent(
        matrices: MatrixStack,
        mouseX: Int,
        mouseY: Int,
        posX: Double,
        posY: Double,
        delta: Float,
        mouseOver: Boolean
    ) {
        val x = floor(posX).toInt()
        val y = floor(posY).toInt()
        for (entry in checkboxes.entries) {
            val widget = entry.value
            widget.updateState(x, y, mouseOver)
            widget.render(matrices, mouseX, mouseY, delta)
            val text = entry.key.getDisplayText()
            //RenderKit.renderOutlineText(matrices, text, widget.x + 24.0f, widget.y + 5.0f)
            MinecraftClient.getInstance().textRenderer.drawWithShadow(matrices, text, widget.x + 20.0f, widget.y + 4.0f, 0xFFFFFF)
        }
    }*/

    override fun reload(memory: CriteriaState<Equipment>) {
        memory.getFilter(FILTER_KEY)?.let {
            if (it is TierFilter) {
                for (entry in checkboxes.entries) {
                    entry.value.setChecked(entry.key in it.tiers)
                }
            }
        }
    }

    //override fun getTitle(): Text = Translations.UI_FILTER_RARITY.formatted(Formatting.GOLD)

    //override fun getContentHeight(): Int = contentHeight

    /*override fun onClick(mouseX: Int, mouseY: Int, button: Int): Boolean {
        if (checkboxes.values.any { it.mouseClicked(mouseX.toDouble(), mouseY.toDouble(), button) }){
            memory.putFilter(TierFilter(checkboxes.entries
                .filter { it.value.isChecked() }
                .map { it.key }.toSet()))
            return true
        }
        return false
    }*/

    class TierFilter(val tiers: Set<Tier>) : CriteriaState.Filter<Equipment> {

        override fun accept(item: Equipment): Boolean {
            return item.getTier() in tiers
        }

        override fun getKey(): String = FILTER_KEY
    }
}