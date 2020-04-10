package com.example.gesturemediaplayer;

import java.io.Serializable;

public class UserData implements Serializable {
    private String gender;
    private String initial;
    private String method;
    private String trial;
    private int trialnumber;


    public int getTrialnumber() {
        return trialnumber;
    }

    public void setTrialnumber(int trialnumber) {
        this.trialnumber = trialnumber;
    }

    public String getTrial() {
        return trial;
    }

    public void setTrial(String trial) {
        this.trial = trial;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getInitial() {
        return initial;
    }

    public void setInitial(String initial) {
        this.initial = initial;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }




}