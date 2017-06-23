package com.anahata.malefemalesvgdrawing.svg;

import com.anahata.malefemalesvgdrawing.svg.model.PathValue;
import java.text.DecimalFormat;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;

/**
 * Utility class for the Path implementation.
 *
 * @author sai.dandem
 */
public class PathUtils {

    private static final DecimalFormat df = new DecimalFormat("#.0");

    public static Double[] STROKE_DASHED_ARR = new Double[]{5D, 5D};

    /**
     * Returns a new path by scaling the given path with the provided scale.
     *
     * @param path  Path to be scale.
     * @param scale Scale factor.
     * @return Scaled Path.
     */
    public static Path getScaledPath(Path path, double scale) {
        Path scaledPath = new Path();
        scaledPath.setStrokeWidth(path.getStrokeWidth() * scale);
        scaledPath.setStroke(path.getStroke());
        scaledPath.setTranslateX(path.getTranslateX() * scale);
        scaledPath.setTranslateY(path.getTranslateY() * scale);
        if (!path.getStrokeDashArray().isEmpty()) {
            Stream.of(STROKE_DASHED_ARR).forEach(d -> {
                scaledPath.getStrokeDashArray().add(d * scale);
            });
        }
        path.getElements().forEach(ele -> {
            if (ele instanceof MoveTo) {
                scaledPath.getElements().add(new MoveTo(((MoveTo)ele).getX() * scale, ((MoveTo)ele).getY() * scale));
            } else {
                scaledPath.getElements().add(new LineTo(((LineTo)ele).getX() * scale, ((LineTo)ele).getY() * scale));
            }
        });
        return scaledPath;
    }

    /**
     * Converts the given Path node to the PathValue object.
     *
     * @param path Path node.
     * @return PathValue.
     */
    public static PathValue convertToPathValue(Path path) {
        String d = path.getElements().stream().map(element -> {
            if (element instanceof MoveTo) {
                return "M " + df.format(((MoveTo)element).getX()) + " " + df.format(((MoveTo)element).getY());
            } else {
                return "L " + df.format(((LineTo)element).getX()) + " " + df.format(((LineTo)element).getY());
            }
        }).collect(Collectors.joining(","));
        Color c = (Color)path.getStroke();
        PathValue pathValue = new PathValue();
        pathValue.setColor(getHex(c));
        pathValue.setStrokeWidth(path.getStrokeWidth());
        pathValue.setDValue(d);
        pathValue.setTranslateX(Double.parseDouble(df.format(path.getTranslateX())));
        pathValue.setTranslateY(Double.parseDouble(df.format(path.getTranslateY())));
        pathValue.setDotted(!path.getStrokeDashArray().isEmpty());
        return pathValue;
    }

    /**
     * Converts the given string to Path node.
     *
     * @param pathValue PathValue object.
     * @return Path node.
     */
    public static Path convertToPath(PathValue pathValue) {
        Path path = new Path();
        path.setStrokeWidth(pathValue.getStrokeWidth());
        path.setStroke(Paint.valueOf(pathValue.getColor()));
        path.setTranslateX(pathValue.getTranslateX());
        path.setTranslateY(pathValue.getTranslateY());
        if (pathValue.getDValue() != null && !pathValue.getDValue().isEmpty()) {
            Stream.of(pathValue.getDValue().split(",")).forEach(seg -> {
                String[] arr = seg.split(" ");
                if (arr[0].equals("M")) {
                    path.getElements().add(new MoveTo(Double.parseDouble(arr[1]), Double.parseDouble(arr[2])));
                } else {
                    path.getElements().add(new LineTo(Double.parseDouble(arr[1]), Double.parseDouble(arr[2])));
                }
            });
        }
        if (pathValue.isDotted()) {
            path.getStrokeDashArray().addAll(STROKE_DASHED_ARR);
        }
        return path;
    }

    public static Circle createDot(double x, double y, double scale, Color color,double rScale) {
        Circle c = new Circle(.5 * rScale);
        c.setFill(color);
        c.setTranslateX(x * scale);
        c.setTranslateY(y * scale);
        c.setUserData(String.format("%.1f", x) + " " + String.format("%.1f", y));
        return c;
    }

    /**
     * Returns the hex value of the given Color instance.
     *
     * @param c Color instance.
     * @return Hex value of color.
     */
    public static String getHex(Color c) {
        return String.format("#%02X%02X%02X",
                (int)(c.getRed() * 255),
                (int)(c.getGreen() * 255),
                (int)(c.getBlue() * 255));
    }

}
