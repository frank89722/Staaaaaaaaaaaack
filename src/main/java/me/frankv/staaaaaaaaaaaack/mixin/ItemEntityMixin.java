package me.frankv.staaaaaaaaaaaack.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

import static me.frankv.staaaaaaaaaaaack.StxckUtil.*;

@Mixin(ItemEntity.class)
public class ItemEntityMixin {
    private static final String EXTRA_ITEM_TAG = "ExtraItemCount";

    @Shadow private int pickupDelay;
    @Shadow private int age;


    @Inject(
            method = "<init>(Lnet/minecraft/world/entity/item/ItemEntity;)V",
            at = @At("RETURN")
    )
    private void constructorSetExtraCountInject(ItemEntity itemEntity, CallbackInfo ci) {
        setExtraItemCount(getThis(), getExtraItemCount(itemEntity));
    }

    @Inject(
            method = "<init>(Lnet/minecraft/world/level/Level;DDDLnet/minecraft/world/item/ItemStack;DDD)V",
            at = @At("RETURN")
    )
    private void constructorSetExtraCountInject(CallbackInfo ci) {
        setExtraItemCount(getThis(), 0);
    }

    @Inject(method = "defineSynchedData", at = @At("RETURN"))
    private void defineSynchedDataForExtraItemCount(CallbackInfo ci) {
        getThis().getEntityData().define(DATA_EXTRA_ITEM_COUNT, 0);
    }

    @Redirect(
            method = "playerTouch",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;setCount(I)V")
    )
    private void cancelPlayerTouchSetCount(ItemStack instance, int p_41765_) {}

    @Inject(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/Entity;tick()V",
                    shift = At.Shift.AFTER
            )
    )
    private void tickInject(CallbackInfo ci) {
        refillItemStack(getThis());
    }

    @Inject(method = "isMergable", at = @At("HEAD"), cancellable = true)
    private void replaceIsMergable(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(getThis().isAlive() && pickupDelay != 32767 && age != -32768 && age < 6000);
    }

    @Inject(method = "tryToMerge", at = @At("HEAD"), cancellable = true)
    private void replaceTryToMerge(ItemEntity itemEntity1, CallbackInfo ci) {
        var itemEntity = getThis();

        if (Objects.equals(itemEntity.getOwner(), itemEntity1.getOwner())
                && areMergable(itemEntity.getItem(), itemEntity1.getItem())) {
            if (getTotalCount(itemEntity1) < getTotalCount(itemEntity)) {
                merge(itemEntity, itemEntity1);
            } else {
                merge(itemEntity1, itemEntity);
            }
        }

        ci.cancel();
    }

    @Inject(method = "addAdditionalSaveData", at = @At("RETURN"))
    private void saveExtraItemCount(CompoundTag compoundTag, CallbackInfo ci) {
        var self = getThis();
        if (getExtraItemCount(self) > 0) {
            compoundTag.putInt(EXTRA_ITEM_TAG, getExtraItemCount(self));
        }
    }

    @Inject(method = "readAdditionalSaveData", at = @At("RETURN"))
    private void readExtraItemCount(CompoundTag compoundTag, CallbackInfo ci) {
        if (compoundTag.contains(EXTRA_ITEM_TAG)) {
            setExtraItemCount(getThis(), compoundTag.getInt(EXTRA_ITEM_TAG));
        }
    }

    @Inject(method = "setItem", at = @At("HEAD"), cancellable = true)
    private void refillOnSetEmptyItem(ItemStack item, CallbackInfo ci) {
        if (item == ItemStack.EMPTY || item.is(Items.AIR)) {
            var self = getThis();
            if (getExtraItemCount(self) <= 0) return;

            refillItemStack(self);
            ci.cancel();
        }
    }

    private static void merge(ItemEntity itemEntity, ItemEntity itemEntity1) {
        var entityAccessor = (ItemEntityAccessor) itemEntity;
        var entityAccessor1 = (ItemEntityAccessor) itemEntity1;

        entityAccessor.setPickupDelay(Math.max(entityAccessor.getPickupDelay(), entityAccessor1.getPickupDelay()));
        entityAccessor.setAge(Math.min(entityAccessor.getAge(), entityAccessor1.getAge()));

        grow(itemEntity, getTotalCount(itemEntity1));

        setExtraItemCount(itemEntity1, 0);
        itemEntity1.setItem(ItemStack.EMPTY);
        itemEntity1.discard();
    }

    private ItemEntity getThis() {
        return (ItemEntity) (Object) this;
    }

}
