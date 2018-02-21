package com.rtstl.soulmate4u;

import com.google.android.gms.maps.model.Marker;

/**
 * Created by RTSTL17 on 19-01-2018.
 */

public class OpponentMarkerModel {

    Marker opponentMarker;
    String userID = "";

    public OpponentMarkerModel(Marker opponentMarker, String userID) {
        this.opponentMarker = opponentMarker;
        this.userID = userID;
    }

    public Marker getOpponentMarker() {
        return opponentMarker;
    }

    public void setOpponentMarker(Marker opponentMarker) {
        this.opponentMarker = opponentMarker;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
