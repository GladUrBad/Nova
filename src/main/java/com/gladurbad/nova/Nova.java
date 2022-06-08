package com.gladurbad.nova;

import com.gladurbad.nova.data.manager.PlayerDataManager;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class Nova {
    private NovaPlugin plugin;

    public void setPlugin(NovaPlugin plugin) {
        if (Nova.plugin != null) {
            throw new UnsupportedOperationException("Plugin is already defined.");
        }

        Nova.plugin = plugin;
    }

    public NovaPlugin getPlugin() {
        return plugin;
    }

    public PlayerDataManager getPlayerDataManager() {
        return plugin.getPlayerDataManager();
    }
}
