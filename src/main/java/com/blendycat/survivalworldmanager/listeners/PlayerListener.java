package com.blendycat.survivalworldmanager.listeners;

import com.blendycat.survivalworldmanager.Main;
import com.blendycat.survivalworldmanager.npc.SleeperTrait;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.NPCDeathEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.citizensnpcs.api.trait.trait.Inventory;
import net.citizensnpcs.api.trait.trait.Spawned;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.block.data.Levelled;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
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
            if(!trait.isDead()) {
                Equipment equipment = npc.getTrait(Equipment.class);
                Inventory inventory = npc.getTrait(Inventory.class);

                player.teleport(npc.getStoredLocation());
                player.setTotalExperience(trait.getTotalExperience());
                player.setHealth(trait.getHealth());
                player.setFoodLevel(trait.getHunger());
                player.setSaturation((float) trait.getSaturation());

                PlayerInventory playerInv = player.getInventory();
                for (int i = 0; i < inventory.getContents().length && i < 36; i++) {
                    playerInv.setItem(i, inventory.getContents()[i]);
                }

                EntityEquipment playerEquipment = player.getEquipment();

                if (playerEquipment != null) {
                    playerEquipment.setBoots(equipment.get(Equipment.EquipmentSlot.BOOTS));
                    playerEquipment.setLeggings(equipment.get(Equipment.EquipmentSlot.LEGGINGS));
                    playerEquipment.setChestplate(equipment.get(Equipment.EquipmentSlot.CHESTPLATE));
                    playerEquipment.setHelmet(equipment.get(Equipment.EquipmentSlot.HELMET));
                    playerEquipment.setItemInOffHand(equipment.get(Equipment.EquipmentSlot.OFF_HAND));
                    //playerEquipment.setItemInMainHand(equipment.get(Equipment.EquipmentSlot.HAND));
                }
                HumanEntity npcEntity = (HumanEntity) npc.getEntity();
                player.setHealth(npcEntity.getHealth());
            } else {
                player.sendMessage("You were killed while offline!");
                player.setHealth(20);
                player.setFoodLevel(20);
                player.setSaturation(20);
                World world = Main.getMainWorld();
                if(world != null) {
                    player.teleport(world.getSpawnLocation());
                }
            }
            npc.despawn();
            CitizensAPI.getNPCRegistry().deregister(npc);
        } else {
            if(!player.hasPlayedBefore()) {
                e.setJoinMessage(ChatColor.LIGHT_PURPLE + "Welcome " + player.getName() + " to BlendyCraft!");
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
        // add the sleeper trait to the npc
        npc.addTrait(sleeperTrait);
        // copy the player's inventory to the sleeper npc
        Inventory npcInv = npc.getTrait(Inventory.class);
        Equipment npcEquipment = npc.getTrait(Equipment.class);
        npcInv.setContents(playerInv.getStorageContents());
        // copy the player's equipment to the sleeper npc
        if(equipment != null) {
            npcEquipment.set(Equipment.EquipmentSlot.BOOTS, equipment.getBoots());
            npcEquipment.set(Equipment.EquipmentSlot.LEGGINGS, equipment.getLeggings());
            npcEquipment.set(Equipment.EquipmentSlot.CHESTPLATE, equipment.getChestplate());
            npcEquipment.set(Equipment.EquipmentSlot.HELMET, equipment.getHelmet());
            //npcEquipment.set(Equipment.EquipmentSlot.HAND, equipment.getItemInMainHand());
            npcEquipment.set(Equipment.EquipmentSlot.OFF_HAND, equipment.getItemInOffHand());
        }
        // clear the players inventory and set their experience to 0
        player.getInventory().clear();
        player.setTotalExperience(0);
    }

    @EventHandler
    public void onNPCDeath(NPCDeathEvent e){
        NPC npc = e.getNPC();
        if(npc.getEntity() instanceof Player) {
            if(npc.hasTrait(SleeperTrait.class)) {
                SleeperTrait trait = npc.getTrait(SleeperTrait.class);

                e.setDroppedExp(trait.getTotalExperience());

                Inventory inv = npc.getTrait(Inventory.class);

                e.getDrops().clear();
                e.getDrops().addAll(Arrays.asList(inv.getContents()));

                npc.despawn();
                trait.setDead(true);
                inv.setContents(new ItemStack[inv.getContents().length]);
                Spawned spawned = npc.getTrait(Spawned.class);
                spawned.setSpawned(false);
            }
        }
    }

    @EventHandler
    public void onNPCInteract(NPCRightClickEvent e) {
        Player player = e.getClicker();
        NPC npc = e.getNPC();
        if(npc.hasTrait(SleeperTrait.class)) {
            SleeperTrait trait = npc.getTrait(SleeperTrait.class);
            Inventory inv = npc.getTrait(Inventory.class);
            inv.openInventory(player);
        }
    }

    @EventHandler
    public void onPlaceLavaBucket(PlayerBucketEmptyEvent e) {
        if(e.getBlock().getType() == Material.LAVA) {
            Levelled lava = (Levelled) e.getBlock().getBlockData();
            if(lava.getLevel() == 0) {
                e.setCancelled(true);
            }
        }
    }
}
