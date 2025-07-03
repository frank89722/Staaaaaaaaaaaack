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

import static me.frankv.staaaaaaaaaaaack.StxckUtil.*;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity {
    @Unique
    private static final EntityDataAccessor<Integer> STXCK_DATA_EXTRA_ITEM_COUNT;

    @Unique
    private boolean discardedTick = false;


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
        setExtraItemCount(stxck$getThis(), getExtraItemCount(itemEntity));
    }

    @Inject(
            method = "<init>(Lnet/minecraft/world/level/Level;DDDLnet/minecraft/world/item/ItemStack;DDD)V",
            at = @At("RETURN")
    )
    private void constructorSetExtraCountInject(CallbackInfo ci) {
        setExtraItemCount(stxck$getThis(), 0);
    }

    @Inject(method = "defineSynchedData", at = @At("RETURN"))
    private void defineSynchedDataForExtraItemCount(SynchedEntityData.Builder builder, CallbackInfo ci) {
        builder.define(STXCK_DATA_EXTRA_ITEM_COUNT, 0);
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
        discardedTick = false;
        refillItemStack(stxck$getThis());
    }

    @Inject(method = "isMergable", at = @At("HEAD"), cancellable = true)
    private void replaceIsMergable(CallbackInfoReturnable<Boolean> cir) {
        var self = stxck$getThis();
        var itemStack = self.getItem();
        if (isBlackListItem(itemStack)
                || getExtraItemCount(self) >= Staaaaaaaaaaaack.commonConfig.getMaxSize()) return;
        cir.setReturnValue(isMergable(stxck$getThis()));
    }

    @Inject(method = "tryToMerge", at = @At("HEAD"), cancellable = true)
    private void replaceTryToMerge(ItemEntity itemEntity1, CallbackInfo ci) {
        var self = stxck$getThis();
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
        return stxck$getThis().getBoundingBox().inflate(h, v, h);
    }

    @Inject(
            method = "addAdditionalSaveData",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isEmpty()Z")
    )
    private void saveExtraItemCount(CompoundTag compoundTag, CallbackInfo ci) {
        var extraCount = getExtraItemCount(stxck$getThis());
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
            setExtraItemCount(stxck$getThis(), compoundTag.getInt(EXTRA_ITEM_COUNT_TAG));
        }
    }

    @Inject(method = "setItem", at = @At("HEAD"), cancellable = true)
    private void handleSetEmpty(ItemStack item, CallbackInfo ci) {
        if (discardedTick) {
            ci.cancel();
            return;
        }
        if (item != ItemStack.EMPTY && !item.is(Items.AIR)) return;
        var self = stxck$getThis();
        if (getExtraItemCount(self) <= 0) return;
        var copied = self.getItem().copy();
        if (!copied.isEmpty()) {
            self.setItem(copied);
            copied.setCount(0);
        }
        ci.cancel();
    }

    @Inject(method = "playerTouch", at = @At("HEAD"), cancellable = true)
    private void cancelPlayerTouchOnDiscard(Player player, CallbackInfo ci) {
        if (discardedTick) {
            ci.cancel();
        }
    }

    @Inject(method = "playerTouch", at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lnet/minecraft/world/entity/player/Player;onItemPickup(Lnet/minecraft/world/entity/item/ItemEntity;)V "))
    private void syncItemOnPickup(Player player, CallbackInfo ci) {
        var self = stxck$getThis();
        var item = self.getItem();
        if (!item.isEmpty()) {
            self.setItem(item.copy());
        }
    }

    @Override
    public void remove(RemovalReason reason) {
        if (tryRefillItemStackOnEntityRemove(stxck$getThis(), reason)) {
            discardedTick = true;
            unsetRemoved();
            return;
        }
        super.remove(reason);
    }

    @Unique
    private ItemEntity stxck$getThis() {
        return (ItemEntity) (Object) this;
    }

}
