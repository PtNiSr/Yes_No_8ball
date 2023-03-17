package com.ni___ckel.yesnoshake;

import com.google.gson.annotations.SerializedName;

public class AnswerFromBall {

    @SerializedName("reading")
    private String reading;

    public AnswerFromBall(String reading) {
        this.reading = reading;
    }

    public String getReading() {
        return reading;
    }

}
