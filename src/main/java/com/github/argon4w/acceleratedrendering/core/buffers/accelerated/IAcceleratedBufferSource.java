package com.github.argon4w.acceleratedrendering.core.buffers.accelerated;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;

public interface IAcceleratedBufferSource extends MultiBufferSource {

    boolean isAccelerated(RenderType renderType);
    void drawBuffers();
    void clearBuffers();
}
