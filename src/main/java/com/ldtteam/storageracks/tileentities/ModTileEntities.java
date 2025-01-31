package com.ldtteam.storageracks.tileentities;

import com.ldtteam.storageracks.blocks.ModBlocks;
import com.ldtteam.storageracks.blocks.RackBlock;
import com.ldtteam.storageracks.utils.Constants;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.Util;
import net.minecraft.util.datafix.fixes.References;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModTileEntities
{
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Constants.MOD_ID);

    public static DeferredHolder<BlockEntityType<?>,BlockEntityType<TileEntityRack>>
      RACK = BLOCK_ENTITIES.register("rack", () -> BlockEntityType.Builder.of(TileEntityRack::new, ModBlocks.racks.stream().map(DeferredHolder::get).toList().toArray(new RackBlock[0])).build(Util.fetchChoiceType(References.BLOCK_ENTITY, Constants.MOD_ID + ":rack")));

    public static DeferredHolder<BlockEntityType<?>,BlockEntityType<? extends TileEntityController>> CONTROLLER = BLOCK_ENTITIES.register("controller", () -> BlockEntityType.Builder.of(TileEntityController::new, ModBlocks.stoneController.get(), ModBlocks.goldController.get(), ModBlocks.ironController.get(), ModBlocks.emeraldController.get(), ModBlocks.diamondController.get(), ModBlocks.netherite_controller.get()).build(Util.fetchChoiceType(References.BLOCK_ENTITY, Constants.MOD_ID + ":controller")));
}
