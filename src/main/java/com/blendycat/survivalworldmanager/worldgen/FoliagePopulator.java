package com.blendycat.survivalworldmanager.worldgen;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;

import java.util.Random;

public class FoliagePopulator extends BlockPopulator {

    private final int CHUNK_MAX_FOLIAGE = 30;

    @Override
    public void populate(World world, Random random, Chunk chunk) {
        for(int i = 0; i < random.nextInt(CHUNK_MAX_FOLIAGE); i++) {
            int x = random.nextInt(15);
            int z = random.nextInt(15);
            int y = world.getMaxHeight() -1;
            while(chunk.getBlock(x, y, z).getType() == Material.AIR && y > 0) y --;
            if(chunk.getBlock(x, y, z).getType() == Material.GRASS_BLOCK) {
                chunk.getBlock(x, y+1, z).setType(Material.GRASS);
            }
        }
    }
}
