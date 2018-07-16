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

import io.realm.Realm;

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

    @AfterViews
    public void init() {
        realm = Realm.getDefaultInstance();
        if (uid!=null) {
            usr = realm.where(User.class).equalTo("uid", uid).findFirst();
            etFullName.setText(usr.getFullName());
            etEmail.setText(usr.getEmail());
            etEmail.setEnabled(false);
            etPassword.setText(usr.getPassword());
            etPassRepeat.setText(usr.getPassword());
            spnRole.setSelection(((ArrayAdapter)spnRole.getAdapter()).getPosition(usr.getRole()));
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
        final String fullName = etFullName.getText().toString();
        final String email = etEmail.getText().toString();
        final String pword = etPassword.getText().toString();
        final String role = spnRole.getSelectedItem().toString();

        User user;

        if (fullName.length()!=0 && pword.length()!=0 && email.length()!=0 ) {
            if (usr==null && isInUserList(email)) {
                Toast toast = Toast.makeText(this, "An account has been registered with this email!", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
                toast.show();
            }
            else {
                if (pword.equals(etPassRepeat.getText().toString())) {
                    if (usr != null) {
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                            User user = realm.where(User.class).equalTo("uid", usr.getUid()).findFirst();
                            user.setFullName(fullName);
                            user.setPassword(pword);
                            user.setRole(role);
                            if (savedImage != null) {
                                user.setAvatarPath(savedImage.getAbsolutePath());
                            }
                            }
                        });
                    } else {
                        user = new User();
                        user.setFullName(fullName);
                        user.setEmail(email);
                        user.setPassword(pword);
                        user.setRole(role);
                        if (savedImage != null) {
                            user.setAvatarPath(savedImage.getAbsolutePath());
                        }
                        realm.beginTransaction();
                        realm.copyToRealm(user);
                        realm.commitTransaction();

                    }
                    finish();
                }
                else {
                    Toast toast = Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
                    toast.show();
                }
            }
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
        return realm.where(User.class).equalTo("email", email).findAll().size() > 0;
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        realm.close();
    }

}
