package mod.kelvinlby;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class AgentCrafterConfig {
    private static final String CONFIG_FILE = "agent-crafter.json";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public boolean status = true;
    public String host = "127.0.0.1";
    public int port = 1210;

    public static AgentCrafterConfig load() {
        File configFile = new File(FabricLoader.getInstance().getConfigDir().toFile(), CONFIG_FILE);

        if (!configFile.exists()) {
            AgentCrafterConfig config = new AgentCrafterConfig();
            config.save();
            return config;
        }

        try (FileReader reader = new FileReader(configFile)) {
            return GSON.fromJson(reader, AgentCrafterConfig.class);
        } catch (IOException e) {
            AgentCrafter.LOGGER.error("Failed to load config file", e);
            return new AgentCrafterConfig();
        }
    }

    public void save() {
        File configFile = new File(FabricLoader.getInstance().getConfigDir().toFile(), CONFIG_FILE);

        try (FileWriter writer = new FileWriter(configFile)) {
            GSON.toJson(this, writer);
        } catch (IOException e) {
            AgentCrafter.LOGGER.error("Failed to save config file", e);
        }
    }
}