package com.ldtteam.storageracks;

import com.ldtteam.storageracks.blocks.ModBlocks;
import com.ldtteam.storageracks.blocks.RackBlock;
import com.ldtteam.storageracks.utils.Constants;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Util;
import net.minecraft.util.datafix.TypeReferences;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(Constants.MOD_ID)
@Mod.EventBusSubscriber(modid = Constants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModTileEntities
{
    @ObjectHolder("rack")
    public static TileEntityType<TileEntityRack> RACK;

    @ObjectHolder("controller")
    public static TileEntityType<? extends TileEntityController> CONTROLLER;

    @SubscribeEvent
    public static void registerTileEntity(final RegistryEvent.Register<TileEntityType<?>> event)
    {
        RACK = TileEntityType.Builder.of(TileEntityRack::new, ModBlocks.racks.toArray(new RackBlock[0])).build(Util.fetchChoiceType(TypeReferences.BLOCK_ENTITY, "rack"));
        RACK.setRegistryName(Constants.MOD_ID, "rack");
        event.getRegistry().register(RACK);

        CONTROLLER = TileEntityType.Builder.of(TileEntityController::new, ModBlocks.stoneController, ModBlocks.ironController, ModBlocks.emeraldController, ModBlocks.diamondController).build(Util.fetchChoiceType(TypeReferences.BLOCK_ENTITY, "controller"));
        CONTROLLER.setRegistryName(Constants.MOD_ID, "controller");
        event.getRegistry().register(CONTROLLER);
    }
}
