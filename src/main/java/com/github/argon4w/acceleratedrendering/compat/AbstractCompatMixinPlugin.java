package com.github.argon4w.acceleratedrendering.compat;

import net.neoforged.fml.loading.LoadingModList;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public abstract class AbstractCompatMixinPlugin implements IMixinConfigPlugin {

	protected abstract String getModID();

	@Override
	public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
		return LoadingModList
				.get			()
				.getModFileById	(getModID()) != null;
	}

	@Override
	public String getRefMapperConfig() {
		return null;
	}

	@Override
	public List<String> getMixins() {
		return null;
	}

	@Override
	public void onLoad(String mixinPackage) {

	}

	@Override
	public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

	}

	@Override
	public void preApply(
			String		targetClassName,
			ClassNode	targetClass,
			String		mixinClassName,
			IMixinInfo	mixinInfo
	) {

	}

	@Override
	public void postApply(
			String		targetClassName,
			ClassNode	targetClass,
			String		mixinClassName,
			IMixinInfo	mixinInfo
	) {

	}
}
