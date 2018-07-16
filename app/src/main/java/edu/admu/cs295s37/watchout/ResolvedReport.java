package edu.admu.cs295s37.watchout;

import io.realm.RealmObject;
import io.realm.annotations.Required;

public class ResolvedReport extends RealmObject {
    @Required
    private String uid;
    @Required
    private String hrid;

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

}
