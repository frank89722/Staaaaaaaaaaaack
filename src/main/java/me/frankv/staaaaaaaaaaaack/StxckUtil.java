package me.frankv.staaaaaaaaaaaack;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;


public class StxckUtil {
    public static final EntityDataAccessor<Integer> DATA_EXTRA_ITEM_COUNT = SynchedEntityData.defineId(ItemEntity.class, EntityDataSerializers.INT);

    public static void refillItemStack(ItemEntity entity) {
        var extraItemCount = getExtraItemCount(entity);
        if (extraItemCount <= 0) return ;

        var stack = entity.getItem();
        var maxSize = stack.getMaxStackSize();
//        if (stack == ItemStack.EMPTY || stack.is(Items.AIR) || stack.getCount() == maxSize) return;
        if (stack.getCount() == maxSize) return;

        var x = maxSize - stack.getCount();
        var refillCount = Math.min(x, extraItemCount);

        stack.grow(refillCount);
        setExtraItemCount(entity, extraItemCount - refillCount);
        entity.setItem(stack.copy());
    }

    public static boolean areMergable(ItemStack itemStack, ItemStack itemStack1) {
        if (!itemStack1.is(itemStack.getItem())) {
            return false;
        } else if (itemStack1.hasTag() ^ itemStack.hasTag()) {
            return false;
        } else if (!itemStack.areCapsCompatible(itemStack1)) {
            return false;
        } else {
            return !itemStack1.hasTag() || itemStack1.getTag().equals(itemStack.getTag());
        }
    }

    public static void grow(ItemEntity entity, int count) {
        setExtraItemCount(entity, getExtraItemCount(entity) + count);
        refillItemStack(entity);
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
}
