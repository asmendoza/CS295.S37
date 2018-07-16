package edu.admu.cs295s37.watchout;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import io.realm.Realm;

@EActivity(R.layout.activity_new_report)
public class NewReportActivity extends AppCompatActivity {

    Realm realm;
    @ViewById
    Button bOK;
    @ViewById
    Button bCancel;
    @ViewById
    EditText etTitle;
    @ViewById
    Spinner spnHazardType;
    // TODO: Add geolocation record
    @ViewById
    EditText actvLocation;
    @ViewById
    EditText etDescription;
    @ViewById
    ImageView ivReport;

    File savedImage;
    boolean hasSavedImage;
    HazardReport hr;
    @Extra
    String hrid;
    @Extra
    String uid;

    @AfterViews
    public void init() {
        realm = MyRealm.getRealm();
        if (hrid!=null) {
            hr = realm.where(HazardReport.class).equalTo("hrid", hrid).findFirst();
            etTitle.setText(hr.getTitle());
            spnHazardType.setSelection(((ArrayAdapter)spnHazardType.getAdapter()).getPosition(hr.getHazardType()));
            actvLocation.setText(hr.getLocation());
            etDescription.setText(hr.getDescription());
            savedImage = new File(hr.getImgPath());
            Picasso.with(this).load(savedImage).networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE).into(ivReport);
        }
    }

    @Click(R.id.ivReport)
    public void ivReport() {
            ImageActivity_.intent(this).startForResult(0);
    }

    @Click(R.id.bOK)
    public void bOK(){
        final String title = etTitle.getText().toString();
        final String hazardType = spnHazardType.getSelectedItem().toString();
        final String location = actvLocation.getText().toString();
        final String description = etDescription.getText().toString();
        HazardReport hazRep;

        if (title.length()!=0 && location.length()!=0 && description.length()!=0 ) {
            if (hr != null) {
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                    HazardReport hazRep = realm.where(HazardReport.class).equalTo("hrid", hr.getHrid()).findFirst();
                    hazRep.setTitle(title);
                    hazRep.setHazardType(hazardType);
                    hazRep.setLocation(location);
                    hazRep.setDescription(description);
                    if (savedImage != null) {
                        hazRep.setImgPath(savedImage.getAbsolutePath());
                    }
                    }
                });
            } else {
                hazRep = new HazardReport();
                hazRep.setDateReported(new Date(System.currentTimeMillis()));
                hazRep.setTitle(title);
                hazRep.setHazardType(hazardType);
                hazRep.setLocation(location);
                hazRep.setDescription(description);
                if (savedImage != null) {
                    hazRep.setImgPath(savedImage.getAbsolutePath());
                }
                hazRep.setReporter(uid);
                realm.beginTransaction();
                realm.copyToRealm(hazRep);
                realm.commitTransaction();
            }

            finish();

        }
        else {
            Toast toast = Toast.makeText(this, "All fields required!", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
            toast.show();
        }

    }

    @Click(R.id.bCancel)
    public void bCancel(){
        if (hasSavedImage && savedImage!=null) {
            File file = new File(savedImage.getAbsolutePath());
            if(file.delete()) {
                System.out.println("File deleted successfully");
            }
            else {
                System.out.println("Failed to delete the file");
            }
        }
        setResult(0);
        finish();
    }

    public void onActivityResult(int requestCode, int responseCode, Intent data){
        if (requestCode==0) {
            if (responseCode==100) {
                // save rawImage to file savedImage.jpeg
                // load file via picasso
                byte[] jpeg = data.getByteArrayExtra("rawJpeg");

                try {
                    savedImage = saveFile(jpeg);
                    refreshImageView(savedImage);
                    hasSavedImage = true;
                }
                catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @NonNull
    private File saveFile(byte[] jpeg) throws IOException {
        File getImageDir = getExternalCacheDir();

        File savedImage = new File(getImageDir, System.currentTimeMillis()+".jpeg");

        FileOutputStream fos = new FileOutputStream(savedImage);
        fos.write(jpeg);
        fos.close();
        System.out.println(savedImage.getAbsolutePath());
        return savedImage;
    }

    private void refreshImageView(File savedImage) {
        Picasso.with(this)
                .load(savedImage)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .into(ivReport);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        realm.close();
    }
}
