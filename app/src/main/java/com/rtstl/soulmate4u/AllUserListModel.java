package com.rtstl.soulmate4u;

/**
 * Created by RTSTL17 on 31-01-2018.
 */

public class AllUserListModel {

    String id = "", fbID = "", name = "", email = "", picURL = "", gender = "", phone = "", interestedIN = "",
            oppponentProfession = "", lastLat = "", lastLong = "";
    boolean isOnline, isVisible;
    int likeStatus = 0;
    String profession = "", dob = "", lastActive = "";
    int distanceFromMe = 0;
    int isFriend = 0, moodID = 0;
    String moodURL = "", sourceLatLng = "", destLatLng = "";

    public AllUserListModel(String id, String fbID, String name, String email, String picURL, String gender, String phone, String interestedIN, String oppponentProfession, String lastLat, String lastLong, boolean isOnline, boolean isVisible, int likeStatus, String profession, String dob, String lastActive, int distanceFromMe, int isFriend, int moodID, String moodURL, String sourceLatLng, String destLatLng) {
        this.id = id;
        this.fbID = fbID;
        this.name = name;
        this.email = email;
        this.picURL = picURL;
        this.gender = gender;
        this.phone = phone;
        this.interestedIN = interestedIN;
        this.oppponentProfession = oppponentProfession;
        this.lastLat = lastLat;
        this.lastLong = lastLong;
        this.isOnline = isOnline;
        this.isVisible = isVisible;
        this.likeStatus = likeStatus;
        this.profession = profession;
        this.dob = dob;
        this.lastActive = lastActive;
        this.distanceFromMe = distanceFromMe;
        this.isFriend = isFriend;
        this.moodID = moodID;
        this.moodURL = moodURL;
        this.sourceLatLng = sourceLatLng;
        this.destLatLng = destLatLng;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getLastActive() {
        return lastActive;
    }

    public void setLastActive(String lastActive) {
        this.lastActive = lastActive;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFbID() {
        return fbID;
    }

    public void setFbID(String fbID) {
        this.fbID = fbID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPicURL() {
        return picURL;
    }

    public void setPicURL(String picURL) {
        this.picURL = picURL;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getInterestedIN() {
        return interestedIN;
    }

    public void setInterestedIN(String interestedIN) {
        this.interestedIN = interestedIN;
    }

    public String getOppponentProfession() {
        return oppponentProfession;
    }

    public void setOppponentProfession(String oppponentProfession) {
        this.oppponentProfession = oppponentProfession;
    }

    public String getLastLat() {
        return lastLat;
    }

    public void setLastLat(String lastLat) {
        this.lastLat = lastLat;
    }

    public String getLastLong() {
        return lastLong;
    }

    public void setLastLong(String lastLong) {
        this.lastLong = lastLong;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    public int getLikeStatus() {
        return likeStatus;
    }

    public void setLikeStatus(int likeStatus) {
        this.likeStatus = likeStatus;
    }

    public int getDistanceFromMe() {
        return distanceFromMe;
    }

    public void setDistanceFromMe(int distanceFromMe) {
        this.distanceFromMe = distanceFromMe;
    }

    public int getIsFriend() {
        return isFriend;
    }

    public void setIsFriend(int isFriend) {
        this.isFriend = isFriend;
    }

    public int getMoodID() {
        return moodID;
    }

    public void setMoodID(int moodID) {
        this.moodID = moodID;
    }

    public String getMoodURL() {
        return moodURL;
    }

    public void setMoodURL(String moodURL) {
        this.moodURL = moodURL;
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
