package com.github.argon4w.acceleratedrendering.features.touhoulittlemaid.mixins;

import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.builders.IAcceleratedVertexConsumer;
import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.renderers.IAcceleratedRenderer;
import com.github.argon4w.acceleratedrendering.features.entities.AcceleratedEntityRenderingFeature;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.IGeoRenderer;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.animated.AnimatedGeoBone;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.util.FastColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(IGeoRenderer.class)
public interface IGeoRendererMixin {

    @SuppressWarnings("unchecked")
    @Inject(method = "renderCubesOfBone", at = @At(value = "INVOKE", target = "Lcom/github/tartaricacid/touhoulittlemaid/geckolib3/geo/animated/AnimatedGeoBone;geoBone()Lcom/github/tartaricacid/touhoulittlemaid/geckolib3/geo/render/built/GeoBone;"), cancellable = true)
    default void renderBoneFast(
            AnimatedGeoBone bone,
            PoseStack poseStack,
            VertexConsumer buffer,
            int packedLight,
            int packedOverlay,
            float red,
            float green,
            float blue,
            float alpha,
            CallbackInfo ci
    ) {
        IAcceleratedVertexConsumer extension = (IAcceleratedVertexConsumer) buffer;

        if (!AcceleratedEntityRenderingFeature.isEnabled()) {
            return;
        }

        if (!AcceleratedEntityRenderingFeature.shouldUseAcceleratedPipeline()) {
            return;
        }

        if (!extension.isAccelerated()) {
            return;
        }

        ci.cancel();

        PoseStack.Pose pose = poseStack.last();

        extension.doRender(
                (IAcceleratedRenderer<Void>) bone.geoBone(),
                null,
                pose.pose(),
                pose.normal(),
                packedLight,
                packedOverlay,
                FastColor.ARGB32.color(
                        (int) (alpha * 255.0f),
                        (int) (red * 255.0f),
                        (int) (green * 255.0f),
                        (int) (blue * 255.0f)
                )
        );
    }
}
