package edu.admu.cs295s37.watchout;

import android.app.Application;
import io.realm.Realm;

public class MyApplication extends Application {
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        Realm.removeDefaultConfiguration();
    }
}
