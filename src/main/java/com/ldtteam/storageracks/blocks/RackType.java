package com.ldtteam.storageracks.blocks;

import net.minecraft.util.IStringSerializable;

/**
 * Defines the types of Racks that the {@link RackBlock} supports.
 */
public enum RackType implements IStringSerializable
{
    DEFAULT( "empty", "empty"),
    FULL("full", "full");

    private final String name;
    private final String unlocalizedName;

    RackType(final String name, final String unlocalizedName)
    {
        this.name = name;
        this.unlocalizedName = unlocalizedName;
    }

    @Override
    public String toString()
    {
        return this.name;
    }

    public String getName()
    {
        return this.name;
    }

    @Override
    public String getSerializedName()
    {
        return getName();
    }
}
