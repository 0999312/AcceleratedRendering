package com.github.argon4w.acceleratedrendering.core.buffers;

import com.github.argon4w.acceleratedrendering.core.buffers.builders.AcceleratedBufferBuilder;
import com.github.argon4w.acceleratedrendering.core.buffers.environments.IBufferEnvironment;
import com.github.argon4w.acceleratedrendering.core.gl.programs.Program;
import com.github.argon4w.acceleratedrendering.core.programs.culling.ICullingProgram;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static org.lwjgl.opengl.GL46.*;

public class AcceleratedBufferSource extends MultiBufferSource.BufferSource implements IAcceleratedBufferSource {

    public static final AcceleratedBufferSource CORE = initCoreBufferSource();

    public static AcceleratedBufferSource initCoreBufferSource() {
        return new AcceleratedBufferSource(IBufferEnvironment.CORE);
    }

    private final IBufferEnvironment bufferEnvironment;
    private final AcceleratedBufferSetPool acceleratedBufferSetPool;
    private final Map<RenderType, ByteBufferBuilder> vanillaBuffers;

    private final Map<RenderType, AcceleratedBufferBuilder> acceleratedBuilders;
    private final Map<RenderType, BufferBuilder> vanillaBuilders;

    private AcceleratedBufferSetPool.BufferSet bufferSet;

    public AcceleratedBufferSource(IBufferEnvironment bufferEnvironment) {
        super(null, null);

        this.bufferEnvironment = bufferEnvironment;
        this.acceleratedBufferSetPool = new AcceleratedBufferSetPool(this.bufferEnvironment);
        this.vanillaBuffers = new Object2ObjectLinkedOpenHashMap<>();

        this.acceleratedBuilders = new Object2ObjectLinkedOpenHashMap<>();
        this.vanillaBuilders = new Object2ObjectLinkedOpenHashMap<>();

        this.bufferSet = acceleratedBufferSetPool.getBufferSet();
    }

    @Override
    public void endLastBatch() {

    }

    @Override
    public void endBatch() {

    }

    @Override
    public void endBatch(RenderType pRenderType) {

    }

    @Override
    public @NotNull VertexConsumer getBuffer(@NotNull RenderType pRenderType) {
        return bufferEnvironment.isAccelerated(pRenderType.format)
                ? getAcceleratedBuffer(pRenderType)
                : getVanillaBuffer(pRenderType);
    }

    @Override
    public IBufferEnvironment getBufferEnvironment() {
        return bufferEnvironment;
    }

    public void drawBuffers() {
        if (!vanillaBuilders.isEmpty()) {
            drawVanillaBuffers();
        }

        if (!acceleratedBuilders.isEmpty()) {
            drawAcceleratedBuffers();
        }
    }

    public VertexConsumer getAcceleratedBuffer(RenderType renderType) {
        AcceleratedBufferBuilder builder = acceleratedBuilders.get(renderType);

        if (builder != null) {
            return builder;
        }

        builder = AcceleratedBufferBuilder.create(
                bufferSet.getElementBuffer(renderType),
                bufferEnvironment,
                bufferSet,
                renderType
        );

        acceleratedBuilders.put(renderType, builder);
        return builder;
    }

    public VertexConsumer getVanillaBuffer(RenderType renderType) {
        BufferBuilder builder = vanillaBuilders.get(renderType);

        if (builder != null) {
            return builder;
        }

        ByteBufferBuilder buffer = vanillaBuffers.get(renderType);

        if (buffer == null) {
            buffer = new ByteBufferBuilder(renderType.bufferSize);
            vanillaBuffers.put(renderType, buffer);
        }

        builder = new BufferBuilder(buffer, renderType.mode, renderType.format);
        vanillaBuilders.put(renderType, builder);

        return builder;
    }

    public void drawAcceleratedBuffers() {
        Program transformProgram = bufferEnvironment.selectTransformProgram();
        transformProgram.useProgram();

        bufferSet.bindTransformBuffers();
        bufferEnvironment.getServerMeshBuffer().bindBase(GL_SHADER_STORAGE_BUFFER, 4);

        glDispatchCompute(bufferSet.getVertexCount(), 1, 1);
        glMemoryBarrier(GL_SHADER_STORAGE_BARRIER_BIT);

        transformProgram.resetProgram();

        BufferUploader.reset();
        bufferSet.bindVertexArray();

        for (RenderType renderType : acceleratedBuilders.keySet()) {
            VertexFormat.Mode mode = renderType.mode;
            ElementBuffer elementBuffer = bufferSet.getElementBuffer(renderType);
            AcceleratedBufferBuilder builder = acceleratedBuilders.get(renderType);
            ICullingProgram program = bufferEnvironment.selectCullProgram(renderType);

            program.useProgram();
            program.uploadUniforms();

            bufferSet.bindCullingBuffers(elementBuffer.getBufferSize());
            elementBuffer.bindBase(GL_SHADER_STORAGE_BUFFER, 5);

            int count = program.getCount(
                    mode,
                    elementBuffer,
                    builder
            );

            glDispatchCompute(count, 1, 1);

            glMemoryBarrier(
                    GL_SHADER_STORAGE_BARRIER_BIT
                            | GL_ATOMIC_COUNTER_BARRIER_BIT
            );

            program.resetProgram();

            bufferSet.bindDrawBuffers();

            renderType.setupRenderState();
            bufferEnvironment.setupBufferState();

            ShaderInstance shader = RenderSystem.getShader();

            shader.setDefaultUniforms(
                    mode,
                    RenderSystem.getModelViewMatrix(),
                    RenderSystem.getProjectionMatrix(),
                    Minecraft.getInstance().getWindow());
            shader.apply();

            glDrawElementsIndirect(
                    mode.asGLMode,
                    VertexFormat.IndexType.INT.asGLType,
                    0
            );

            glMemoryBarrier(
                    GL_ELEMENT_ARRAY_BARRIER_BIT
                            | GL_COMMAND_BARRIER_BIT
            );

            shader.clear();
            renderType.clearRenderState();
        }

        bufferSet.resetVertexArray();
        acceleratedBuilders.clear();
    }

    public void drawVanillaBuffers() {
        for (RenderType renderType : vanillaBuilders.keySet()) {
            ByteBufferBuilder buffer = vanillaBuffers.get(renderType);
            MeshData meshData = vanillaBuilders.get(renderType).build();

            if (meshData == null) {
                continue;
            }

            if (renderType.sortOnUpload) {
                meshData.sortQuads(buffer, RenderSystem.getVertexSorting());
            }

            renderType.draw(meshData);
        }

        vanillaBuilders.clear();
    }

    public void clearBuffers() {
        for (ByteBufferBuilder builder : vanillaBuffers.values()) {
            builder.clear();
        }

        bufferSet.reset();
        bufferSet.setInFlight();
        bufferSet = acceleratedBufferSetPool.getBufferSet();
    }
}
