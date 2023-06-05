package me.frankv.staaaaaaaaaaaack.client;

import com.mojang.blaze3d.vertex.PoseStack;
import me.frankv.staaaaaaaaaaaack.mixin.client.EntityRenderDispatcherAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.item.ItemEntity;

import static me.frankv.staaaaaaaaaaaack.StxckUtil.getTotalCountForDisplay;

public class ItemCountRenderer {
    public static void renderItemCount(
            ItemEntity entity,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int light,
            EntityRenderDispatcherAccessor entityRenderDispatcher
    ) {
        getTotalCountForDisplay(entity).ifPresent(itemCount -> {
            poseStack.pushPose();
            poseStack.translate(0.0D, entity.getBbHeight() + 0.75f, 0.0D);
            poseStack.mulPose(entityRenderDispatcher.getCameraOrientation());
            poseStack.scale(-0.025F, -0.025F, 0.025F);

            var component = Component.literal(itemCount);
            var matrix4f = poseStack.last().pose();
            var f1 = Minecraft.getInstance().options.getBackgroundOpacity(0.25F);
            var font = entityRenderDispatcher.getFont();
            var j = (int)(f1 * 255.0F) << 24;
            var f2 = (float)(-font.width(component) / 2);
            font.drawInBatch(component, f2, 0, 553648127, false, matrix4f, bufferSource, false, j, light);
            font.drawInBatch(component, f2, 0, -1, false, matrix4f, bufferSource, false, 0, light);

            poseStack.popPose();
        });
    }
}
