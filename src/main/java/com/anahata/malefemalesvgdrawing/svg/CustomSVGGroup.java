/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.anahata.malefemalesvgdrawing.svg;

import afester.javafx.svg.SvgLoader;
import com.anahata.malefemalesvgdrawing.svg.model.ImageFrame;
import com.anahata.malefemalesvgdrawing.svg.model.MasterImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import javafx.scene.Group;

/**
 * Customized SVGGroup node to get Group object from svg and hold extra details.
 *
 * @author sai.dandem
 */
public class CustomSVGGroup extends Group {
    private Group group;

    private final double originalWidth;

    private final double originalHeight;

    public CustomSVGGroup(MasterImage masterImage) {
        this.originalWidth = masterImage.getContentWidth();
        this.originalHeight = masterImage.getContentHeight();

        InputStream is = new ByteArrayInputStream(masterImage.getBytes());
        SvgLoader loader = new SvgLoader();
        setGroup(loader.loadSvg(is));
        getChildren().add(getGroup());
    }

    public double getOriginalWidth() {
        return originalWidth;
    }

    public double getOriginalHeight() {
        return originalHeight;
    }

    public double getScaleBySize(double size) {
        return Math.min(size / originalWidth, size / originalHeight);
    }

    public double getSvgScaleByFrameAndSize(ImageFrame f, double size) {
        return Math.min(size / f.getWidth(), size / f.getHeight());
    }

    public void scaleTo(double scale) {
        setScaleX(scale);
        setScaleY(scale);
    }

    public void scaleToSize(double size) {
        scaleTo(getScaleBySize(size));
    }

    public double getWidthByScale(double scale) {
        return originalWidth * scale;
    }

    public double getHeightByScale(double scale) {
        return originalHeight * scale;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

}
