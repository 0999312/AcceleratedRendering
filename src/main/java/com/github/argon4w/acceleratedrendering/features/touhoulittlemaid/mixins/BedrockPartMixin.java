package com.github.argon4w.acceleratedrendering.features.touhoulittlemaid.mixins;

import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.builders.IAcceleratedVertexConsumer;
import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.renderers.IAcceleratedRenderer;
import com.github.argon4w.acceleratedrendering.core.buffers.graphs.IBufferGraph;
import com.github.argon4w.acceleratedrendering.core.meshes.IMesh;
import com.github.argon4w.acceleratedrendering.core.meshes.collectors.MeshCollectorCuller;
import com.github.argon4w.acceleratedrendering.features.entities.AcceleratedEntityRenderingFeature;
import com.github.tartaricacid.touhoulittlemaid.client.model.bedrock.BedrockCube;
import com.github.tartaricacid.touhoulittlemaid.client.model.bedrock.BedrockPart;
import com.github.tartaricacid.touhoulittlemaid.client.model.bedrock.BedrockPolygon;
import com.github.tartaricacid.touhoulittlemaid.client.model.bedrock.BedrockVertex;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.FastColor;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(BedrockPart.class)
public class BedrockPartMixin implements IAcceleratedRenderer<Void> {

    @Shadow @Final public ObjectList<BedrockCube> cubes;

    @Unique private final Map<IBufferGraph, IMesh> meshes = new Object2ObjectOpenHashMap<>();

    @Inject(method = "compile", at = @At("HEAD"), cancellable = true)
    public void compileFast(
            PoseStack.Pose pose,
            VertexConsumer consumer,
            int texU,
            int texV,
            float red,
            float green,
            float blue,
            float alpha,
            CallbackInfo ci
    ) {
        IAcceleratedVertexConsumer extension = (IAcceleratedVertexConsumer) consumer;

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

        extension.doRender(
                this,
                null,
                pose.pose(),
                pose.normal(),
                texU,
                texV,
                FastColor.ARGB32.color(
                        (int) (alpha * 255.0f),
                        (int) (red * 255.0f),
                        (int) (green * 255.0f),
                        (int) (blue * 255.0f)
                )
        );
    }

    @Override
    public void render(
            VertexConsumer vertexConsumer,
            Void context,
            Matrix4f transform,
            Matrix3f normal,
            int light,
            int overlay,
            int color
    ) {
        IAcceleratedVertexConsumer extension = ((IAcceleratedVertexConsumer) vertexConsumer);

        IBufferGraph bufferGraph = extension.getBufferGraph();
        RenderType renderType = extension.getRenderType();

        IMesh mesh = meshes.get(bufferGraph);

        extension.beginTransform(transform, normal);

        if (mesh != null) {
            mesh.write(
                    extension,
                    color,
                    light,
                    overlay
            );

            extension.endTransform();
            return;
        }

        MeshCollectorCuller meshCollectorCuller = new MeshCollectorCuller(renderType);
        VertexConsumer meshBuilder = extension.decorate(meshCollectorCuller);

        for (BedrockCube cube : cubes) {
            for (BedrockPolygon polygon : cube.getPolygons()) {
                Vector3f polygonNormal = polygon.normal;

                for (BedrockVertex vertex : polygon.vertices) {
                    Vector3f vertexPosition = vertex.pos;

                    meshBuilder.addVertex(
                            vertexPosition.x / 16.0f,
                            vertexPosition.y / 16.0f,
                            vertexPosition.z / 16.0f,
                            -1,
                            vertex.u,
                            vertex.v,
                            overlay,
                            0,
                            polygonNormal.x,
                            polygonNormal.y,
                            polygonNormal.z
                    );
                }
            }
        }

        meshCollectorCuller.flush();

        mesh = AcceleratedEntityRenderingFeature
                .getMeshType()
                .getBuilder()
                .build(meshCollectorCuller.getMeshCollector());

        meshes.put(bufferGraph, mesh);
        mesh.write(
                extension,
                color,
                light,
                overlay
        );

        extension.endTransform();
    }
}
