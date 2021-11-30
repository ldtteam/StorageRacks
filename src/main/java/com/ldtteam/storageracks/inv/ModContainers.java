package com.ldtteam.storageracks.inv;

import com.ldtteam.storageracks.gui.WindowInsert;
import com.ldtteam.storageracks.gui.WindowRack;
import com.ldtteam.storageracks.utils.Constants;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(Constants.MOD_ID)
@Mod.EventBusSubscriber(modid = Constants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModContainers
{
    @ObjectHolder("rack_inv")
    public static MenuType<ContainerRack> rackInv;

    @ObjectHolder("insert_inv")
    public static MenuType<InsertContainer> insertInv;

    @SubscribeEvent
    public static void registerContainers(final RegistryEvent.Register<MenuType<?>> event)
    {
        ModContainers.rackInv = (MenuType<ContainerRack>) IForgeMenuType.create(ContainerRack::fromPacketBuffer).setRegistryName("rack_inv");
        ModContainers.insertInv = (MenuType<InsertContainer>) IForgeMenuType.create(InsertContainer::fromPacketBuffer).setRegistryName("insert_inv");

        event.getRegistry().registerAll(ModContainers.rackInv, ModContainers.insertInv);
    }

    @SubscribeEvent
    public static void doClientStuff(final FMLClientSetupEvent event)
    {
        MenuScreens.register(ModContainers.rackInv, WindowRack::new);
        MenuScreens.register(ModContainers.insertInv, WindowInsert::new);
    }
}
