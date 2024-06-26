package me.frankv.staaaaaaaaaaaack;

import me.frankv.staaaaaaaaaaaack.mixin.ItemEntityAccessor;
import me.frankv.staaaaaaaaaaaack.mixin.ItemStackAccessor;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;
import java.util.Optional;

public class StxckUtil {
    public static final String EXTRA_ITEM_COUNT_TAG = "StxckExtraItemCount";
    private static EntityDataAccessor<Integer> DATA_EXTRA_ITEM_COUNT;


    public static void setDataExtraItemCount(EntityDataAccessor<Integer> entityDataAccessor) {
        if (DATA_EXTRA_ITEM_COUNT != null) return;
        DATA_EXTRA_ITEM_COUNT = entityDataAccessor;
    }

    public static void refillItemStack(ItemEntity entity) {
        var extraItemCount = getExtraItemCount(entity);
        if (extraItemCount <= 0) return ;

        var stack = entity.getItem();
        Optional.ofNullable(((ItemStackAccessor) (Object) stack).accessItem())
                .map(Item::getDefaultMaxStackSize)
                .ifPresent(maxSize -> {
                    if (stack.getCount() == maxSize) return;
                    var x = maxSize - stack.getCount();
                    var refillCount = Math.min(x, extraItemCount);
                    stack.grow(refillCount);
                    setExtraItemCount(entity, extraItemCount - refillCount);
                    entity.setItem(stack.copy());
                });
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

        return ItemStack.isSameItemSameComponents(itemStack, itemStack1);
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

    public static Optional<String> getOverlayText(ItemEntity entity) {
        boolean alwaysShowItemCount = Staaaaaaaaaaaack.clientConfig.isAlwaysShowItemCount();

        return switch(Staaaaaaaaaaaack.clientConfig.getOverlayDisplayMode()) {
            case ITEM_COUNT -> StxckUtil.getTotalCountOverlayText(entity, alwaysShowItemCount);
            case STACK_COUNT -> {
                var maxStackSize = entity.getItem().getMaxStackSize();
                var stackCount = (int) Math.ceil((double) getTotalCount(entity) / maxStackSize);
                var show = stackCount > 1 || alwaysShowItemCount;
                yield show ? Optional.of(String.format("%dx", stackCount)) : Optional.empty();
            }
        };
    }

    private static Optional<String> getTotalCountOverlayText(ItemEntity entity, boolean alwaysShowItemCount) {
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
        if (alwaysShowItemCount || total > entity.getItem().getMaxStackSize()) {
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
        tryToMerge(itemEntity, itemEntity1, false);
    }

    public static void tryToMerge(ItemEntity itemEntity, ItemEntity itemEntity1, boolean mergeToExisted) {
        if (Objects.equals(itemEntity.getOwner(), itemEntity1.getOwner())
                && areMergable(itemEntity, itemEntity1)
        ) {
            if (mergeToExisted || getTotalCount(itemEntity1) < getTotalCount(itemEntity)) {
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
