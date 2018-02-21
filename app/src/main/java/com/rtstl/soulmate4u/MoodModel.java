package com.rtstl.soulmate4u;

/**
 * Created by RTSTL17 on 16-02-2018.
 */

public class MoodModel {

    String id = "", moodName = "", moodUrl = "";

    public MoodModel(String id, String moodName, String moodUrl) {
        this.id = id;
        this.moodName = moodName;
        this.moodUrl = moodUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMoodName() {
        return moodName;
    }

    public void setMoodName(String moodName) {
        this.moodName = moodName;
    }

    public String getMoodUrl() {
        return moodUrl;
    }

    public void setMoodUrl(String moodUrl) {
        this.moodUrl = moodUrl;
    }
}
