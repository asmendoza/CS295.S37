package edu.admu.cs295s37.watchout;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.common.internal.safeparcel.SafeParcelable;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import io.realm.Realm;
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
    @Extra
    String uid;
    @Extra
    String hrid;

    @AfterViews
    public void init(){
        realm = Realm.getDefaultInstance();
        HazardReport hr = realm.where(HazardReport.class).equalTo("hrid", hrid).findFirst();
        tvTitle.setText(hr.getTitle());
        tvHazardType.setText(hr.getHazardType());
        tvLocation.setText(hr.getLocation());
        tvDescription.setText(hr.getLocation());
        StringBuilder statusText = new StringBuilder();
        if(hr.isResponded()){
            if(hr.isConfirmed()){
                statusText.append(hr.getConfirmedUsers().size() + " user/s confirmed resolution!");
            }
            else{
                statusText.append("Resolved!");
            }
        }
        else {
            if(hr.isConfirmed()){
                statusText.append(hr.getConfirmedResolvedUsers().size() + " user/s confirmed!");
            }
            else{
                statusText.append("Reported!");
            }
        }
        tvStatus.setText(statusText);
        if (uid.equals(hr.getReporter())){
            swConfirmReport.setClickable(false);
        }
        swConfirmReport.setChecked(hr.getConfirmedUsers().indexOf(uid) != -1);
        if (hr.getResponder()!=null){
            swConfirmResolved.setClickable(true);
            swConfirmResolved.setChecked(hr.getConfirmedResolvedUsers().indexOf(uid) != -1);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close()
    }
}
