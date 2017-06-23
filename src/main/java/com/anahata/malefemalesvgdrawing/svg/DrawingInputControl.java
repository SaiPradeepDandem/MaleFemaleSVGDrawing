/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.anahata.malefemalesvgdrawing.svg;

import com.anahata.malefemalesvgdrawing.svg.model.DotValue;
import com.anahata.malefemalesvgdrawing.svg.model.DrawingValueHolder;
import com.anahata.malefemalesvgdrawing.svg.model.ImageFrame;
import com.anahata.malefemalesvgdrawing.svg.model.PathValue;
import java.util.List;

/**
 *
 * @author sai.dandem
 */
public interface DrawingInputControl {

    public CustomSVGGroup getSvg();

    public void zoomFrame(FramePane framePane);

    public DrawingValueHolder getValueHolder();

    public void updateValueHolder(ImageFrame frame, List<PathValue> values, List<DotValue> dotValues);
}
