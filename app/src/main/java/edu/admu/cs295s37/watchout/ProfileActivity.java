package edu.admu.cs295s37.watchout;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.io.File;

import io.realm.Realm;
import io.realm.RealmResults;

@EActivity(R.layout.activity_profile)
public class ProfileActivity extends AppCompatActivity {

    public Realm realm;
    @ViewById
    ImageView ivAvatar;
    @ViewById
    TextView tvFullName;
    @ViewById
    TextView tvRole;
    @ViewById
    ListView lvReports;
    @ViewById
    Button bEditProfile;
    @ViewById
    Button bBack;
    @Extra
    String uid;

    ReportAdapter reportAdapter;

    @AfterViews
    public void init() {
        realm = MyRealm.getRealm();

        RealmResults<User> userList = realm.where(User.class).equalTo("uid",uid).findAll();
        File savedImage = new File(userList.first().getAvatarPath());
        if(savedImage!=null){
            Picasso.with(this).load(savedImage).networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE).into(ivAvatar);
        }
        tvFullName.setText(userList.first().getFullName());
        tvRole.setText(userList.first().getRole());

        RealmResults<HazardReport> reportsList = realm.where(HazardReport.class).equalTo("reporter",uid).findAll();
        reportAdapter = new ReportAdapter(this, reportsList);
        lvReports.setAdapter(reportAdapter);
    }

    @Click(R.id.bEditProfile)
    public void bEditProfile(){
        RegisterActivity_.intent(this).uid(uid).start();
    }

    @Click(R.id.bBack)
    public void bBack(){
        finish();
    }

    public void editHazardReport(View view){
        //HazardReport hr = realm.copyFromRealm((HazardReport) view.getTag());
        String hrid = (String) view.getTag();
        NewReportActivity_.intent(this).hrid(hrid).start();
    }

    public void deleteHazardReport(View view){
        final HazardReport hr = (HazardReport) view.getTag();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                hr.deleteFromRealm();
            }
        });
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        realm.close();
    }
}
