package com.ldtteam.storageracks.datagen;

import com.ldtteam.storageracks.blocks.CornerBlock;
import com.ldtteam.storageracks.blocks.ModBlocks;
import com.ldtteam.storageracks.blocks.RackBlock;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.CopyNameFunction;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.neoforged.neoforge.registries.DeferredBlock;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.function.Consumer;


public class DefaultBlockLootTableProvider extends BlockLootSubProvider
{
    public DefaultBlockLootTableProvider(@NotNull final HolderLookup.Provider provider)
    {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), provider);
    }

    @Override
    public void generate() {
        for (final DeferredBlock<CornerBlock> block : ModBlocks.corners)
        {
            saveBlock(block.get());
        }

        for (final DeferredBlock<RackBlock> block : ModBlocks.racks)
        {
            saveBlock(block.get());
        }

        saveBlock(ModBlocks.diamondController.get());
        saveBlock(ModBlocks.emeraldController.get());
        saveBlock(ModBlocks.goldController.get());
        saveBlock(ModBlocks.ironController.get());
        saveBlock(ModBlocks.stoneController.get());
    }

    private void saveBlock(@NotNull final Block block)
    {
        final LootPoolSingletonContainer.Builder<?> item = LootItem.lootTableItem(block);
        item.apply(CopyNameFunction.copyName(CopyNameFunction.NameSource.BLOCK_ENTITY));
        this.saveBlock(block, lootPool -> lootPool.add(item).when(ExplosionCondition.survivesExplosion()));
    }

    private void saveBlock(@NotNull final Block block, final Consumer<LootPool.Builder> lootPoolConfigurer)
    {
        final LootPool.Builder lootPoolbuilder = LootPool.lootPool();
        lootPoolConfigurer.accept(lootPoolbuilder);
        add(block, LootTable.lootTable().withPool(lootPoolbuilder));
    }
}
