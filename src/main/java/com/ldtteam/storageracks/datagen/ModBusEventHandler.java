package com.ldtteam.storageracks.datagen;

import com.ldtteam.storageracks.utils.Constants;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.WritableRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@EventBusSubscriber(modid = Constants.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class ModBusEventHandler
{
    @SubscribeEvent
    public static void dataGeneratorSetup(final GatherDataEvent event)
    {
        event.getGenerator().addProvider(true, new DefaultRecipeProvider(event.getGenerator().getPackOutput(), event.getLookupProvider()));
        event.getGenerator().addProvider(true, new BlockStateProvider(event.getGenerator()));
        event.getGenerator().addProvider(true, new ItemModelProvider(event.getGenerator()));
        event.getGenerator().addProvider(true, new BlockModelProvider(event.getGenerator()));
        event.getGenerator().addProvider(event.includeServer(), new LootTableProviders(event.getGenerator().getPackOutput(), event.getLookupProvider()));

        event.getGenerator().addProvider(true, new DefaultBlockTagsProvider(event.getGenerator().getPackOutput(), event.getLookupProvider(), event.getExistingFileHelper()));
        event.getGenerator().addProvider(true, new LangEntryProvider(event.getGenerator().getPackOutput()));
    }

    private static final class LootTableProviders extends LootTableProvider
    {
        public LootTableProviders(final PackOutput packOutput, CompletableFuture<HolderLookup.Provider> provider)
        {
            super(packOutput, Set.of(), List.of(
                    new SubProviderEntry(DefaultBlockLootTableProvider::new, LootContextParamSets.BLOCK)
            ), provider);
        }
    }
}
