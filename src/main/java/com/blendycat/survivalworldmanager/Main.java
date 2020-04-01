package com.blendycat.survivalworldmanager;

import com.blendycat.survivalworldmanager.listeners.PlayerListener;
import com.blendycat.survivalworldmanager.npc.SleeperTrait;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;
public class Main extends JavaPlugin {

    private static FileConfiguration config;
    public static Main instance;
    private static Team internalTeam;

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
        createInternalTeams();
        net.citizensnpcs.api.CitizensAPI.getTraitFactory().registerTrait(net.citizensnpcs.api.trait.TraitInfo.create(SleeperTrait.class).withName("sleeper-trait"));
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

    private void createInternalTeams() {
        String teamName = "swm-sleepers";
        ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
        if(scoreboardManager != null) {
            Scoreboard scoreboard = scoreboardManager.getMainScoreboard();
            Team team = scoreboard.getTeam(teamName);

            if(team == null) {
                team = scoreboard.registerNewTeam(teamName);
            }

            team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
            internalTeam = team;
        }
    }

    public static Team getInternalTeam() {
        return internalTeam;
    }
}
