package com.blendycat.survivalworldmanager.listeners;

import com.blendycat.survivalworldmanager.Main;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.*;

public class PlayerListener implements Listener {

    private final Biome[] FORBIDDEN_BIOMES = {
            Biome.OCEAN,
            Biome.COLD_OCEAN,
            Biome.DEEP_COLD_OCEAN,
            Biome.DEEP_FROZEN_OCEAN,
            Biome.DEEP_LUKEWARM_OCEAN,
            Biome.DEEP_OCEAN,
            Biome.DEEP_WARM_OCEAN,
            Biome.FROZEN_OCEAN,
            Biome.LUKEWARM_OCEAN,
            Biome.WARM_OCEAN,
            Biome.RIVER
    };

    @EventHandler
    public void onPortalEvent(PlayerPortalEvent e){
        Location to = e.getTo();
        if(to != null) {
            World toWorld = to.getWorld();
            if(toWorld != null) {
                WorldBorder border = toWorld.getWorldBorder();
                if(!border.isInside(to)) {
                    e.setCanCreatePortal(false);
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        // Check first if the player has a bed spawn location
        if(player.getBedSpawnLocation() != null) {
            // This will be where they spawn if they have a bed
            player.teleport(player.getBedSpawnLocation());
        } else {
            World world = Main.getMainWorld();
            if(world != null) {
                player.teleport(getSpawnLocation(player, world));
            }
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        Player player = e.getPlayer();
        if(player.getBedSpawnLocation() == null) {
            World world = Main.getMainWorld();
            if(world != null) {
                e.setRespawnLocation(getSpawnLocation(player, world));
            }
        }
    }

    public Location getSpawnLocation(Player player, World world) {
        List<Biome> forbiddenBiomes = Arrays.asList(FORBIDDEN_BIOMES);
        WorldBorder border = world.getWorldBorder();
        Location center = border.getCenter();
        int size = (int) border.getSize();
        Random random = new Random();
        int x;
        int z;
        Biome biome;
        do {
            x = random.nextInt(size) - size / 2;
            z = random.nextInt(size) - size / 2;
            biome = world.getBiome(x, 50, z);
        } while (forbiddenBiomes.contains(biome));
        int y = world.getMaxHeight() - 1;
        while(world.getBlockAt(x, y, z).getType() == Material.AIR && y > 0) y--;

        return new Location(world, x, y+1, z);
    }
}
