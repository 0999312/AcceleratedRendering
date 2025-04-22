package com.github.argon4w.acceleratedrendering.core.buffers.graphs;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;

import java.util.Objects;

public class SpriteBufferGraph implements IBufferGraph {

    private final IBufferGraph parent;
    private final TextureAtlasSprite sprite;

    public SpriteBufferGraph(IBufferGraph parent, TextureAtlasSprite sprite) {
        this.parent = parent;
        this.sprite = sprite;
    }

    @Override
    public int hashCode() {
        return parent.hashCode() ^ sprite.hashCode();
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

        SpriteBufferGraph that = (SpriteBufferGraph) obj;

        return Objects.equals(parent, that.parent)
                && Objects.equals(sprite, that.sprite);
    }
}
