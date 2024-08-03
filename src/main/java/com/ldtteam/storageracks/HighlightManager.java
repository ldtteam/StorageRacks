package com.ldtteam.storageracks;

import com.ldtteam.storageracks.utils.RenderUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@OnlyIn(Dist.CLIENT)
public class HighlightManager
{
    /**
     * A position to highlight with a unique id.
     */
    @Nullable
    public static final Map<String, List<TimedBoxRenderData>> HIGHLIGHT_MAP = new HashMap<>();

    /**
     * Used to catch the renderWorldLastEvent in order to draw the debug nodes for pathfinding.
     *
     * @param event the catched event.
     */
    @SubscribeEvent
    public static void renderWorldLastEvent(@NotNull final RenderLevelStageEvent event)
    {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_TRIPWIRE_BLOCKS)
        {
            if (!HIGHLIGHT_MAP.isEmpty())
            {
                final Minecraft mc = Minecraft.getInstance();

                final Vec3 viewPosition = mc.gameRenderer.getMainCamera().getPosition();
                final PoseStack matrixStack = event.getPoseStack();
                final MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();

                matrixStack.pushPose();
                matrixStack.translate(-viewPosition.x(), -viewPosition.y(), -viewPosition.z());

                final long worldTime = Minecraft.getInstance().level.getGameTime();
                for (final Iterator<List<TimedBoxRenderData>> categoryIterator = HIGHLIGHT_MAP.values().iterator(); categoryIterator.hasNext(); )
                {
                    final List<TimedBoxRenderData> boxes = categoryIterator.next();
                    for (final Iterator<TimedBoxRenderData> boxListIterator = boxes.iterator(); boxListIterator.hasNext(); )
                    {
                        final TimedBoxRenderData boxRenderData = boxListIterator.next();
                        if (boxRenderData.removalTimePoint <= worldTime)
                        {
                            boxListIterator.remove();
                        }
                        else
                        {
                            RenderUtils.renderBox(boxRenderData.pos,
                              boxRenderData.pos,
                              boxRenderData.getRed(),
                              boxRenderData.getGreen(),
                              boxRenderData.getBlue(),
                              0xff,
                              0.002D,
                              event.getPoseStack(),
                              bufferSource);

                            RenderUtils.renderDebugText(boxRenderData.pos, boxRenderData.text, event.getPoseStack(),
                              boxRenderData.getRed(),
                              boxRenderData.getGreen(),
                              boxRenderData.getBlue(),
                              0xff,  bufferSource);

                        }
                    }

                    if (boxes.isEmpty())
                    {
                        categoryIterator.remove();
                    }
                }
                bufferSource.endBatch();
                matrixStack.popPose();
            }
        }
    }

    /**
     * Box data for rendering
     */
    public static class TimedBoxRenderData
    {
        /**
         * List of strings to display
         */
        private List<String> text = new ArrayList<>();

        /**
         * Position to display at
         */
        private BlockPos pos = BlockPos.ZERO;

        /**
         * Timepoint of removal (world gametime)
         */
        private long removalTimePoint = 0;

        /**
         * Color code for the box
         */
        private int hexColor = 0xFFFFFF;

        public TimedBoxRenderData addText(final String text)
        {
            this.text.add(text);
            return this;
        }

        public TimedBoxRenderData setRemovalTimePoint(final long removalTimePoint)
        {
            this.removalTimePoint = removalTimePoint;
            return this;
        }

        public TimedBoxRenderData setPos(final BlockPos pos)
        {
            this.pos = pos;
            return this;
        }

        public TimedBoxRenderData setColor(final int hexColor)
        {
            this.hexColor = hexColor;
            return this;
        }

        /**
         * Get red %
         *
         * @return
         */
        private int getRed()
        {
            return ((hexColor >> 16) & 255);
        }

        /**
         * Get green %
         *
         * @return
         */
        private int getGreen()
        {
            return ((hexColor >> 8) & 255);
        }

        /**
         * Get blue %
         *
         * @return
         */
        private int getBlue()
        {
            return ((hexColor) & 255);
        }
    }

    /**
     * Adds a box to be rendered for the given category
     *
     * @param category
     * @param data
     */
    public static void addRenderBox(final String category, final TimedBoxRenderData data)
    {
        HIGHLIGHT_MAP.computeIfAbsent(category, k -> new ArrayList<>()).add(data);
    }

    /**
     * Clears all boxes of a category
     *
     * @param category
     */
    public static void clearCategory(final String category)
    {
        HIGHLIGHT_MAP.remove(category);
    }
}
