package com.blendycat.survivalworldmanager;

import com.blendycat.survivalworldmanager.listeners.PlayerListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class Main extends JavaPlugin {

    private static FileConfiguration config;
    public static Main instance;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        config = getConfig();
        instance = this;

        ConfigurationSection worlds = config.getConfigurationSection("worlds");
        if(worlds != null) {
            getLogger().info("Worlds:" + worlds.getKeys(false).toString());
            for (String worldName : worlds.getKeys(false)) {
                World world = Bukkit.getWorld(worldName);
                int size = worlds.getInt(worldName + ".border-size");
                getLogger().info("Set border of \"" + worldName + "\" to " +
                        size + " blocks wide!");
                if (world != null) {
                    world.getWorldBorder().setSize(size);
                }
            }
        }
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
    }

    @Override
    public void onDisable() {

    }

    public static World getMainWorld() {
        String worldName = config.getString("main-world");
        if(worldName != null) {
            return Bukkit.getWorld(worldName);
        }
        return null;
    }
}
