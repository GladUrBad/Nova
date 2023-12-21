package com.gladurbad.nova;

import com.gladurbad.nova.data.manager.PlayerDataManager;
import com.gladurbad.nova.listener.PlayerListener;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

@Getter
public final class NovaPlugin extends JavaPlugin {

    private PlayerDataManager playerDataManager;

    @Override
    public void onEnable() {
        // Plugin startup logic
        Nova.setPlugin(this);

        playerDataManager = new PlayerDataManager();

        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);

        // Create plugin folders
        File modelsFolder = new File(this.getDataFolder(), "models");
        if (!modelsFolder.exists()) modelsFolder.mkdirs();

        File trainingData = new File(this.getDataFolder(), "training_data");
        if (!trainingData.exists()) trainingData.mkdirs();

        File testingData = new File(this.getDataFolder(), "testing_data");
        if (!testingData.exists()) testingData.mkdirs();
    }
}
