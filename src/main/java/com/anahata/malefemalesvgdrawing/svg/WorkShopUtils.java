/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.anahata.malefemalesvgdrawing.svg;

import com.anahata.malefemalesvgdrawing.svg.model.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.Scene;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author sai.dandem
 */
public class WorkShopUtils {

    public static void showWebView(DrawingInput input, DrawingValueHolder valueHolder) {
        WebView webView = new WebView();
        try {
            String html;
            html = "<html><body><h1>Drawing rendering in HTML</h1><br>" + getHtmlContent(input,
                    valueHolder) + "</body></html>";

            try {
                SerializationUtil.serialize(html, "WebHtml");
            } catch (IOException ex) {
                System.out.println(ex);
                ex.printStackTrace();
            }
            webView.getEngine().loadContent(html);
        } catch (IOException ex) {
            Logger.getLogger(WorkShopUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        Stage stg = new Stage();
        stg.setScene(new Scene(webView, 800, 800));
        stg.show();
    }

    private static String getHtmlContent(DrawingInput input, DrawingValueHolder valueHolder) throws IOException {
        final MasterImage masterImage = input.getImage(valueHolder.getImageType());
        final String svgContent = buildSvgContent(input, valueHolder);

        StringBuilder content = new StringBuilder();
        // Adding Master Image
        content.append(buildSvgViewBox(masterImage.getRenderSize(), 0, 0, masterImage.getWidth(),
                masterImage.getHeight(), svgContent));
        content.append("<br>");

        // Adding each image frame in heirarchy which has paths.
        masterImage.getFrames().forEach((frame) -> {
            List<SvgValue> values = valueHolder.getValuesByFrame(frame);
            if (!values.isEmpty()) {
                content.append(buildSvgViewBox(masterImage.getRenderSize(), frame.getStartX(), frame.getStartY(),
                        frame.getWidth(), frame.getHeight(), svgContent));
                content.append("<br>");
            }
            frame.getFrames().forEach(f -> {
                List<SvgValue> pvs = valueHolder.getValuesByFrame(f);
                if (!pvs.isEmpty()) {
                    content.append(buildSvgViewBox(masterImage.getRenderSize(), f.getStartX(), f.getStartY(),
                            f.getWidth(), f.getHeight(), svgContent));
                    content.append("<br>");
                }
            });
        });
        System.out.println("-----------------------------------------------------------------------");
        System.out.println(content.toString());
        return content.toString();
    }

    private static String buildSvgContent(DrawingInput input, DrawingValueHolder valueHolder) throws IOException {
        final MasterImage masterImage = input.getImage(valueHolder.getImageType());

        StringWriter writer = new StringWriter();
        InputStream is = new ByteArrayInputStream(masterImage.getBytes());
        IOUtils.copy(is, writer, "UTF-8");
        String mainSVG = writer.toString();
        mainSVG = mainSVG.substring(0, mainSVG.indexOf("st=\"\"") + 6) + " {} " + mainSVG.substring(mainSVG.indexOf(
                "nd=\"\""));

        StringBuilder paths = new StringBuilder();
        // Adding drawn paths
        masterImage.getFrames().forEach((frame) -> {
            List<SvgValue> values = valueHolder.getValuesByFrame(frame);
            if (!values.isEmpty()) {
                paths.append(buildPathGroup(frame, values));
            }
            frame.getFrames().forEach(f -> {
                List<SvgValue> pvs = valueHolder.getValuesByFrame(f);
                if (!pvs.isEmpty()) {
                    paths.append(buildPathGroup(f, pvs));
                }
            });
        });

        mainSVG = mainSVG.substring(0, mainSVG.indexOf("</svg>")) + " " + paths.toString() + " </svg>";
        return mainSVG;
    }

    private static String buildPathGroup(ImageFrame frame, List<SvgValue> values) {
        StringBuilder pathGroupSvg = new StringBuilder();
        pathGroupSvg.append("<g transform='translate(").append(frame.getStartX() + 1).append(",")
                .append(frame.getStartY() + 5).append(")' >"); // TODO: As of now a minor tweak to add 5 to y translate.
        values.forEach(v -> {
            if (v instanceof PathValue) {
                PathValue p = (PathValue)v;
                pathGroupSvg.append(
                        "<path d='" + p.getDValue() + "' stroke='" + p.getColor() + "' stroke-width='" + p.getStrokeWidth() + "' fill='none' ");
                if (p.isDotted()) {
                    pathGroupSvg.append(" stroke-dasharray='5,5'");
                }
                pathGroupSvg.append(" />");
            } else {
                DotValue d = (DotValue)v;
                d.updateStringToDots();
                d.getDots().forEach(dot -> {
                    pathGroupSvg.append(
                            "<circle cx='" + dot.split(" ")[0] + "' cy='" + dot.split(" ")[1] + "' r='.5' fill='" + d.getColor() + "' />");
                });
            }
        });
        pathGroupSvg.append("</g>");
        return pathGroupSvg.toString();
    }

    private static String buildSvgViewBox(double size, double startX, double startY, double width, double height,
            String svgContent) {
        String divStyle = " style='padding:10px;text-align:center;border-style:solid;border-width:1px;border-color:grey;width:" + size + "px;'";
        double scale = Math.min(size / width, size / height);
        StringBuilder c = new StringBuilder();
        c.append("<div ").append(divStyle).append(" >");
        String box = "viewBox='" + startX + " " + startY + " " + width + " " + height + "'  width='" + (width * scale) + "' height='" + (height * scale) + "'";
        c.append(svgContent.replace("{}", box));
        c.append("</div>");
        return c.toString();
    }
}
