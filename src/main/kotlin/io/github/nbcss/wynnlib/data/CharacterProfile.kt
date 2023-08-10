package io.github.nbcss.wynnlib.data

import io.github.nbcss.wynnlib.events.EventHandler
import io.github.nbcss.wynnlib.events.WorldStateChangeEvent
import io.github.nbcss.wynnlib.utils.WorldState
import net.minecraft.client.MinecraftClient
import net.minecraft.client.item.TooltipContext
import net.minecraft.item.ItemStack

object CharacterProfile {
    var id: String? = null

    private val SOUL_POINT_SLOT = 8

    fun updateCharacterId() {
        val soulPoint: ItemStack = MinecraftClient.getInstance().player?.inventory?.getStack(SOUL_POINT_SLOT) ?: return
        id = soulPoint.getTooltip(MinecraftClient.getInstance().player, TooltipContext.BASIC).last().string
    }


    object WorldStateListener : EventHandler<WorldStateChangeEvent> {
        override fun handle(event: WorldStateChangeEvent) {
            if (event.newState == WorldState.IN_WORLD) {
                updateCharacterId()
            } else {
                id = null
            }
        }
    }
}