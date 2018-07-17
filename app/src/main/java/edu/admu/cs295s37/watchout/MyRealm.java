package edu.admu.cs295s37.watchout;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import io.realm.Realm;
import io.realm.SyncConfiguration;
import io.realm.SyncUser;

public class MyRealm {
    public static Realm getRealm() {
        SyncConfiguration configuration;

        if(SyncUser.current() != null) {
            configuration = SyncUser.current()
                    .createConfiguration(Constants.REALM_INSTANCE_URL + "/default")
                    .build();
            //Realm.setDefaultConfiguration(configuration);
            return Realm.getInstance(configuration);
        }

        return Realm.getDefaultInstance();
    }

    public static Realm getRealm(SyncUser theUser) {
        SyncConfiguration configuration;

        if(SyncUser.current() != null) {
            configuration = theUser
                    .createConfiguration(Constants.REALM_INSTANCE_URL + "/default")
                    .build();
            //Realm.setDefaultConfiguration(configuration);
            return Realm.getInstance(configuration);
        }

        return Realm.getDefaultInstance();
    }


    public static void logoutUser(){
        if(SyncUser.current() != null) {
            SyncUser.current().logOut();
        }
    }

    public static boolean isNetworkAvailable(Context c) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
