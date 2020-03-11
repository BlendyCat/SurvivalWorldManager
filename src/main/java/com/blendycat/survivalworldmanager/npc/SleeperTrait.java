package com.blendycat.survivalworldmanager.npc;

import com.blendycat.survivalworldmanager.Main;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.util.DataKey;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.UUID;

public class SleeperTrait extends Trait {

    private UUID uuid;
    private int totalExperience;
    private double health;
    private int hunger;
    private double saturation;
    private boolean teamAdded = false;

    public SleeperTrait() {
        super("sleeper-trait");
    }

    public UUID getUUID() {
        return uuid;
    }

    public double getHealth() {
        return health;
    }

    public int getHunger() {
        return hunger;
    }

    public double getSaturation() {
        return saturation;
    }

    public int getTotalExperience() {
        return totalExperience;
    }

    public void setUUID(UUID uuid) {
        this.uuid = uuid;
    }

    public void setTotalExperience(int totalExperience) {
        this.totalExperience = totalExperience;
    }

    public void setHealth(double health) {
        this.health = health;
    }

    public void setHunger(int hunger) {
        this.hunger = hunger;
    }

    public void setSaturation(double saturation) {
        this.saturation = saturation;
    }

    @Override
    public void save(DataKey key) {
        key.setString("uuid", uuid.toString());
        key.setInt("totalExp", totalExperience);
        key.setDouble("health", health);
        key.setInt("hunger", hunger);
        key.setDouble("saturation", saturation);
    }

    @Override
    public void load(DataKey key) {
        uuid = UUID.fromString(key.getString("uuid"));
        totalExperience = key.getInt("totalExp");
        health = key.getDouble("health");
        hunger = key.getInt("hunger");
        saturation = key.getDouble("saturation");
    }

    @Override
    public void run() {
        if(!teamAdded && npc.getEntity() != null) {
            Main.getInternalTeam().addEntry(npc.getName());
        }
    }

    @Override
    public void onDespawn() {
        Main.getInternalTeam().removeEntry(npc.getName());
    }
}
