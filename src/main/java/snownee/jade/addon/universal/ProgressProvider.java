package snownee.jade.addon.universal;

import java.util.Optional;
import java.util.function.Function;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec2;
import snownee.jade.api.Accessor;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.Identifiers;
import snownee.jade.api.TooltipPosition;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.BoxStyle;
import snownee.jade.api.ui.IElementHelper;
import snownee.jade.api.view.ClientViewGroup;
import snownee.jade.api.view.ViewGroup;
import snownee.jade.impl.WailaClientRegistration;
import snownee.jade.impl.WailaCommonRegistration;
import snownee.jade.impl.ui.HorizontalLineElement;
import snownee.jade.impl.ui.ScaledTextElement;

public enum ProgressProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {

	INSTANCE;

	public static void append(ITooltip tooltip, Accessor<?> accessor, IPluginConfig config) {
		if (accessor.getServerData().contains("JadeProgress")) {
			var provider = Optional.ofNullable(ResourceLocation.tryParse(accessor.getServerData().getString("JadeProgressUid"))).map(WailaClientRegistration.INSTANCE.progressProviders::get);
			if (provider.isPresent()) {
				var groups = provider.get().getClientGroups(accessor, ViewGroup.readList(accessor.getServerData(), "JadeProgress", Function.identity()));
				if (groups.isEmpty()) {
					return;
				}

				IElementHelper helper = IElementHelper.get();
				boolean renderGroup = groups.size() > 1 || groups.get(0).shouldRenderGroup();
				var box = new BoxStyle();
				box.bgColor = 0x88000000;
				ClientViewGroup.tooltip(tooltip, groups, renderGroup, (theTooltip, group) -> {
					if (renderGroup) {
						if (group.title != null) {
							theTooltip.add(new HorizontalLineElement());
							theTooltip.append(new ScaledTextElement(group.title, 0.5F));
							theTooltip.append(new HorizontalLineElement());
						} else if (group.bgColor == 0) {
							theTooltip.add(new HorizontalLineElement());
						}
					}
					for (var view : group.views) {
						if (view.text != null)
							theTooltip.add(new ScaledTextElement(view.text, 0.75F));
						theTooltip.add(helper.progress(view.progress, null, view.style, box, false).size(new Vec2(10, 4)));
					}
				});
			}
		}
	}

	public static void putData(Accessor<?> accessor) {
		CompoundTag tag = accessor.getServerData();
		Object target = accessor.getTarget();
		ServerPlayer player = (ServerPlayer) accessor.getPlayer();
		boolean showDetails = accessor.showDetails();
		for (var provider : WailaCommonRegistration.INSTANCE.progressProviders.get(target)) {
			var groups = provider.getGroups(player, player.serverLevel(), target, showDetails);
			if (groups != null) {
				if (ViewGroup.saveList(tag, "JadeProgress", groups, Function.identity()))
					tag.putString("JadeProgressUid", provider.getUid().toString());
				return;
			}
		}
	}

	@Override
	public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
		append(tooltip, accessor, config);
	}

	@Override
	public void appendServerData(CompoundTag data, BlockAccessor accessor) {
		putData(accessor);
	}

	@Override
	public ResourceLocation getUid() {
		return Identifiers.UNIVERSAL_PROGRESS;
	}

	@Override
	public int getDefaultPriority() {
		return TooltipPosition.BODY + 1000;
	}

	@Override
	public boolean isRequired() {
		return true;
	}

}
