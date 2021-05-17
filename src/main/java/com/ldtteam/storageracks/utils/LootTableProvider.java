package com.ldtteam.storageracks.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ldtteam.datagenerators.loot_table.LootTableJson;
import com.ldtteam.datagenerators.loot_table.LootTableTypeEnum;
import com.ldtteam.datagenerators.loot_table.pool.PoolJson;
import com.ldtteam.datagenerators.loot_table.pool.conditions.survives_explosion.SurvivesExplosionConditionJson;
import com.ldtteam.datagenerators.loot_table.pool.entry.EntryJson;
import com.ldtteam.datagenerators.loot_table.pool.entry.EntryTypeEnum;
import com.ldtteam.storageracks.blocks.ModBlocks;
import com.ldtteam.storageracks.blocks.RackBlock;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;

import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import org.jetbrains.annotations.NotNull;

public class LootTableProvider implements IDataProvider
{
    private static final String DATAPACK_DIR = "data/" + Constants.MOD_ID + "/";
    public static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();
    public static final String LOOT_TABLES_DIR = DATAPACK_DIR + "loot_tables/blocks";

    private final DataGenerator generator;

    public LootTableProvider(final DataGenerator generator)
    {
        this.generator = generator;
    }

    @Override
    public void run(@NotNull DirectoryCache cache) throws IOException
    {
        for (RackBlock block : ModBlocks.racks)
        {
            saveBlock(block, cache);
        }

        saveBlock(ModBlocks.stoneController, cache);
        saveBlock(ModBlocks.ironController, cache);
        saveBlock(ModBlocks.emeraldController, cache);
        saveBlock(ModBlocks.diamondController, cache);
    }

    private void saveBlock(Block block, final DirectoryCache cache) throws IOException
    {
        if (block.getRegistryName() != null)
        {
            final EntryJson entryJson = new EntryJson();
            entryJson.setType(EntryTypeEnum.ITEM);
            entryJson.setName(block.getRegistryName().toString());

            final PoolJson poolJson = new PoolJson();
            poolJson.setEntries(Collections.singletonList(entryJson));
            poolJson.setRolls(1);
            poolJson.setConditions(Collections.singletonList(new SurvivesExplosionConditionJson()));

            final LootTableJson lootTableJson = new LootTableJson();
            lootTableJson.setType(LootTableTypeEnum.BLOCK);
            lootTableJson.setPools(Collections.singletonList(poolJson));

            final Path savePath = generator.getOutputFolder().resolve(LOOT_TABLES_DIR).resolve(block.getRegistryName().getPath() + ".json");
            IDataProvider.save(GSON, cache, lootTableJson.serialize(), savePath);
        }
    }

    @Override
    @NotNull
    public String getName()
    {
        return "Default Block Loot Tables Provider";
    }
}

