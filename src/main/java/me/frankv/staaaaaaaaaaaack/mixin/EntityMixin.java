package me.frankv.staaaaaaaaaaaack.mixin;

import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static me.frankv.staaaaaaaaaaaack.StxckUtil.*;

@Mixin(Entity.class)
public class EntityMixin {

    @Inject(method = "remove", at = @At("HEAD"), cancellable = true)
    private void handleItemEntityRemove(Entity.RemovalReason reason, CallbackInfo ci) {
        if (tryRefillItemStackOnEntityRemove(getThis(), reason)) {
            ci.cancel();
        }
    }

    private Entity getThis() {
        return (Entity) (Object) this;
    }
}
