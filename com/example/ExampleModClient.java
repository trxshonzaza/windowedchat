package com.example;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

import com.example.renderer.ExternalRenderer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;

public class ExampleModClient implements ClientModInitializer {

	public boolean show = false;

	@Override
	public void onInitializeClient() {

		System.setProperty("java.awt.headless", "false");

		HudRenderCallback.EVENT.register((matrices, tickDelta) -> {

			if(show) {

				ExternalRenderer.render(matrices);

			}

		});

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(CommandManager.literal("toggle")
				.executes(context -> {

					show = !show;

					context.getSource().sendMessage(Text.literal("toggled window."));

					return 1;

				})));

	}
}