package com.ldtteam.storageracks.inv;

import net.minecraft.world.Nameable;
import org.jetbrains.annotations.Nullable;

/**
 * Created by marcf on 3/25/2017.
 */
public interface IWorldNameableModifiable extends Nameable
{
    /**
     * Method to set the name of this.
     *
     * @param name The new name of this, or null to reset it to its default.
     */
    void setName(@Nullable String name);
}
