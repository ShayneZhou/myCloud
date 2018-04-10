package com.example.ramsey.myCloud;

/**
 * Created by hello on 2018/4/3.
 */

public class sAction {
    String uid;
    String solution;
    String section;
    String isdone;
    String feedback;
    String feedback_image_url;
    String performence;

    public String getIsdone() {
        return isdone;
    }

    public String getFeedback() {
        return feedback;
    }

    public String getFeedback_image_url() {
        return feedback_image_url;
    }

    public String getPerformence() {
        return performence;
    }

    public String getSection() {
        return section;
    }

    public String getSolution() {
        return solution;
    }

    public String getUid() {
        return uid;
    }
    public sAction(String muid, String msolution, String msection, String misdone, String mfeedback, String mfeedback_image_url, String mperformence)
    {
        uid=muid;
        solution=msolution;
        section=msection;
        isdone=misdone;
        feedback=mfeedback;
        feedback_image_url=mfeedback_image_url;
        performence=mperformence;

    }
}
