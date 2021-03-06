package edu.admu.cs295s37.watchout;

import android.location.Location;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import io.realm.RealmList;
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
    private double latitude;
    private double longitude;
    @Required
    private String description;
    @Required
    private String imgPath;
    @Required
    private String reporter;
//    private boolean confirmed;
    private RealmList<String> confirmedUsers;
//    private boolean responded;
    private String responder;
//    private boolean confirmedResolved;
    private RealmList<String> confirmedResolvedUsers;

    public void HazaradReport() {
        this.confirmedUsers = new RealmList<String>();
        this.confirmedResolvedUsers = new RealmList<String>();
    }

//    public void setConfirmedUsers(RealmList<String> confirmedUsers) {
//        this.confirmedUsers = confirmedUsers;
//    }
//
//    public void setConfirmedResolvedUsers(RealmList<String> confirmedResolvedUsers) {
//        this.confirmedResolvedUsers = confirmedResolvedUsers;
//    }

    public void addConfirmedUser(String uid){
        this.confirmedUsers.add(uid);
    }

    public void addConfirmedResolvedUser(String uid){
        this.confirmedResolvedUsers.add(uid);
    }

    public void removeConfirmedUser(String uid){
        this.confirmedUsers.remove(uid);
    }

    public void removeConfirmedResolvedUser(String uid){
        this.confirmedResolvedUsers.remove(uid);
    }

    public void removeAllConfirmedResolvedUsers(){
        this.confirmedResolvedUsers = new RealmList<String>();
    }

    public RealmList<String> getConfirmedUsers() {

        return confirmedUsers;
    }

    public RealmList<String> getConfirmedResolvedUsers() {
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

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

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

//    public boolean isConfirmed() {
//        return confirmed;
//    }
//
//    public void setConfirmed(boolean confirmed) {
//        this.confirmed = confirmed;
//    }

//    public boolean isResponded() {
//        return responded;
//    }
//
//    public void setResponded(boolean responded) {
//        this.responded = responded;
//    }

    public String getResponder() {
        return responder;
    }

    public void setResponder(String responder) {
        this.responder = responder;
    }

//    public boolean isConfirmedResolved() {
//        return confirmedResolved;
//    }
//
//    public void setConfirmedResolved(boolean confirmedResolved) {
//        this.confirmedResolved = confirmedResolved;
//    }
}
