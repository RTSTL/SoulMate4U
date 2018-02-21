package com.rtstl.soulmate4u;

/**
 * Created by RTSTL17 on 15-01-2018.
 */

public class Webservice {

    public static String baseURL = "http://soulmate4uapi.bongstyle.co.in/";
    public static String addUser = baseURL + "User/Adduser";
    public static String updateUser = baseURL + "User/UpdateUser";
    public static String getUserByID = baseURL + "User/GetUserbyID";
    public static String getUserList = baseURL + "User/GetUserListByID";
    public static String sendLatLng = baseURL + "User/GetUserListLatLong";
    public static String visibleInvisible = baseURL + "User/Updatevisiblestatus";
    public static String likeDislike = baseURL + "User/UpdateUserLike";
    public static String getAllUserList = baseURL + "User/GetAllUserList";
    public static String getFriendList = baseURL + "User/GetFriendList";
    public static String getPendingList = baseURL + "User/GetPendingForApproval";
    public static String getRejectList = baseURL + "User/GetRejectedList";
    public static String getBlockedList = baseURL + "User/";
    public static String approveFriendReq = baseURL + "User/ApproveFriendReq";
    public static String sendFriendReq = baseURL + "User/SendFriendReq";
    public static String rejectFriendReq = baseURL + "User/RejectFriendReq";
    public static String moodList = baseURL + "User/GetAllMoodList";
    public static String setMood = baseURL + "User/SetUserMood";



}
