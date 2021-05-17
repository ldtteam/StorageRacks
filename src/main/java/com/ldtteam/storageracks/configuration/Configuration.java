package com.ldtteam.storageracks.configuration;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Mod root configuration.
 */
public class Configuration
{
    /**
     * Loaded serverside, synced on connection
     */
    private final ServerConfiguration serverConfig;

    /**
     * Builds configuration tree.
     */
    public Configuration()
    {
        final Pair<ServerConfiguration, ForgeConfigSpec> ser = new ForgeConfigSpec.Builder().configure(ServerConfiguration::new);

        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ser.getRight());

        serverConfig = ser.getLeft();
    }

    public ServerConfiguration getServer()
    {
        return serverConfig;
    }
}
