package com.gladurbad.nova;

import com.gladurbad.nova.data.manager.PlayerDataManager;
import com.gladurbad.nova.listener.PlayerListener;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class NovaPlugin extends JavaPlugin {

    private PlayerDataManager playerDataManager;

    @Override
    public void onEnable() {
        // Plugin startup logic
        Nova.setPlugin(this);

        playerDataManager = new PlayerDataManager();

        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
    }
}
