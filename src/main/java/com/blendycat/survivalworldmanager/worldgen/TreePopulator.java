package com.blendycat.survivalworldmanager.worldgen;

import org.bukkit.*;
import org.bukkit.generator.BlockPopulator;

import java.util.Random;

public class TreePopulator extends BlockPopulator {

    private final int CHUNK_MAX_TREE_COUNT = 15;

    @Override
    public void populate(World world, Random random, Chunk chunk) {
        if(random.nextBoolean()) {
            for(int i = 0; i < random.nextInt(CHUNK_MAX_TREE_COUNT); i++){
                int x = random.nextInt(15);
                int z = random.nextInt(15);
                int y = world.getMaxHeight() - 1;
                while(chunk.getBlock(x, y, z).getType() == Material.AIR) {
                    y--;
                }
                y++;
                world.generateTree(chunk.getBlock(x, y, z).getLocation(), TreeType.TREE);
            }
        }
    }
}
