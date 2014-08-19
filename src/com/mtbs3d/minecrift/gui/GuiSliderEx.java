/**
 * Copyright 2013 Mark Browning, StellaArtois
 * Licensed under the LGPL 3.0 or later (See LICENSE.md for details)
 */
package com.mtbs3d.minecrift.gui;

import com.mtbs3d.minecrift.settings.VRSettings;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;

public class GuiSliderEx extends GuiButtonEx
{
    /** The relative value of this slider control. */
    private float sliderValue = 1.0F;

    /** Is this slider control being dragged. */
    private boolean dragging = false;

    /** Additional ID for this slider control. */
    private VRSettings.VrOptions idFloat = null;

    /** The maximum actual value of this slider control. */
    private float maxValue = 1.0f;

    /** The minimum actual value of this slider control. */
    private float minValue = 0.0f;

    /** The allowable increment of the actual value of this slider control. */
    private float increment;

    /** The last actual value of this slider control */
    private float lastValue;

    /** The last known value x position of the mouse pointer */
    private int lastMouseX = -1;

    GuiEventEx _eventHandler = null;

    public GuiSliderEx(int par1, int par2, int par3,
                       VRSettings.VrOptions par4EnumOptions, String par5Str,
                       float minValue, float maxValue, float increment, float currentValue)
    {
        super(par1, par2, par3, 150, 20, par5Str);
        this.idFloat = par4EnumOptions;
        this.increment = increment;
        this.lastValue = Math.round(currentValue / this.increment) * this.increment;

        this.minValue = minValue;
        this.maxValue = maxValue;
        if (this.lastValue > this.maxValue)
            this.lastValue = this.maxValue;
        else if (this.lastValue < this.minValue)
            this.lastValue = this.minValue;

        float range = this.maxValue - this.minValue;
        this.sliderValue = (this.lastValue - this.minValue) / range;
    }

    void setEventHandler(GuiEventEx eventHandler)
    {
        _eventHandler = eventHandler;
    }

    /**
     * Returns 0 if the button is disabled, 1 if the mouse is NOT hovering over this button and 2 if it IS hovering over
     * this button.
     */
    public int getHoverState(boolean par1)
    {
        return 0;
    }

    /**
     * Fired when the mouse button is dragged. Equivalent of MouseListener.mouseDragged(MouseEvent e).
     */
    protected void mouseDragged(Minecraft par1Minecraft, int par2, int par3)
    {
        if (this.enabled && this.field_146125_m)
        {
            if (this.dragging && par2 != this.lastMouseX)
            {
                float startValue = this.lastValue;
                this.lastMouseX = -1;
                this.sliderValue = (float)(par2 - (this.field_146128_h + 4)) / (float)(this.field_146120_f - 8);

                if (this.sliderValue < 0.0F)
                {
                    this.sliderValue = 0.0F;
                }

                if (this.sliderValue > 1.0F)
                {
                    this.sliderValue = 1.0F;
                }

                float range = this.maxValue - this.minValue;
                this.lastValue = this.minValue + (this.sliderValue * range);
                this.lastValue = Math.round(this.lastValue / this.increment) * this.increment;
                par1Minecraft.vrSettings.setOptionFloatValue(this.idFloat, this.lastValue);
                this.displayString = par1Minecraft.vrSettings.getKeyBinding(this.idFloat);

                if (_eventHandler != null && startValue != this.lastValue)
                    _eventHandler.event(GuiEventEx.ID_VALUE_CHANGED, this.idFloat);
            }

            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.drawTexturedModalRect(this.field_146128_h + (int)(this.sliderValue * (float)(this.field_146120_f - 8)), this.field_146129_i, 0, 66, 4, 20);
            this.drawTexturedModalRect(this.field_146128_h + (int)(this.sliderValue * (float)(this.field_146120_f - 8)) + 4, this.field_146129_i, 196, 66, 4, 20);
        }
    }

    /**
     * Returns true if the mouse has been pressed on this control. Equivalent of MouseListener.mousePressed(MouseEvent
     * e).
     */
    public boolean mousePressed(Minecraft par1Minecraft, int par2, int par3)
    {
        if (this.enabled && super.mousePressed(par1Minecraft, par2, par3))
        {
            float tempSliderValue = (float)(par2 - (this.field_146128_h + 4)) / (float)(this.field_146120_f - 8);

            if (tempSliderValue < 0.0F)
            {
                tempSliderValue = 0.0F;
            }

            if (tempSliderValue > 1.0F)
            {
                tempSliderValue = 1.0F;
            }

            float range = this.maxValue - this.minValue;
            float tempValue = this.minValue + (tempSliderValue * range);
            tempValue = Math.round(tempValue / this.increment) * this.increment;

            // For a mouse press only (before the mouse is dragged), we want a single
            // increment increase or decrease, if possible.
            if (tempValue > this.lastValue)
                this.lastValue += increment;
            else if (tempValue < this.lastValue)
                this.lastValue -= increment;

            if (this.lastValue > this.maxValue)
                this.lastValue = this.maxValue;
            else if (this.lastValue < this.minValue)
                this.lastValue = this.minValue;

            this.sliderValue = (this.lastValue - this.minValue) / range;
            par1Minecraft.vrSettings.setOptionFloatValue(this.idFloat, this.lastValue);
            this.displayString = par1Minecraft.vrSettings.getKeyBinding(this.idFloat);
            this.lastMouseX = par2;
            this.dragging = true;

            if (_eventHandler != null)
                _eventHandler.event(GuiEventEx.ID_VALUE_CHANGED, this.idFloat);

            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Fired when the mouse button is released. Equivalent of MouseListener.mouseReleased(MouseEvent e).
     */
    public void mouseReleased(int par1, int par2)
    {
        if (this.enabled)
        {
            this.lastMouseX = -1;
            float range = this.maxValue - this.minValue;
            this.sliderValue = (this.lastValue - this.minValue) / range;  // Sync slider pos with last (actual) value
            this.dragging = false;
        }
    }
}
