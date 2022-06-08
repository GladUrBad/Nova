package com.gladurbad.nova.util.string;

import lombok.experimental.UtilityClass;
import org.bukkit.ChatColor;

@UtilityClass
public class StringUtil {
    public String color(String input) {
        return ChatColor.translateAlternateColorCodes('&', input);
    }
}
