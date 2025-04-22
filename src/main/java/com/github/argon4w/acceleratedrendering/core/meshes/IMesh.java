package com.github.argon4w.acceleratedrendering.core.meshes;

import com.github.argon4w.acceleratedrendering.core.buffers.accelerated.builders.IAcceleratedVertexConsumer;
import com.github.argon4w.acceleratedrendering.core.meshes.collectors.MeshCollector;

public interface IMesh {

    void write(IAcceleratedVertexConsumer extension, int color, int light, int overlay);

    interface Builder {

        IMesh build(MeshCollector collector);
        void close();
    }
}
