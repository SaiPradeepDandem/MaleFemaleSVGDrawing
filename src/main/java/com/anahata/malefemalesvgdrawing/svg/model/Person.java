/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.anahata.malefemalesvgdrawing.svg.model;

import java.util.Date;

/**
 *
 * @author sai.dandem
 */
public class Person {
    enum Gender {
        MALE, FEMALE
    };

    private Gender gender;

    private Date dob;

    public Person(Gender gender, Date dob) {
        this.gender = gender;
        this.dob = dob;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }
    
    
}
