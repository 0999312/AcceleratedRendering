package com.github.argon4w.acceleratedrendering.compat.iris.mixins;

import com.github.argon4w.acceleratedrendering.AcceleratedRenderingModEntry;
import com.github.argon4w.acceleratedrendering.compat.iris.programs.IrisPrograms;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AcceleratedRenderingModEntry.class)
public class AcceleratedRenderingModEntryMixin {

    @Inject(method = "<init>", at = @At("TAIL"))
    public void registerIrisEvents(IEventBus modEventBus, ModContainer modContainer, CallbackInfo ci) {
        modEventBus.register(IrisPrograms.class);
    }
}
