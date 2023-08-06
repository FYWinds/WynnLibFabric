package io.github.nbcss.wynnlib.gui.widgets

import com.mojang.blaze3d.systems.RenderSystem
import io.github.nbcss.wynnlib.abilities.Ability
import io.github.nbcss.wynnlib.abilities.AbilityTree
import io.github.nbcss.wynnlib.gui.TooltipScreen
import io.github.nbcss.wynnlib.mixins.MouseInvoker
import io.github.nbcss.wynnlib.render.RenderKit
import io.github.nbcss.wynnlib.render.TextureData
import io.github.nbcss.wynnlib.utils.AlphaColor
import io.github.nbcss.wynnlib.utils.Color
import io.github.nbcss.wynnlib.utils.IntPos
import io.github.nbcss.wynnlib.utils.playSound
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.sound.SoundEvents
import net.minecraft.util.Identifier
import net.minecraft.util.math.MathHelper
import org.lwjgl.glfw.GLFW
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

abstract class ATreeScrollWidget(
    screen: TooltipScreen, x: Int, y: Int,
    sliderX: Int, sliderY: Int
) :
    ScrollPaneWidget(SCROLL, screen, x, y, WIDTH, HEIGHT) {
    companion object {
        private val SCROLL: TextureData = TextureData(
            Identifier("wynnlib", "textures/gui/ability_tree_scroll.png"),
            0, 0, 222, 384
        )
        private val TEXTURE = Identifier("wynnlib", "textures/gui/ability_ui.png")
        private val SLIDER_TEXTURE = TextureData(TEXTURE, 246, 0)
        private const val WIDTH = 222
        private const val HEIGHT = 138
        private const val NODE_SIZE = 24
    }

    private val slider = VerticalSliderWidget(sliderX, sliderY, 9, HEIGHT, 30, SLIDER_TEXTURE) {
        setScrollPosition(getMaxPosition() * it)
    }

    abstract fun getAbilityTree(): AbilityTree

    fun toScreenPosition(height: Int, position: Int): IntPos {
        return toInnerPoint(height, position, getScrollPosition())
    }

    fun isOverNode(node: IntPos, mouseX: Int, mouseY: Int): Boolean {
        return abs(node.x - mouseX) <= 11 && abs(node.y - mouseY) <= 11
    }

    fun focusOnAbility(ability: Ability) {
        val currPos = toInnerPoint(ability.getHeight(), ability.getPosition(), 0.0)
        // Scroll such ability is in middle of widget if possible
        val scrollAmount = MathHelper.clamp((currPos.y - y - height / 2).toDouble(), 0.0, getMaxPosition())

        val window = client.window

        setScrollPosition(scrollAmount, scrollDelay)
        // Convert from Rendering Coordinates to Screen Coordinates
        val newX = currPos.x.toDouble() * window.width / window.scaledWidth
        val newY = (currPos.y - scrollAmount) * window.height / window.scaledHeight

        GLFW.glfwSetCursorPos(
            window.handle,
            newX,
            newY
        )

        // For some reason the cursor hook isn't called
        (client.mouse as MouseInvoker).callOnCursorPos(window.handle, newX, newY)
    }

    open fun onClickNode(ability: Ability, button: Int): Boolean {
        val dependency = ability.getAbilityDependency()
        if (button == 1 && dependency != null) {
            playSound(SoundEvents.ENTITY_ITEM_PICKUP)
            focusOnAbility(dependency)
        } else {
            playSound(SoundEvents.ENTITY_SHULKER_HURT_CLOSED)
        }
        return true
    }

    fun drawOuterEdge(matrices: MatrixStack, from: IntPos, to: IntPos, color: Int, reroute: Boolean) {
        RenderSystem.enableDepthTest()
        if (from.x != to.x) {
            val posY = if (reroute) to.y else from.y
            fill(matrices, from.x, posY - 2, to.x, posY + 2, color)
        }
        if (from.y != to.y) {
            val fromY = from.y + if (from.x == to.x || reroute) 0 else -2
            val toY = to.y + if (from.x != to.x && reroute) 2 else 0
            val posX = if (reroute) from.x else to.x
            fill(matrices, posX - 2, fromY, posX + 2, toY, color)
        }
    }

    fun drawInnerEdge(matrices: MatrixStack, from: IntPos, to: IntPos, color: Int, reroute: Boolean) {
        if (from.x != to.x) {
            val posY = if (reroute) to.y else from.y
            fill(matrices, from.x, posY - 1, to.x, posY + 1, color)
        }
        if (from.y != to.y) {
            val fromY = from.y + if (from.x == to.x || reroute) 0 else -1
            val toY = to.y + if (from.x != to.x && reroute) 1 else 0
            val posX = if (reroute) from.x else to.x
            fill(matrices, posX - 1, fromY, posX + 1, toY, color)
        }
    }

    fun renderEdges(abilities: List<Ability>, matrices: MatrixStack, color: AlphaColor, inner: Boolean) {
        abilities.forEach {
            val to = toScreenPosition(it.getHeight(), it.getPosition())
            it.getPredecessors().forEach { node ->
                val from = toScreenPosition(node.getHeight(), node.getPosition())
                val height = min(it.getHeight(), node.getHeight())
                val reroute = getAbilityTree().getAbilityFromPosition(height, it.getPosition()) != null
                if (inner) {
                    drawInnerEdge(matrices, from, to, color.code(), reroute)
                } else {
                    drawOuterEdge(matrices, from, to, color.code(), reroute)
                }
            }
        }
    }

    fun renderArchetypeOutline(matrices: MatrixStack, ability: Ability, x: Int, y: Int) {
        if (ability.getTier().getLevel() != 0) {
            ability.getArchetype()?.let { arch ->
                val color = Color.fromFormatting(arch.getFormatting())
                val itemX = x - 15
                val itemY = y - 15
                val u = 64 + 30 * (ability.getTier().getLevel() - 1)
                RenderKit.renderTextureWithColor(
                    matrices, TEXTURE, color.withAlpha(165), itemX, itemY,
                    u, 182, 30, 30, 256, 256
                )
            }
        }
    }

    private fun toInnerPoint(height: Int, position: Int, scrollPos: Double): IntPos {
        val posX = x + 3 + NODE_SIZE / 2 + (position + 4) * NODE_SIZE
        val posY = y + 1 + NODE_SIZE / 2 + height * NODE_SIZE - scrollPos
        return IntPos(posX, posY.roundToInt())
    }

    override fun getSlider(): VerticalSliderWidget = slider

    override fun onContentClick(mouseX: Double, mouseY: Double, button: Int): Boolean {
        for (ability in getAbilityTree().getAbilities()) {
            val node = toScreenPosition(ability.getHeight(), ability.getPosition())
            if (isOverNode(node, mouseX.toInt(), mouseY.toInt())) {
                return onClickNode(ability, button)
            }
        }
        return super.onContentClick(mouseX, mouseY, button)
    }

    override fun getContentHeight(): Int {
        return max(0, 6 + (1 + getAbilityTree().getMaxHeight()) * NODE_SIZE)
    }
}