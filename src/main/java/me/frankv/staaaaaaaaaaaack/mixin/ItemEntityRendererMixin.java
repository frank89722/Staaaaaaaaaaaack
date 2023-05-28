package me.frankv.staaaaaaaaaaaack.mixin;

import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import static me.frankv.staaaaaaaaaaaack.StxckUtil.*;

@Mixin(ItemEntityRenderer.class)
public class ItemEntityRendererMixin {
//
//    @ModifyVariable(
//            method = "render(Lnet/minecraft/world/entity/item/ItemEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
//            at = @At("STORE"),
//            ordinal = 1
//    )
//    private int largerRenderAmount(int j, ItemEntity entity) {
//        var count = getTotalCount(entity);
//
//        if (count > 1024) {
//            return 15;
//        } else if (count > 896) {
//            return 14;
//        } else if (count > 768) {
//            return 13;
//        } else if (count > 640) {
//            return 12;
//        } else if (count > 512) {
//            return 11;
//        } else if (count > 384) {
//            return 10;
//        } else if (count > 256) {
//            return 9;
//        } else if (count > 128) {
//            return 8;
//        } else if (count > 96) {
//            return 7;
//        } else if (count > 64) {
//            return 6;
//        } else if (count > 48) {
//            return 5;
//        } else if (count > 32) {
//            return 4;
//        } else if (count > 16) {
//            return 3;
//        } else if (count > 1) {
//            return 2;
//        } else {
//            return 1;
//        }
//    }
//
//    @ModifyArgs(
//            method = "render(Lnet/minecraft/world/entity/item/ItemEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
//            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(DDD)V")
//    )
//    private void inj(Args args) {
//        for (int i = 0; i < 3; ++i) {
//            double n = args.get(i);
//            if (n > 0f) {
//                args.set(i, n * 1.5f);
//            }
//        }
//    }
}
