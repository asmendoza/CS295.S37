package edu.admu.cs295s37.watchout;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import io.realm.ObjectServerError;
import io.realm.Realm;
import io.realm.SyncConfiguration;
import io.realm.SyncCredentials;
import io.realm.SyncUser;

public class MyRealm {
    private static boolean userSet;

    public static Realm getRealm() {
        SyncConfiguration configuration;

        if(SyncUser.current() != null) {
            configuration = SyncUser.current()
                    .createConfiguration(Constants.INSTANCE_ADDRESS + "/default")
                    .build();
            Realm.setDefaultConfiguration(configuration);
        }

        return Realm.getDefaultInstance();
    }

    public static Realm getRealm(String nickname) {
        SyncConfiguration configuration;

        if(setUser(nickname)) {
            if(SyncUser.current() != null) {
                configuration = SyncUser.current()
                        .createConfiguration(Constants.INSTANCE_ADDRESS + "/default")
                        .build();
                Realm.setDefaultConfiguration(configuration);
            }
        }

        return Realm.getDefaultInstance();
    }


    public static boolean setUser(String nickname) {
        SyncCredentials credentials = SyncCredentials.nickname(nickname, false);
        SyncUser.logInAsync(credentials, Constants.AUTH_URL, new SyncUser.Callback<SyncUser>() {
            @Override
            public void onSuccess(SyncUser user) {
                userSet = true;
            }

            @Override
            public void onError(ObjectServerError error) {
                userSet = false;
                Log.e("Login error", error.toString());
            }
        });

        return userSet;
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
