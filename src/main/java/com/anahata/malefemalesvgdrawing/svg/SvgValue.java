/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.anahata.malefemalesvgdrawing.svg;

import com.anahata.malefemalesvgdrawing.svg.model.Base;
import com.anahata.malefemalesvgdrawing.svg.model.DrawingValueHolder;
import com.anahata.malefemalesvgdrawing.svg.model.ImageFrame;
import java.io.Serializable;

/**
 *
 * @author sai.dandem
 */
public abstract class SvgValue extends Base implements Serializable {
    private DrawingValueHolder valueHolder;

    private ImageFrame imageFrame;

    private String color;

    private String dValue;

    public abstract SvgValue doCopy();

    protected void doCopy(SvgValue copy) {
        copy.setImageFrame(getImageFrame());
        copy.setColor(getColor());
        copy.setDValue(getDValue());
    }

    public DrawingValueHolder getValueHolder() {
        return valueHolder;
    }

    public void setValueHolder(DrawingValueHolder valueHolder) {
        this.valueHolder = valueHolder;
    }

    public ImageFrame getImageFrame() {
        return imageFrame;
    }

    public void setImageFrame(ImageFrame imageFrame) {
        this.imageFrame = imageFrame;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getDValue() {
        return dValue;
    }

    public void setDValue(String dValue) {
        this.dValue = dValue;
    }

}
