package edu.admu.cs295s37.watchout;



import android.location.Location;

import java.util.Date;
import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class HazardReport extends RealmObject {
    @PrimaryKey
    private String hid = UUID.randomUUID().toString();
    @Required
    private Date dateReported;
    @Required
    private String title;

    private float latitude;

    private float longitude;
    @Required
    private String description;
    @Required
    private String imgPath;
    @Required
    private String reporter;

    private int confirmed;

    private boolean hasRespondedTo;
    private String responder;

    private int confirmedResolved;


    
}
