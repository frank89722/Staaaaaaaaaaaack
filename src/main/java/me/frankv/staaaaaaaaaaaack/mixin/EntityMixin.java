package me.frankv.staaaaaaaaaaaack.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static me.frankv.staaaaaaaaaaaack.StxckUtil.*;

@Mixin(Entity.class)
public class EntityMixin {

    @Inject(method = "remove", at = @At("HEAD"), cancellable = true)
    private void updateItemStackOnItemEntityRemove(Entity.RemovalReason reason, CallbackInfo ci) {
        if (!getThis().getType().equals(EntityType.ITEM)) return;
        if (!reason.equals(Entity.RemovalReason.DISCARDED)) return;

        var entity = (ItemEntity) getThis();
        entity.getItem().setCount(0);

        if (getTotalCount(entity) == 0) return;

        refillItemStack(entity);
        ci.cancel();
    }

    private Entity getThis() {
        return (Entity) (Object) this;
    }
}
