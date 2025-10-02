package mod.kelvinlby.util;

import mod.kelvinlby.AgentCrafter;

public class SocketUtil {
    public void start(String host, int port) {
        AgentCrafter.LOGGER.info("Starting AgentCrafter socket on {}:{}", host, port);
    }

    public void stop() {
        AgentCrafter.LOGGER.info("Stopping AgentCrafter socket");
    }

    public boolean isRunning() {
        return true;
    }
}
