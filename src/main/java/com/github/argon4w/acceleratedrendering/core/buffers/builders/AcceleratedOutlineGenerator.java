package com.github.argon4w.acceleratedrendering.core.buffers.builders;

import com.github.argon4w.acceleratedrendering.core.buffers.environments.IBufferEnvironment;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.RenderType;

import java.nio.ByteBuffer;
import java.util.Set;

public class AcceleratedOutlineGenerator implements VertexConsumer, IVertexConsumerExtension {

    private final IBufferEnvironment bufferEnvironment;
    private final VertexConsumer vertexConsumer;
    private final RenderType renderType;
    private final int teamColor;

    public AcceleratedOutlineGenerator(
            IBufferEnvironment bufferEnvironment,
            VertexConsumer vertexConsumer,
            RenderType renderType,
            int teamColor
    ) {
        this.bufferEnvironment = bufferEnvironment;
        this.vertexConsumer = vertexConsumer;
        this.renderType = renderType;
        this.teamColor = teamColor;
    }

    @Override
    public void beginTransform(PoseStack.Pose pose) {
        ((IVertexConsumerExtension) vertexConsumer).beginTransform(pose);
    }

    @Override
    public void endTransform() {
        ((IVertexConsumerExtension) vertexConsumer).endTransform();
    }

    @Override
    public void addClientMesh(RenderType renderType, ByteBuffer vertexBuffer, int size, int color, int light, int overlay) {
        ((IVertexConsumerExtension) vertexConsumer).addClientMesh(renderType, vertexBuffer, size, teamColor, -1, -1);
    }

    @Override
    public void addServerMesh(RenderType renderType, int offset, int size, int color, int light, int overlay) {
        ((IVertexConsumerExtension) vertexConsumer).addServerMesh(renderType, offset, size, teamColor, -1, -1);
    }

    @Override
    public boolean supportAcceleratedRendering() {
        return true;
    }

    @Override
    public Set<RenderType> getRenderTypes() {
        return Set.of(renderType);
    }

    @Override
    public IBufferEnvironment getBufferEnvironment() {
        return bufferEnvironment;
    }

    @Override
    public VertexConsumer addVertex(float pX, float pY, float pZ) {
        vertexConsumer.addVertex(pX, pY, pZ);
        return this;
    }

    @Override
    public VertexConsumer setColor(int pRed, int pGreen, int pBlue, int pAlpha) {
        return this;
    }

    @Override
    public VertexConsumer setUv(float pU, float pV) {
        vertexConsumer.setUv(pU, pV);
        return this;
    }

    @Override
    public VertexConsumer setUv1(int pU, int pV) {
        return this;
    }

    @Override
    public VertexConsumer setUv2(int pU, int pV) {
        return this;
    }

    @Override
    public VertexConsumer setNormal(float pNormalX, float pNormalY, float pNormalZ) {
        return this;
    }

    @Override
    public void addVertex(
            float pX,
            float pY,
            float pZ,
            int pColor,
            float pU,
            float pV,
            int pPackedOverlay,
            int pPackedLight,
            float pNormalX,
            float pNormalY,
            float pNormalZ) {
        addVertex(pX, pY, pZ)
                .setColor(pColor)
                .setUv(pU, pV);
    }
}
