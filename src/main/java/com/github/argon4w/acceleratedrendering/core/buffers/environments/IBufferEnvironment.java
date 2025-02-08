package com.github.argon4w.acceleratedrendering.core.buffers.environments;

import com.github.argon4w.acceleratedrendering.core.gl.buffers.IServerBuffer;
import com.github.argon4w.acceleratedrendering.core.gl.programs.ComputeProgram;
import com.github.argon4w.acceleratedrendering.core.programs.IProgramDispatcher;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import net.minecraft.client.renderer.RenderType;

public interface IBufferEnvironment {

    void setupBufferState();
    void addExtraSharings(long address);
    void addExtraVertex(long address);
    boolean isAccelerated(VertexFormat vertexFormat);
    IServerBuffer getServerMeshBuffer();
    ComputeProgram selectTransformProgram();
    IProgramDispatcher selectCullProgramDispatcher(RenderType renderType);
    IProgramDispatcher selectProcessingProgramDispatcher(VertexFormat.Mode mode);
    RenderType getRenderType(RenderType renderType);
    int getOffset(VertexFormatElement element);
    int getSharingFlags();
    int getVertexSize();

    final class Presets {

        public static final IBufferEnvironment ENTITY = new VanillaBufferEnvironment(DefaultVertexFormat.NEW_ENTITY);
        public static final IBufferEnvironment POS_TEX_COLOR = new VanillaBufferEnvironment(DefaultVertexFormat.POSITION_TEX_COLOR);
        public static final IBufferEnvironment POS_TEX = new VanillaBufferEnvironment(DefaultVertexFormat.POSITION_TEX);

        private Presets() {

        }
    }
}
