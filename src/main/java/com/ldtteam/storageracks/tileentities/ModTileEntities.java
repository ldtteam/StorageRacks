package com.ldtteam.storageracks.tileentities;

import com.ldtteam.storageracks.blocks.ModBlocks;
import com.ldtteam.storageracks.blocks.RackBlock;
import com.ldtteam.storageracks.utils.Constants;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.Util;
import net.minecraft.util.datafix.fixes.References;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = Constants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModTileEntities
{
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, Constants.MOD_ID);

    public static RegistryObject<BlockEntityType<TileEntityRack>> RACK = BLOCK_ENTITIES.register("rack", () -> BlockEntityType.Builder.of(TileEntityRack::new, ModBlocks.racks.stream().map(RegistryObject::get).toList().toArray(new RackBlock[0])).build(Util.fetchChoiceType(References.BLOCK_ENTITY, Constants.MOD_ID + ":rack")));

    public static RegistryObject<BlockEntityType<? extends TileEntityController>> CONTROLLER = BLOCK_ENTITIES.register("controller", () -> BlockEntityType.Builder.of(TileEntityController::new, ModBlocks.stoneController.get(), ModBlocks.ironController.get(), ModBlocks.emeraldController.get(), ModBlocks.diamondController.get()).build(Util.fetchChoiceType(References.BLOCK_ENTITY, Constants.MOD_ID + ":controller")));
}
