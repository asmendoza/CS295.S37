package edu.admu.cs295s37.watchout;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

import io.realm.ObjectServerError;
import io.realm.Realm;
import io.realm.SyncCredentials;
import io.realm.SyncUser;

@EActivity(R.layout.activity_register)
public class RegisterActivity extends AppCompatActivity {

    Realm realm;
    @ViewById
    ImageView ivAvatar;
    @ViewById
    EditText etFullName;
    @ViewById
    EditText etEmail;
    @ViewById
    EditText etPassword;
    @ViewById
    EditText etPassRepeat;
    @ViewById
    Spinner spnRole;
    @ViewById
    Button bOK;
    @ViewById
    Button bCancel;
    @Extra
    String uid;
    User usr;
    File savedImage;
    boolean hasSavedImage;
    Context c;
    boolean editMode;

    @AfterViews
    public void init() {
        c = this;
        if (uid!=null) {
            editMode = true;
            realm = MyRealm.getRealm();
            usr = realm.where(User.class).equalTo("uid", uid).findFirst();
            etFullName.setText(usr.getFullName());
            etEmail.setText(usr.getEmail());
            etEmail.setEnabled(false);
            etPassword.setText(usr.getPassword());
            etPassRepeat.setText(usr.getPassword());
            spnRole.setSelection(((ArrayAdapter)spnRole.getAdapter()).getPosition(usr.getRole()));
            spnRole.setEnabled(false);
            savedImage = new File(usr.getAvatarPath());
            Picasso.with(this).load(savedImage).networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE).into(ivAvatar);
        }
    }

    @Click(R.id.ivAvatar)
    public void ivAvatar(){
        ImageActivity_.intent(this).startForResult(0);
    }

    @Click(R.id.bOK)
    public void bOK(){
        if(!MyRealm.isNetworkAvailable(this)) {
            Snackbar.make(bOK,"No internet connection detected. Try again later."
                    ,Snackbar.LENGTH_SHORT)
                    .show();
            return;
        }

        final String fullName = etFullName.getText().toString();
        final String email = etEmail.getText().toString();
        final String pword = etPassword.getText().toString();
        final String role = spnRole.getSelectedItem().toString();

        if (fullName.trim().length() == 0
                && pword.trim().length() == 0
                && email.trim().length() == 0) {
            Toast toast = Toast.makeText(c, "All fields required!", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
            toast.show();

            return;
        }

        if (!pword.trim().equals(etPassRepeat.getText().toString().trim())) {
            Toast toast = Toast.makeText(c, "Passwords don't match!", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
            toast.show();

            return;
        }
        if(editMode){
            realm.beginTransaction();
            User user = usr;
            user.setFullName(fullName);
            user.setEmail(email);
            user.setPassword(pword);
            user.setRole(role);
            if (savedImage != null) {
                user.setAvatarPath(savedImage.getAbsolutePath());
            }else {
                user.setAvatarPath("");
            }
            //realm.copyToRealm(user);
            realm.commitTransaction();

            finish();
        }else {
            try {

                if (SyncUser.current() != null) {
                    MyRealm.logoutUser();
                }

                SyncCredentials credentials = SyncCredentials.usernamePassword(email, pword, true);

                //SyncUser.logIn(credentials, Constants.AUTH_URL);

                SyncUser.logInAsync(credentials, Constants.AUTH_URL, new SyncUser.Callback<SyncUser>() {
                    @Override
                    public void onSuccess(SyncUser result) {
                        Log.e("Login Success", result.getIdentity());
                        realm = MyRealm.getRealm();

                        
                        User user;

                        if (isInUserList(email)) {
                            Toast toast = Toast.makeText(c, "An account has been registered with this email!", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
                            toast.show();
                        } else {
                            user = new User();
                            user.setFullName(fullName);
                            user.setEmail(email);
                            user.setPassword(pword);
                            user.setRole(role);
                            if (savedImage != null) {
                                user.setAvatarPath(savedImage.getAbsolutePath());
                            }else {
                                user.setAvatarPath("");
                            }

                            realm.beginTransaction();
                            realm.copyToRealm(user);
                            realm.commitTransaction();

                            finish();
                        }
                    }

                    @Override
                    public void onError(ObjectServerError error) {
                        Log.e("Login Error", error.toString());
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                Toast toast = Toast.makeText(this, "Cannot login to server!", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
                toast.show();
            } finally {
                MyRealm.logoutUser();
            }
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

    public void onActivityResult(int requestCode, int responseCode, Intent data)
    {
        if (requestCode==0)
        {
            if (responseCode==100)
            {
                // save rawImage to file savedImage.jpeg
                // load file via picasso
                byte[] jpeg = data.getByteArrayExtra("rawJpeg");

                try {
                    savedImage = saveFile(jpeg);
                    refreshImageView(savedImage);
                    hasSavedImage = true;
                }
                catch(Exception e)
                {
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
                .into(ivAvatar);
    }

    private boolean isInUserList(String email) {
        return realm.where(User.class).equalTo("email", email).findFirst() != null;
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        realm.close();
    }

}
