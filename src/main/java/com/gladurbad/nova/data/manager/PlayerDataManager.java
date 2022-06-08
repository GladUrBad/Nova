package com.gladurbad.nova.data.manager;

import com.gladurbad.nova.data.PlayerData;
import com.google.common.collect.Maps;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

public class PlayerDataManager {
    private final Map<UUID, PlayerData> dataMap = Maps.newConcurrentMap();

    public PlayerData get(Player player) {
        UUID uuid = player.getUniqueId();
        PlayerData data = dataMap.get(uuid);
        if (data == null) dataMap.put(uuid, data = new PlayerData(player));
        return data;
    }

    public void remove(Player player) {
        dataMap.remove(player.getUniqueId());
    }

    public Collection<PlayerData> getPlayerData() {
        return dataMap.values();
    }
}
