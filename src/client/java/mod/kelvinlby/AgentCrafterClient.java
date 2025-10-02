package mod.kelvinlby;

import mod.kelvinlby.ui.ConfigScreen;
import mod.kelvinlby.util.SocketUtil;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class AgentCrafterClient implements ClientModInitializer {
	private static KeyBinding openMenuKeyBinding;
	private static AgentCrafterConfig config;
	private static SocketUtil socketUtil;

	@Override
	public void onInitializeClient() {
	config = AgentCrafterConfig.load();
		socketUtil = new SocketUtil();

		if (config.status) {
			socketUtil.start(config.host, config.port);
		}

		// Register key binding
		openMenuKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"key.agent-crafter.menu",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_I,
				"category.agent-crafter"
		));

		// Register client tick event to handle key press
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (openMenuKeyBinding.wasPressed()) {
				if (client.currentScreen == null) {
					client.setScreen(new ConfigScreen());
				}
			}
		});

		// Register shutdown hook to save data on game exit
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			if (socketUtil != null && socketUtil.isRunning()) {
				AgentCrafter.LOGGER.info("Game shutting down, stopping AgentCrafter server");
				socketUtil.stop();
			}
		}, "AgentCrafter_Shutdown"));
	}

	public static AgentCrafterConfig getConfig() {
		return config;
	}

	public static SocketUtil getSocketUtil() {
		return socketUtil;
	}
}