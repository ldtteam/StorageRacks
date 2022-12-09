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
import com.ldtteam.storageracks.utils.Constants;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;


public class DefaultBlockLootTableProvider implements DataProvider
{
    private final DataGenerator generator;

    public DefaultBlockLootTableProvider(final DataGenerator generator)
    {
        this.generator = generator;
    }

    /**
     * All generated models.
     */
    private final List<Tuple<LootTableJson, String>> models = new ArrayList<>();

    @Override
    public CompletableFuture<?> run(@NotNull final CachedOutput cache)
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

        return generateAll(cache);
    }

    private void saveBlock(final Block block, final CachedOutput cache)
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

        this.models.add(new Tuple<>(lootTableJson, ForgeRegistries.BLOCKS.getKey(block).getPath()));
    }

    protected CompletableFuture<?> generateAll(CachedOutput cache)
    {
        CompletableFuture<?>[] futures = new CompletableFuture<?>[this.models.size()];
        int i = 0;

        for (Tuple<LootTableJson, String> model : this.models)
        {
            Path target = getPath(model.getB());
            futures[i++] = DataProvider.saveStable(cache, model.getA().serialize(), target);
        }

        return CompletableFuture.allOf(futures);
    }

    protected Path getPath(final String name)
    {
        return this.generator.getPackOutput()
                 .getOutputFolder(PackOutput.Target.RESOURCE_PACK)
                 .resolve(Constants.MOD_ID)
                 .resolve(Constants.LOOT_TABLES_DIR)
                 .resolve(name + ".json");
    }

    @Override
    @NotNull
    public String getName()
    {
        return "Default Block Loot Tables Provider";
    }
}
