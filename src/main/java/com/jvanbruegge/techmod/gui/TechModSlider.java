package com.jvanbruegge.techmod.gui;

import net.minecraftforge.fml.client.gui.widget.Slider;

public class TechModSlider extends Slider {
    private final IPressed pressed;
    private final IReleased released;

    public TechModSlider(int xPos, int yPos, int width, int height, String prefix, String suf, double minVal, double maxVal, double currentVal, boolean showDec, boolean drawStr, ISlider onUpdate, IPressed pressed, IReleased released) {
        super(xPos, yPos, width, height, prefix, suf, minVal, maxVal, currentVal, showDec, drawStr, null, onUpdate);
        this.pressed = pressed;
        this.released = released;
    }

    public void setEnabled(boolean enabled) {
        this.active = enabled;
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        super.onClick(mouseX, mouseY);
        if(this.pressed != null) {
            this.pressed.onPressed(this);
        }
    }

    @Override
    public void onRelease(double mouseX, double mouseY) {
        super.onRelease(mouseX, mouseY);
        if(this.released != null) {
            this.released.onReleased(this);
        }
    }

    public interface IPressed {
        void onPressed(TechModSlider slider);
    }
    public interface IReleased {
        void onReleased(TechModSlider slider);
    }
}
