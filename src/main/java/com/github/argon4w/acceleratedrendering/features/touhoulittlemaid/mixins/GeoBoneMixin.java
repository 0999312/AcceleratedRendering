package com.github.argon4w.acceleratedrendering.features.touhoulittlemaid.mixins;

import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.builders.IAcceleratedVertexConsumer;
import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.renderers.IAcceleratedRenderer;
import com.github.argon4w.acceleratedrendering.core.buffers.graphs.IBufferGraph;
import com.github.argon4w.acceleratedrendering.core.meshes.IMesh;
import com.github.argon4w.acceleratedrendering.core.meshes.collectors.MeshCollectorCuller;
import com.github.argon4w.acceleratedrendering.features.entities.AcceleratedEntityRenderingFeature;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.render.built.GeoBone;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.render.built.GeoCube;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.render.built.GeoQuad;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.render.built.GeoVertex;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.renderer.RenderType;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;
import java.util.Map;

@Mixin(GeoBone.class)
public class GeoBoneMixin implements IAcceleratedRenderer<Void> {

    @Shadow @Final private List<GeoCube> cubes;

    @Unique private final Map<IBufferGraph, IMesh> meshes = new Object2ObjectOpenHashMap<>();

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

        for (GeoCube cube : cubes) {
            for (GeoQuad quad : cube.quads) {
                Vector3f polygonNormal = quad.normal;

                for (GeoVertex vertex : quad.vertices) {
                    Vector3f vertexPosition = vertex.position;

                    meshBuilder.addVertex(
                            vertexPosition.x,
                            vertexPosition.y,
                            vertexPosition.z,
                            -1,
                            vertex.textureU,
                            vertex.textureV,
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
