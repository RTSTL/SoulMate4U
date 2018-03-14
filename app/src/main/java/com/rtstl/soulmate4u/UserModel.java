package com.rtstl.soulmate4u;

/**
 * Created by RTSTL17 on 15-01-2018.
 */

public class UserModel {

    String id = "", latitude = "", longitude = "", name = "", gender = "", url;
    boolean isvisible;
    int isLiked;
    String fbID = "";
    String myProfession = "", opponentProfession = "", interestedIn = "";
    String moodid = "";
    int isFriend = 0;
    String sourceLatLng = "", destLatLng = "";

    public UserModel(String id, String latitude, String longitude, String name, String gender, String url, boolean isvisible, int isLiked, String fbID, String myProfession, String opponentProfession, String interestedIn, String moodid, int isFriend, String sourceLatLng, String destLatLng) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
        this.gender = gender;
        this.url = url;
        this.isvisible = isvisible;
        this.isLiked = isLiked;
        this.fbID = fbID;
        this.myProfession = myProfession;
        this.opponentProfession = opponentProfession;
        this.interestedIn = interestedIn;
        this.moodid = moodid;
        this.isFriend = isFriend;
        this.sourceLatLng = sourceLatLng;
        this.destLatLng = destLatLng;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isIsvisible() {
        return isvisible;
    }

    public void setIsvisible(boolean isvisible) {
        this.isvisible = isvisible;
    }

    public int getIsLiked() {
        return isLiked;
    }

    public void setIsLiked(int isLiked) {
        this.isLiked = isLiked;
    }

    public String getFbID() {
        return fbID;
    }

    public void setFbID(String fbID) {
        this.fbID = fbID;
    }

    public String getMyProfession() {
        return myProfession;
    }

    public void setMyProfession(String myProfession) {
        this.myProfession = myProfession;
    }

    public String getOpponentProfession() {
        return opponentProfession;
    }

    public void setOpponentProfession(String opponentProfession) {
        this.opponentProfession = opponentProfession;
    }

    public String getInterestedIn() {
        return interestedIn;
    }

    public void setInterestedIn(String interestedIn) {
        this.interestedIn = interestedIn;
    }

    public String getMoodid() {
        return moodid;
    }

    public void setMoodid(String moodid) {
        this.moodid = moodid;
    }

    public int getIsFriend() {
        return isFriend;
    }

    public void setIsFriend(int isFriend) {
        this.isFriend = isFriend;
    }

    public String getSourceLatLng() {
        return sourceLatLng;
    }

    public void setSourceLatLng(String sourceLatLng) {
        this.sourceLatLng = sourceLatLng;
    }

    public String getDestLatLng() {
        return destLatLng;
    }

    public void setDestLatLng(String destLatLng) {
        this.destLatLng = destLatLng;
    }
}
