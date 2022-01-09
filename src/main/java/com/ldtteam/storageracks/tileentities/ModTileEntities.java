package com.ldtteam.storageracks.tileentities;

import com.ldtteam.storageracks.blocks.ModBlocks;
import com.ldtteam.storageracks.blocks.RackBlock;
import com.ldtteam.storageracks.utils.Constants;
import com.mojang.datafixers.types.Type;
import net.minecraft.core.Registry;
import net.minecraft.util.datafix.schemas.V2842;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.Util;
import net.minecraft.util.datafix.fixes.References;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(Constants.MOD_ID)
@Mod.EventBusSubscriber(modid = Constants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModTileEntities
{
    @ObjectHolder("rack")
    public static BlockEntityType<TileEntityRack> RACK;

    @ObjectHolder("controller")
    public static BlockEntityType<? extends TileEntityController> CONTROLLER;

    @SubscribeEvent
    public static void registerTileEntity(final RegistryEvent.Register<BlockEntityType<?>> event)
    {
        RACK = BlockEntityType.Builder.of(TileEntityRack::new, ModBlocks.racks.toArray(new RackBlock[0])).build(Util.fetchChoiceType(References.BLOCK_ENTITY, Constants.MOD_ID + ":rack"));
        RACK.setRegistryName(Constants.MOD_ID, "rack");
        event.getRegistry().register(RACK);

        CONTROLLER = BlockEntityType.Builder.of(TileEntityController::new, ModBlocks.stoneController, ModBlocks.ironController, ModBlocks.emeraldController, ModBlocks.diamondController).build(Util.fetchChoiceType(References.BLOCK_ENTITY, Constants.MOD_ID + ":controller"));
        CONTROLLER.setRegistryName(Constants.MOD_ID, "controller");
        event.getRegistry().register(CONTROLLER);
    }
}
