package edu.admu.cs295s37.watchout;

import java.util.Date;
import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;
/**
 * ConfirmedReport contains Hazard Report IDs with those User IDs that confirmed it.
 */
public class ConfirmedReport extends RealmObject {
    @Required
    private String uid;
    @Required
    private String hrid;
    @Required
    private Date dateConfirmed;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getHrid() {
        return hrid;
    }

    public void setHrid(String hrid) {
        this.hrid = hrid;
    }

    public Date getDateConfirmed() {
        return dateConfirmed;
    }

    public void setDateConfirmed(Date dateConfirmed) {
        this.dateConfirmed = dateConfirmed;
    }
}
