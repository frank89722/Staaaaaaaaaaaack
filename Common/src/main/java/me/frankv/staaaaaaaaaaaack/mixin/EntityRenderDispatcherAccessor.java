package me.frankv.staaaaaaaaaaaack.mixin;

import com.mojang.math.Quaternion;
import net.minecraft.client.gui.Font;
import net.minecraft.world.entity.Entity;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(EntityRenderDispatcher.class)
public interface EntityRenderDispatcherAccessor {
    @Accessor("font")
    Font getFont();
    @Accessor("cameraOrientation")
    Quaternion getCameraOrientation();
    @Invoker("distanceToSqr")
    double invokeDistanceToSqr(Entity entity);
}
