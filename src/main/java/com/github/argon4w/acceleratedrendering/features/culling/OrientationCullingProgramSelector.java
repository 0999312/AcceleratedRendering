package com.github.argon4w.acceleratedrendering.features.culling;

import com.github.argon4w.acceleratedrendering.core.programs.culling.ICullingProgramSelector;
import com.github.argon4w.acceleratedrendering.core.programs.dispatchers.IPolygonProgramDispatcher;
import com.github.argon4w.acceleratedrendering.core.programs.extras.FlagsExtraVertexData;
import com.github.argon4w.acceleratedrendering.core.programs.extras.IExtraVertexData;
import com.github.argon4w.acceleratedrendering.core.utils.RenderTypeUtils;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class OrientationCullingProgramSelector implements ICullingProgramSelector {

    public static final FlagsExtraVertexData EMPTY = new FlagsExtraVertexData();
    public static final FlagsExtraVertexData NO_CULL = new FlagsExtraVertexData(0);

    private final ICullingProgramSelector parent;
    private final VertexFormat.Mode mode;
    private final IPolygonProgramDispatcher dispatcher;

    public OrientationCullingProgramSelector(
            ICullingProgramSelector parent,
            VertexFormat.Mode mode,
            ResourceLocation key
    ) {
        this.parent = parent;
        this.mode = mode;
        this.dispatcher = new OrientationCullingProgramDispatcher(mode, key);
    }

    @Override
    public IPolygonProgramDispatcher select(RenderType renderType) {
        if (!OrientationCullingFeature.isEnabled()) {
            return parent.select(renderType);
        }

        if (this.mode != renderType.mode) {
            return parent.select(renderType);
        }

        if (OrientationCullingFeature.shouldIgnoreCullState()) {
            return dispatcher;
        }

        if (RenderTypeUtils.isCulled(renderType)) {
            return dispatcher;
        }

        return parent.select(renderType);
    }

    @Override
    public IExtraVertexData getExtraVertex(VertexFormat.Mode mode) {
        if (!OrientationCullingFeature.isEnabled()) {
            return parent.getExtraVertex(mode);
        }

        if (this.mode != mode) {
            return parent.getExtraVertex(mode);
        }

        if (!OrientationCullingFeature.shouldCull()) {
            return EMPTY;
        }

        return NO_CULL;
    }
}
