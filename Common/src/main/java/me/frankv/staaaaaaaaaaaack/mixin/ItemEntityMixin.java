package me.frankv.staaaaaaaaaaaack.mixin;

import me.frankv.staaaaaaaaaaaack.Staaaaaaaaaaaack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
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

    public ItemEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
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
        var self = getThis();
        var itemStack = self.getItem();
        if (isBlackListItem(itemStack)
                || getExtraItemCount(self) >= Staaaaaaaaaaaack.commonConfig.getMaxSize()) return;
        cir.setReturnValue(isMergable(getThis()));
    }

    @Inject(method = "tryToMerge", at = @At("HEAD"), cancellable = true)
    private void replaceTryToMerge(ItemEntity itemEntity1, CallbackInfo ci) {
        var self = getThis();
        if (isBlackListItem(self.getItem())) return;
        tryToMerge(self, itemEntity1);
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
        var h = Staaaaaaaaaaaack.commonConfig.getMaxMergeDistanceHorizontal();
        var v = Staaaaaaaaaaaack.commonConfig.getMaxMergeDistanceVertical();
        return getThis().getBoundingBox().inflate(h, v, h);
    }

    @Inject(
            method = "addAdditionalSaveData",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isEmpty()Z")
    )
    private void saveExtraItemCount(CompoundTag compoundTag, CallbackInfo ci) {
        var extraCount = getExtraItemCount(getThis());
        if (extraCount > 0) {
            compoundTag.putInt(EXTRA_ITEM_COUNT_TAG, extraCount);
        }
    }

    @Inject(
            method = "readAdditionalSaveData",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/nbt/CompoundTag;getCompound(Ljava/lang/String;)Lnet/minecraft/nbt/CompoundTag;"
            )
    )
    private void readExtraItemCount(CompoundTag compoundTag, CallbackInfo ci) {
        if (compoundTag.contains(EXTRA_ITEM_COUNT_TAG)) {
            setExtraItemCount(getThis(), compoundTag.getInt(EXTRA_ITEM_COUNT_TAG));
        }
    }

    @Inject(method = "setItem", at = @At("HEAD"), cancellable = true)
    private void refillOnSetEmptyItem(ItemStack item, CallbackInfo ci) {
        var self = getThis();
        if (item == ItemStack.EMPTY || item.is(Items.AIR)) {
            if (getExtraItemCount(self) <= 0) return;
            self.getItem().setCount(0);
            refillItemStack(self);
            if (!self.getItem().isEmpty()) {
                ci.cancel();
            }
        }
        refillItemStack(self);
    }

    @Inject(method = "playerTouch", at = @At("RETURN"))
    private void syncItemOnPickup(Player player, CallbackInfo ci) {
        var self = getThis();
        var item = self.getItem();
        if (!item.isEmpty()) {
            self.setItem(item.copy());
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
