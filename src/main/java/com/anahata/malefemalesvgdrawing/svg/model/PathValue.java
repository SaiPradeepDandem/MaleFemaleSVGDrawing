/*
 *  Copyright © - 2015 Urgent Learning. All rights reserved.
 */
package com.anahata.malefemalesvgdrawing.svg.model;

import com.anahata.malefemalesvgdrawing.svg.SvgValue;
import java.io.Serializable;

/**
 * Entity for holding the values related to each Path element.
 *
 * @author sai.dandem
 */
public class PathValue extends SvgValue implements Serializable {

    private double translateX;

    private double translateY;

    private double strokeWidth;

    private boolean dotted;

    /**
     * Creates a new instance of PathValue with the same property values.
     *
     * @return A new instance of PathValue.
     */
    @Override
    public PathValue doCopy() {
        final PathValue copy = new PathValue();
        super.doCopy(copy);
        copy.setStrokeWidth(strokeWidth);
        copy.setDotted(dotted);
        copy.setTranslateX(getTranslateX());
        copy.setTranslateY(getTranslateY());
        return copy;
    }

    public double getStrokeWidth() {
        return strokeWidth;
    }

    public void setStrokeWidth(double strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    public boolean isDotted() {
        return dotted;
    }

    public void setDotted(boolean dotted) {
        this.dotted = dotted;
    }

    public double getTranslateX() {
        return translateX;
    }

    public void setTranslateX(double translateX) {
        this.translateX = translateX;
    }

    public double getTranslateY() {
        return translateY;
    }

    public void setTranslateY(double translateY) {
        this.translateY = translateY;
    }

}
