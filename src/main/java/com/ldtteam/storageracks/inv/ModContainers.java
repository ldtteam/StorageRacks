package com.ldtteam.storageracks.inv;

import com.ldtteam.storageracks.gui.WindowInsert;
import com.ldtteam.storageracks.gui.WindowRack;
import com.ldtteam.storageracks.utils.Constants;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@EventBusSubscriber(modid = Constants.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class ModContainers
{
    public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(Registries.MENU, Constants.MOD_ID);

    public static DeferredHolder<MenuType<?>, MenuType<ContainerRack>>   rackInv   = CONTAINERS.register("rack_inv", () -> IMenuTypeExtension.create(ContainerRack::fromPacketBuffer));
    public static DeferredHolder<MenuType<?>, MenuType<InsertContainer>> insertInv = CONTAINERS.register("insert_inv", () -> IMenuTypeExtension.create(InsertContainer::fromPacketBuffer));

    @SubscribeEvent
    public static void doClientStuff(final RegisterMenuScreensEvent event)
    {
        event.register(ModContainers.rackInv.get(), WindowRack::new);
        event.register(ModContainers.insertInv.get(), WindowInsert::new);
    }
}
