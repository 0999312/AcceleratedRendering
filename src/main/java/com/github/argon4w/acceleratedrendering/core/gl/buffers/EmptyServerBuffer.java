package com.github.argon4w.acceleratedrendering.core.gl.buffers;

public class EmptyServerBuffer implements IServerBuffer {

    public static final EmptyServerBuffer INSTANCE = new EmptyServerBuffer();

    @Override
    public int getBufferHandle() {
        return 0;
    }

    @Override
    public void bind(int target) {

    }

    @Override
    public void bindBase(int target, int index) {

    }
}
