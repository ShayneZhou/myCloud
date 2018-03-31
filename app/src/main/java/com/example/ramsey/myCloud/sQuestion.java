package com.example.ramsey.myCloud;

/**
 * Created by hello on 2018/3/29.
 */


public class sQuestion {
    private String Title;
    private String Unique_Id;
    private String Level;

    public String getLevel() {
        return Level;
    }

    public String getUnique_Id() {
        return Unique_Id;
    }

    public String getTitle() {
        return Title;
    }
    public sQuestion(String title, String unique_id, String level)
    {
        Title=title;
        Unique_Id=unique_id;
        Level=level;
    }
}

