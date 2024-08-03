package com.ldtteam.storageracks.utils;

import com.ldtteam.blockui.Color;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RegisterRenderBuffersEvent;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import java.util.List;

/**
 * Straight structurize copy.
 */
public class RenderUtils
{
    @SubscribeEvent
    public static void registerGlobalRenderBuffers(final RegisterRenderBuffersEvent event)
    {
        event.registerRenderBuffer(RenderUtils.LINES_WITH_WIDTH);
    }

    private static final int MAX_DEBUG_TEXT_RENDER_DIST_SQUARED = 8 * 8 * 16;
    public static final RenderType LINES_WITH_WIDTH = RenderTypes.LINES_WITH_WIDTH;

    /**
     * Render a box around two positions
     *
     * @param posA         First position
     * @param posB         Second position
     * @param red          red colour float 0 - 1
     * @param green        green colour float 0 - 1
     * @param blue         blue colour float 0 - 1
     * @param alpha        opacity 0 - 1
     * @param boxGrow      size grow in every direction
     * @param bufferSource
     */
    public static void renderBox(
      final BlockPos posA,
      final BlockPos posB,
      final int red,
      final int green,
      final int blue,
      final int alpha,
      final double boxGrow,
      final PoseStack matrixStack, final MultiBufferSource.BufferSource bufferSource)
    {
        renderLineBox(bufferSource.getBuffer(LINES_WITH_WIDTH),
          matrixStack,
          Math.min(posA.getX(), posB.getX()),
          Math.min(posA.getY(), posB.getY()),
          Math.min(posA.getZ(), posB.getZ()),
          Math.max(posA.getX(), posB.getX()) + 1,
          Math.max(posA.getY(), posB.getY()) + 1,
          Math.max(posA.getZ(), posB.getZ()) + 1,
          red,
          green,
          blue,
          alpha,
          0.02f);
    }

    /**
     * Render a box around two positions
     */
    public static void renderLineBox(final VertexConsumer buffer,
      final PoseStack ps,
      float minX,
      float minY,
      float minZ,
      float maxX,
      float maxY,
      float maxZ,
      final int red,
      final int green,
      final int blue,
      final int alpha,
      final float lineWidth)
    {
        if (alpha == 0)
        {
            return;
        }

        final float halfLine = lineWidth / 2.0f;
        minX -= halfLine;
        minY -= halfLine;
        minZ -= halfLine;
        final float minX2 = minX + lineWidth;
        final float minY2 = minY + lineWidth;
        final float minZ2 = minZ + lineWidth;

        maxX += halfLine;
        maxY += halfLine;
        maxZ += halfLine;
        final float maxX2 = maxX - lineWidth;
        final float maxY2 = maxY - lineWidth;
        final float maxZ2 = maxZ - lineWidth;

        final Matrix4f m = ps.last().pose();
        populateRenderLineBox(minX, minY, minZ, minX2, minY2, minZ2, maxX, maxY, maxZ, maxX2, maxY2, maxZ2, red, green, blue, alpha, m, buffer);
    }

    public static void populateRenderLineBox(final float minX,
      final float minY,
      final float minZ,
      final float minX2,
      final float minY2,
      final float minZ2,
      final float maxX,
      final float maxY,
      final float maxZ,
      final float maxX2,
      final float maxY2,
      final float maxZ2,
      final int red,
      final int green,
      final int blue,
      final int alpha,
      final Matrix4f m,
      final VertexConsumer buf)
    {
        // z plane

        buf.addVertex(m, minX, minY, minZ).setColor(red, green, blue, alpha);
        buf.addVertex(m, maxX2, minY2, minZ).setColor(red, green, blue, alpha);
        buf.addVertex(m, maxX, minY, minZ).setColor(red, green, blue, alpha);

        buf.addVertex(m, minX, minY, minZ).setColor(red, green, blue, alpha);
        buf.addVertex(m, minX2, minY2, minZ).setColor(red, green, blue, alpha);
        buf.addVertex(m, maxX2, minY2, minZ).setColor(red, green, blue, alpha);

        buf.addVertex(m, minX, minY, minZ).setColor(red, green, blue, alpha);
        buf.addVertex(m, minX2, maxY2, minZ).setColor(red, green, blue, alpha);
        buf.addVertex(m, minX2, minY2, minZ).setColor(red, green, blue, alpha);

        buf.addVertex(m, minX, minY, minZ).setColor(red, green, blue, alpha);
        buf.addVertex(m, minX, maxY, minZ).setColor(red, green, blue, alpha);
        buf.addVertex(m, minX2, maxY2, minZ).setColor(red, green, blue, alpha);

        buf.addVertex(m, maxX, maxY, minZ).setColor(red, green, blue, alpha);
        buf.addVertex(m, minX2, maxY2, minZ).setColor(red, green, blue, alpha);
        buf.addVertex(m, minX, maxY, minZ).setColor(red, green, blue, alpha);

        buf.addVertex(m, maxX, maxY, minZ).setColor(red, green, blue, alpha);
        buf.addVertex(m, maxX2, maxY2, minZ).setColor(red, green, blue, alpha);
        buf.addVertex(m, minX2, maxY2, minZ).setColor(red, green, blue, alpha);

        buf.addVertex(m, maxX, maxY, minZ).setColor(red, green, blue, alpha);
        buf.addVertex(m, maxX2, minY2, minZ).setColor(red, green, blue, alpha);
        buf.addVertex(m, maxX2, maxY2, minZ).setColor(red, green, blue, alpha);

        buf.addVertex(m, maxX, maxY, minZ).setColor(red, green, blue, alpha);
        buf.addVertex(m, maxX, minY, minZ).setColor(red, green, blue, alpha);
        buf.addVertex(m, maxX2, minY2, minZ).setColor(red, green, blue, alpha);

        //

        buf.addVertex(m, minX, maxY2, minZ2).setColor(red, green, blue, alpha);
        buf.addVertex(m, minX2, minY2, minZ2).setColor(red, green, blue, alpha);
        buf.addVertex(m, minX2, maxY2, minZ2).setColor(red, green, blue, alpha);

        buf.addVertex(m, minX, maxY2, minZ2).setColor(red, green, blue, alpha);
        buf.addVertex(m, minX, minY2, minZ2).setColor(red, green, blue, alpha);
        buf.addVertex(m, minX2, minY2, minZ2).setColor(red, green, blue, alpha);

        buf.addVertex(m, minX2, minY2, minZ2).setColor(red, green, blue, alpha);
        buf.addVertex(m, minX2, minY, minZ2).setColor(red, green, blue, alpha);
        buf.addVertex(m, maxX2, minY, minZ2).setColor(red, green, blue, alpha);

        buf.addVertex(m, minX2, minY2, minZ2).setColor(red, green, blue, alpha);
        buf.addVertex(m, maxX2, minY, minZ2).setColor(red, green, blue, alpha);
        buf.addVertex(m, maxX2, minY2, minZ2).setColor(red, green, blue, alpha);

        buf.addVertex(m, maxX, maxY2, minZ2).setColor(red, green, blue, alpha);
        buf.addVertex(m, maxX2, maxY2, minZ2).setColor(red, green, blue, alpha);
        buf.addVertex(m, maxX2, minY2, minZ2).setColor(red, green, blue, alpha);

        buf.addVertex(m, maxX, maxY2, minZ2).setColor(red, green, blue, alpha);
        buf.addVertex(m, maxX2, minY2, minZ2).setColor(red, green, blue, alpha);
        buf.addVertex(m, maxX, minY2, minZ2).setColor(red, green, blue, alpha);

        buf.addVertex(m, minX2, maxY2, minZ2).setColor(red, green, blue, alpha);
        buf.addVertex(m, maxX2, maxY, minZ2).setColor(red, green, blue, alpha);
        buf.addVertex(m, minX2, maxY, minZ2).setColor(red, green, blue, alpha);

        buf.addVertex(m, minX2, maxY2, minZ2).setColor(red, green, blue, alpha);
        buf.addVertex(m, maxX2, maxY2, minZ2).setColor(red, green, blue, alpha);
        buf.addVertex(m, maxX2, maxY, minZ2).setColor(red, green, blue, alpha);

        //

        buf.addVertex(m, minX, maxY2, maxZ2).setColor(red, green, blue, alpha);
        buf.addVertex(m, minX2, maxY2, maxZ2).setColor(red, green, blue, alpha);
        buf.addVertex(m, minX2, minY2, maxZ2).setColor(red, green, blue, alpha);

        buf.addVertex(m, minX, maxY2, maxZ2).setColor(red, green, blue, alpha);
        buf.addVertex(m, minX2, minY2, maxZ2).setColor(red, green, blue, alpha);
        buf.addVertex(m, minX, minY2, maxZ2).setColor(red, green, blue, alpha);

        buf.addVertex(m, minX2, minY2, maxZ2).setColor(red, green, blue, alpha);
        buf.addVertex(m, maxX2, minY, maxZ2).setColor(red, green, blue, alpha);
        buf.addVertex(m, minX2, minY, maxZ2).setColor(red, green, blue, alpha);

        buf.addVertex(m, minX2, minY2, maxZ2).setColor(red, green, blue, alpha);
        buf.addVertex(m, maxX2, minY2, maxZ2).setColor(red, green, blue, alpha);
        buf.addVertex(m, maxX2, minY, maxZ2).setColor(red, green, blue, alpha);

        buf.addVertex(m, maxX, maxY2, maxZ2).setColor(red, green, blue, alpha);
        buf.addVertex(m, maxX2, minY2, maxZ2).setColor(red, green, blue, alpha);
        buf.addVertex(m, maxX2, maxY2, maxZ2).setColor(red, green, blue, alpha);

        buf.addVertex(m, maxX, maxY2, maxZ2).setColor(red, green, blue, alpha);
        buf.addVertex(m, maxX, minY2, maxZ2).setColor(red, green, blue, alpha);
        buf.addVertex(m, maxX2, minY2, maxZ2).setColor(red, green, blue, alpha);

        buf.addVertex(m, minX2, maxY2, maxZ2).setColor(red, green, blue, alpha);
        buf.addVertex(m, minX2, maxY, maxZ2).setColor(red, green, blue, alpha);
        buf.addVertex(m, maxX2, maxY, maxZ2).setColor(red, green, blue, alpha);

        buf.addVertex(m, minX2, maxY2, maxZ2).setColor(red, green, blue, alpha);
        buf.addVertex(m, maxX2, maxY, maxZ2).setColor(red, green, blue, alpha);
        buf.addVertex(m, maxX2, maxY2, maxZ2).setColor(red, green, blue, alpha);

        //

        buf.addVertex(m, minX, minY, maxZ).setColor(red, green, blue, alpha);
        buf.addVertex(m, maxX, minY, maxZ).setColor(red, green, blue, alpha);
        buf.addVertex(m, maxX2, minY2, maxZ).setColor(red, green, blue, alpha);

        buf.addVertex(m, minX, minY, maxZ).setColor(red, green, blue, alpha);
        buf.addVertex(m, maxX2, minY2, maxZ).setColor(red, green, blue, alpha);
        buf.addVertex(m, minX2, minY2, maxZ).setColor(red, green, blue, alpha);

        buf.addVertex(m, minX, minY, maxZ).setColor(red, green, blue, alpha);
        buf.addVertex(m, minX2, minY2, maxZ).setColor(red, green, blue, alpha);
        buf.addVertex(m, minX2, maxY2, maxZ).setColor(red, green, blue, alpha);

        buf.addVertex(m, minX, minY, maxZ).setColor(red, green, blue, alpha);
        buf.addVertex(m, minX2, maxY2, maxZ).setColor(red, green, blue, alpha);
        buf.addVertex(m, minX, maxY, maxZ).setColor(red, green, blue, alpha);

        buf.addVertex(m, maxX, maxY, maxZ).setColor(red, green, blue, alpha);
        buf.addVertex(m, minX, maxY, maxZ).setColor(red, green, blue, alpha);
        buf.addVertex(m, minX2, maxY2, maxZ).setColor(red, green, blue, alpha);

        buf.addVertex(m, maxX, maxY, maxZ).setColor(red, green, blue, alpha);
        buf.addVertex(m, minX2, maxY2, maxZ).setColor(red, green, blue, alpha);
        buf.addVertex(m, maxX2, maxY2, maxZ).setColor(red, green, blue, alpha);

        buf.addVertex(m, maxX, maxY, maxZ).setColor(red, green, blue, alpha);
        buf.addVertex(m, maxX2, maxY2, maxZ).setColor(red, green, blue, alpha);
        buf.addVertex(m, maxX2, minY2, maxZ).setColor(red, green, blue, alpha);

        buf.addVertex(m, maxX, maxY, maxZ).setColor(red, green, blue, alpha);
        buf.addVertex(m, maxX2, minY2, maxZ).setColor(red, green, blue, alpha);
        buf.addVertex(m, maxX, minY, maxZ).setColor(red, green, blue, alpha);

        // x plane

        buf.addVertex(m, minX, minY, minZ).setColor(red, green, blue, alpha);
        buf.addVertex(m, minX, minY, maxZ).setColor(red, green, blue, alpha);
        buf.addVertex(m, minX, minY2, maxZ2).setColor(red, green, blue, alpha);

        buf.addVertex(m, minX, minY, minZ).setColor(red, green, blue, alpha);
        buf.addVertex(m, minX, minY2, maxZ2).setColor(red, green, blue, alpha);
        buf.addVertex(m, minX, minY2, minZ2).setColor(red, green, blue, alpha);

        buf.addVertex(m, minX, minY, minZ).setColor(red, green, blue, alpha);
        buf.addVertex(m, minX, minY2, minZ2).setColor(red, green, blue, alpha);
        buf.addVertex(m, minX, maxY2, minZ2).setColor(red, green, blue, alpha);

        buf.addVertex(m, minX, minY, minZ).setColor(red, green, blue, alpha);
        buf.addVertex(m, minX, maxY2, minZ2).setColor(red, green, blue, alpha);
        buf.addVertex(m, minX, maxY, minZ).setColor(red, green, blue, alpha);

        buf.addVertex(m, minX, maxY, maxZ).setColor(red, green, blue, alpha);
        buf.addVertex(m, minX, maxY, minZ).setColor(red, green, blue, alpha);
        buf.addVertex(m, minX, maxY2, minZ2).setColor(red, green, blue, alpha);

        buf.addVertex(m, minX, maxY, maxZ).setColor(red, green, blue, alpha);
        buf.addVertex(m, minX, maxY2, minZ2).setColor(red, green, blue, alpha);
        buf.addVertex(m, minX, maxY2, maxZ2).setColor(red, green, blue, alpha);

        buf.addVertex(m, minX, maxY, maxZ).setColor(red, green, blue, alpha);
        buf.addVertex(m, minX, maxY2, maxZ2).setColor(red, green, blue, alpha);
        buf.addVertex(m, minX, minY2, maxZ2).setColor(red, green, blue, alpha);

        buf.addVertex(m, minX, maxY, maxZ).setColor(red, green, blue, alpha);
        buf.addVertex(m, minX, minY2, maxZ2).setColor(red, green, blue, alpha);
        buf.addVertex(m, minX, minY, maxZ).setColor(red, green, blue, alpha);

        //

        buf.addVertex(m, minX2, maxY2, minZ).setColor(red, green, blue, alpha);
        buf.addVertex(m, minX2, maxY2, minZ2).setColor(red, green, blue, alpha);
        buf.addVertex(m, minX2, minY2, minZ2).setColor(red, green, blue, alpha);

        buf.addVertex(m, minX2, maxY2, minZ).setColor(red, green, blue, alpha);
        buf.addVertex(m, minX2, minY2, minZ2).setColor(red, green, blue, alpha);
        buf.addVertex(m, minX2, minY2, minZ).setColor(red, green, blue, alpha);

        buf.addVertex(m, minX2, minY2, minZ2).setColor(red, green, blue, alpha);
        buf.addVertex(m, minX2, minY, maxZ2).setColor(red, green, blue, alpha);
        buf.addVertex(m, minX2, minY, minZ2).setColor(red, green, blue, alpha);

        buf.addVertex(m, minX2, minY2, minZ2).setColor(red, green, blue, alpha);
        buf.addVertex(m, minX2, minY2, maxZ2).setColor(red, green, blue, alpha);
        buf.addVertex(m, minX2, minY, maxZ2).setColor(red, green, blue, alpha);

        buf.addVertex(m, minX2, maxY2, maxZ).setColor(red, green, blue, alpha);
        buf.addVertex(m, minX2, minY2, maxZ2).setColor(red, green, blue, alpha);
        buf.addVertex(m, minX2, maxY2, maxZ2).setColor(red, green, blue, alpha);

        buf.addVertex(m, minX2, maxY2, maxZ).setColor(red, green, blue, alpha);
        buf.addVertex(m, minX2, minY2, maxZ).setColor(red, green, blue, alpha);
        buf.addVertex(m, minX2, minY2, maxZ2).setColor(red, green, blue, alpha);

        buf.addVertex(m, minX2, maxY2, minZ2).setColor(red, green, blue, alpha);
        buf.addVertex(m, minX2, maxY, minZ2).setColor(red, green, blue, alpha);
        buf.addVertex(m, minX2, maxY, maxZ2).setColor(red, green, blue, alpha);

        buf.addVertex(m, minX2, maxY2, minZ2).setColor(red, green, blue, alpha);
        buf.addVertex(m, minX2, maxY, maxZ2).setColor(red, green, blue, alpha);
        buf.addVertex(m, minX2, maxY2, maxZ2).setColor(red, green, blue, alpha);

        //

        buf.addVertex(m, maxX2, maxY2, minZ).setColor(red, green, blue, alpha);
        buf.addVertex(m, maxX2, minY2, minZ2).setColor(red, green, blue, alpha);
        buf.addVertex(m, maxX2, maxY2, minZ2).setColor(red, green, blue, alpha);

        buf.addVertex(m, maxX2, maxY2, minZ).setColor(red, green, blue, alpha);
        buf.addVertex(m, maxX2, minY2, minZ).setColor(red, green, blue, alpha);
        buf.addVertex(m, maxX2, minY2, minZ2).setColor(red, green, blue, alpha);

        buf.addVertex(m, maxX2, minY2, minZ2).setColor(red, green, blue, alpha);
        buf.addVertex(m, maxX2, minY, minZ2).setColor(red, green, blue, alpha);
        buf.addVertex(m, maxX2, minY, maxZ2).setColor(red, green, blue, alpha);

        buf.addVertex(m, maxX2, minY2, minZ2).setColor(red, green, blue, alpha);
        buf.addVertex(m, maxX2, minY, maxZ2).setColor(red, green, blue, alpha);
        buf.addVertex(m, maxX2, minY2, maxZ2).setColor(red, green, blue, alpha);

        buf.addVertex(m, maxX2, maxY2, maxZ).setColor(red, green, blue, alpha);
        buf.addVertex(m, maxX2, maxY2, maxZ2).setColor(red, green, blue, alpha);
        buf.addVertex(m, maxX2, minY2, maxZ2).setColor(red, green, blue, alpha);

        buf.addVertex(m, maxX2, maxY2, maxZ).setColor(red, green, blue, alpha);
        buf.addVertex(m, maxX2, minY2, maxZ2).setColor(red, green, blue, alpha);
        buf.addVertex(m, maxX2, minY2, maxZ).setColor(red, green, blue, alpha);

        buf.addVertex(m, maxX2, maxY2, minZ2).setColor(red, green, blue, alpha);
        buf.addVertex(m, maxX2, maxY, maxZ2).setColor(red, green, blue, alpha);
        buf.addVertex(m, maxX2, maxY, minZ2).setColor(red, green, blue, alpha);

        buf.addVertex(m, maxX2, maxY2, minZ2).setColor(red, green, blue, alpha);
        buf.addVertex(m, maxX2, maxY2, maxZ2).setColor(red, green, blue, alpha);
        buf.addVertex(m, maxX2, maxY, maxZ2).setColor(red, green, blue, alpha);

        //

        buf.addVertex(m, maxX, minY, minZ).setColor(red, green, blue, alpha);
        buf.addVertex(m, maxX, minY2, maxZ2).setColor(red, green, blue, alpha);
        buf.addVertex(m, maxX, minY, maxZ).setColor(red, green, blue, alpha);

        buf.addVertex(m, maxX, minY, minZ).setColor(red, green, blue, alpha);
        buf.addVertex(m, maxX, minY2, minZ2).setColor(red, green, blue, alpha);
        buf.addVertex(m, maxX, minY2, maxZ2).setColor(red, green, blue, alpha);

        buf.addVertex(m, maxX, minY, minZ).setColor(red, green, blue, alpha);
        buf.addVertex(m, maxX, maxY2, minZ2).setColor(red, green, blue, alpha);
        buf.addVertex(m, maxX, minY2, minZ2).setColor(red, green, blue, alpha);

        buf.addVertex(m, maxX, minY, minZ).setColor(red, green, blue, alpha);
        buf.addVertex(m, maxX, maxY, minZ).setColor(red, green, blue, alpha);
        buf.addVertex(m, maxX, maxY2, minZ2).setColor(red, green, blue, alpha);

        buf.addVertex(m, maxX, maxY, maxZ).setColor(red, green, blue, alpha);
        buf.addVertex(m, maxX, maxY2, minZ2).setColor(red, green, blue, alpha);
        buf.addVertex(m, maxX, maxY, minZ).setColor(red, green, blue, alpha);

        buf.addVertex(m, maxX, maxY, maxZ).setColor(red, green, blue, alpha);
        buf.addVertex(m, maxX, maxY2, maxZ2).setColor(red, green, blue, alpha);
        buf.addVertex(m, maxX, maxY2, minZ2).setColor(red, green, blue, alpha);

        buf.addVertex(m, maxX, maxY, maxZ).setColor(red, green, blue, alpha);
        buf.addVertex(m, maxX, minY2, maxZ2).setColor(red, green, blue, alpha);
        buf.addVertex(m, maxX, maxY2, maxZ2).setColor(red, green, blue, alpha);

        buf.addVertex(m, maxX, maxY, maxZ).setColor(red, green, blue, alpha);
        buf.addVertex(m, maxX, minY, maxZ).setColor(red, green, blue, alpha);
        buf.addVertex(m, maxX, minY2, maxZ2).setColor(red, green, blue, alpha);

        // y plane

        buf.addVertex(m, minX, minY, minZ).setColor(red, green, blue, alpha);
        buf.addVertex(m, minX2, minY, maxZ2).setColor(red, green, blue, alpha);
        buf.addVertex(m, minX, minY, maxZ).setColor(red, green, blue, alpha);

        buf.addVertex(m, minX, minY, minZ).setColor(red, green, blue, alpha);
        buf.addVertex(m, minX2, minY, minZ2).setColor(red, green, blue, alpha);
        buf.addVertex(m, minX2, minY, maxZ2).setColor(red, green, blue, alpha);

        buf.addVertex(m, minX, minY, minZ).setColor(red, green, blue, alpha);
        buf.addVertex(m, maxX2, minY, minZ2).setColor(red, green, blue, alpha);
        buf.addVertex(m, minX2, minY, minZ2).setColor(red, green, blue, alpha);

        buf.addVertex(m, minX, minY, minZ).setColor(red, green, blue, alpha);
        buf.addVertex(m, maxX, minY, minZ).setColor(red, green, blue, alpha);
        buf.addVertex(m, maxX2, minY, minZ2).setColor(red, green, blue, alpha);

        buf.addVertex(m, maxX, minY, maxZ).setColor(red, green, blue, alpha);
        buf.addVertex(m, maxX2, minY, minZ2).setColor(red, green, blue, alpha);
        buf.addVertex(m, maxX, minY, minZ).setColor(red, green, blue, alpha);

        buf.addVertex(m, maxX, minY, maxZ).setColor(red, green, blue, alpha);
        buf.addVertex(m, maxX2, minY, maxZ2).setColor(red, green, blue, alpha);
        buf.addVertex(m, maxX2, minY, minZ2).setColor(red, green, blue, alpha);

        buf.addVertex(m, maxX, minY, maxZ).setColor(red, green, blue, alpha);
        buf.addVertex(m, minX2, minY, maxZ2).setColor(red, green, blue, alpha);
        buf.addVertex(m, maxX2, minY, maxZ2).setColor(red, green, blue, alpha);

        buf.addVertex(m, maxX, minY, maxZ).setColor(red, green, blue, alpha);
        buf.addVertex(m, minX, minY, maxZ).setColor(red, green, blue, alpha);
        buf.addVertex(m, minX2, minY, maxZ2).setColor(red, green, blue, alpha);

        //

        buf.addVertex(m, maxX2, minY2, minZ).setColor(red, green, blue, alpha);
        buf.addVertex(m, minX2, minY2, minZ2).setColor(red, green, blue, alpha);
        buf.addVertex(m, maxX2, minY2, minZ2).setColor(red, green, blue, alpha);

        buf.addVertex(m, maxX2, minY2, minZ).setColor(red, green, blue, alpha);
        buf.addVertex(m, minX2, minY2, minZ).setColor(red, green, blue, alpha);
        buf.addVertex(m, minX2, minY2, minZ2).setColor(red, green, blue, alpha);

        buf.addVertex(m, minX2, minY2, minZ2).setColor(red, green, blue, alpha);
        buf.addVertex(m, minX, minY2, minZ2).setColor(red, green, blue, alpha);
        buf.addVertex(m, minX, minY2, maxZ2).setColor(red, green, blue, alpha);

        buf.addVertex(m, minX2, minY2, minZ2).setColor(red, green, blue, alpha);
        buf.addVertex(m, minX, minY2, maxZ2).setColor(red, green, blue, alpha);
        buf.addVertex(m, minX2, minY2, maxZ2).setColor(red, green, blue, alpha);

        buf.addVertex(m, maxX2, minY2, maxZ).setColor(red, green, blue, alpha);
        buf.addVertex(m, maxX2, minY2, maxZ2).setColor(red, green, blue, alpha);
        buf.addVertex(m, minX2, minY2, maxZ2).setColor(red, green, blue, alpha);

        buf.addVertex(m, maxX2, minY2, maxZ).setColor(red, green, blue, alpha);
        buf.addVertex(m, minX2, minY2, maxZ2).setColor(red, green, blue, alpha);
        buf.addVertex(m, minX2, minY2, maxZ).setColor(red, green, blue, alpha);

        buf.addVertex(m, maxX2, minY2, minZ2).setColor(red, green, blue, alpha);
        buf.addVertex(m, maxX, minY2, maxZ2).setColor(red, green, blue, alpha);
        buf.addVertex(m, maxX, minY2, minZ2).setColor(red, green, blue, alpha);

        buf.addVertex(m, maxX2, minY2, minZ2).setColor(red, green, blue, alpha);
        buf.addVertex(m, maxX2, minY2, maxZ2).setColor(red, green, blue, alpha);
        buf.addVertex(m, maxX, minY2, maxZ2).setColor(red, green, blue, alpha);

        //

        buf.addVertex(m, maxX2, maxY2, minZ).setColor(red, green, blue, alpha);
        buf.addVertex(m, maxX2, maxY2, minZ2).setColor(red, green, blue, alpha);
        buf.addVertex(m, minX2, maxY2, minZ2).setColor(red, green, blue, alpha);

        buf.addVertex(m, maxX2, maxY2, minZ).setColor(red, green, blue, alpha);
        buf.addVertex(m, minX2, maxY2, minZ2).setColor(red, green, blue, alpha);
        buf.addVertex(m, minX2, maxY2, minZ).setColor(red, green, blue, alpha);

        buf.addVertex(m, minX2, maxY2, minZ2).setColor(red, green, blue, alpha);
        buf.addVertex(m, minX, maxY2, maxZ2).setColor(red, green, blue, alpha);
        buf.addVertex(m, minX, maxY2, minZ2).setColor(red, green, blue, alpha);

        buf.addVertex(m, minX2, maxY2, minZ2).setColor(red, green, blue, alpha);
        buf.addVertex(m, minX2, maxY2, maxZ2).setColor(red, green, blue, alpha);
        buf.addVertex(m, minX, maxY2, maxZ2).setColor(red, green, blue, alpha);

        buf.addVertex(m, maxX2, maxY2, maxZ).setColor(red, green, blue, alpha);
        buf.addVertex(m, minX2, maxY2, maxZ2).setColor(red, green, blue, alpha);
        buf.addVertex(m, maxX2, maxY2, maxZ2).setColor(red, green, blue, alpha);

        buf.addVertex(m, maxX2, maxY2, maxZ).setColor(red, green, blue, alpha);
        buf.addVertex(m, minX2, maxY2, maxZ).setColor(red, green, blue, alpha);
        buf.addVertex(m, minX2, maxY2, maxZ2).setColor(red, green, blue, alpha);

        buf.addVertex(m, maxX2, maxY2, minZ2).setColor(red, green, blue, alpha);
        buf.addVertex(m, maxX, maxY2, minZ2).setColor(red, green, blue, alpha);
        buf.addVertex(m, maxX, maxY2, maxZ2).setColor(red, green, blue, alpha);

        buf.addVertex(m, maxX2, maxY2, minZ2).setColor(red, green, blue, alpha);
        buf.addVertex(m, maxX, maxY2, maxZ2).setColor(red, green, blue, alpha);
        buf.addVertex(m, maxX2, maxY2, maxZ2).setColor(red, green, blue, alpha);

        //

        buf.addVertex(m, minX, maxY, minZ).setColor(red, green, blue, alpha);
        buf.addVertex(m, minX, maxY, maxZ).setColor(red, green, blue, alpha);
        buf.addVertex(m, minX2, maxY, maxZ2).setColor(red, green, blue, alpha);

        buf.addVertex(m, minX, maxY, minZ).setColor(red, green, blue, alpha);
        buf.addVertex(m, minX2, maxY, maxZ2).setColor(red, green, blue, alpha);
        buf.addVertex(m, minX2, maxY, minZ2).setColor(red, green, blue, alpha);

        buf.addVertex(m, minX, maxY, minZ).setColor(red, green, blue, alpha);
        buf.addVertex(m, minX2, maxY, minZ2).setColor(red, green, blue, alpha);
        buf.addVertex(m, maxX2, maxY, minZ2).setColor(red, green, blue, alpha);

        buf.addVertex(m, minX, maxY, minZ).setColor(red, green, blue, alpha);
        buf.addVertex(m, maxX2, maxY, minZ2).setColor(red, green, blue, alpha);
        buf.addVertex(m, maxX, maxY, minZ).setColor(red, green, blue, alpha);

        buf.addVertex(m, maxX, maxY, maxZ).setColor(red, green, blue, alpha);
        buf.addVertex(m, maxX, maxY, minZ).setColor(red, green, blue, alpha);
        buf.addVertex(m, maxX2, maxY, minZ2).setColor(red, green, blue, alpha);

        buf.addVertex(m, maxX, maxY, maxZ).setColor(red, green, blue, alpha);
        buf.addVertex(m, maxX2, maxY, minZ2).setColor(red, green, blue, alpha);
        buf.addVertex(m, maxX2, maxY, maxZ2).setColor(red, green, blue, alpha);

        buf.addVertex(m, maxX, maxY, maxZ).setColor(red, green, blue, alpha);
        buf.addVertex(m, maxX2, maxY, maxZ2).setColor(red, green, blue, alpha);
        buf.addVertex(m, minX2, maxY, maxZ2).setColor(red, green, blue, alpha);

        buf.addVertex(m, maxX, maxY, maxZ).setColor(red, green, blue, alpha);
        buf.addVertex(m, minX2, maxY, maxZ2).setColor(red, green, blue, alpha);
        buf.addVertex(m, minX, maxY, maxZ).setColor(red, green, blue, alpha);
    }

    private static final class RenderTypes extends RenderType
    {
        private RenderTypes(final String nameIn,
          final VertexFormat formatIn,
          final VertexFormat.Mode drawModeIn,
          final int bufferSizeIn,
          final boolean useDelegateIn,
          final boolean needsSortingIn,
          final Runnable setupTaskIn,
          final Runnable clearTaskIn)
        {
            super(nameIn, formatIn, drawModeIn, bufferSizeIn, useDelegateIn, needsSortingIn, setupTaskIn, clearTaskIn);
            throw new IllegalStateException();
        }

        private static final RenderType LINES_WITH_WIDTH = create("structurize_lines_with_width",
          DefaultVertexFormat.POSITION_COLOR,
          VertexFormat.Mode.TRIANGLES,
          1 << 13,
          false,
          false,
          RenderType.CompositeState.builder()
            .setTextureState(NO_TEXTURE)
            .setShaderState(POSITION_COLOR_SHADER)
            .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
            .setDepthTestState(LEQUAL_DEPTH_TEST)
            .setCullState(CULL)
            .setLightmapState(NO_LIGHTMAP)
            .setOverlayState(NO_OVERLAY)
            .setLayeringState(NO_LAYERING)
            .setOutputState(MAIN_TARGET)
            .setTexturingState(DEFAULT_TEXTURING)
            .setWriteMaskState(COLOR_DEPTH_WRITE)
            .createCompositeState(false));
    }

    public static class AlwaysDepthTestStateShard extends RenderStateShard.DepthTestStateShard
    {
        public static final DepthTestStateShard ALWAYS_DEPTH_TEST = new AlwaysDepthTestStateShard();

        private AlwaysDepthTestStateShard()
        {
            super("true_always", -1);
            setupState = () -> {
                RenderSystem.enableDepthTest();
                RenderSystem.depthFunc(GL11.GL_ALWAYS);
            };
        }
    }

    /**
     * Renders the given list of strings, 3 elements a row.
     *
     * @param pos                     position to render at
     * @param text                    text list
     * @param matrixStack             stack to use
     * @param buffer                  render buffer
     */
    public static void renderDebugText(final BlockPos pos,
      final List<String> text,
      final PoseStack matrixStack,
      final int red,
      final int green,
      final int blue,
      final int alpha,
      final MultiBufferSource buffer)
    {
        final EntityRenderDispatcher erm = Minecraft.getInstance().getEntityRenderDispatcher();
        final int cap = text.size();
        if (cap > 0 && erm.distanceToSqr(pos.getX(), pos.getY(), pos.getZ()) <= MAX_DEBUG_TEXT_RENDER_DIST_SQUARED)
        {
            final Font fontrenderer = Minecraft.getInstance().font;

            matrixStack.pushPose();
            matrixStack.translate(pos.getX() + 0.5d, pos.getY() + 0.75d, pos.getZ() + 0.5d);
            matrixStack.mulPose(erm.cameraOrientation());
            matrixStack.scale(-0.014f, -0.014f, 0.014f);
            matrixStack.translate(0.0d, 18.0d, 0.0d);

            final float backgroundTextOpacity = Minecraft.getInstance().options.getBackgroundOpacity(0.25F);
            final int alphaMask = (int) (backgroundTextOpacity * 255.0F) << 24;

            final Matrix4f rawPosMatrix = matrixStack.last().pose();

            for (int i = 0; i < cap; i += 1)
            {
                final MutableComponent renderText = Component.literal(text.get(i));
                final float textCenterShift = (float) (-fontrenderer.width(renderText) / 2);

                fontrenderer.drawInBatch(renderText,
                  textCenterShift,
                  0.0F,
                  0xff,
                  false,
                  rawPosMatrix,
                  buffer,
                  Font.DisplayMode.SEE_THROUGH,
                  alphaMask,
                  0x00f000f0);
                matrixStack.translate(0.0d, fontrenderer.lineHeight + 1, 0.0d);
            }

            matrixStack.popPose();
        }
    }
}
