package com.example.ramsey.myCloud;

/**
 * Created by hello on 2018/3/29.
 */


public class sQuestion {
    private String Title;
    private String PositionNumber;
    private String Level;
    private String CreatedAt;
    private String ExampleImageUrl;

    public String getLevel() {
        return Level;
    }


    public String getPositionNumber() {
        return PositionNumber;
    }

    public String getCreatedAt() {
        return CreatedAt;
    }

    public String getExampleImageUrl() {
        return ExampleImageUrl;
    }

    public String getTitle() {
        return Title;
    }

    public sQuestion(String title, String positionNumber, String level, String createdAt, String example_image_url)
    {
        Title = title;
        PositionNumber = positionNumber;
        Level = level;
        CreatedAt = createdAt;
        ExampleImageUrl = example_image_url;
    }
}

