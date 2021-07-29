package com.ldtteam.storageracks.utils;

import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.OptionalDouble;

/**
 * Straight structurize copy.
 */
public class RenderUtils
{
    public static final RenderType LINES_GLINT = RenderTypes.LINES_GLINT;

    /**
     * Render a box around two positions
     *
     * @param posA    First position
     * @param posB    Second position
     * @param red     red colour float 0 - 1
     * @param green   green colour float 0 - 1
     * @param blue    blue colour float 0 - 1
     * @param alpha   opacity 0 - 1
     * @param boxGrow size grow in every direction
     */
    public static void renderBox(
      final BlockPos posA,
      final BlockPos posB,
      final float red,
      final float green,
      final float blue,
      final float alpha,
      final double boxGrow,
      final PoseStack matrixStack,
      final VertexConsumer buffer)
    {
        final double minX = Math.min(posA.getX(), posB.getX()) - boxGrow;
        final double minY = Math.min(posA.getY(), posB.getY()) - boxGrow;
        final double minZ = Math.min(posA.getZ(), posB.getZ()) - boxGrow;

        final double maxX = Math.max(posA.getX(), posB.getX()) + 1 + boxGrow;
        final double maxY = Math.max(posA.getY(), posB.getY()) + 1 + boxGrow;
        final double maxZ = Math.max(posA.getZ(), posB.getZ()) + 1 + boxGrow;

        final Vec3 viewPosition = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();

        matrixStack.pushPose();
        matrixStack.translate(-viewPosition.x, -viewPosition.y, -viewPosition.z);

        LevelRenderer.renderLineBox(matrixStack, buffer, minX, minY, minZ, maxX, maxY, maxZ, red, green, blue, alpha);

        matrixStack.popPose();
    }

    public static final class RenderTypes extends RenderType
    {
        public RenderTypes(
          final String nameIn,
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

        private static final RenderType LINES_GLINT = create("storageracks_lines_glint",
            DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.LINES, 256, false, false,
            RenderType.CompositeState.builder()
              .setShaderState(RENDERTYPE_LINES_SHADER)
              .setLineState(new LineStateShard(OptionalDouble.of(4.0)))
              .setLayeringState(VIEW_OFFSET_Z_LAYERING)
              .setTransparencyState(NO_TRANSPARENCY)
              .setOutputState(ITEM_ENTITY_TARGET)
              .setWriteMaskState(COLOR_WRITE)
              .setCullState(NO_CULL)
              .setDepthTestState(NO_DEPTH_TEST)
              .createCompositeState(false));
    }
}
