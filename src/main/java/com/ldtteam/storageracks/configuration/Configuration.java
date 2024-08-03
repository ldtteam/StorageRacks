package com.ldtteam.storageracks.configuration;

import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.javafmlmod.FMLModContainer;
import net.neoforged.neoforge.common.ModConfigSpec;
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
    public Configuration(final FMLModContainer modContainer)
    {
        final Pair<ServerConfiguration, ModConfigSpec> ser = new ModConfigSpec.Builder().configure(ServerConfiguration::new);
        modContainer.registerConfig(ModConfig.Type.SERVER, ser.getRight());

        serverConfig = ser.getLeft();
    }

    public ServerConfiguration getServer()
    {
        return serverConfig;
    }
}
