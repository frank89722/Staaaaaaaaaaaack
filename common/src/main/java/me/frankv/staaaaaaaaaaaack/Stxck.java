package me.frankv.staaaaaaaaaaaack;


import me.frankv.staaaaaaaaaaaack.config.StxckClientConfig;
import me.frankv.staaaaaaaaaaaack.config.StxckCommonConfig;
import me.frankv.staaaaaaaaaaaack.mixin.ItemEntityAccessor;
import me.frankv.staaaaaaaaaaaack.mixin.ItemStackAccessor;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class Stxck {
    public static final String MODID = "staaaaaaaaaaaack";

    public static final TagKey<Item> BLACK_LIST_TAG = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(MODID, "blacklist"));
    public static final String EXTRA_ITEM_COUNT_TAG = "StxckExtraItemCount";
    private static Set<Item> itemList;
    private static Set<ResourceLocation> dimensionList;

    public static StxckCommonConfig commonConfig;
    public static StxckClientConfig clientConfig;
    private static EntityDataAccessor<Integer> DATA_EXTRA_ITEM_COUNT;


    private static Set<Item> getItemList() {
        if (itemList == null) {
            itemList = commonConfig.getItemList().stream()
                    .map(ResourceLocation::parse)
                    .map(BuiltInRegistries.ITEM::get)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .map(Holder.Reference::value)
                    .collect(Collectors.toUnmodifiableSet());
        }
        return itemList;
    }

    private static Set<ResourceLocation> getDimensionList() {
        if (dimensionList == null) {
            dimensionList = commonConfig.getDimensionList().stream()
                    .map(ResourceLocation::parse)
                    .collect(Collectors.toUnmodifiableSet());
        }
        return dimensionList;
    }

    public static void setDataExtraItemCount(EntityDataAccessor<Integer> entityDataAccessor) {
        if (DATA_EXTRA_ITEM_COUNT != null) return;
        DATA_EXTRA_ITEM_COUNT = entityDataAccessor;
    }

    public static void refillItemStack(ItemEntity entity) {
        var extraItemCount = getExtraItemCount(entity);
        if (extraItemCount <= 0) return;

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
        var maxExtraSize = commonConfig.getMaxSize();
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
        boolean alwaysShowItemCount = clientConfig.isAlwaysShowItemCount();

        return switch (clientConfig.getOverlayDisplayMode()) {
            case ITEM_COUNT -> getTotalCountOverlayText(entity, alwaysShowItemCount);
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
            return Optional.of(String.format("%.3fB", total / 1_000_000_000f));
        }
        if (total >= 1_000_000) {
            return Optional.of(String.format("%.2fM", total / 1_000_000f));
        }
        if (total >= 10_000) {
            return Optional.of(String.format("%.1fK", total / 1_000f));
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

    public static boolean isBlackListItem(ItemEntity itemEntity) {
        var dim = itemEntity.level().dimension().location();
        if (getDimensionList().contains(dim) ^ commonConfig.isDimensionListAsWhitelist()) {
            return true;
        }

        var itemStack = itemEntity.getItem();
        if (!commonConfig.isEnableForUnstackableItem() && itemStack.getMaxStackSize() == 1) {
            return true;
        }

        return itemStack.is(BLACK_LIST_TAG)
                || getItemList().contains(itemStack.getItem()) ^ commonConfig.isItemListAsWhitelist();
    }
}
