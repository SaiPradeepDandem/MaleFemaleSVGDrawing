/*
 *  Copyright © - 2015 Urgent Learning. All rights reserved.
 */
package com.anahata.malefemalesvgdrawing.svg.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity to hold the frame bound details in a ImageFrame.
 *
 * @author sai.dandem
 */
public class ImageFrame extends Base implements Frame, Serializable {

    private double startX;

    private double startY;

    private double endX;

    private double endY;

    private boolean toFront = false;
    
    private MasterImage image;

    private ImageFrame parentFrame;

    private List<ImageFrame> frames = new ArrayList<>();
    
    public ImageFrame() {
    }

    public ImageFrame(long id, double startX, double startY, double endX, double endY, MasterImage image) {
        setId(id);
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.image = image;
    }

    public ImageFrame(long id, double startX, double startY, double endX, double endY, ImageFrame parentFrame) {
        setId(id);
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.parentFrame = parentFrame;
    }

    public MasterImage getMasterImage() {
        if (this.image != null) {
            return this.image;
        } else {
            return this.parentFrame.getMasterImage();
        }
    }

    public MasterImage getImage() {
        return image;
    }

    public void setImage(MasterImage image) {
        this.image = image;
    }

    public double getStartX() {
        return startX;
    }

    public void setStartX(double startX) {
        this.startX = startX;
    }

    public double getStartY() {
        return startY;
    }

    public void setStartY(double startY) {
        this.startY = startY;
    }

    public double getEndX() {
        return endX;
    }

    public void setEndX(double endX) {
        this.endX = endX;
    }

    public double getEndY() {
        return endY;
    }

    public void setEndY(double endY) {
        this.endY = endY;
    }

    public boolean isToFront() {
        return toFront;
    }

    public void setToFront(boolean toFront) {
        this.toFront = toFront;
    }

    public ImageFrame getParentFrame() {
        return parentFrame;
    }

    public void setParentFrame(ImageFrame parentFrame) {
        this.parentFrame = parentFrame;
    }

    @Override
    public List<ImageFrame> getFrames() {
        return frames;
    }

    public void setFrames(List<ImageFrame> frames) {
        this.frames = frames;
    }

    
    // Utils
    public double getWidth() {
        return endX - startX;
    }

    public double getHeight() {
        return endY - startY;
    }

    public double getWidthByScale(double scale) {
        return getWidth() * scale;
    }

    public double getHeightByScale(double scale) {
        return getHeight() * scale;
    }

    public double getCenterX() {
        return startX + (getWidth() / 2);
    }

    public double getCenterY() {
        return startY + (getHeight() / 2);
    }

    public double getCenterXByScale(double scale) {
        return getCenterX() * scale;
    }

    public double getCenterYByScale(double scale) {
        return getCenterY() * scale;
    }

    // -----------------------------------------------------------------------------
   
    public double getRenderStartX(){
        return startX - getMasterImage().getContentXOffSet();
    }
    public double getRenderStartY(){
        return startY - getMasterImage().getContentYOffSet();
    }
    public double getRenderEndX(){
        return endX - getMasterImage().getContentXOffSet();
    }
    public double getRenderEndY(){
        return endY - getMasterImage().getContentYOffSet();
    }
    public Frame getParent() {
        return (parentFrame != null) ? parentFrame : image;
    }
    
     public double getRenderCenterX() {
        return getRenderStartX() + (getWidth() / 2);
    }

    public double getRenderCenterY() {
        return getRenderStartY() + (getHeight() / 2);
    }

    public double getRenderCenterXByScale(double scale) {
        return getRenderCenterX() * scale;
    }

    public double getRenderCenterYByScale(double scale) {
        return getRenderCenterY() * scale;
    }

  
}
