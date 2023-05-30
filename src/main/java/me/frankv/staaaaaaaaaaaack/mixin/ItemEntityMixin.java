package me.frankv.staaaaaaaaaaaack.mixin;

import me.frankv.staaaaaaaaaaaack.Staaaaaaaaaaaack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.ParametersAreNonnullByDefault;

import static me.frankv.staaaaaaaaaaaack.StxckUtil.*;

@ParametersAreNonnullByDefault
@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity {
    @Unique
    private static final EntityDataAccessor<Integer> STXCK_DATA_EXTRA_ITEM_COUNT;

    static {
        STXCK_DATA_EXTRA_ITEM_COUNT = SynchedEntityData.defineId(ItemEntityMixin.class, EntityDataSerializers.INT);
        setDataExtraItemCount(STXCK_DATA_EXTRA_ITEM_COUNT);
    }

    public ItemEntityMixin(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

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
        getThis().getEntityData().define(STXCK_DATA_EXTRA_ITEM_COUNT, 0);
    }

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
        cir.setReturnValue(isMergable(getThis()));
    }

    @Inject(method = "tryToMerge", at = @At("HEAD"), cancellable = true)
    private void replaceTryToMerge(ItemEntity itemEntity1, CallbackInfo ci) {
        tryToMerge(getThis(), itemEntity1);
        ci.cancel();
    }

    @ModifyArg(
            method = "mergeWithNeighbours",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;getEntitiesOfClass(Ljava/lang/Class;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;)Ljava/util/List;"
            ),
            index = 1
    )
    private AABB mergeWithNeighbours(AABB uwu) {
        var h = Staaaaaaaaaaaack.config.getMaxMergeDistanceHorizontal();
        var v = Staaaaaaaaaaaack.config.getMaxMergeDistanceVerital();
        return getThis().getBoundingBox().inflate(h, v, h);
    }

    @Inject(method = "addAdditionalSaveData", at = @At("RETURN"))
    private void saveExtraItemCount(CompoundTag compoundTag, CallbackInfo ci) {
        var self = getThis();
        if (getExtraItemCount(self) > 0) {
            compoundTag.putInt(EXTRA_ITEM_COUNT_TAG, getExtraItemCount(self));
        }
    }

    @Inject(method = "readAdditionalSaveData", at = @At("RETURN"))
    private void readExtraItemCount(CompoundTag compoundTag, CallbackInfo ci) {
        if (compoundTag.contains(EXTRA_ITEM_COUNT_TAG)) {
            setExtraItemCount(getThis(), compoundTag.getInt(EXTRA_ITEM_COUNT_TAG));
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

    @Override
    public void remove(RemovalReason reason) {
        if (tryRefillItemStackOnEntityRemove(getThis(), reason)) return;
        super.remove(reason);
    }

    private ItemEntity getThis() {
        return (ItemEntity) (Object) this;
    }

}
