package io.github.nbcss.wynnlib.events

import io.github.nbcss.wynnlib.data.CharacterProfile
import io.github.nbcss.wynnlib.function.*
import io.github.nbcss.wynnlib.readers.AbilityTreeHandler
import io.github.nbcss.wynnlib.timer.custom.HoundTimerIndicator
import io.github.nbcss.wynnlib.utils.WorldState

object EventRegistry {
    /**
     * Register all event listeners here
     */
    fun registerEvents() {
        InventoryUpdateEvent.registerListener(AbilityTreeHandler)

        ArmourStandUpdateEvent.registerListener(HoundTimerIndicator)

        RenderItemOverrideEvent.registerListener(DurabilityRender)
        RenderItemOverrideEvent.registerListener(SPNumberRender.Render)
        RenderItemOverrideEvent.registerListener(ConsumableChargeRender.Render)
        RenderItemOverrideEvent.registerListener(EmeraldPouchBarRender)

        InventoryRenderEvent.registerListener(CharacterInfoInventoryRender)

        SlotClickEvent.registerListener(SlotLocker.ClickListener)
        SlotClickEvent.registerListener(ItemProtector.ClickListener)
        SlotClickEvent.registerListener(PouchInChest.ClickListener)

        SlotPressEvent.registerListener(SlotLocker.PressListener)

        DrawSlotEvent.registerListener(SlotLocker.LockRender)
        DrawSlotEvent.registerListener(ItemProtector.ProtectRender)

        ItemLoadEvent.registerListener(DurabilityRender.LoadListener)
        ItemLoadEvent.registerListener(AnalyzeMode)
        ItemLoadEvent.registerListener(SPNumberRender.Reader)
        ItemLoadEvent.registerListener(ConsumableChargeRender.Reader)
        ItemLoadEvent.registerListener(EmeraldPouchBarRender.LoadListener)

        PlayerSendChatEvent.registerListener(KeyValidation)

        InventoryPressEvent.registerListener(ItemProtector.PressListener)

        PlayerReceiveChatEvent.registerListener(WorldState.HandleChat)

        WorldStateChangeEvent.registerListener(CharacterProfile.WorldStateListener)
        //It does not work
        //SpellCastEvent.registerListener(ShieldIndicator.SpellTrigger)
        //ArmourStandUpdateEvent.registerListener(ShieldIndicator.EntitySpawn)
        //ClientTickEvent.registerListener(ShieldIndicator.Ticker)
    }
}