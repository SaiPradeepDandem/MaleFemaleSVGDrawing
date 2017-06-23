/*
 *  Copyright © - 2015 Urgent Learning. All rights reserved.
 */
package com.anahata.malefemalesvgdrawing.svg.model;

import com.anahata.malefemalesvgdrawing.svg.model.Person.Gender;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 *
 * @author sai.dandem
 */
public class DrawingInput extends Base implements Serializable {

    private List<MasterImage> images = new ArrayList<>();

    public DrawingValueHolder doCreateValue() {
        return new DrawingValueHolder();
    }

    public List<MasterImage> getImages() {
        return images;
    }

    public MasterImage getImage(Person person) {
        SVGImageType type = SVGImageType.MALE;
        Date dob = person.getDob();
        Gender gender = person.getGender();
        if (dob != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(dob);
            LocalDate localDate = LocalDate.now();
            LocalDate birthDate = dob.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            Period age = Period.between(birthDate, localDate).normalized();
            if (age != null && age.getYears() <= 3) {
                type = SVGImageType.CHILD;
            }
        }
        if (type == SVGImageType.MALE && gender != null && gender == Gender.FEMALE) {
            type = SVGImageType.FEMALE;
        }
        return getImage(type);
    }

    public MasterImage getImage(SVGImageType type) {
        return images.stream().filter(i -> i.getType() == type).findFirst().orElse(null);
    }

}
