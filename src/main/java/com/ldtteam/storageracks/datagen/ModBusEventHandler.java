package com.ldtteam.storageracks.datagen;

import com.ldtteam.datagenerators.lang.LangJson;
import com.ldtteam.storageracks.utils.Constants;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Constants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModBusEventHandler
{
    @SubscribeEvent
    public static void dataGeneratorSetup(final GatherDataEvent event)
    {
        final LangJson langJson = new LangJson();

        event.getGenerator().addProvider(true, new RecipeProvider(event.getGenerator()));
        event.getGenerator().addProvider(true, new BlockStateProvider(event.getGenerator()));
        event.getGenerator().addProvider(true, new ItemModelProvider(event.getGenerator()));
        event.getGenerator().addProvider(true, new BlockModelProvider(event.getGenerator()));
        event.getGenerator().addProvider(true, new DefaultBlockLootTableProvider(event.getGenerator()));
        event.getGenerator().addProvider(true, new LangEntryProvider(event.getGenerator(), langJson));
    }
}
