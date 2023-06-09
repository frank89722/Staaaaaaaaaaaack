package me.frankv.staaaaaaaaaaaack.mixin.client;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static me.frankv.staaaaaaaaaaaack.StxckUtil.*;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {

    @Redirect(
            method = "handleTakeItemEntity",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/multiplayer/ClientLevel;removeEntity(ILnet/minecraft/world/entity/Entity$RemovalReason;)V",
                    ordinal = 0
            )
    )
    private void handleRemoveItemEntity(ClientLevel instance, int entityId, Entity.RemovalReason reason) {
        var entity = getThis().getLevel().getEntity(entityId);
        if (entity == null || tryRefillItemStackOnEntityRemove(entity, reason)) return;
        getThis().getLevel().removeEntity(entityId, reason);
    }

    private ClientPacketListener getThis() {
        return (ClientPacketListener) (Object) this;
    }
}
