package edu.admu.cs295s37.watchout;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {
    @ViewById(R.id.btnMaps)
    Button btnMaps;

    @Click(R.id.btnMaps)
    public void OpenMap(){
        MapsActivity_.intent(this).start();
    }
}
