package me.frankv.staaaaaaaaaaaack;

import me.frankv.staaaaaaaaaaaack.mixin.ItemEntityAccessor;
import me.frankv.staaaaaaaaaaaack.mixin.ItemStackAccessor;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;
import java.util.function.BiPredicate;

public class StxckUtil {
    public static final String EXTRA_ITEM_COUNT_TAG = "StxckExtraItemCount";
    private static EntityDataAccessor<Integer> DATA_EXTRA_ITEM_COUNT;

    public static BiPredicate<ItemStack, ItemStack> itemStackMergablePredicate = null;


    public static void setDataExtraItemCount(EntityDataAccessor<Integer> entityDataAccessor) {
        if (DATA_EXTRA_ITEM_COUNT != null) return;
        DATA_EXTRA_ITEM_COUNT = entityDataAccessor;
    }

    public static void refillItemStack(ItemEntity entity) {
        var extraItemCount = getExtraItemCount(entity);
        if (extraItemCount <= 0) return ;

        var stack = entity.getItem();
        var maxSize = ((ItemStackAccessor) (Object) stack).accessItem().getMaxStackSize();
//        if (stack == ItemStack.EMPTY || stack.is(Items.AIR) || stack.getCount() == maxSize) return;
        if (stack.getCount() == maxSize) return;

        var x = maxSize - stack.getCount();
        var refillCount = Math.min(x, extraItemCount);

        stack.grow(refillCount);
        setExtraItemCount(entity, extraItemCount - refillCount);
        entity.setItem(stack.copy());
    }

    public static boolean areMergable(ItemStack itemStack, ItemStack itemStack1) {
        if (itemStackMergablePredicate != null) {
            return itemStackMergablePredicate.test(itemStack, itemStack1);
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
        itemEntity.getItem().setCount(0);

        if (getTotalCount(itemEntity) == 0) return false;
        refillItemStack(itemEntity);
        return true;
    }

    public static boolean tryToMerge(ItemEntity itemEntity, ItemEntity itemEntity1) {
        if (Objects.equals(itemEntity.getOwner(), itemEntity1.getOwner())
                && areMergable(itemEntity.getItem(), itemEntity1.getItem())) {
            if (getTotalCount(itemEntity1) < getTotalCount(itemEntity)) {
                merge(itemEntity, itemEntity1);
            } else {
                merge(itemEntity1, itemEntity);
            }
            return true;
        }
        return false;
    }

    public static void merge(ItemEntity itemEntity, ItemEntity itemEntity1) {
        var entityAccessor = (ItemEntityAccessor) itemEntity;
        var entityAccessor1 = (ItemEntityAccessor) itemEntity1;

        entityAccessor.setPickupDelay(Math.max(entityAccessor.getPickupDelay(), entityAccessor1.getPickupDelay()));
        entityAccessor.setAge(Math.min(entityAccessor.getAge(), entityAccessor1.getAge()));

        grow(itemEntity, getTotalCount(itemEntity1));

        setExtraItemCount(itemEntity1, 0);
        itemEntity1.setItem(ItemStack.EMPTY);
        itemEntity1.discard();
    }

    public static boolean isBlackListItem(ItemStack itemStack) {
        return (!Staaaaaaaaaaaack.commonConfig.isEnableForUnstackableItem() && itemStack.getMaxStackSize() == 1)
                || (itemStack.is(Staaaaaaaaaaaack.BLACK_LIST_TAG)
                || Staaaaaaaaaaaack.getItemBlackList().contains(itemStack.getItem()));
    }
}
