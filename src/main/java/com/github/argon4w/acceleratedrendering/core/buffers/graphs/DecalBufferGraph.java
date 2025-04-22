package com.github.argon4w.acceleratedrendering.core.buffers.graphs;

import org.joml.Matrix4f;

import java.util.Objects;

public class DecalBufferGraph implements IBufferGraph {

    private final IBufferGraph parent;
    private final Matrix4f localTransform;

    public DecalBufferGraph(IBufferGraph parent, Matrix4f localTransform) {
        this.parent = parent;
        this.localTransform = localTransform;
    }

    @Override
    public int hashCode() {
        return parent.hashCode() ^ localTransform.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (obj.getClass() != this.getClass()) {
            return false;
        }

        DecalBufferGraph that = (DecalBufferGraph) obj;

        return Objects.equals(parent, that.parent)
                && localTransform.equals(that.localTransform, 1e-5f);
    }
}
