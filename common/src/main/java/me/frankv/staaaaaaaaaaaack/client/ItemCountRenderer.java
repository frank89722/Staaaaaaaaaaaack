package me.frankv.staaaaaaaaaaaack.client;

import com.mojang.blaze3d.vertex.PoseStack;
import me.frankv.staaaaaaaaaaaack.Staaaaaaaaaaaack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.item.ItemEntity;

import static me.frankv.staaaaaaaaaaaack.StxckUtil.getOverlayText;

public class ItemCountRenderer {

    public static void renderItemCount(
            ItemEntity entity,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int light,
            EntityRenderDispatcher entityRenderDispatcher
    ) {
        var player = Minecraft.getInstance().player;
        if (player != null && player.isShiftKeyDown()) return;

        getOverlayText(entity).ifPresent(itemCount -> {
            var scale = 0.025f * (float) Staaaaaaaaaaaack.clientConfig.getOverlaySizeMultiplier();
            poseStack.pushPose();
            poseStack.translate(0d, entity.getBbHeight() + 0.75f, 0d);
            poseStack.mulPose(entityRenderDispatcher.cameraOrientation());
            poseStack.scale(scale, -scale, scale);

            var component = Component.literal(itemCount);
            var matrix4f = poseStack.last().pose();
            var opacity = (int) (Minecraft.getInstance().options.getBackgroundOpacity(0.25F) * 255.0F) << 24;
            var font = Minecraft.getInstance().font;
            var f2 = (float) (-font.width(component)) / 2.5F;
            font.drawInBatch(component, f2, 0, -2130706433, false, matrix4f, bufferSource, Font.DisplayMode.NORMAL, opacity, light);
            font.drawInBatch(component, f2, 0, -1, false, matrix4f, bufferSource, Font.DisplayMode.NORMAL, 0, light);

            poseStack.popPose();
        });
    }

}
