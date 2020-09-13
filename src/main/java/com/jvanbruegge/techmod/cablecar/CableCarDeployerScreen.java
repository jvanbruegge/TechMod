package com.jvanbruegge.techmod.cablecar;

import com.jvanbruegge.techmod.TechMod;
import com.jvanbruegge.techmod.gui.TechModSlider;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class CableCarDeployerScreen extends ContainerScreen<CablecarDeployerContainer> {
    private final ResourceLocation guiTexture = new ResourceLocation(TechMod.MODID, "textures/gui/cablecar_deployer.png");

    private TechModSlider multiplier;
    private Button mode;
    private Button keepCarts;

    public CableCarDeployerScreen(CablecarDeployerContainer container, PlayerInventory inventory, ITextComponent textComponent) {
        super(container, inventory, textComponent);
        this.ySize = 176;
    }

    @Override
    protected void init() {
        super.init();
        this.minecraft.keyboardListener.enableRepeatEvents(true);
        this.multiplier = new TechModSlider(
                this.guiLeft + 41, this.guiTop + 29, 128, 20,
                I18n.format("block.techmod.cablecar_deployer.multiplier") + ": ", "",
                1, 64, this.container.getMultiplier(), false, true, slider -> {
                    this.container.setMultiplier(slider.getValueInt(), true);
                }, slider -> this.container.setActive(true), slider -> this.container.setActive(false)
        );
        this.multiplier.setEnabled(this.container.isEnabled());
        this.children.add(this.multiplier);
        this.container.setScreen(this);

        this.mode = new Button(this.guiLeft + 7, this.guiTop + 55, 76, 20, this.getModeText(), button -> {
            this.container.setBinary(!this.container.isBinary(), true);
            button.setMessage(this.getModeText());
        });
        this.keepCarts = new Button(this.guiLeft + 93, this.guiTop + 55, 76, 20, this.getKeepCartsText(), button -> {
            this.container.setKeepCarts(!this.container.isKeepCarts(), true);
            button.setMessage(this.getKeepCartsText());
        });
        this.addButton(mode);
        this.addButton(keepCarts);
    }

    private String getModeText() {
        String base = I18n.format("block.techmod.cablecar_deployer.mode");

        String binary = this.container.isBinary() ? "digital" : "analog";
        String name = I18n.format("block.techmod.cablecar_deployer." + binary);
        return base + ": " + name;
    }

    private String getKeepCartsText() {
        String base = I18n.format("block.techmod.cablecar_deployer.carts");

        String keep = this.container.isKeepCarts() ? "keep" : "eject";
        String name = I18n.format("block.techmod.cablecar_deployer." + keep);
        return base + ": " + name;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        this.renderBackground();
        super.render(mouseX, mouseY, partialTicks);
        RenderSystem.disableBlend();
        this.multiplier.render(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        this.font.drawString(this.title.getFormattedText(), 8.0F, 6.0F, 4210752);
        this.font.drawString(this.playerInventory.getDisplayName().getFormattedText(), 8.0F, (float)(this.ySize - 96 + 2), 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        this.renderBackground();
        this.minecraft.getTextureManager().bindTexture(guiTexture);
        this.blit(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        this.blit(this.guiLeft + 87, this.guiTop + 31, 0,  this.container.isEnabled() ? 176 : 192, 28, 16);
    }

    public void setSliderEnabled(boolean enabled) {
        this.multiplier.setEnabled(enabled);
    }

    public void setMuliplier(int multiplier) {
        this.multiplier.setValue(multiplier);
        this.multiplier.updateSlider();
    }

    public void updateMode() {
        this.mode.setMessage(this.getModeText());
    }

    public void updateKeepCarts() {
        this.keepCarts.setMessage(this.getKeepCartsText());
    }
}
