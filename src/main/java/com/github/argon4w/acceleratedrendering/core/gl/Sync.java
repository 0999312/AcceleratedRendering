package com.github.argon4w.acceleratedrendering.core.gl;

import static org.lwjgl.opengl.GL46.*;

public class Sync {

    private long syncHandle;

    public Sync() {
        this.syncHandle = -1;
    }

    public boolean isSyncSet() {
        return syncHandle != -1;
    }

    public boolean isSyncSignaled() {
        return glGetSynci(syncHandle, GL_SYNC_STATUS, null) == GL_SIGNALED;
    }

    public void waitSync() {
        glClientWaitSync(syncHandle, GL_SYNC_FLUSH_COMMANDS_BIT, Long.MAX_VALUE);
    }

    public void setSync() {
        syncHandle = glFenceSync(GL_SYNC_GPU_COMMANDS_COMPLETE, 0);
    }

    public void resetSync() {
        glDeleteSync(syncHandle);
        syncHandle = -1;
    }
}
