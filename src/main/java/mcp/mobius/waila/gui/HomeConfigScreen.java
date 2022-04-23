package mcp.mobius.waila.gui;

import com.mojang.blaze3d.vertex.PoseStack;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.impl.config.PluginConfig;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TranslatableComponent;
import snownee.jade.Jade;

public class HomeConfigScreen extends Screen {

	private final Screen parent;

	public HomeConfigScreen(Screen parent) {
		super(new TranslatableComponent("gui.waila.configuration"));
		this.parent = parent;
	}

	@Override
	protected void init() {
		addRenderableWidget(new Button(width / 2 - 105, height / 2 - 10, 100, 20, new TranslatableComponent("gui.waila.waila_settings", Jade.NAME), w -> {
			minecraft.setScreen(new WailaConfigScreen(HomeConfigScreen.this));
		}));
		addRenderableWidget(new Button(width / 2 + 5, height / 2 - 10, 100, 20, new TranslatableComponent("gui.waila.plugin_settings"), w -> {
			minecraft.setScreen(new PluginsConfigScreen(HomeConfigScreen.this));
		}));
		addRenderableWidget(new Button(width / 2 - 50, height / 2 + 20, 100, 20, new TranslatableComponent("gui.done"), w -> {
			Waila.CONFIG.save();
			PluginConfig.INSTANCE.save();
			minecraft.setScreen(parent);
		}));
	}

	@Override
	public void render(PoseStack matrixStack, int x, int y, float partialTicks) {
		renderBackground(matrixStack);
		drawCenteredString(matrixStack, font, title.getString(), width / 2, height / 3, 16777215);
		super.render(matrixStack, x, y, partialTicks);
	}
}
