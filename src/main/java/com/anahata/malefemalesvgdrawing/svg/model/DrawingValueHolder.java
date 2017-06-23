/*
 *  Copyright © - 2015 Urgent Learning. All rights reserved.
 */
package com.anahata.malefemalesvgdrawing.svg.model;

import com.anahata.malefemalesvgdrawing.svg.SvgValue;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Value holder for the {@code DrawingInput} class.
 *
 * @author sai.dandem
 */
public class DrawingValueHolder extends Base implements Serializable {

    private SVGImageType imageType;

    private List<SvgValue> values = new ArrayList<>();

    public boolean isEmpty() {
        return values.isEmpty();
    }

    public DrawingValueHolder doCopy() {
        DrawingValueHolder copy = new DrawingValueHolder();
        values.forEach(value -> {
            SvgValue pCopy = value.doCopy();
            pCopy.setValueHolder(copy);
            copy.getValues().add(pCopy);
        });
        return copy;
    }

    /**
     * Returns all the {@code SvgValue}(s) for the provided {@code ImageFrame}.
     *
     * @param frame ImageFrame
     * @return List of {@code SvgValue}.
     */
    public List<SvgValue> getValuesByFrame(ImageFrame frame) {
        return values.stream().filter(p -> p.getImageFrame().equals(frame)).collect(Collectors.toList());
    }

    /**
     * Returns all the {@code PathValue}(s) for the provided {@code ImageFrame}.
     *
     * @param frame ImageFrame
     * @return List of {@code PathValue}.
     */
    public List<PathValue> getPathValuesByFrame(ImageFrame frame) {
        return getValuesByFrame(frame).stream()
                .filter(v -> v instanceof PathValue)
                .map(v -> (PathValue)v).collect(Collectors.toList());
    }

    /**
     * Returns all the {@code DotValue}(s) for the provided {@code ImageFrame}.
     *
     * @param frame ImageFrame
     * @return List of {@code DotValue}.
     */
    public List<DotValue> getDotValuesByFrame(ImageFrame frame) {
        return getValuesByFrame(frame).stream()
                .filter(v -> v instanceof DotValue)
                .map(v -> (DotValue)v).collect(Collectors.toList());
    }

    public SVGImageType getImageType() {
        return imageType;
    }

    public void setImageType(SVGImageType imageType) {
        this.imageType = imageType;
    }

    public List<SvgValue> getValues() {
        return values;
    }

    public void setValues(List<SvgValue> values) {
        this.values = values;
    }

}
