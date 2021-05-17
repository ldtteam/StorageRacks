package com.ldtteam.storageracks.blocks;

import com.ldtteam.storageracks.utils.Constants;
import com.ldtteam.structurize.api.blocks.IBlockList;
import com.ldtteam.structurize.api.generation.*;
import com.ldtteam.structurize.blocks.types.WoodType;
import net.minecraft.block.BlockState;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelBuilder;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;

/**
 * Creates types for TimberFrame with different variants of wood and texture
 */
public class RackList implements IBlockList<RackBlock>
{
    @Override
    public List<RegistryObject<RackBlock>> getRegisteredBlocks()
    {
        return Collections.emptyList();
    }

    @Override
    public void generateBlockStates(final ModBlockStateProvider states)
    {
        ModBlocks.racks.forEach(
          block -> processStates(
            states,
            block)
        );
    }

    public void processStates(ModBlockStateProvider states, RackBlock block)
    {
        states.getVariantBuilder(block)
          .forAllStates(state -> ConfiguredModel.builder()
                                   .modelFile(getModel(state, states, block))
                                   .build());
    }

    private ModelFile getModel(final BlockState state, final ModBlockStateProvider states, final RackBlock block)
    {
        final String variant = (state.getValue(RackBlock.VARIANT) == RackType.DEFAULT ? "empty" : "full");
        return states.models()
                 .getBuilder("block/racks/" + block.getRegistryName().getPath() + variant)
                 .parent(new ModelFile.UncheckedModelFile(states.modLoc("block/" + variant + "rack")))
                 .texture("0",
                   block.getWoodType() == WoodType.CACTUS
                     ? "structurize:blocks/cactus/blockcactusplank"
                     : "minecraft:block/" + block.getWoodType().getMaterial().getRegistryName().getPath())
                 .texture("1", "minecraft:block/" + block.getFrameType().getMaterial().getRegistryName().getPath())
                 .texture("particle", "minecraft:block/" + block.getFrameType().getMaterial().getRegistryName().getPath());
    }

    @Override
    public void generateItemModels(final ModItemModelProvider models)
    {
        ModBlocks.racks.forEach(block -> models.getBuilder(getRegistryPath(block))
                                           .parent(new ModelFile.UncheckedModelFile(models.modLoc("block/racks/" + getRegistryPath(block) + "empty")))
                                           .transforms()
                                           .transform(ModelBuilder.Perspective.THIRDPERSON_RIGHT)
                                           .rotation(45, 45, 0)
                                           .translation(0.0f, 2.5f, 0.0f)
                                           .scale(0.375f, 0.375f, 0.375f)
                                           .end()
                                           .transform(ModelBuilder.Perspective.THIRDPERSON_LEFT)
                                           .rotation(45, 45, 0)
                                           .translation(0.0f, 2.5f, 0.0f)
                                           .scale(0.375f, 0.375f, 0.375f)
                                           .end()
                                           .transform(ModelBuilder.Perspective.FIRSTPERSON_RIGHT)
                                           .rotation(0, 45, 0)
                                           .translation(0.0f, 1.75f, 0.0f)
                                           .scale(0.4f, 0.4f, 0.4f)
                                           .end()
                                           .transform(ModelBuilder.Perspective.FIRSTPERSON_LEFT)
                                           .rotation(0, 45, 0)
                                           .translation(0.0f, 1.75f, 0.0f)
                                           .scale(0.4f, 0.4f, 0.4f)
                                           .end()
                                           .transform(ModelBuilder.Perspective.GROUND)
                                           .rotation(0, 3, 0)
                                           .scale(0.25f, 0.25f, 0.25f)
                                           .end()
                                           .transform(ModelBuilder.Perspective.GUI)
                                           .rotation(30, 225, 0)
                                           .translation(0.0f, 0.5f, 0.0f)
                                           .scale(0.625f, 0.625f, 0.625f)
                                           .end()
                                           .transform(ModelBuilder.Perspective.FIXED)
                                           .scale(0.6f, 0.6f, 0.6f)
                                           .end()
        );
    }

    @Override
    public void generateRecipes(final ModRecipeProvider provider)
    {
        ModBlocks.racks.forEach(block -> {
            provider.add(
              consumer -> new ShapedRecipeBuilder(block, 1)
                            .pattern("FFF")
                            .pattern("XCX")
                            .pattern("FFF")
                            .define('F',
                              ForgeRegistries.BLOCKS.getValue(new ResourceLocation("minecraft", block.getWoodType().name().toLowerCase() + "_slab")))
                            .define('C', block.getFrameType().getCost())
                            .define('X', Items.STICK)
                            .unlockedBy("has_" + block.getRegistryName().getPath(), provider.getCriterion(block))
                            .save(consumer, new ResourceLocation(Constants.MOD_ID, block.getRegistryName().getPath())));
        });

        provider.add(
          consumer -> new ShapedRecipeBuilder(ModBlocks.stoneController, 1)
                        .pattern("SSS")
                        .pattern("SPS")
                        .pattern("SSS")
                        .define('S', Items.STONE_SLAB)
                        .define('P', Items.PAPER)
                        .unlockedBy("has_" + ModBlocks.stoneController.getRegistryName().getPath(), provider.getCriterion(ModBlocks.stoneController))
                        .save(consumer, new ResourceLocation(Constants.MOD_ID, ModBlocks.stoneController.getRegistryName().getPath())));

        provider.add(
          consumer -> new ShapedRecipeBuilder(ModBlocks.ironController, 1)
                        .pattern("SSS")
                        .pattern("SPS")
                        .pattern("SSS")
                        .define('S', Items.IRON_INGOT)
                        .define('P', Items.PAPER)
                        .unlockedBy("has_" + ModBlocks.ironController.getRegistryName().getPath(), provider.getCriterion(ModBlocks.ironController))
                        .save(consumer, new ResourceLocation(Constants.MOD_ID, ModBlocks.ironController.getRegistryName().getPath())));

        provider.add(
          consumer -> new ShapedRecipeBuilder(ModBlocks.emeraldController, 1)
                        .pattern("SSS")
                        .pattern("SPS")
                        .pattern("SSS")
                        .define('S', Items.EMERALD)
                        .define('P', Items.PAPER)
                        .unlockedBy("has_" + ModBlocks.emeraldController.getRegistryName().getPath(), provider.getCriterion(ModBlocks.emeraldController))
                        .save(consumer, new ResourceLocation(Constants.MOD_ID, ModBlocks.emeraldController.getRegistryName().getPath())));

        provider.add(
          consumer -> new ShapedRecipeBuilder(ModBlocks.diamondController, 1)
                        .pattern("SSS")
                        .pattern("SPS")
                        .pattern("SSS")
                        .define('S', Items.DIAMOND)
                        .define('P', Items.PAPER)
                        .unlockedBy("has_" + ModBlocks.diamondController.getRegistryName().getPath(), provider.getCriterion(ModBlocks.diamondController))
                        .save(consumer, new ResourceLocation(Constants.MOD_ID, ModBlocks.diamondController.getRegistryName().getPath())));
    }

    @Override
    public void generateTags(final ModBlockTagsProvider blocks, final ModItemTagsProvider items)
    {

    }

    @Override
    public void generateTranslations(final ModLanguageProvider lang)
    {
        lang.translate(ModBlocks.racks, block ->
                                          ModLanguageProvider.format(block.getWoodType().getSerializedName()) + " " +
                                            ModLanguageProvider.format(block.getFrameType().getSerializedName()) + " " + "Rack");
    }
}
