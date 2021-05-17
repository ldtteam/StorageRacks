package com.ldtteam.storageracks;

import com.ldtteam.storageracks.utils.Constants;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.extensions.IForgeContainerType;
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
    public static ContainerType<ContainerRack> rackInv;

    @SubscribeEvent
    public static void registerContainers(final RegistryEvent.Register<ContainerType<?>> event)
    {
        ModContainers.rackInv = (ContainerType<ContainerRack>) IForgeContainerType.create(ContainerRack::fromPacketBuffer).setRegistryName("rack_inv");

        event.getRegistry().registerAll(ModContainers.rackInv);
    }

    @SubscribeEvent
    public static void doClientStuff(final FMLClientSetupEvent event)
    {
        ScreenManager.register(ModContainers.rackInv, WindowRack::new);
    }
}
