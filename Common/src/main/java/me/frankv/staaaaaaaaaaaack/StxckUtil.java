package me.frankv.staaaaaaaaaaaack;

import me.frankv.staaaaaaaaaaaack.mixin.ItemEntityAccessor;
import me.frankv.staaaaaaaaaaaack.mixin.ItemStackAccessor;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;
import java.util.Optional;
import java.util.function.BiPredicate;

public class StxckUtil {
    public static final String EXTRA_ITEM_COUNT_TAG = "StxckExtraItemCount";
    private static EntityDataAccessor<Integer> DATA_EXTRA_ITEM_COUNT;

    public static BiPredicate<ItemStack, ItemStack> areMergableReplacementPredicate;


    public static void setDataExtraItemCount(EntityDataAccessor<Integer> entityDataAccessor) {
        if (DATA_EXTRA_ITEM_COUNT != null) return;
        DATA_EXTRA_ITEM_COUNT = entityDataAccessor;
    }

    public static void refillItemStack(ItemEntity entity) {
        var extraItemCount = getExtraItemCount(entity);
        if (extraItemCount <= 0) return ;

        var stack = entity.getItem();
        var maxSize = ((ItemStackAccessor) (Object) stack).accessItem().getMaxStackSize();
        if (stack.getCount() == maxSize) return;

        var x = maxSize - stack.getCount();
        var refillCount = Math.min(x, extraItemCount);

        stack.grow(refillCount);
        setExtraItemCount(entity, extraItemCount - refillCount);
        entity.setItem(stack.copy());
    }

    public static boolean areMergable(ItemEntity itemEntity, ItemEntity itemEntity1) {
        var maxExtraSize = Staaaaaaaaaaaack.commonConfig.getMaxSize();
        if (maxExtraSize - getExtraItemCount(itemEntity) < getTotalCount(itemEntity1)
                && maxExtraSize - getExtraItemCount(itemEntity1) < getTotalCount(itemEntity)
        ) {
            return false;
        }

        var itemStack = itemEntity.getItem();
        var itemStack1 = itemEntity1.getItem();
        if (areMergableReplacementPredicate != null) {
            return areMergableReplacementPredicate.test(itemStack, itemStack1);
        }

        if (!itemStack1.is(itemStack.getItem())) {
            return false;
        }
        if (itemStack1.hasTag() ^ itemStack.hasTag()) {
            return false;
        }
        return !itemStack1.hasTag() || Objects.equals(itemStack1.getTag(), itemStack.getTag());
    }

    public static void grow(ItemEntity entity, int count) {
        setExtraItemCount(entity, getExtraItemCount(entity) + count);
        refillItemStack(entity);
    }

    public static boolean isMergable(ItemEntity entity) {
        var accessor = (ItemEntityAccessor) entity;
        var pickupDelay = accessor.getPickupDelay();
        var age = accessor.getAge();
        return entity.isAlive() && pickupDelay != 32767 && age != -32768 && age < 6000;
    }

    public static Optional<String> getTotalCountForDisplay(ItemEntity entity) {
        var total = getTotalCount(entity);

        if (total >= 1_000_000_000) {
            return Optional.of(String.format("%.3fB", total/1_000_000_000f));
        }
        if (total >= 1_000_000) {
            return Optional.of(String.format("%.2fM", total/1_000_000f));
        }
        if (total >= 10_000) {
            return Optional.of(String.format("%.1fK", total/1_000f));
        }
        if (total > entity.getItem().getMaxStackSize()) {
            return Optional.of(String.valueOf(total));
        }
        return Optional.empty();
    }

    public static int getTotalCount(ItemEntity entity) {
        return entity.getItem().getCount() + getExtraItemCount(entity);
    }

    public static int getExtraItemCount(ItemEntity entity) {
        return entity.getEntityData().get(DATA_EXTRA_ITEM_COUNT);
    }

    public static void setExtraItemCount(ItemEntity entity, int count) {
        entity.getEntityData().set(DATA_EXTRA_ITEM_COUNT, count);
    }

    public static boolean tryRefillItemStackOnEntityRemove(Entity entity, Entity.RemovalReason reason) {
        if (!entity.getType().equals(EntityType.ITEM) || !reason.equals(Entity.RemovalReason.DISCARDED)) return false;

        var itemEntity = (ItemEntity) entity;
        if (getExtraItemCount(itemEntity) <= 0) return false;

        var copied = itemEntity.getItem().copy();
        itemEntity.setItem(copied);
        copied.setCount(0);
        refillItemStack(itemEntity);
        return true;
    }

    public static void tryToMerge(ItemEntity itemEntity, ItemEntity itemEntity1) {
        if (Objects.equals(itemEntity.getOwner(), itemEntity1.getOwner())
                && areMergable(itemEntity, itemEntity1)
        ) {
            if (getTotalCount(itemEntity1) < getTotalCount(itemEntity)) {
                merge(itemEntity, itemEntity1);
            } else {
                merge(itemEntity1, itemEntity);
            }
        }
    }

    public static void merge(ItemEntity consumer, ItemEntity supplier) {
        var consumerAccessor = (ItemEntityAccessor) consumer;
        var supplierAccessor = (ItemEntityAccessor) supplier;

        consumerAccessor.setPickupDelay(Math.max(consumerAccessor.getPickupDelay(), supplierAccessor.getPickupDelay()));
        consumerAccessor.setAge(Math.min(consumerAccessor.getAge(), supplierAccessor.getAge()));

        grow(consumer, getTotalCount(supplier));

        setExtraItemCount(supplier, 0);
        supplier.setItem(ItemStack.EMPTY);
        supplier.discard();
    }

    public static boolean isBlackListItem(ItemStack itemStack) {
        if (!Staaaaaaaaaaaack.commonConfig.isEnableForUnstackableItem() && itemStack.getMaxStackSize() == 1) {
            return true;
        }

        return itemStack.is(Staaaaaaaaaaaack.BLACK_LIST_TAG)
                || Staaaaaaaaaaaack.getItemBlackList().contains(itemStack.getItem());
    }
}
