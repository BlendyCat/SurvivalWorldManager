package com.blendycat.survivalworldmanager.npc;

import com.blendycat.survivalworldmanager.Main;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.util.DataKey;

import java.util.UUID;

public class SleeperTrait extends Trait {

    private UUID uuid;
    private int totalExperience;
    private double health;
    private int hunger;
    private double saturation;
    private boolean dead;

    public SleeperTrait() {
        super("sleeper-trait");
        dead = false;
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

    public void setDead(boolean isDead) {
        dead = isDead;
    }

    public boolean isDead() {
        return dead;
    }

    @Override
    public void save(DataKey key) {
        key.setString("uuid", uuid.toString());
        key.setInt("totalExp", totalExperience);
        key.setDouble("health", health);
        key.setInt("hunger", hunger);
        key.setDouble("saturation", saturation);
        key.setBoolean("dead", dead);
    }

    @Override
    public void load(DataKey key) {
        uuid = UUID.fromString(key.getString("uuid"));
        totalExperience = key.getInt("totalExp");
        health = key.getDouble("health");
        hunger = key.getInt("hunger");
        saturation = key.getDouble("saturation");
        dead = key.getBoolean("dead");
    }

    @Override
    public void run() {
        if(npc.getEntity() != null) {
            Main.getInternalTeam().addEntry(npc.getName());
        }
    }

    @Override
    public void onDespawn() {
        Main.getInternalTeam().removeEntry(npc.getName());
    }
}
