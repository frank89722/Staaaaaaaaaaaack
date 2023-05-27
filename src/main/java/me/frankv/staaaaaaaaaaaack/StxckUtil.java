package me.frankv.staaaaaaaaaaaack;

import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;


public class StxckUtil {
    public static final EntityDataAccessor<Integer> DATA_EXTRA_ITEM_COUNT = SynchedEntityData.defineId(ItemEntity.class, EntityDataSerializers.INT);

    public static void refillItemStack(ItemEntity entity) {
        var extraItemCount = getExtraItemCount(entity);
        if (extraItemCount == 0) return ;

        var stack = entity.getItem();
        final var maxStackSize = stack.getMaxStackSize();
        var x = maxStackSize - stack.getCount();

        if (extraItemCount >= x) {
            stack.setCount(maxStackSize);
            setExtraItemCount(entity, extraItemCount - x);
        } else {
            stack.setCount(extraItemCount);
            setExtraItemCount(entity, 0);
        }
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
        var totalCount = getTotalCount(entity);
        setTotalCount(entity, totalCount + count);
    }

    public static void shrink(ItemEntity entity, int count) {
        grow(entity, -count);
    }

    public static int getTotalCount(ItemEntity entity) {
        return entity.getItem().getCount() + getExtraItemCount(entity);
    }

    public static int getExtraItemCount(ItemEntity entity) {
        return entity.getEntityData().get(DATA_EXTRA_ITEM_COUNT);
    }

    public static void setTotalCount(ItemEntity entity, int count) {
        var item = entity.getItem();
        var x = item.getMaxStackSize() - (item.getMaxStackSize() - item.getCount());

        if (count > x) {
            item.setCount(item.getMaxStackSize());
            setExtraItemCount(entity, count - x);
            return;
        }

        item.grow(count);
    }

    public static void setExtraItemCount(ItemEntity entity, int count) {
        if (count != 0) {
            entity.setCustomName(Component.literal(String.valueOf(entity.getItem().getCount() + count)));
            entity.setCustomNameVisible(true);
        } else {
            entity.setCustomName(null);
            entity.setCustomNameVisible(false);
        }
        entity.getEntityData().set(DATA_EXTRA_ITEM_COUNT, count);
    }
}
