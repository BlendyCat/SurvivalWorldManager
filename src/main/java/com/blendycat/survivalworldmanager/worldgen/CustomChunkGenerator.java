package com.blendycat.survivalworldmanager.worldgen;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.util.noise.SimplexOctaveGenerator;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class CustomChunkGenerator extends ChunkGenerator {

    private final int DIRT_HEIGHT = 4;
    private final int BASE_GRASS_LEVEL = 50;
    private final int MULTIPLIER = 20;

    @Override
    public ChunkData generateChunkData(World world, Random random, int chunkX, int chunkZ, BiomeGrid biome) {
        ChunkData chunk = createChunkData(world);
        SimplexOctaveGenerator generator = new SimplexOctaveGenerator(new Random(world.getSeed()),8);
        generator.setScale(0.005);
        int currentHeight;

        for(int x = 0; x < 16; x++) {
            for(int z = 0; z < 16; z++) {
                currentHeight = (int) ((generator.noise(chunkX * 16 + x, chunkZ * 16 + z, 0.5, 0.5, true) + 1) * MULTIPLIER + BASE_GRASS_LEVEL);
                chunk.setBlock(x, currentHeight, z, Material.GRASS_BLOCK);
                for(int i = currentHeight - 1; i > (currentHeight - DIRT_HEIGHT) - 1; i--)
                    chunk.setBlock(x, i, z, Material.DIRT);
                for(int i = (currentHeight - DIRT_HEIGHT) - 1; i > 0; i--)
                    chunk.setBlock(x, i, z, Material.STONE);
                chunk.setBlock(x, 0, z, Material.BEDROCK);
            }
        }

        return chunk;
    }

    @Override
    public List<BlockPopulator> getDefaultPopulators(World world) {
        return Arrays.asList(
                (BlockPopulator)new TreePopulator(),
                (BlockPopulator)new FoliagePopulator()
        );
    }
}
