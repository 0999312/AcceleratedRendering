package com.github.argon4w.acceleratedrendering.compat.iris.mixins.acceleratedrendering;

import com.github.argon4w.acceleratedrendering.compat.iris.IrisBufferEnvironment;
import com.github.argon4w.acceleratedrendering.core.buffers.environments.IBufferEnvironment;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.irisshaders.iris.vertices.IrisVertexFormats;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(IBufferEnvironment.Presets.class)
public class IBufferEnvironmentPresetsMixin {

    @Mutable @Shadow @Final private static IBufferEnvironment ENTITY;

    @WrapOperation(method = "<clinit>", at = @At(value = "FIELD", target = "Lcom/github/argon4w/acceleratedrendering/core/buffers/environments/IBufferEnvironment$Presets;ENTITY:Lcom/github/argon4w/acceleratedrendering/core/buffers/environments/IBufferEnvironment;", opcode = Opcodes.PUTSTATIC))
    private static void useIrisEntityEnvironment(IBufferEnvironment value, Operation<Void> original) {
        original.call(new IrisBufferEnvironment(
                value,
                DefaultVertexFormat.NEW_ENTITY,
                IrisVertexFormats.ENTITY
        ));
    }
}
