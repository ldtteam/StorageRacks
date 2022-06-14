package com.ldtteam.storageracks.inv;

import com.ldtteam.storageracks.gui.WindowInsert;
import com.ldtteam.storageracks.gui.WindowRack;
import com.ldtteam.storageracks.utils.Constants;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = Constants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModContainers
{
    public final static DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, Constants.MOD_ID);

    public static RegistryObject<MenuType<ContainerRack>>   rackInv   = CONTAINERS.register("rack_inv", () -> IForgeMenuType.create(ContainerRack::fromPacketBuffer));
    public static RegistryObject<MenuType<InsertContainer>> insertInv = CONTAINERS.register("insert_inv", () -> IForgeMenuType.create(InsertContainer::fromPacketBuffer));

    @SubscribeEvent
    public static void doClientStuff(final FMLClientSetupEvent event)
    {
        MenuScreens.register(ModContainers.rackInv.get(), WindowRack::new);
        MenuScreens.register(ModContainers.insertInv.get(), WindowInsert::new);
    }
}
