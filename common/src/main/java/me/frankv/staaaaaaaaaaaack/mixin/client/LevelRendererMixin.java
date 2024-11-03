package me.frankv.staaaaaaaaaaaack.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import me.frankv.staaaaaaaaaaaack.Staaaaaaaaaaaack;
import me.frankv.staaaaaaaaaaaack.client.ItemCountRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(value = LevelRenderer.class)
public abstract class LevelRendererMixin {

    @Shadow
    @Final
    private EntityRenderDispatcher entityRenderDispatcher;


    @SuppressWarnings("unchecked")
    @Inject(
            method = "renderEntity",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/entity/EntityRenderDispatcher;render(Lnet/minecraft/world/entity/Entity;DDDFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"
            )
    )
    private void renderItemCount(
            Entity entity,
            double x, double y, double z,
            float partialTicks,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            CallbackInfo ci
    ) {
        if (entityRenderDispatcher.camera == null) return; // Optifine still leaves this as null when renderEntity is called
        var maxDistance = Staaaaaaaaaaaack.clientConfig.getMinItemCountRenderDistance();
        if (entityRenderDispatcher.distanceToSqr(entity) > maxDistance * maxDistance) return;
        if (entity instanceof ItemEntity itemEntity) {
            var renderer = (EntityRenderer<Entity, EntityRenderState>) entityRenderDispatcher.getRenderer(entity);
            var renderState = renderer.createRenderState(itemEntity, partialTicks);
            var offset = renderer.getRenderOffset(renderState);
            var light = entityRenderDispatcher.getPackedLightCoords(entity, partialTicks);
            var nx = Mth.lerp(partialTicks, entity.xOld, entity.getX()) - x + offset.x();
            var ny = Mth.lerp(partialTicks, entity.yOld, entity.getY()) - y + offset.y();
            var nz = Mth.lerp(partialTicks, entity.zOld, entity.getZ()) - z + offset.z();

            poseStack.pushPose();
            poseStack.translate(nx, ny, nz);
            ItemCountRenderer.renderItemCount(itemEntity, poseStack, bufferSource, light, entityRenderDispatcher);
            poseStack.popPose();
        }
    }

}
