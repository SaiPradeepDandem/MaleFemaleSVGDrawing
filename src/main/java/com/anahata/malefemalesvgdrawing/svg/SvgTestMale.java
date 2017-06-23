/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.anahata.malefemalesvgdrawing.svg;

import com.anahata.malefemalesvgdrawing.svg.FrameDrawingEditor.EditorMode;
import com.anahata.malefemalesvgdrawing.svg.FrameDrawingEditor.StrokeStyle;
import com.anahata.malefemalesvgdrawing.svg.model.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 *
 * @author sai.dandem
 */
public class SvgTestMale extends Application implements DrawingInputControl {
    enum FrameMode {
        SELECTABLE, EDITABLE
    };
    
    private double size;
    
    private StackPane content;
    
    private CustomSVGGroup svg;
    
    private StackPane bodyGrpPane;
    
    private StackPane wrapper;
    
    private StackPane wrapperClipper;
    
    private StackPane selectables;
    
    private AnchorPane toolLayer;
    
    private StackPane actionButtonsPane;
    
    private FrameDrawingEditor frameEditor;
    
    private DrawingInput element;
    
    private DrawingValueHolder valueHolder = new DrawingValueHolder();
    
    private Map<Frame, List<FramePane>> framePanesMap = new HashMap<>();
    
    private FramePane currentWorkingFramePane;
    
    private ObjectProperty<FrameMode> frameMode = new SimpleObjectProperty<>();
    
    public static void main(String[] args) {
        launch(args);
    }
    
    public DrawingInput getElement() {
        if (element == null) {
            element = DataGenerator.getMaleElement();
            valueHolder.setImageType(SVGImageType.MALE);
        }
        return element;
    }
    
    @Override
    public void start(Stage primaryStage) {
        size = getElement().getImage(valueHolder.getImageType()).getRenderSize();
        
        content = new StackPane();
        setPrefSize(content, size + 100);
        content.getStyleClass().add("drawing-content");
        
        wrapper = new StackPane();
        wrapper.getStyleClass().add("drawing-wrapper");
        setPrefSize(wrapper, size);
        
        svg = new CustomSVGGroup(getElement().getImage(valueHolder.getImageType()));
        svg.scaleToSize(size);
        
        bodyGrpPane = new StackPane();
        bodyGrpPane.getChildren().add(svg);
        //bodyGrpPane.setStyle("-fx-background-color:red;-fx-opacity:.4;");

        wrapperClipper = new StackPane();
        setPrefSize(wrapperClipper, size);
        wrapperClipper.getChildren().add(bodyGrpPane);
        
        selectables = new StackPane();
        //selectables.setStyle("-fx-background-color:yellow;-fx-opacity:.4;");
        selectables.setAlignment(Pos.TOP_LEFT);
        setPrefSize(selectables, size);
        
        frameEditor = new FrameDrawingEditor();
        wrapper.getChildren().addAll(wrapperClipper, selectables, frameEditor);
        
        toolLayer = buildToolLayer();
        content.getChildren().addAll(toolLayer, wrapper);

        // ****************************************************** 
        StackPane root = new StackPane();
        Scene scene = new Scene(root, size + 200, size + 200);
        primaryStage.setScene(scene);
        scene.getStylesheets().add("/com/anahata/malefemalesvgdrawing/svg/svgtest.css");
        primaryStage.show();
        
        VBox vb = new VBox();
        vb.setSpacing(10);
        Button save = new Button("Save");
        save.setOnAction(e -> {
            try {
                SerializationUtil.serialize(getValueHolder(), "DrawingValue");
                SerializationUtil.serialize(getElement(), "DrawingInput");
            } catch (IOException ex) {
                System.out.println(ex);
                ex.printStackTrace();
            }
        });
        Button load = new Button("Load");
        load.setOnAction(e -> {
            try {
                valueHolder = (DrawingValueHolder)SerializationUtil.deserialize("DrawingValue");
                element = (DrawingInput)SerializationUtil.deserialize("DrawingInput");
                renderFrames(getElement().getImage(valueHolder.getImageType()));
            } catch (IOException | ClassNotFoundException ex) {
                System.out.println("Exception...");
            }
        });
        Button web = new Button("Show WebView");
        web.setOnAction(e -> WorkShopUtils.showWebView(getElement(), getValueHolder()));
        
        HBox hb = new HBox();
        hb.setSpacing(15);
        hb.getChildren().addAll(save, load, web);
        
        vb.getChildren().addAll(content, hb);
        
        root.getChildren().addAll(vb);
        renderFrames(getElement().getImage(valueHolder.getImageType()));

        // showContentSizes(DataGenerator.getMaleElement().getImages().get(0), "Male");
        // showContentSizes(DataGenerator.getFemaleElement().getImages().get(0), "Female");
        // showContentSizes(DataGenerator.getChildElement().getImages().get(0), "Child");
        //ScenicView.show(s);
    }
    
    private void showContentSizes(MasterImage mi, String type) {
        Stage stg = new Stage();
        CustomSVGGroup g = new CustomSVGGroup((DataGenerator.getMaleElement().getImages().get(0)));
        Scene s = new Scene(g, 600, 600);
        stg.setScene(s);
        stg.show();
        Platform.runLater(() -> {
            System.out.println(type + " content width " + g.getBoundsInLocal().getWidth());
            System.out.println(type + " content height " + g.getBoundsInLocal().getHeight());
            stg.close();
        });
    }
    
    private void setPrefSize(Pane pane, double size) {
        pane.setPrefSize(size, size);
        pane.setMinSize(size, size);
        pane.setMaxSize(size, size);
    }
    
    private AnchorPane buildToolLayer() {
        final AnchorPane layer = new AnchorPane();
        layer.setVisible(false);
        layer.managedProperty().bind(layer.visibleProperty());
        
        final HBox editorToolBar = createEditorToolBar();
        AnchorPane.setTopAnchor(editorToolBar, 8.0);
        AnchorPane.setLeftAnchor(editorToolBar, 0.0);
        editorToolBar.translateXProperty()
                .bind(layer.widthProperty().subtract(editorToolBar.widthProperty()).divide(2));
        
        final StackPane doneBtn = buildButton("Done", e -> unZoom(), "save-button");
        
        final StackPane zoomBtn = buildButton("Zoom In", e -> frameMode.set(FrameMode.SELECTABLE), "search-button");
        zoomBtn.visibleProperty().bind(editorToolBar.visibleProperty());
        
        final StackPane drawBtn = buildButton("Draw", e -> frameMode.set(FrameMode.EDITABLE), "edit-button");
        drawBtn.visibleProperty().bind(editorToolBar.visibleProperty().not());
        
        actionButtonsPane = new StackPane();
        actionButtonsPane.setAlignment(Pos.CENTER_RIGHT);
        actionButtonsPane.getChildren().addAll(zoomBtn, drawBtn);
        
        final HBox btnPane = new HBox();
        btnPane.setSpacing(15);
        btnPane.setAlignment(Pos.CENTER_RIGHT);
        btnPane.getChildren().addAll(actionButtonsPane, doneBtn);
        AnchorPane.setBottomAnchor(btnPane, 10.0);
        AnchorPane.setRightAnchor(btnPane, 10.0);
        
        frameMode.addListener((p1, p2, mode) -> {
            if (mode != null) {
                if (mode == FrameMode.EDITABLE) {
                    editorToolBar.setVisible(true);
                    toggleSelectables(false);
                    frameEditor.toFront();
                    frameEditor.setFrameLevel(this.currentWorkingFramePane.getFrame().getImage() == null ? 2 : 1);
                    
                } else if (mode == FrameMode.SELECTABLE) {
                    editorToolBar.setVisible(false);
                    frameEditor.updatePathsToFramePane();
                    toggleSelectables(true);
                    selectables.toFront();
                }
                showCurrentWorkingFrameEditor();
            } else {
                toggleSelectables(true);
                selectables.toFront();
            }
        });
        
        final Label drawLbl = new Label("* Please draw within the dotted lines.");
        drawLbl.setPadding(new Insets(0, 0, 0, 5));
        drawLbl.visibleProperty().bind(editorToolBar.visibleProperty());
        drawLbl.managedProperty().bind(drawLbl.visibleProperty());
        drawLbl.getStyleClass().add("italicLbl");
        
        final Label zoomLbl = new Label("* Please click the frames to zoom further.");
        zoomLbl.setPadding(new Insets(0, 0, 0, 5));
        zoomLbl.visibleProperty().bind(editorToolBar.visibleProperty().not());
        zoomLbl.managedProperty().bind(zoomLbl.visibleProperty());
        zoomLbl.getStyleClass().add("italicLbl");
        
        final StackPane lblPane = new StackPane();
        lblPane.setAlignment(Pos.CENTER_LEFT);
        lblPane.getChildren().addAll(drawLbl, zoomLbl);
        AnchorPane.setBottomAnchor(lblPane, 10.0);
        AnchorPane.setLeftAnchor(lblPane, 10.0);
        
        layer.getChildren().addAll(editorToolBar, btnPane, lblPane);
        return layer;
    }
    
    private StackPane buildButton(String txt, EventHandler<MouseEvent> eventHandler, String styleClass) {
        final StackPane btn = new StackPane();
        Label lbl = new Label(txt);
        lbl.getStyleClass().add(styleClass);
        //lbl.setStyle("-fx-text-fill:#FFFFFF;");
        btn.getChildren().add(lbl);
        btn.getStyleClass().add("button");
        btn.setOnMouseClicked(eventHandler);
        btn.setMaxHeight(32);
        btn.setMinHeight(32);
        return btn;
    }
    
    private void toggleSelectables(boolean show) {
        selectables.setVisible(true);
        selectables.getChildren().stream().map(n -> (FramePane)n).forEach(fp -> {
            fp.getShowBorder().set(show);
            fp.setDisable(!show);
        });
    }
    
    private void renderFrames(Frame frameObj) {
        if (framePanesMap.get(frameObj) == null) {
            List<FramePane> list = new ArrayList<>();
            frameObj.getFrames().forEach(frame -> {
                FramePane p = new FramePane(frame, this);
                list.add(p);
            });
            framePanesMap.put(frameObj, list);
        }
        selectables.getChildren().clear();
        List<FramePane> toFrontPanes = new ArrayList<>();
        framePanesMap.get(frameObj).stream().forEach(framePane -> {
            selectables.getChildren().add(framePane);
            framePane.layout(size);
            if (framePane.getFrame().isToFront()) {
                toFrontPanes.add(framePane);
            }
        });
        toFrontPanes.forEach(FramePane::toFront);
    }
    
    @Override
    public void zoomFrame(FramePane framePane) {
        selectables.getChildren().clear();
        frameEditor.setVisible(false);
        this.currentWorkingFramePane = framePane;
        final ImageFrame frame = currentWorkingFramePane.getFrame();
        double scale = svg.getSvgScaleByFrameAndSize(frame, size);
        
        wrapperClipper.setClip(null);
        Rectangle clip = new Rectangle(frame.getWidthByScale(scale), frame.getHeightByScale(scale));
        wrapperClipper.setClip(clip);

        // Moving scaled svg to wrapper corner.
        double tx1 = (svg.getWidthByScale(scale) / 2) - (size / 2);
        double ty1 = (svg.getHeightByScale(scale) / 2) - (size / 2);
        // Getting the center of the scaled frame.
        double tx2 = frame.getRenderCenterXByScale(scale);
        double ty2 = frame.getRenderCenterYByScale(scale);
        
        double finalTx = tx1 - tx2 + (size / 2);
        double finalTy = ty1 - ty2 + (size / 2);
        
        final KeyValue kv1 = new KeyValue(svg.scaleXProperty(), scale);
        final KeyValue kv2 = new KeyValue(svg.scaleYProperty(), scale);
        final KeyValue kv3 = new KeyValue(clip.translateXProperty(), ((size - clip.getWidth()) / 2));
        final KeyValue kv4 = new KeyValue(clip.translateYProperty(), ((size - clip.getHeight()) / 2));
        final KeyValue kv5 = new KeyValue(bodyGrpPane.translateXProperty(), finalTx);
        final KeyValue kv6 = new KeyValue(bodyGrpPane.translateYProperty(), finalTy);
        
        final EventHandler<ActionEvent> onFinished = (t) -> {
            toolLayer.setVisible(true);
            renderFrames(frame);
            actionButtonsPane.setVisible(currentWorkingFramePane.hasSubFrames());
            showCurrentWorkingFrameEditor();
            frameMode.set(currentWorkingFramePane.hasSubFrames() ? FrameMode.SELECTABLE : FrameMode.EDITABLE);
        };
        final Timeline timeline = new Timeline();
        timeline.setCycleCount(1);
        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(200), onFinished, kv1, kv2, kv3, kv4, kv5, kv6));
        timeline.play();
    }
    
    private void showCurrentWorkingFrameEditor() {
        double scale = svg.getSvgScaleByFrameAndSize(currentWorkingFramePane.getFrame(), size);
        frameEditor.show(currentWorkingFramePane, scale);
    }
    
    private void unZoom() {
        frameEditor.updatePathsToFramePane();
        frameEditor.setVisible(false);
        selectables.setVisible(false);
        final Frame parentFrame = currentWorkingFramePane.getFrame().getParent();
        if (parentFrame instanceof MasterImage) {
            toolLayer.setVisible(false);
            frameEditor.close();
            
            double initialScale = svg.getScaleBySize(size);
            final KeyValue kv1 = new KeyValue(svg.scaleXProperty(), initialScale);
            final KeyValue kv2 = new KeyValue(svg.scaleYProperty(), initialScale);
            final KeyValue kv3 = new KeyValue(bodyGrpPane.translateXProperty(), 0);
            final KeyValue kv4 = new KeyValue(bodyGrpPane.translateYProperty(), 0);
            final EventHandler<ActionEvent> onFinished = (t) -> {
                wrapperClipper.setClip(null);
                frameMode.set(null);
                frameMode.set((parentFrame instanceof MasterImage) ? null : FrameMode.SELECTABLE);
                renderFrames(parentFrame);
            };
            final Timeline timeline = new Timeline();
            timeline.setCycleCount(1);
            timeline.getKeyFrames().add(new KeyFrame(Duration.millis(200), onFinished, kv1, kv2, kv3, kv4));
            timeline.play();
            
        } else if (parentFrame instanceof ImageFrame) {
            frameMode.set(null);
            FramePane fp = getFramePaneByImageFrame((ImageFrame)parentFrame);
            zoomFrame(fp);
        }
    }
    
    private HBox createEditorToolBar() {
        HBox editorToolBar = new HBox();
        editorToolBar.managedProperty().bind(editorToolBar.visibleProperty());
        editorToolBar.setPadding(new Insets(5, 10, 5, 10));
        editorToolBar.setPrefHeight(30);
        editorToolBar.getStyleClass().add("editor-tool-bar");
        editorToolBar.setAlignment(Pos.CENTER);
        editorToolBar.setSpacing(10);
        
        HBox colorPane = new HBox();
        colorPane.setAlignment(Pos.CENTER);
        colorPane.setSpacing(10);
        colorPane.getChildren().addAll(getColorBtn(Color.RED), getColorBtn(Color.BLACK), getColorBtn(Color.PURPLE));
        
        Pane eraserPane = new Pane();
        Tooltip.install(eraserPane, new Tooltip("Eraser"));
        eraserPane.setMaxSize(20, 20);
        eraserPane.setMinSize(20, 20);
        eraserPane.setOnMouseClicked(e -> frameEditor.setEditorMode(EditorMode.ERASING));
        eraserPane.getStyleClass().add("eraser-btn");
        
        editorToolBar.getChildren().addAll(colorPane, new Separator(Orientation.VERTICAL), eraserPane, new Separator(
                Orientation.VERTICAL), getStrokeStylePane(), new Separator(Orientation.VERTICAL), getStrokeWidthPane());
        
        return editorToolBar;
    }
    
    private HBox getStrokeWidthPane() {
        HBox strokeWidthPane = new HBox();
        strokeWidthPane.setAlignment(Pos.CENTER);
        strokeWidthPane.setMaxHeight(20);
        strokeWidthPane.setMinHeight(20);
        ToggleGroup tg = new ToggleGroup();
        
        tg.selectedToggleProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue == null && oldValue != null) {
                tg.selectToggle(oldValue);
            }
        });
        ToggleButton left = new ToggleButton();
        left.setPrefSize(50, 20);
        left.setToggleGroup(tg);
        left.setGraphic(getLine(2, false));
        left.setOnAction(e -> frameEditor.setStrokeWidth(1d));
        left.setSelected(true);
        left.getStyleClass().add("pill-left");
        
        ToggleButton cen = new ToggleButton();
        cen.setPrefSize(50, 20);
        cen.setToggleGroup(tg);
        cen.setGraphic(getLine(4, false));
        cen.setOnAction(e -> frameEditor.setStrokeWidth(2d));
        cen.getStyleClass().add("pill-center");
        
        ToggleButton rght = new ToggleButton();
        rght.setPrefSize(50, 20);
        rght.setToggleGroup(tg);
        rght.setGraphic(getLine(6, false));
        rght.setOnAction(e -> frameEditor.setStrokeWidth(3d));
        rght.getStyleClass().add("pill-right");
        
        strokeWidthPane.getChildren().addAll(left, cen, rght);
        return strokeWidthPane;
    }
    
    private HBox getStrokeStylePane() {
        HBox strokeStylePane = new HBox();
        strokeStylePane.setAlignment(Pos.CENTER);
        strokeStylePane.setMaxHeight(20);
        strokeStylePane.setMinHeight(20);
        ToggleGroup tg = new ToggleGroup();
        
        tg.selectedToggleProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue == null && oldValue != null) {
                tg.selectToggle(oldValue);
            }
        });
        ToggleButton left = new ToggleButton();
        left.setPrefSize(50, 20);
        left.setToggleGroup(tg);
        left.setGraphic(getLine(2, false));
        left.setOnAction(e -> frameEditor.setStrokeStyle(StrokeStyle.SOLID));
        left.setSelected(true);
        left.getStyleClass().add("pill-left");
        
        ToggleButton center = new ToggleButton();
        center.setPrefSize(50, 20);
        center.setToggleGroup(tg);
        center.setGraphic(getLine(2, true));
        center.setOnAction(e -> frameEditor.setStrokeStyle(StrokeStyle.DASHED));
        center.getStyleClass().add("pill-center");
        
        Pane sprayIcon = new Pane();
        sprayIcon.setMaxSize(18,18);
        sprayIcon.setMinSize(18,18);
        sprayIcon.getStyleClass().add("spray-btn");
        
        ToggleButton rght = new ToggleButton();
        rght.setPrefSize(50, 20);
        rght.setToggleGroup(tg);
        rght.setGraphic(sprayIcon);
        rght.setOnAction(e -> frameEditor.setStrokeStyle(StrokeStyle.SPRAY));
        rght.getStyleClass().add("pill-right");
        
        strokeStylePane.getChildren().addAll(left, center, rght);
        return strokeStylePane;
    }
    
    private Node getLine(double width, boolean dotted) {
        Line p = new Line(0, 0, 35, 0);
        p.setStroke(Color.valueOf("#333333"));
        p.setStrokeWidth(width);
        if (dotted) {
            p.getStrokeDashArray().addAll(5d, 5d);
        }
        return p;
    }
    
    private Pane getColorBtn(Color c) {
        Pane btn = new Pane();
        btn.setMaxSize(20, 20);
        btn.setMinSize(20, 20);
        btn.setStyle("-fx-background-insets:0,1,2;-fx-background-color:#A0A0A0,#FFFFFF," + PathUtils.getHex(c));
        btn.setOnMouseClicked(e -> frameEditor.setPathColor(c));
        return btn;
    }
    
    private FramePane getFramePaneByImageFrame(ImageFrame frame) {
        List<FramePane> list = framePanesMap.get(frame.getParent());
        if (list != null && !list.isEmpty()) {
            return list.stream().filter(fp -> fp.getFrame().equals(frame)).findFirst().orElse(null);
        }
        return null;
    }
    
    @Override
    public DrawingValueHolder getValueHolder() {
        return valueHolder;
    }
    
    @Override
    public void updateValueHolder(ImageFrame frame, List<PathValue> pathValues, List<DotValue> dotValues) {
        final List<SvgValue> otherFrameValues = getValueHolder().getValues().stream()
                .filter(pv -> !pv.getImageFrame().equals(frame))
                .collect(Collectors.toList());
        otherFrameValues.addAll(pathValues);
        otherFrameValues.addAll(dotValues);
        
        getValueHolder().getValues().clear();
        getValueHolder().getValues().addAll(otherFrameValues);
    }
    
    @Override
    public CustomSVGGroup getSvg() {
        return svg;
    }
    
}
