package mod.kelvinlby.ui;

import mod.kelvinlby.AgentCrafterClient;
import mod.kelvinlby.AgentCrafterConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;

public class ConfigScreen extends Screen {
    private static final int UPDATE_INTERVAL_MIN = 1;
    private static final int UPDATE_INTERVAL_MAX = 1000;
    private final AgentCrafterConfig config;

    public ConfigScreen() {
        super(Text.translatable("screen.agent-crafter.config"));
        this.config = AgentCrafterClient.getConfig();
    }

    @Override
    protected void init() {
        super.init();

        int centerX = this.width / 2;
        int startY = this.height / 2 - 50;

        // Add minimap toggle button
        this.addDrawableChild(ButtonWidget.builder(
            Text.translatable("button.agent-crafter.status", AgentCrafterClient.shouldShowMinimap() ? Text.translatable("button.chunk-radar.on") : Text.translatable("button.chunk-radar.off")),
            button -> {
                AgentCrafterClient.setShowMinimap(!AgentCrafterClient.shouldShowMinimap());
                button.setMessage(Text.translatable("button.chunk-radar.chunkmap_toggle", AgentCrafterClient.shouldShowMinimap() ? Text.translatable("button.chunk-radar.on") : Text.translatable("button.chunk-radar.off")));
            })
            .dimensions(centerX - 100, startY, 200, 20)
            .build());

        // Add update interval slider
        this.addDrawableChild(new SliderWidget(centerX - 100, startY + 30, 200, 20,
            Text.translatable("slider.chunk-radar.update_interval", config.updateInterval),
            (config.updateInterval - UPDATE_INTERVAL_MIN) / (double)(UPDATE_INTERVAL_MAX - UPDATE_INTERVAL_MIN)) {
            @Override
            protected void updateMessage() {
                config.updateInterval = (int)(this.value * (UPDATE_INTERVAL_MAX - UPDATE_INTERVAL_MIN)) + UPDATE_INTERVAL_MIN;
                this.setMessage(Text.translatable("slider.chunk-radar.update_interval", config.updateInterval));
            }

            @Override
            protected void applyValue() {
                config.updateInterval = (int)(this.value * (UPDATE_INTERVAL_MAX - UPDATE_INTERVAL_MIN)) + UPDATE_INTERVAL_MIN;
            }
        });

        // Add detection method cycling button
        this.addDrawableChild(ButtonWidget.builder(
            Text.translatable("button.chunk-radar.detection_method", config.detectionMethod),
            button -> {
                // Cycle through methods
                String[] methods = {
                    Text.translatable("option.chunk-radar.detection_method_1").getString(),
                    Text.translatable("option.chunk-radar.detection_method_2").getString(),
                    Text.translatable("option.chunk-radar.detection_method_3").getString(),
                    Text.translatable("option.chunk-radar.detection_method_4").getString()
                };
                for (int i = 0; i < methods.length; i++) {
                    if (methods[i].equals(config.detectionMethod)) {
                        config.detectionMethod = methods[(i + 1) % methods.length];
                        button.setMessage(Text.translatable("button.chunk-radar.detection_method", config.detectionMethod));
                        break;
                    }
                }
            })
            .dimensions(centerX - 100, startY + 60, 200, 20)
            .build());

        // Add close button
        this.addDrawableChild(ButtonWidget.builder(
            Text.translatable("gui.done"),
            button -> {
                config.save();
                // No need to update detector - it reads config dynamically
                this.close();
            })
            .dimensions(centerX - 50, startY + 90, 100, 20)
            .build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        // Draw title
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}