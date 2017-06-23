/*
 *  Copyright © - 2015 Urgent Learning. All rights reserved.
 */
package com.anahata.malefemalesvgdrawing.svg.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity to hold the details of master image and its frames.
 *
 * @author sai.dandem
 */
public class MasterImage extends Base implements Frame, Serializable {

    private double renderSize;

    private double width;

    private double height;

    private double contentWidth;

    private double contentHeight;

    private double translateX;

    private double translateY;

    private double contentXOffSet;

    private double contentYOffSet;

    private SVGImageType type;
    
    private String dValue;

    private byte[] bytes;

    private List<ImageFrame> frames = new ArrayList<>();
    
    public double getRenderSize() {
        return renderSize;
    }

    public void setRenderSize(double renderSize) {
        this.renderSize = renderSize;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public String getDValue() {
        return dValue;
    }

    public void setDValue(String dValue) {
        this.dValue = dValue;
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

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setContentWidth(double contentWidth) {
        this.contentWidth = contentWidth;
    }

    public double getContentWidth() {
        return contentWidth;
    }

    public void setContentHeight(double contentHeight) {
        this.contentHeight = contentHeight;
    }

    public double getContentHeight() {
        return contentHeight;
    }

    public double getContentXOffSet() {
        return contentXOffSet;
    }

    public void setContentXOffSet(double contentXOffSet) {
        this.contentXOffSet = contentXOffSet;
    }

    public double getContentYOffSet() {
        return contentYOffSet;
    }

    public void setContentYOffSet(double contentYOffSet) {
        this.contentYOffSet = contentYOffSet;
    }

    public double getXOffSet() {
        return this.contentXOffSet;//(this.width - this.contentWidth) / 2;
    }

    public double getYOffSet() {
        return this.contentYOffSet;//(this.height - this.contentHeight) / 2;
    }

    @Override
    public List<ImageFrame> getFrames() {
        return frames;
    }

    public SVGImageType getType() {
        return type;
    }

    public void setType(SVGImageType type) {
        this.type = type;
    }
    
    
}
