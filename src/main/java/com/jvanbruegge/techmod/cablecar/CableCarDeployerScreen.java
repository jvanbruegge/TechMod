package com.jvanbruegge.techmod.cablecar;

import com.jvanbruegge.techmod.TechMod;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class CableCarDeployerScreen extends ContainerScreen<CablecarDeployerContainer> {
    private final ResourceLocation guiTexture = new ResourceLocation(TechMod.MODID, "textures/gui/cablecar_deployer.png");
    private final ITextComponent multiplierText = new TranslationTextComponent("block.techmod.cablecar_deployer.multiplier");

    private TextFieldWidget multiplier;

    public CableCarDeployerScreen(CablecarDeployerContainer container, PlayerInventory inventory, ITextComponent textComponent) {
        super(container, inventory, textComponent);
    }

    @Override
    protected void init() {
        super.init();
        this.minecraft.keyboardListener.enableRepeatEvents(true);
        this.multiplier = new TextFieldWidget(this.font, this.guiLeft + 54, this.guiTop + 36, 24, 12, I18n.format("block.techmod.cablecar_deployer.multiplier", new Object[0]));
        this.multiplier.setCanLoseFocus(false);
        this.multiplier.changeFocus(true);
        this.multiplier.setTextColor(-1);
        this.multiplier.setDisabledTextColour(-1);
        this.multiplier.setEnableBackgroundDrawing(false);
        this.multiplier.setMaxStringLength(2);
        this.multiplier.setEnabled(this.container.isEnabled());
        this.multiplier.setValidator(text -> text.matches("\\d*") && (text.length() == 0 || Integer.parseInt(text) <= 64));
        this.multiplier.setText(Integer.toString(this.container.getMultiplier()));
        this.multiplier.setResponder(this::onTextUpdate);
        this.children.add(this.multiplier);
        this.container.setScreen(this);
        this.setFocusedDefault(this.multiplier);
    }

    private void onTextUpdate(String text) {
        int mult = text.equals("") ? 0 : Integer.parseInt(text);
        this.container.setMultiplier(mult, true);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        this.renderBackground();
        super.render(mouseX, mouseY, partialTicks);
        RenderSystem.disableBlend();
        this.multiplier.render(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    public void setEnabled(boolean enabled) {
        this.multiplier.setEnabled(enabled);
    }

    @Override
    public boolean keyPressed(int keyCode, int p_keyPressed_2_, int p_keyPressed_3_) {
        if (keyCode == 256) {
            this.minecraft.player.closeScreen();
        }

        return !this.multiplier.keyPressed(keyCode, p_keyPressed_2_, p_keyPressed_3_) && !this.multiplier.canWrite() ? super.keyPressed(keyCode, p_keyPressed_2_, p_keyPressed_3_) : true;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        this.font.drawString(this.title.getFormattedText(), 8.0F, 6.0F, 4210752);
        this.font.drawString(this.playerInventory.getDisplayName().getFormattedText(), 8.0F, (float)(this.ySize - 96 + 2), 4210752);
        this.font.drawString(this.multiplierText.getFormattedText(), 50.0F, 22.0F, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        this.renderBackground();
        this.minecraft.getTextureManager().bindTexture(guiTexture);
        this.blit(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        this.blit(this.guiLeft + 50, this.guiTop + 32, 0,  this.container.isEnabled() ? 166 : 182, 28, 16);
    }

    public void setMuliplier(int multiplier) {
        this.multiplier.setText(Integer.toString(multiplier));
    }

    public void setTextEnabled(boolean enabled) {
        this.multiplier.setEnabled(enabled);
    }
}
