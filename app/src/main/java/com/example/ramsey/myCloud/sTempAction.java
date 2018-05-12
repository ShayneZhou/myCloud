package com.example.ramsey.myCloud;

/**
 * Created by hello on 2018/5/7.
 */

public class sTempAction {
    private String tempaction;
    private String tempsection;
    private String isdone;
    private String feedback;
    private String feedback_image_url;
    private String uid;

    public String getFeedback() {
        return feedback;
    }

    public String getTempsection() {
        return tempsection;
    }
    public String getTempaction()
    {
        return tempaction;
    }
    public String getIsdone()
    {
        return isdone;
    }
    public String getFeedback_image_url()
    {
        return feedback_image_url;
    }
    public String getTempActionUid()
    {
        return uid;
    }
    public sTempAction (String Uid, String Tempaction, String Section, String Isdone, String Feedback, String FeedBack_Url)
    {
        uid=Uid;
        tempaction=Tempaction;
        tempsection=Section;
        isdone=Isdone;
        feedback=Feedback;
        feedback_image_url=FeedBack_Url;
    }
}
