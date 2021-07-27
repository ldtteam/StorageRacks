package com.ldtteam.storageracks.gui;

import com.ldtteam.storageracks.inv.InsertContainer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class WindowInsert extends AbstractContainerScreen<InsertContainer>
{
    /**
     * Hopper inv ui texture location.
     */
    private static final ResourceLocation HOPPER_LOCATION = new ResourceLocation("textures/gui/container/hopper.png");

    /**
     * Create a new insert window.
     * @param container the container it belongs to.
     * @param player the player opening it.
     * @param label the label to display.
     */
    public WindowInsert(@NotNull final InsertContainer container, @NotNull final Inventory player, @NotNull final Component label)
    {
        super(container, player, label);
        this.passEvents = false;
        this.imageHeight = 133;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    public void render(@NotNull final PoseStack stack, int partialTicks, int mouseX, float mouseY)
    {
        this.renderBackground(stack);
        super.render(stack, partialTicks, mouseX, mouseY);
        this.renderTooltip(stack, partialTicks, mouseX);
    }

    @Override
    protected void renderBg(@NotNull final PoseStack stack, float partialTicks, int mouseX, int mouseY)
    {
        RenderSystem.setShaderTexture(0, HOPPER_LOCATION);
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        this.blit(stack, i, j, 0, 0, this.imageWidth, this.imageHeight);
    }
}
