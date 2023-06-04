package me.frankv.staaaaaaaaaaaack.mixin;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ItemEntity.class)
public interface ItemEntityAccessor {
    @Accessor
    int getPickupDelay();

    @Accessor @Mutable
    void setPickupDelay(int pickupDelay);

    @Accessor
    int getAge();

    @Accessor @Mutable
    void setAge(int age);

    @Invoker("merge")
    static void invokeMerge(ItemEntity itemEntity, ItemStack itemStack, ItemEntity itemEntity1, ItemStack itemStack1) {
        throw new AssertionError();
    }
}
