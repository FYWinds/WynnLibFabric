package io.github.nbcss.wynnlib.mixins.world;

import io.github.nbcss.wynnlib.events.PlayerReceiveChatEvent;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ReceiveChatMixin {

//    @Shadow
//    private CombinedDynamicRegistries<ClientDynamicRegistryType> combinedDynamicRegistries;
//
//    @Inject(method = "onChatMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/message/MessageHandler;onChatMessage(Lnet/minecraft/network/message/SignedMessage;Lcom/mojang/authlib/GameProfile;Lnet/minecraft/network/message/MessageType$Parameters;)V"), cancellable = true)
//    public void onChatMessage(ChatMessageS2CPacket packet, CallbackInfo ci) {
//        Text message = packet.unsignedContent();
//        assert message != null;
//        Optional<MessageType.Parameters> params = packet.serializedParameters().toParameters(this.combinedDynamicRegistries.getCombinedRegistryManager());
//        if (params.isPresent()) {
//            MessageType type = params.get().type();
//            PlayerReceiveChatEvent event = new PlayerReceiveChatEvent(type, message);
//            PlayerReceiveChatEvent.Companion.handleEvent(event);
//            if (event.getCancelled()) {
//                ci.cancel();
//            }
//        }
//    }
//
//    @Inject(method = "onProfilelessChatMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/message/MessageHandler;onProfilelessMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageType$Parameters;)V"), cancellable = true)
//    public void onProfilelessChatMessage(ProfilelessChatMessageS2CPacket packet, CallbackInfo ci) {
//        Text message = packet.message();
//        assert message != null;
//        Optional<MessageType.Parameters> params = packet.chatType().toParameters(this.combinedDynamicRegistries.getCombinedRegistryManager());
//        if (params.isPresent()) {
//            MessageType type = params.get().type();
//            PlayerReceiveChatEvent event = new PlayerReceiveChatEvent(type, message);
//            PlayerReceiveChatEvent.Companion.handleEvent(event);
//            if (event.getCancelled()) {
//                ci.cancel();
//            }
//        }
//    }

    @Inject(method = "onGameMessage", at = @At(value = "HEAD"), cancellable = true)
    public void onGameMessage(GameMessageS2CPacket packet, CallbackInfo ci) {
        Text message = packet.content();
        assert message != null;
        PlayerReceiveChatEvent event = new PlayerReceiveChatEvent(message);
        PlayerReceiveChatEvent.Companion.handleEvent(event);
        if (event.getCancelled()) {
            ci.cancel();
        }
    }
}
