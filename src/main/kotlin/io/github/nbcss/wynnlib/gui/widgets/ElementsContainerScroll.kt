package io.github.nbcss.wynnlib.gui.widgets

import io.github.nbcss.wynnlib.gui.TooltipScreen
import io.github.nbcss.wynnlib.gui.widgets.scrollable.ScrollElement
import io.github.nbcss.wynnlib.render.TextureData
import net.minecraft.client.gui.Element
import net.minecraft.client.util.math.MatrixStack

open class ElementsContainerScroll(
    background: TextureData?,
    screen: TooltipScreen,
    x: Int,
    y: Int,
    width: Int,
    height: Int,
    private var elements: MutableList<ScrollElement> = mutableListOf(),
    private var contentHeight: Int = 0,
    scrollDelay: Long = 200L,
    scrollUnit: Double = 32.0
) :
    AbstractElementScroll(background, screen, x, y, width, height, scrollDelay, scrollUnit) {

    fun setElements(elements: MutableList<ScrollElement>) {
        this.elements = elements
    }

    fun addElement(element: ScrollElement) {
        this.elements.add(element)
    }

    fun setContentHeight(h: Int) {
        contentHeight = h
    }

    override fun getElements(): List<Element> = elements

    override fun getContentHeight(): Int = contentHeight

    override fun renderContents(
        matrices: MatrixStack,
        mouseX: Int,
        mouseY: Int,
        position: Double,
        delta: Float,
        mouseOver: Boolean
    ) {
        val posX = x
        val posY = (y - position).toInt()
        elements.forEach {
            it.updateState(posX, posY, mouseOver)
            it.render(matrices, mouseX, mouseY, delta)
        }
    }
}