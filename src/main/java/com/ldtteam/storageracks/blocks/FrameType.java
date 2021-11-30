package com.ldtteam.storageracks.blocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.util.StringRepresentable;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

/**
 * The different frame types.
 */
public enum FrameType implements StringRepresentable
{
    WOOD("wood", Blocks.OAK_PLANKS, Items.STONE),
    STONE("stone", Blocks.STONE, Items.IRON_INGOT),
    IRON("iron", Blocks.IRON_BLOCK, Items.GOLD_INGOT),
    GOLD("gold", Blocks.GOLD_BLOCK, Items.EMERALD),
    EMERALD("emerald", Blocks.EMERALD_BLOCK, Items.DIAMOND),
    DIAMOND("diamond", Blocks.DIAMOND_BLOCK, null);

    private final String                name;
    private final Block                 material;
    private final RegistryObject<Block> registeredMaterial;
    private final Item                  cost;

    FrameType(final String nameIn, final Block material, final Item cost)
    {
        this.name = nameIn;
        this.material = material;
        this.registeredMaterial = null;
        this.cost = cost;
    }

    @NotNull
    @Override
    public String getSerializedName()
    {
        return this.name;
    }

    public Block getMaterial()
    {
        return material == null && registeredMaterial != null ? registeredMaterial.get() : material;
    }

    public Item getCost()
    {
        return cost;
    }
}
