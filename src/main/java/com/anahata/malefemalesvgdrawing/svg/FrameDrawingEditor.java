/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.anahata.malefemalesvgdrawing.svg;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javafx.animation.Animation.Status;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.ImageCursor;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.util.Duration;

/**
 *
 * @author sai.dandem
 */
public class FrameDrawingEditor extends StackPane {
    enum EditorMode {
        DRAWING, ERASING
    };

    enum StrokeStyle {
        SOLID, DASHED, SPRAY
    };

    private List<Path> paths = new ArrayList<>();

    private List<Circle> dots = new ArrayList<>();

    private Map<Color, Set<String>> dotsMap = new HashMap<>();

    private Path path;

    private FramePane framePane;

    private boolean draw = false;

    private double scale;

    private double tx, ty;

    private StackPane borderContainer;

    private StackPane pathsContainer;

    private StackPane cursorContainer;

    private Node cursor;

    private double width;

    private double height;

    private Group pencil;

    private Group spray;

    private Pane eraser;

    private ObjectProperty<Color> pathColor = new SimpleObjectProperty<>(Color.RED);

    private DoubleProperty strokeWidth = new SimpleDoubleProperty(1);

    private ObjectProperty<StrokeStyle> strokeStyle = new SimpleObjectProperty<>(StrokeStyle.SOLID);

    private ObjectProperty<EditorMode> editorMode = new SimpleObjectProperty<>(EditorMode.DRAWING);

    private final Timeline sprayTimeline = new Timeline();

    private final DoubleProperty sprayValue = new SimpleDoubleProperty();

    private int frameLevel = 1;

    public FrameDrawingEditor() {
        init();
    }

    private void init() {
        sprayTimeline.setCycleCount(1);
        sprayTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(50), (e) -> applySpray(), new KeyValue(sprayValue,
                1)));
        managedProperty().bind(visibleProperty());
        setVisible(false);
        setAlignment(Pos.TOP_LEFT);
        setCursor(ImageCursor.NONE);
        setOnMouseMoved(e -> {
            double x = e.getX();
            double y = e.getY();
            if (x >= 0 && x <= width && y >= 0 && y <= height) {
                cursorContainer.setVisible(true);
                moveCursor(e);
            } else {
                cursorContainer.setVisible(false);
            }
        });
        setOnMousePressed(e -> drawOrErasePath(e));
        setOnMouseDragged(e -> drawOrErasePath(e));
        setOnMouseEntered(e -> {
            draw = true;
            cursorContainer.setVisible(true);
        });
        setOnMouseExited(e -> {
            draw = false;
            cursorContainer.setVisible(false);
        });

        pathsContainer = new StackPane();
        pathsContainer.setAlignment(Pos.TOP_LEFT);

        editorMode.addListener((p1, p2, mode) -> {
            if (mode == EditorMode.ERASING) {
                cursor = getEraser();
                draw = false;
            } else {
                if (strokeStyle.get() == StrokeStyle.SPRAY) {
                    cursor = getSpray();
                } else {
                    cursor = getPencil();
                }
                draw = true;
            }
            cursorContainer.getChildren().clear();
            cursorContainer.getChildren().add(cursor);
        });
        strokeStyle.addListener((p1, p2, style) -> {
            if (style == StrokeStyle.SPRAY) {
                cursor = getSpray();
            } else {
                cursor = getPencil();
            }
            cursorContainer.getChildren().clear();
            cursorContainer.getChildren().add(cursor);
        });
        cursor = getPencil();
        cursorContainer = new StackPane();
        cursorContainer.setVisible(false);
        cursorContainer.setAlignment(Pos.TOP_LEFT);
        cursorContainer.getChildren().add(cursor);

        borderContainer = new StackPane();
        borderContainer.getStyleClass().add("editing-frame");
        getChildren().addAll(borderContainer, pathsContainer, cursorContainer);
    }

    private Group getPencil() {
        if (pencil == null) {
            SVGPath pencilPath = new SVGPath();
            pencilPath.setRotate(90);
            pencilPath.setScaleX(.45);
            pencilPath.setScaleY(.45);
            pencilPath.setStrokeWidth(2);
            String content = "M52.618,2.631c-3.51-3.508-9.219-3.508-12.729,0L3.827,38.693C3.81,38.71,3.8,38.731,3.785,38.749  c-0.021,0.024-0.039,0.05-0.058,0.076c-0.053,0.074-0.094,0.153-0.125,0.239c-0.009,0.026-0.022,0.049-0.029,0.075  c-0.003,0.01-0.009,0.02-0.012,0.03l-3.535,14.85c-0.016,0.067-0.02,0.135-0.022,0.202C0.004,54.234,0,54.246,0,54.259  c0.001,0.114,0.026,0.225,0.065,0.332c0.009,0.025,0.019,0.047,0.03,0.071c0.049,0.107,0.11,0.21,0.196,0.296  c0.095,0.095,0.207,0.168,0.328,0.218c0.121,0.05,0.25,0.075,0.379,0.075c0.077,0,0.155-0.009,0.231-0.027l14.85-3.535  c0.027-0.006,0.051-0.021,0.077-0.03c0.034-0.011,0.066-0.024,0.099-0.039c0.072-0.033,0.139-0.074,0.201-0.123  c0.024-0.019,0.049-0.033,0.072-0.054c0.008-0.008,0.018-0.012,0.026-0.02l36.063-36.063C56.127,11.85,56.127,6.14,52.618,2.631z"
                    + " M51.204,4.045c2.488,2.489,2.7,6.397,0.65,9.137l-9.787-9.787C44.808,1.345,48.716,1.557,51.204,4.045z"
                    + " M46.254,18.895l-9.9-9.9  l1.414-1.414l9.9,9.9L46.254,18.895z "
                    + " M4.961,50.288c-0.391-0.391-1.023-0.391-1.414,0L2.79,51.045l2.554-10.728l4.422-0.491  l-0.569,5.122c-0.004,0.038,0.01,0.073,0.01,0.11c0,0.038-0.014,0.072-0.01,0.11c0.004,0.033,0.021,0.06,0.028,0.092  c0.012,0.058,0.029,0.111,0.05,0.165c0.026,0.065,0.057,0.124,0.095,0.181c0.031,0.046,0.062,0.087,0.1,0.127  c0.048,0.051,0.1,0.094,0.157,0.134c0.045,0.031,0.088,0.06,0.138,0.084C9.831,45.982,9.9,46,9.972,46.017  c0.038,0.009,0.069,0.03,0.108,0.035c0.036,0.004,0.072,0.006,0.109,0.006c0,0,0.001,0,0.001,0c0,0,0.001,0,0.001,0h0.001  c0,0,0.001,0,0.001,0c0.036,0,0.073-0.002,0.109-0.006l5.122-0.569l-0.491,4.422L4.204,52.459l0.757-0.757  C5.351,51.312,5.351,50.679,4.961,50.288z "
                    + " M17.511,44.809L39.889,22.43c0.391-0.391,0.391-1.023,0-1.414s-1.023-0.391-1.414,0  L16.097,43.395l-4.773,0.53l0.53-4.773l22.38-22.378c0.391-0.391,0.391-1.023,0-1.414s-1.023-0.391-1.414,0L10.44,37.738  l-3.183,0.354L34.94,10.409l9.9,9.9L17.157,47.992L17.511,44.809z"
                    + " M49.082,16.067l-9.9-9.9l1.415-1.415l9.9,9.9L49.082,16.067z";
            pencilPath.setContent(content);
            pathColor.addListener((p1, p2, color) -> pencilPath.setFill(color));
            pencilPath.setFill(pathColor.get());
            pencil = new Group();
            pencil.getChildren().add(pencilPath);
        }
        return pencil;
    }

    private Group getSpray() {
        if (spray == null) {
            Rectangle br = new Rectangle(14, 21);
            br.strokeProperty().bind(pathColor);
            br.setStrokeWidth(1.5);
            br.setArcWidth(6);
            br.setArcHeight(6);
            br.setFill(Color.WHITE);

            Rectangle sr = new Rectangle(7, 4);
            sr.strokeProperty().bind(pathColor);
            sr.setStrokeWidth(1.5);
            sr.setFill(Color.WHITE);
            sr.setTranslateX(3.5);
            sr.setTranslateY(-4);

            Line l = new Line(0, 0, 0, 15);
            l.strokeProperty().bind(pathColor);
            l.setTranslateX(7);
            l.setTranslateY(3);

            spray = new Group();
            spray.getChildren().addAll(br, sr, l);

            for (int i = 0; i < 3; i++) {
                for (int j = -i; j <= i; j++) {
                    Circle dot = new Circle(.75);
                    dot.fillProperty().bind(pathColor);
                    dot.setTranslateX(-i * 3);
                    dot.setTranslateY((j * 3) - 4);
                    spray.getChildren().add(dot);
                }
            }
        }
        return spray;
    }

    private Pane getEraser() {
        if (eraser == null) {
            eraser = new Pane();
            eraser.setMinSize(15, 15);
            eraser.setMaxSize(15, 15);
            eraser.getStyleClass().add("eraser-cursor");
        }
        return eraser;
    }

    private void moveCursor(MouseEvent e) {
        cursor.setTranslateX(e.getX());
        cursor.setTranslateY(e.getY());
    }

    private void drawOrErasePath(MouseEvent e) {
        if (draw) {
            double x = e.getX();
            double y = e.getY();
            if (x >= 0 && x <= width && y >= 0 && y <= height) {
                cursorContainer.setVisible(true);
                moveCursor(e);
                if (editorMode.get() == EditorMode.DRAWING) {
                    if (e.getEventType() == MouseEvent.MOUSE_PRESSED) {
                        if (strokeStyle.get() == StrokeStyle.SPRAY) {
                            tx = x;
                            ty = y;
                            addSpray();
                        } else {
                            createPath();
                            tx = x;
                            ty = y;
                            path.getElements().add(new MoveTo(x, y));
                            path.setTranslateX(tx);
                            path.setTranslateY(ty);
                        }
                    } else if (e.getEventType() == MouseEvent.MOUSE_DRAGGED) {
                        if (strokeStyle.get() == StrokeStyle.SPRAY) {
                            tx = x;
                            ty = y;
                            addSpray();
                        } else {
                            path.getElements().add(new LineTo(x, y));
                            if (x < tx) {
                                tx = x;
                                path.setTranslateX(tx);
                            }
                            if (y < ty) {
                                ty = y;
                                path.setTranslateY(ty);
                            }
                        }
                    }
                } else if (editorMode.get() == EditorMode.ERASING) {
                    doErase();
                }
            } else {
                cursorContainer.setVisible(false);
            }
        }
    }

    private void addSpray() {
        if (sprayTimeline.getStatus() == Status.STOPPED) {
            sprayTimeline.play();
        }
    }

    private void applySpray() {
        double radius = (10 + ((strokeWidth.get() - 1) * 5)) * frameLevel;
        final Set<String> dotSet;
        if (dotsMap.get(pathColor.get()) == null) {
            dotSet = new HashSet<>();
            dotsMap.put(pathColor.get(), dotSet);
        } else {
            dotSet = dotsMap.get(pathColor.get());
        }

        IntStream.range(0, 15).forEach(i -> {
            double random1 = Math.random();
            double random2 = Math.random();
            if (random2 < random1) {
                double c = random2;
                random2 = random1;
                random1 = c;
            }
            double x = (tx + Math.round(random2 * radius * Math.cos(Math.PI * random1 / random2)));
            double y = (ty + Math.round(random2 * radius * Math.sin(Math.PI * random1 / random2)));
            String sval = String.format("%.1f", x) + " " + String.format("%.1f", y);
            if (!dotSet.contains(sval)) {
                dotSet.add(sval);
                Circle dot = PathUtils.createDot(x, y, 1, pathColor.get(), this.scale);
                pathsContainer.getChildren().add(dot);
                dots.add(dot);
            }
        });
    }

    private void doErase() {
        Path p = null;
        for (int i = (paths.size() - 1); i >= 0; i--) {
            if (eraser.getBoundsInParent().intersects(paths.get(i).getBoundsInParent())) {
                p = paths.get(i);
                break;
            }
        }
        if (p != null) {
            paths.remove(p);
            pathsContainer.getChildren().remove(p);
        }

        List<Circle> dotsToErase = new ArrayList<>();
        List<String> dStrToErase = new ArrayList<>();
        for (int i = (dots.size() - 1); i >= 0; i--) {
            if (eraser.getBoundsInParent().intersects(dots.get(i).getBoundsInParent())) {
                Circle d = dots.get(i);
                dotsToErase.add(d);
                dStrToErase.add(d.getUserData() + "");
            }
        }
        if (!dotsToErase.isEmpty()) {
            dots.removeAll(dotsToErase);
            dotsMap.forEach((key, val) -> val.removeAll(dStrToErase));
            pathsContainer.getChildren().removeAll(dotsToErase);
        }
    }

    private void createPath() {
        path = new Path();
        path.setStrokeWidth(strokeWidth.get() * this.scale);
        path.setStroke(pathColor.get());
        if (strokeStyle.get() == StrokeStyle.DASHED) {
            Stream.of(PathUtils.STROKE_DASHED_ARR).forEach(d -> {
                path.getStrokeDashArray().add(d * this.scale);
            });
        }
        paths.add(path);
        pathsContainer.getChildren().addAll(path);
    }

    public void show(FramePane framePane, double scale) {
        this.framePane = framePane;
        this.scale = scale;
        updateSize();
        List<Path> scaleUpPaths = getScaleUpPaths(framePane.getOriginalScalePaths());
        Map<Color, Set<String>> scaleUpDots = getScaleUpDots(framePane.getOriginalScaleDots());
        framePane.resetPane();
        clear();

        setVisible(true);

        paths.addAll(scaleUpPaths);
        pathsContainer.getChildren().addAll(paths);

        dotsMap.putAll(scaleUpDots);
        scaleUpDots.forEach((color, v) -> {
            v.stream().forEach(coord -> {
                Circle scaleUpDot = PathUtils.createDot(Double.parseDouble(coord.split(" ")[0]), Double.parseDouble(
                        coord.split(" ")[1]), 1, color, getUpScale());
                pathsContainer.getChildren().add(scaleUpDot);
                dots.add(scaleUpDot);
            });
        });

    }

    private void updateSize() {
        this.height = framePane.getFrame().getHeightByScale(scale);
        this.width = framePane.getFrame().getWidthByScale(scale);
        setSizes(this);
        setSizes(pathsContainer);
        setSizes(cursorContainer);
    }

    private void setSizes(Pane p) {
        setPrefSize(width, height);
        setMinSize(width, height);
        setMaxSize(width, height);
    }

    public void setFrameLevel(int frameLevel) {
        this.frameLevel = frameLevel;
    }

    public void updatePathsToFramePane() {
        if (framePane != null) {
            framePane.updateControlValueHolder(getOriginalScalePaths(), getOriginalScaleDots());
        }
    }

    public void close() {
        setVisible(false);
        clear();
    }

    public boolean hasPaths() {
        return !paths.isEmpty();
    }

    private double getUpScale() {
        return Math.min(this.width / framePane.getFrame().getWidth(), this.height / framePane.getFrame().getHeight());
    }

    private List<Path> getScaleUpPaths(List<Path> pathList) {
        double scl = getUpScale();
        return pathList.stream().map(p -> PathUtils.getScaledPath(p, scl)).collect(Collectors.toList());
    }

    private Map<Color, Set<String>> getScaleUpDots(Map<Color, Set<String>> dotMap) {
        double scl = getUpScale();
        final Map<Color, Set<String>> scaledUpMap = new HashMap<>();
        if (!dotMap.isEmpty()) {
            dotMap.forEach((k, v) -> {
                final Set<String> scaledSet = v.stream().map(value -> {
                    String a[] = value.split(" ");
                    return String.format("%.1f", Double.parseDouble(a[0]) * scl) + " " + String.format("%.1f",
                            Double.parseDouble(a[1]) * scl);
                }).collect(Collectors.toSet());
                scaledUpMap.put(k, scaledSet);
            });
        }
        return scaledUpMap;
    }

    private List<Path> getOriginalScalePaths() {
        double scl = Math.min(framePane.getFrame().getWidth() / this.width,
                framePane.getFrame().getHeight() / this.height);
        return paths.stream().map(p -> PathUtils.getScaledPath(p, scl)).collect(Collectors.toList());
    }

    private Map<Color, Set<String>> getOriginalScaleDots() {
        double scl = Math.min(framePane.getFrame().getWidth() / this.width,
                framePane.getFrame().getHeight() / this.height);
        final Map<Color, Set<String>> scaledMap = new HashMap<>();
        if (!dotsMap.isEmpty()) {
            dotsMap.forEach((k, v) -> {
                final Set<String> scaledSet = v.stream().map(value -> {
                    String a[] = value.split(" ");
                    return String.format("%.1f", (Double.parseDouble(a[0]) * scl)) + " " + String.format("%.1f",
                            (Double.parseDouble(a[1]) * scl));
                }).collect(Collectors.toSet());
                scaledMap.put(k, scaledSet);
            });
        }
        return scaledMap;
    }

    private void clear() {
        path = null;
        pathsContainer.getChildren().clear();
        paths.clear();
        dots.clear();
        dotsMap.clear();
    }

    public void setPathColor(Color pathColor) {
        setEditorMode(EditorMode.DRAWING);
        this.pathColor.set(pathColor);
    }

    void setEditorMode(EditorMode editorMode) {
        this.editorMode.set(editorMode);
    }

    public void setStrokeStyle(StrokeStyle strokeStyle) {
        setEditorMode(EditorMode.DRAWING);
        this.strokeStyle.set(strokeStyle);
    }

    public void setStrokeWidth(double strokeWidth) {
        this.strokeWidth.set(strokeWidth);
    }

}
