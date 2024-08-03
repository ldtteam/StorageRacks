package com.ldtteam.storageracks.configuration;

import com.ldtteam.storageracks.utils.Constants;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;
import java.util.function.Predicate;

public abstract class AbstractConfiguration
{
    private final static String BOOLEAN_DEFAULT_KEY = "storageracks.config.default.boolean";
    private final static String INT_DEFAULT_KEY     = "storageracks.config.default.int";
    private final static String LONG_DEFAULT_KEY    = "storageracks.config.default.long";
    private final static String DOUBLE_DEFAULT_KEY  = "storageracks.config.default.double";

    protected void createCategory(final ModConfigSpec.Builder builder, final String key)
    {
        builder.comment(Component.translatable(commentTKey(key)).getString()).push(key);
    }

    protected void swapToCategory(final ModConfigSpec.Builder builder, final String key)
    {
        finishCategory(builder);
        createCategory(builder, key);
    }

    protected void finishCategory(final ModConfigSpec.Builder builder)
    {
        builder.pop();
    }

    private static String nameTKey(final String key)
    {
        return Constants.MOD_ID + ".config." + key;
    }

    private static String commentTKey(final String key)
    {
        return nameTKey(key) + ".comment";
    }

    private static ModConfigSpec.Builder buildBase(final ModConfigSpec.Builder builder, final String key, final String defaultDesc)
    {
        return builder.comment(Component.translatable(commentTKey(key)).getString() + " " + defaultDesc).translation(nameTKey(key));
    }

    protected static ModConfigSpec.IntValue defineInteger(final ModConfigSpec.Builder builder, final String key, final int defaultValue, final int min, final int max)
    {
        return buildBase(builder, key, Component.translatable(INT_DEFAULT_KEY, defaultValue, min, max).getString()).defineInRange(key, defaultValue, min, max);
    }

    protected static ModConfigSpec.LongValue defineLong(final ModConfigSpec.Builder builder, final String key, final long defaultValue)
    {
        return defineLong(builder, key, defaultValue, Long.MIN_VALUE, Long.MAX_VALUE);
    }

    protected static ModConfigSpec.LongValue defineLong(final ModConfigSpec.Builder builder, final String key, final long defaultValue, final long min, final long max)
    {
        return buildBase(builder, key, Component.translatable(LONG_DEFAULT_KEY, defaultValue, min, max).getString()).defineInRange(key, defaultValue, min, max);
    }

    protected static ModConfigSpec.DoubleValue defineDouble(final ModConfigSpec.Builder builder, final String key, final double defaultValue)
    {
        return defineDouble(builder, key, defaultValue, Double.MIN_VALUE, Double.MAX_VALUE);
    }

    protected static ModConfigSpec.DoubleValue defineDouble(final ModConfigSpec.Builder builder, final String key, final double defaultValue, final double min, final double max)
    {
        return buildBase(builder, key, Component.translatable(DOUBLE_DEFAULT_KEY, defaultValue, min, max).getString()).defineInRange(key, defaultValue, min, max);
    }

    protected static <T> ModConfigSpec.ConfigValue<List<? extends T>> defineList(
      final ModConfigSpec.Builder builder,
      final String key,
      final List<? extends T> defaultValue,
      final Predicate<Object> elementValidator)
    {
        return buildBase(builder, key, "").defineList(key, defaultValue, elementValidator);
    }

    protected static <V extends Enum<V>> ModConfigSpec.EnumValue<V> defineEnum(final ModConfigSpec.Builder builder, final String key, final V defaultValue)
    {
        return buildBase(builder, key, "").defineEnum(key, defaultValue);
    }
}
