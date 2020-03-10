package com.blendycat.survivalworldmanager.listeners;

import com.blendycat.survivalworldmanager.Main;
import com.blendycat.survivalworldmanager.npc.SleeperTrait;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.NPCDeathEvent;
import net.citizensnpcs.api.event.NPCDespawnEvent;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.citizensnpcs.api.trait.trait.Inventory;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.PlayerInventory;

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
        /*// Check first if the player has a bed spawn location
        if(player.getBedSpawnLocation() != null) {
            // This will be where they spawn if they have a bed
            player.teleport(player.getBedSpawnLocation());
        } else {
            World world = Main.getMainWorld();
            if(world != null) {
                player.teleport(getRandomLocation(player, world));
            }
        }*/
        UUID uuid = player.getUniqueId();
        NPC npc = null;
        for (NPC npcI : CitizensAPI.getNPCRegistry()) {
            if(npcI.hasTrait(SleeperTrait.class)) {
                SleeperTrait sleeperTrait = npcI.getTrait(SleeperTrait.class);
                if(sleeperTrait.getUUID().equals(uuid)) {
                    npc = npcI;
                    break;
                }
            }
        }

        if(npc != null) {
            SleeperTrait trait = npc.getTrait(SleeperTrait.class);
            Equipment equipment = npc.getTrait(Equipment.class);
            Inventory inventory = npc.getTrait(Inventory.class);

            player.teleport(npc.getStoredLocation());
            player.setTotalExperience(trait.getTotalExperience());
            player.setHealth(trait.getHealth());
            player.setFoodLevel(trait.getHunger());
            player.setSaturation((float) trait.getSaturation());

            PlayerInventory playerInv = player.getInventory();
            for(int i = 0; i < inventory.getContents().length && i < 36; i++) {
                playerInv.setItem(i, inventory.getContents()[i]);
            }

            EntityEquipment playerEquipment = player.getEquipment();

            if(playerEquipment != null) {
                playerEquipment.setBoots(equipment.get(Equipment.EquipmentSlot.BOOTS));
                playerEquipment.setLeggings(equipment.get(Equipment.EquipmentSlot.LEGGINGS));
                playerEquipment.setChestplate(equipment.get(Equipment.EquipmentSlot.CHESTPLATE));
                playerEquipment.setHelmet(equipment.get(Equipment.EquipmentSlot.HELMET));
                playerEquipment.setItemInOffHand(equipment.get(Equipment.EquipmentSlot.OFF_HAND));
                //playerEquipment.setItemInMainHand(equipment.get(Equipment.EquipmentSlot.HAND));
            }

            npc.despawn();
            CitizensAPI.getNPCRegistry().deregister(npc);
        } else {
            if (player.hasPlayedBefore()) {
                player.incrementStatistic(Statistic.DEATHS);
                player.sendMessage("You were killed while offline!");
            }
            World world = Main.getMainWorld();
            if (world != null) {
                if (player.getBedSpawnLocation() != null) {
                    player.teleport(player.getBedSpawnLocation());
                } else {
                    player.teleport(getRandomLocation(world));
                }
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player player = e.getEntity();
        e.setDroppedExp(player.getTotalExperience());
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, player.getName());
        npc.spawn(player.getLocation());
        npc.setProtected(false);
        // Player entity = (Player) npc.getEntity();
        PlayerInventory playerInv = player.getInventory();
        EntityEquipment equipment = player.getEquipment();

        // Custom trait to store sleeper's uuid and experience
        SleeperTrait sleeperTrait = new SleeperTrait();
        sleeperTrait.linkToNPC(npc);
        sleeperTrait.setTotalExperience(player.getTotalExperience());
        sleeperTrait.setUUID(player.getUniqueId());
        sleeperTrait.setHealth(player.getHealth());
        sleeperTrait.setHunger(player.getFoodLevel());
        sleeperTrait.setSaturation(player.getSaturation());

        npc.addTrait(sleeperTrait);

        Inventory npcInv = npc.getTrait(Inventory.class);
        Equipment npcEquipment = npc.getTrait(Equipment.class);
        npcInv.setContents(playerInv.getStorageContents());

        if(equipment != null) {
            npcEquipment.set(Equipment.EquipmentSlot.BOOTS, equipment.getBoots());
            npcEquipment.set(Equipment.EquipmentSlot.LEGGINGS, equipment.getLeggings());
            npcEquipment.set(Equipment.EquipmentSlot.CHESTPLATE, equipment.getChestplate());
            npcEquipment.set(Equipment.EquipmentSlot.HELMET, equipment.getHelmet());
            //npcEquipment.set(Equipment.EquipmentSlot.HAND, equipment.getItemInMainHand());
            npcEquipment.set(Equipment.EquipmentSlot.OFF_HAND, equipment.getItemInOffHand());
        }
        player.getInventory().clear();
        player.setTotalExperience(0);
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        Player player = e.getPlayer();
        if(player.getBedSpawnLocation() == null) {
            World world = Main.getMainWorld();
            if(world != null) {
                e.setRespawnLocation(getRandomLocation(world));
            }
        }
    }

    @EventHandler
    public void onNPCDeath(NPCDeathEvent e){
        NPC npc = e.getNPC();
        if(npc.getEntity() instanceof Player) {
            if(npc.hasTrait(SleeperTrait.class)) {
                SleeperTrait trait = npc.getTrait(SleeperTrait.class);
                Player player = (Player) npc.getEntity();

                e.setDroppedExp(trait.getTotalExperience());

                Inventory inv = npc.getTrait(Inventory.class);
                Equipment equipment = npc.getTrait(Equipment.class);

                e.getDrops().clear();
                e.getDrops().addAll(Arrays.asList(inv.getContents()));
                /*e.getDrops().add(equipment.get(1));
                e.getDrops().add(equipment.get(2));
                e.getDrops().add(equipment.get(3));
                e.getDrops().add(equipment.get(4));
                e.getDrops().add(equipment.get(5));*/

                CitizensAPI.getNPCRegistry().deregister(npc);
            }
        }
    }

    public Location getRandomLocation(World world) {
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
