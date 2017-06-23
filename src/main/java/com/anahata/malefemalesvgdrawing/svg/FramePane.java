/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.anahata.malefemalesvgdrawing.svg;

import com.anahata.malefemalesvgdrawing.svg.model.DotValue;
import com.anahata.malefemalesvgdrawing.svg.model.Frame;
import com.anahata.malefemalesvgdrawing.svg.model.ImageFrame;
import com.anahata.malefemalesvgdrawing.svg.model.MasterImage;
import com.anahata.malefemalesvgdrawing.svg.model.PathValue;
import java.util.*;
import java.util.stream.Collectors;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Path;
import static com.anahata.malefemalesvgdrawing.svg.PathUtils.*;

/**
 *
 * @author sai.dandem
 */
public class FramePane extends StackPane {
    enum FrameState {
        DEFAULT, EDITING, EDITED
    };

    private ObjectProperty<FrameState> state = new SimpleObjectProperty<>(FrameState.DEFAULT);

    private final List<PathValue> pathValues = new ArrayList<>();

    private final List<DotValue> dotValues = new ArrayList<>();

    private BooleanProperty showBorder = new SimpleBooleanProperty(true);

    private final DrawingInputControl control;

    private final CustomSVGGroup svg;

    private final ImageFrame frame;

    private final StackPane border;

    public FramePane(ImageFrame frame, DrawingInputControl control) {
        this.control = control;
        this.svg = control.getSvg();
        this.frame = frame;
        getStyleClass().add("frame-pane");
        setAlignment(Pos.TOP_LEFT);
        setOnMouseClicked(e -> control.zoomFrame(this));

        border = new StackPane();
        border.visibleProperty().bind(showBorder);
        border.getStyleClass().add("default-frame");
        getChildren().add(border);

        state.addListener((p1, p2, value) -> {
            border.getStyleClass().clear();
            switch (value) {
                case EDITED:
                    border.getStyleClass().add("edited-frame");
                    break;
                case EDITING:
                    border.getStyleClass().add("editing-frame");
                    break;
                default:
                    border.getStyleClass().add("default-frame");
            }
        });
    }

    public void layout(double size) {
        double scale = 0, startX = 0, startY = 0, width = 0, height = 0;
        final Frame parent = frame.getParent();
        if (parent instanceof MasterImage) {
            final MasterImage masterImage = (MasterImage)parent;
            scale = svg.getScaleBySize(size);
            double xOffSet = 0;
            double yOffSet = 0;
            if (masterImage.getContentHeight() > masterImage.getContentWidth()) {
                xOffSet = (size - (masterImage.getContentWidth() * scale)) / 2;
            }
            if (masterImage.getContentWidth() > masterImage.getContentHeight()) {
                yOffSet = (size - (masterImage.getContentHeight() * scale)) / 2;
            }

            startX = (frame.getRenderStartX() * scale) + xOffSet;
            startY = (frame.getRenderStartY() * scale) + yOffSet;
            double endX = (frame.getRenderEndX() * scale) + xOffSet;
            double endY = (frame.getRenderEndY() * scale) + yOffSet;
            height = endY - startY;
            width = endX - startX;

        } else if (parent instanceof ImageFrame) {
            final ImageFrame parentFrame = (ImageFrame)parent;
            scale = svg.getSvgScaleByFrameAndSize(parentFrame, size);
            double xOffSet = 0;
            double yOffSet = 0;
            if (parentFrame.getHeight() > parentFrame.getWidth()) {
                xOffSet = (size - (parentFrame.getWidthByScale(scale))) / 2;
            }
            if (parentFrame.getWidth() > parentFrame.getHeight()) {
                yOffSet = (size - (parentFrame.getHeightByScale(scale))) / 2;
            }

            startX = ((frame.getRenderStartX() - parentFrame.getRenderStartX()) * scale) + xOffSet;
            startY = ((frame.getRenderStartY() - parentFrame.getRenderStartY()) * scale) + yOffSet;
            double endX = ((frame.getRenderEndX() - parentFrame.getRenderStartX()) * scale) + xOffSet;
            double endY = ((frame.getRenderEndY() - parentFrame.getRenderStartY()) * scale) + yOffSet;
            height = endY - startY;
            width = endX - startX;
        }

        // Layouting the frame pane.
        setPrefSize(width, height);
        setMinSize(width, height);
        setMaxSize(width, height);
        setTranslateX(startX);
        setTranslateY(startY);

        // Layouting the paths.    
        resetPane();
        double scl = scale;
        getOriginalScalePaths().forEach(p -> {
            Path scaledPath = PathUtils.getScaledPath(p, scl);
            getChildren().add(scaledPath);
        });

        getOriginalScaleDots().forEach((color, dots) -> {
            dots.forEach(coord -> {
                Circle scaledDot = PathUtils.createDot(Double.parseDouble(coord.split(" ")[0]), Double.parseDouble(
                        coord.split(" ")[1]), scl, color, scl);
                getChildren().add(scaledDot);
            });
        });

        // Layouting subframe paths & dots..
        frame.getFrames().forEach(f -> {
            List<PathValue> subPathValues = control.getValueHolder().getPathValuesByFrame(f);
            List<DotValue> subDotValues = control.getValueHolder().getDotValuesByFrame(f);
            if (!subPathValues.isEmpty() || !subDotValues.isEmpty()) {
                StackPane sp = new StackPane();
                sp.setAlignment(Pos.TOP_LEFT);
                double w = f.getWidthByScale(scl);
                double h = f.getHeightByScale(scl);
                sp.setPrefSize(w, h);
                sp.setMinSize(w, h);
                sp.setMaxSize(w, h);
                sp.setTranslateX(((f.getRenderStartX() - frame.getRenderStartX()) * scl));
                sp.setTranslateY(((f.getRenderStartY() - frame.getRenderStartY()) * scl));
                getChildren().add(sp);
                subPathValues.stream().map(v -> PathUtils.convertToPath(v)).forEach(p -> {
                    Path scaledSubPath = PathUtils.getScaledPath(p, scl);
                    sp.getChildren().add(scaledSubPath);
                });

                subDotValues.forEach(dv -> {
                    dv.updateStringToDots();
                    dv.getDots().forEach(coord -> {
                        Circle scaledSubDot = PathUtils.createDot(Double.parseDouble(coord.split(" ")[0]),
                                Double.parseDouble(coord.split(" ")[1]), scl, (Color)Paint.valueOf(dv.getColor()), scl);
                        sp.getChildren().add(scaledSubDot);
                    });
                });
            }
        });

        if (getChildren().size() == 1) { // if only border.
            setState(FrameState.DEFAULT);
        } else {
            setState(FrameState.EDITED);
        }

    }

    public BooleanProperty getShowBorder() {
        return showBorder;
    }

    public ImageFrame getFrame() {
        return frame;
    }

    public List<Path> getOriginalScalePaths() {
        List<PathValue> values = control.getValueHolder().getPathValuesByFrame(frame);
        return values.stream().map(v -> PathUtils.convertToPath(v)).collect(Collectors.toList());
    }

    public Map<Color, Set<String>> getOriginalScaleDots() {
        List<DotValue> values = control.getValueHolder().getDotValuesByFrame(frame);
        Map<Color, Set<String>> map = new HashMap<>();
        values.forEach(dv -> {
            dv.updateStringToDots();
            map.put((Color)Paint.valueOf(dv.getColor()), dv.getDots());
        });
        return map;
    }

    public void setState(FrameState s) {
        state.set(s);
    }

    public void updateControlValueHolder(List<Path> originalScalePaths, Map<Color, Set<String>> originalScaleDots) {
        pathValues.clear();
        pathValues.addAll(originalScalePaths.stream()
                .map(path -> {
                    PathValue pv = PathUtils.convertToPathValue(path);
                    pv.setValueHolder(control.getValueHolder());
                    pv.setImageFrame(frame);
                    return pv;
                }).collect(Collectors.toList()));

        dotValues.clear();
        originalScaleDots.forEach((k, v) -> {
            DotValue dv = new DotValue();
            dv.setValueHolder(control.getValueHolder());
            dv.setImageFrame(frame);
            dv.setColor(getHex(k));
            dv.getDots().addAll(v);
            dv.updateDotsToString();
            dotValues.add(dv);
        });
        control.updateValueHolder(frame, pathValues, dotValues);
    }

    public void resetPane() {
        getChildren().clear();
        getChildren().add(border);
    }

    public boolean hasSubFrames() {
        return !frame.getFrames().isEmpty();
    }
}
