package me.frankv.staaaaaaaaaaaack.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import me.frankv.staaaaaaaaaaaack.client.ItemCountRenderer;
import me.frankv.staaaaaaaaaaaack.StxckCommon;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderDispatcher.class)
public abstract class EntityRenderDispatcherMixin {
    // I have no idea why shadow won't get proper reobf in forge when build with github action so I use accessor for now
    // Help me please 😮‍💨

//    @Final @Shadow private Font font;
//    @Shadow public abstract Quaternionf cameraOrientation();
//    @Shadow public abstract double distanceToSqr(Entity p_114472_);

    @Inject(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/entity/EntityRenderer;render(Lnet/minecraft/world/entity/Entity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
                    shift = At.Shift.AFTER
            )
    )
    private void renderItemCount(
            Entity entity, double x, double y, double z,
            float entityYRotate,
            float partialTicks,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int light,
            CallbackInfo ci
    ) {
        var accessor = (EntityRenderDispatcherAccessor) getSelf();
        if (accessor.invokeDistanceToSqr(entity) > StxckCommon.clientConfig.getMinItemCountRenderDistance()) return;
        if (entity instanceof ItemEntity itemEntity) {
            ItemCountRenderer.renderItemCount(
                    itemEntity,
                    poseStack,
                    bufferSource,
                    light,
                    accessor.getFont(),
                    accessor.getCameraOrientation()
            );
        }
    }

    private EntityRenderDispatcher getSelf() {
        return (EntityRenderDispatcher) (Object) this;
    }

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
