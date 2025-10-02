package mod.kelvinlby.ui;

import mod.kelvinlby.AgentCrafterClient;
import mod.kelvinlby.AgentCrafterConfig;
import mod.kelvinlby.util.SocketUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.text.Text;

public class ConfigScreen extends Screen {
    private final AgentCrafterConfig config;
    private final String originalHost;
    private final int originalPort;
    private final boolean originalStatus;

    private TextFieldWidget hostField;
    private TextFieldWidget portField;

    public ConfigScreen() {
        super(Text.translatable("screen.agent-crafter.config"));
        this.config = AgentCrafterClient.getConfig();
        // Save original values for cancel functionality
        this.originalHost = config.host;
        this.originalPort = config.port;
        this.originalStatus = config.status;
    }

    @Override
    protected void init() {
        super.init();

        int centerX = this.width / 2;
        int startY = this.height / 2 - 60;
        int labelWidth = 40;
        int fieldWidth = 150;

        // Add Agent API toggle button
        ButtonWidget statusButton = ButtonWidget.builder(
                        Text.translatable("button.agent-crafter.status", config.status ? Text.translatable("button.agent-crafter.status.on") : Text.translatable("button.agent-crafter.status.off")),
                        button -> {
                            config.status = !config.status;
                            button.setMessage(Text.translatable("button.agent-crafter.status", config.status ? Text.translatable("button.agent-crafter.status.on") : Text.translatable("button.agent-crafter.status.off")));
                        })
                .dimensions(centerX - 100, startY, 200, 20)
                .build();
        this.addDrawableChild(statusButton);

        // Add Host IP input field (label will be drawn in render method)
        hostField = new TextFieldWidget(this.textRenderer, centerX - 100 + labelWidth + 5, startY + 40, fieldWidth, 20, Text.translatable("field.agent-crafter.host"));
        hostField.setMaxLength(50);
        hostField.setText(config.host);
        this.addDrawableChild(hostField);

        // Add Port input field (label will be drawn in render method)
        portField = new TextFieldWidget(this.textRenderer, centerX - 100 + labelWidth + 5, startY + 70, fieldWidth, 20, Text.translatable("field.agent-crafter.port"));
        portField.setMaxLength(5);
        portField.setText(String.valueOf(config.port));
        this.addDrawableChild(portField);

        // Add Cancel button
        // - Revert all settings
        ButtonWidget cancelButton = ButtonWidget.builder(
                        Text.translatable("gui.cancel"),
                        button -> {
                            // Revert all changes
                            config.host = originalHost;
                            config.port = originalPort;
                            config.status = originalStatus;
                            this.close();
                        })
                .dimensions(centerX - 105, startY + 100, 100, 20)
                .build();
        this.addDrawableChild(cancelButton);

        // Add Done button
        // - Save the values to config
        // - Reset to default if invalid
        // - Restart socket if configuration changed
        // - Show toast notification
        ButtonWidget doneButton = ButtonWidget.builder(
                        Text.translatable("gui.done"),
                        button -> {
                            // Save the values to config
                            config.host = hostField.getText();
                            try {
                                config.port = Integer.parseInt(portField.getText());
                            } catch (NumberFormatException e) {
                                config.port = 1210; // Reset to default if invalid
                            }
                            config.save();

                            // Restart socket if configuration changed
                            SocketUtil socketUtil = AgentCrafterClient.getSocketUtil();
                            if (socketUtil != null) {
                                if (socketUtil.isRunning()) {
                                    socketUtil.stop();
                                }
                                if (config.status) {
                                    socketUtil.start(config.host, config.port);
                                }
                            }

                            // Show toast notification
                            if (this.client != null) {
                                this.client.getToastManager().add(
                                        SystemToast.create(this.client, SystemToast.Type.NARRATOR_TOGGLE,
                                                Text.translatable("toast.agent-crafter.api-updated-toast"),
                                                Text.literal("Serving on: " + config.host + ":" + config.port))
                                );
                            }

                            this.close();
                        })
                .dimensions(centerX + 5, startY + 100, 100, 20)
                .build();
        this.addDrawableChild(doneButton);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        int centerX = this.width / 2;
        int startY = this.height / 2 - 60;

        // Draw title
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFFFF);

        // Draw labels for input fields (aligned with text fields)
        context.drawText(this.textRenderer, Text.translatable("field.agent-crafter.host"), centerX - 100, startY + 46, 0xFFFFFFFF, true);
        context.drawText(this.textRenderer, Text.translatable("field.agent-crafter.port"), centerX - 100, startY + 76, 0xFFFFFFFF, true);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}