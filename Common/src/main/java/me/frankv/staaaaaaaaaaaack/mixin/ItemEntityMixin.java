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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import java.util.UUID;

import static me.frankv.staaaaaaaaaaaack.StxckUtil.*;

@ParametersAreNonnullByDefault
@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity {
    @Shadow @Nullable private UUID thrower;
    @Unique
    private static final EntityDataAccessor<Integer> STXCK_DATA_EXTRA_ITEM_COUNT;
    @Unique
    private static final EntityDataAccessor<ItemStack> STXCK_DATA_ORIGINAL_ITEM_STACK;

    @Unique
    private boolean discardedTick = false;


    static {
        STXCK_DATA_EXTRA_ITEM_COUNT = SynchedEntityData.defineId(ItemEntityMixin.class, EntityDataSerializers.INT);
        STXCK_DATA_ORIGINAL_ITEM_STACK = SynchedEntityData.defineId(ItemEntityMixin.class, EntityDataSerializers.ITEM_STACK);
        setData(STXCK_DATA_EXTRA_ITEM_COUNT, STXCK_DATA_ORIGINAL_ITEM_STACK);
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
        setOriginalStack(getThis(), getOriginalStack(itemEntity));
    }

    @Inject(
            method = "<init>(Lnet/minecraft/world/level/Level;DDDLnet/minecraft/world/item/ItemStack;DDD)V",
            at = @At("RETURN")
    )
    private void constructorSetExtraCountInject(Level level, double $$1, double $$2, double $$3, ItemStack stack, double $$5, double $$6, double $$7, CallbackInfo ci) {
        setExtraItemCount(getThis(), 0);
        setOriginalStack(getThis(), stack);
    }

    @Inject(
            method = "<init>(Lnet/minecraft/world/level/Level;DDDLnet/minecraft/world/item/ItemStack;)V",
            at = @At("RETURN")
    )
    private void constructorSetExtraCountInject(Level $$0, double $$1, double $$2, double $$3, ItemStack stack, CallbackInfo ci) {
        setExtraItemCount(getThis(), 0);
        setOriginalStack(getThis(), stack);
    }

    @Inject(method = "defineSynchedData", at = @At("RETURN"))
    private void defineSynchedDataForExtraItemCount(CallbackInfo ci) {
        getThis().getEntityData().define(STXCK_DATA_EXTRA_ITEM_COUNT, 0);
        getThis().getEntityData().define(STXCK_DATA_ORIGINAL_ITEM_STACK, ItemStack.EMPTY);
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
        refillItemStack(getThis());
        var self = getThis();
        if (self.getItem().isEmpty()) {
            self.discard();
        }
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
    private void handleSetEmpty(ItemStack item, CallbackInfo ci) {
        var self = getThis();
        if (item != ItemStack.EMPTY && !item.is(Items.AIR)) {
            if (self.getItem().isEmpty()) return;
            var originalStack = getOriginalStack(getThis());
            if (areMergable(originalStack, item)) return;

            var copied = originalStack.copy();

            var oldExtraCount = getExtraItemCount(self);
            var theCount = Math.min(oldExtraCount, originalStack.getMaxStackSize());
            setExtraItemCount(self, 0);
            setOriginalStack(self, item);
            if (theCount <= 0) return;
            copied.setCount(theCount);
            var newItemEntity = new ItemEntity(self.level(), self.getX(), self.getY(), self.getZ(), copied);
            setExtraItemCount(newItemEntity, Math.min(0, oldExtraCount - originalStack.getMaxStackSize()));
            newItemEntity.setDeltaMovement(self.getDeltaMovement());
            newItemEntity.level().addFreshEntity(newItemEntity);
        }

        if (discardedTick) {
            ci.cancel();
            return;
        }
        if (getExtraItemCount(self) <= 0) return;
//        var copied = self.getItem().copy();
        var copied = getOriginalStack(getThis()).copy();
        if (!copied.isEmpty()) {
            self.setItem(copied);
            copied.setCount(0);
        }
        ci.cancel();
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
        if (tryRefillItemStackOnEntityRemove(getThis(), reason)) {
            discardedTick = true;
            unsetRemoved();
            return;
        }
        super.remove(reason);
    }

    private ItemEntity getThis() {
        return (ItemEntity) (Object) this;
    }

}
