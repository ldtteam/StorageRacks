package com.ldtteam.storageracks.datagen;

import com.ldtteam.datagenerators.loot_table.LootTableJson;
import com.ldtteam.datagenerators.loot_table.LootTableTypeEnum;
import com.ldtteam.datagenerators.loot_table.pool.PoolJson;
import com.ldtteam.datagenerators.loot_table.pool.conditions.survives_explosion.SurvivesExplosionConditionJson;
import com.ldtteam.datagenerators.loot_table.pool.entry.EntryJson;
import com.ldtteam.datagenerators.loot_table.pool.entry.EntryTypeEnum;
import com.ldtteam.storageracks.blocks.CornerBlock;
import com.ldtteam.storageracks.blocks.ModBlocks;
import com.ldtteam.storageracks.blocks.RackBlock;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;

import static com.ldtteam.storageracks.utils.Constants.LOOT_TABLES_DIR;

public class DefaultBlockLootTableProvider implements DataProvider
{
    private final DataGenerator generator;

    public DefaultBlockLootTableProvider(final DataGenerator generator)
    {
        this.generator = generator;
    }

    @Override
    public void run(@NotNull final CachedOutput cache) throws IOException
    {
        for (final RegistryObject<CornerBlock> block : ModBlocks.corners)
        {
            saveBlock(block.get(), cache);
        }

        for (final RegistryObject<RackBlock> block : ModBlocks.racks)
        {
            saveBlock(block.get(), cache);
        }

        saveBlock(ModBlocks.diamondController.get(), cache);
        saveBlock(ModBlocks.emeraldController.get(), cache);
        saveBlock(ModBlocks.goldController.get(), cache);
        saveBlock(ModBlocks.ironController.get(), cache);
        saveBlock(ModBlocks.stoneController.get(), cache);
    }

    private void saveBlock(final Block block, final CachedOutput cache) throws IOException
    {
        final EntryJson entryJson = new EntryJson();
        entryJson.setType(EntryTypeEnum.ITEM);
        entryJson.setName(ForgeRegistries.BLOCKS.getKey(block).toString());

        final PoolJson poolJson = new PoolJson();
        poolJson.setEntries(Collections.singletonList(entryJson));
        poolJson.setRolls(1);
        poolJson.setConditions(Collections.singletonList(new SurvivesExplosionConditionJson()));

        final LootTableJson lootTableJson = new LootTableJson();
        lootTableJson.setType(LootTableTypeEnum.BLOCK);
        lootTableJson.setPools(Collections.singletonList(poolJson));

        final Path savePath = generator.getOutputFolder().resolve(LOOT_TABLES_DIR).resolve(ForgeRegistries.BLOCKS.getKey(block).getPath() + ".json");
        DataProvider.saveStable(cache, lootTableJson.serialize(), savePath);
    }

    @Override
    @NotNull
    public String getName()
    {
        return "Default Block Loot Tables Provider";
    }
}
