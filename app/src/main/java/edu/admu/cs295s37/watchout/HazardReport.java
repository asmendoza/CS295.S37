package edu.admu.cs295s37.watchout;



import android.location.Location;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * HazardReport contains hazard report details.
 */
public class HazardReport extends RealmObject {
    @PrimaryKey
    private String hrid = UUID.randomUUID().toString();
    @Required
    private Date dateReported;
    @Required
    private String title;
    @Required
    String hazardType;
    @Required
    private String location;
    //private float latitude;
    //private float longitude;
    @Required
    private String description;
    @Required
    private String imgPath;
    @Required
    private String reporter;
    private boolean confirmed;
    private ArrayList<String> confirmedUsers;
    private boolean responded;
    private String responder;
    private boolean confirmedResolved;
    private ArrayList<String> confirmedResolvedUsers;

    public void setConfirmedUsers(ArrayList<String> confirmedUsers) {
        this.confirmedUsers = confirmedUsers;
    }

    public void setConfirmedResolvedUsers(ArrayList<String> confirmedResolvedUsers) {
        this.confirmedResolvedUsers = confirmedResolvedUsers;
    }

    public ArrayList<String> getConfirmedUsers() {

        return confirmedUsers;
    }

    public ArrayList<String> getConfirmedResolvedUsers() {
        return confirmedResolvedUsers;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getHazardType() {
        return hazardType;
    }

    public void setHazardType(String hazardType) {
        this.hazardType = hazardType;
    }

    public String getHrid() {
        return hrid;
    }

    public Date getDateReported() {
        return dateReported;
    }

    public void setDateReported(Date dateReported) {
        this.dateReported = dateReported;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

//    public float getLatitude() {
//        return latitude;
//    }

//    public void setLatitude(float latitude) {
//        this.latitude = latitude;
//    }

//    public float getLongitude() {
//        return longitude;
//    }

//    public void setLongitude(float longitude) {
//        this.longitude = longitude;
//    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public String getReporter() {
        return reporter;
    }

    public void setReporter(String reporter) {
        this.reporter = reporter;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    public boolean isResponded() {
        return responded;
    }

    public void setResponded(boolean responded) {
        this.responded = responded;
    }

    public String getResponder() {
        return responder;
    }

    public void setResponder(String responder) {
        this.responder = responder;
    }

    public boolean isConfirmedResolved() {
        return confirmedResolved;
    }

    public void setConfirmedResolved(boolean confirmedResolved) {
        this.confirmedResolved = confirmedResolved;
    }
}
