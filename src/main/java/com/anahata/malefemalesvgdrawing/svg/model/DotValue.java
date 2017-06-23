/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.anahata.malefemalesvgdrawing.svg.model;

import com.anahata.malefemalesvgdrawing.svg.SvgValue;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author sai.dandem
 */
public class DotValue extends SvgValue implements Serializable {
    
    //@Transient
    private transient Set<String> dots = new HashSet<>();

    @Override
    public DotValue doCopy() {
        DotValue copy = new DotValue();
        super.doCopy(copy);
        copy.getDots().addAll(getDots());
        return copy;
    }

    public void updateDotsToString() {
        setDValue(dots.stream().collect(Collectors.joining(",")));
    }

    public void updateStringToDots() {
        dots = new HashSet<>();
        Stream.of(getDValue().split(",")).forEach(dots::add);
    }

    public Set<String> getDots() {
        return dots;
    }

}
