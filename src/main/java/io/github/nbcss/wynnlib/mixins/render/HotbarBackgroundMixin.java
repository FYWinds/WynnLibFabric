package io.github.nbcss.wynnlib.mixins.render;


import com.mojang.blaze3d.systems.RenderSystem;
import io.github.nbcss.wynnlib.Settings;
import io.github.nbcss.wynnlib.events.RenderItemOverrideEvent;
import io.github.nbcss.wynnlib.matcher.MatchableItem;
import io.github.nbcss.wynnlib.matcher.item.ItemMatcher;
import io.github.nbcss.wynnlib.render.RenderKit;
import io.github.nbcss.wynnlib.utils.Color;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.minecraft.client.gui.widget.ClickableWidget.WIDGETS_TEXTURE;

@Mixin(InGameHud.class)
public class HotbarBackgroundMixin {
    private final Identifier texture = new Identifier("wynnlib", "textures/legacy/lock.png");
    @Shadow
    private int scaledWidth;
    @Shadow
    private int scaledHeight;

    @Shadow
    private PlayerEntity getCameraPlayer() {
        return null;
    }

    @Unique
    private boolean flag = false;

    @Inject(method = "renderHotbar", at = @At("HEAD"))
    public void renderHotbarHead(float tickDelta, MatrixStack matrices, CallbackInfo ci) {
        flag = true;
    }

    @Inject(method = "renderHotbar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;" +
            "drawTexture(Lnet/minecraft/client/util/math/MatrixStack;IIIIII)V",
            shift = At.Shift.AFTER))
    public void renderHotbar(float tickDelta, MatrixStack matrices, CallbackInfo ci) {
        if (flag) {
            flag = false;
            drawSlots(matrices);
        }
    }

    @Redirect(method = "renderHotbarItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/ItemRenderer;renderGuiItemOverlay(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;II)V"))
    public void renderHotbarItem(ItemRenderer instance, MatrixStack matrices, TextRenderer textRenderer, ItemStack stack, int x, int y) {
        if (drawOverrides(matrices, textRenderer, stack, x, y))
            return;
        instance.renderGuiItemOverlay(matrices, textRenderer, stack, x, y);
    }

    @Unique
    private boolean drawOverrides(MatrixStack matrices, TextRenderer renderer, ItemStack stack, int x, int y) {
        RenderItemOverrideEvent event = new RenderItemOverrideEvent(matrices, renderer, stack, x, y);
        RenderItemOverrideEvent.Companion.handleEvent(event);
        return event.getCancelled();
    }

    @Unique
    private void drawSlots(MatrixStack matrices) {
        PlayerEntity playerEntity = this.getCameraPlayer();
        if (playerEntity != null) {
            int y = this.scaledHeight - 19;
            for (int i = 0; i < 6; i++) {
                int x = this.scaledWidth / 2 - 90 + i * 20 + 2;
                ItemStack stack = playerEntity.getInventory().main.get(i);
                if (Settings.INSTANCE.getOption(Settings.SettingOption.ITEM_BACKGROUND_COLOR)) {
                    MatchableItem item = ItemMatcher.Companion.toItem(stack);
                    if (item != null) {
                        RenderSystem.disableDepthTest();
                        Color color = item.getMatcherType().getColor();
                        DrawableHelper.fill(matrices, x, y, x + 16, y + 16, color.withAlpha(0xCC).code());
                    }
                }
                if (playerEntity.experienceLevel > 0 && Settings.INSTANCE.isSlotLocked(36 + i)) {
                    RenderSystem.enableBlend();
                    RenderSystem.disableDepthTest();
                    RenderKit.INSTANCE.renderTexture(matrices, texture, x - 2, y - 2,
                            0, 0, 20, 20, 20, 20);
                }
            }
            //bind back origin texture
            RenderSystem.setShaderTexture(0, WIDGETS_TEXTURE);
        }
    }
}
