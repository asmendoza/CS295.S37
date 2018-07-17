package edu.admu.cs295s37.watchout;

import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.android.gms.maps.model.LatLng;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

@EActivity(R.layout.activity_report_detail)
public class ReportDetailActivity extends AppCompatActivity {

    Realm realm;
    @ViewById
    ImageView ivReport;
    @ViewById
    TextView tvTitle;
    @ViewById
    TextView tvHazardType;
    @ViewById
    TextView tvLocation;
    @ViewById
    TextView tvDescription;
    @ViewById
    TextView tvStatus;
    @ViewById
    Switch swConfirmReport;
    @ViewById
    Switch swConfirmResolved;
    @ViewById
    LinearLayout lyConfirmation;
    @ViewById
    Switch swResolve;
    @ViewById
    Button bBack;

    @Extra
    String uid;
    @Extra
    String hrid;
    @Extra
    LatLng latLng;

    User user;
    HazardReport hr;

    @AfterViews
    public void init(){
        realm = MyRealm.getRealm();

        user = realm.where(User.class).equalTo("uid",uid).findFirst();

        if(hrid != null) {
            hr = realm.where(HazardReport.class).equalTo("hrid", hrid).findFirst();
            tvTitle.setText(hr.getTitle());
            tvHazardType.setText(hr.getHazardType());
            tvLocation.setText(hr.getLocation());
            tvDescription.setText(hr.getDescription());
            StringBuilder statusText = new StringBuilder();
            if (hr.getResponder() != null) {
                if (hr.getConfirmedResolvedUsers().size() != 0) {
                    statusText.append(hr.getConfirmedResolvedUsers().size() + " user/s confirmed resolution!");
                } else {
                    statusText.append("Resolved!");
                }
            } else {
                if (hr.getConfirmedUsers().size() != 0) {
                    statusText.append(hr.getConfirmedUsers().size() + " user/s confirmed!");
                } else {
                    statusText.append("Reported!");
                }
            }
            tvStatus.setText(statusText);

            if (user.getRole().equals("Reporter")) {
                lyConfirmation.setVisibility(View.VISIBLE);
                swConfirmReport.setClickable(true);
                if (uid.equals(hr.getReporter())) {
                    swConfirmReport.setClickable(false);
                }
                swConfirmReport.setChecked(hr.getConfirmedUsers().indexOf(uid) != -1);
                if (hr.getResponder() != null) {
                    swConfirmReport.setClickable(false);
                    swConfirmResolved.setClickable(true);
                    swConfirmResolved.setChecked(hr.getConfirmedResolvedUsers().indexOf(uid) != -1);
                }
            } else if (user.getRole().equals("Responder")) {
                swResolve.setVisibility(View.VISIBLE);
                swResolve.setClickable(true);
            }
        }

    }

    @Click(R.id.bBack)
    public void bBack(){
        onBackPressed();
    }

    @Click(R.id.swConfirmReport)
    public void swConfirmReport(){
        // TODO: Update counters
        if (swConfirmReport.isChecked() && hr.getConfirmedUsers().indexOf(uid)==-1){
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    HazardReport hazRep = realm.where(HazardReport.class).equalTo("hrid", hrid).findFirst();
                    hazRep.addConfirmedUser(uid);
                }
            });
        }
        else if(!swConfirmReport.isChecked() && hr.getConfirmedUsers().indexOf(uid)!=-1) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    HazardReport hazRep = realm.where(HazardReport.class).equalTo("hrid", hrid).findFirst();
                    hazRep.removeConfirmedUser(uid);
                }
            });
        }
    }

    @Click(R.id.swConfirmResolved)
    public void swConfirmResolved(){
        // TODO: Update counters
        if (swConfirmResolved.isChecked() && hr.getConfirmedResolvedUsers().indexOf(uid)==-1){
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    HazardReport hazRep = realm.where(HazardReport.class).equalTo("hrid", hrid).findFirst();
                    hazRep.addConfirmedResolvedUser(uid);
                }
            });
        }
        else if(!swConfirmResolved.isChecked() && hr.getConfirmedResolvedUsers().indexOf(uid)!=-1) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    HazardReport hazRep = realm.where(HazardReport.class).equalTo("hrid", hrid).findFirst();
                    hazRep.removeConfirmedResolvedUser(uid);
                }
            });
        }
    }

    @Click(R.id.swResolve)
    public void swResolved(){
        // TODO: Update counters
        if(swResolve.isChecked() && hr.getResponder()==null){
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    HazardReport hazRep = realm.where(HazardReport.class).equalTo("hrid", hrid).findFirst();
                    hazRep.setResponder(uid);
                }
            });
        }
        else if (swResolve.isChecked() && hr.getResponder()!=null) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    HazardReport hazRep = realm.where(HazardReport.class).equalTo("hrid", hrid).findFirst();
                    hazRep.setResponder(null);
                    hazRep.removeAllConfirmedResolvedUsers();
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
