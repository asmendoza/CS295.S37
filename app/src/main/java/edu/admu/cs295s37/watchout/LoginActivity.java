package edu.admu.cs295s37.watchout;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import io.realm.ObjectServerError;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmModel;
import io.realm.RealmObjectChangeListener;
import io.realm.RealmResults;
import io.realm.SyncCredentials;
import io.realm.SyncUser;

@EActivity(R.layout.activity_login)
class LoginActivity extends AppCompatActivity {

    Toast toast;
    Realm realm;
    @ViewById
    EditText etEmail;
    @ViewById
    EditText etPassword;
    @ViewById
    CheckBox cbRememberMe;
    @ViewById
    Button bSignIn;
    @ViewById
    Button bSignUp;

    Context c;

    RealmResults<User> user;
    String email;
    String pword;

    @AfterViews
    public void init(){
        //realm = Realm.getDefaultInstance();

        c = this;
        SharedPreferences prefs = getSharedPreferences("UserData",MODE_PRIVATE);
        if (prefs.getBoolean("RememberMe", false)) {
            etEmail.setText(prefs.getString("LastUser", null));
            etPassword.setText(prefs.getString("LastPass", null));
            cbRememberMe.setChecked(true);
        }
    }

    @Click(R.id.bSignIn)
    public void bSignIn(){
        if(!MyRealm.isNetworkAvailable(this)) {
            Snackbar.make(bSignIn,"No internet connection detected. Try again later."
                    ,Snackbar.LENGTH_SHORT)
                    .show();
            return;
        }

        email = etEmail.getText().toString();
        pword = etPassword.getText().toString();

        SharedPreferences prefs = getSharedPreferences("UserData",MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        if (cbRememberMe.isChecked()) {
            editor.putString("LastUser", email);
            editor.putString("LastPass", pword);
            editor.putBoolean("RememberMe", true);
            editor.commit();
        }
        else {
            editor.putBoolean("RememberMe", false);
            editor.commit();
        }

        try{
            if(SyncUser.current() != null) {
                MyRealm.logoutUser();
            }

            SyncCredentials credentials = SyncCredentials.usernamePassword(email, pword, false);

            //SyncUser.logIn(credentials, Constants.AUTH_URL);

            SyncUser.logInAsync(credentials, Constants.AUTH_URL, new SyncUser.Callback<SyncUser>() {
                        @Override
                        public void onSuccess(SyncUser result) {
                            Log.e("Login success","Login Successful!");
                            //realm = MyRealm.getRealm(result);
                            //realm = MyRealm.getRealm(result);

                            GoToNextScreen();
                            Log.e("Checking URL"
                                    ,realm.getConfiguration().getPath() + "\n"
                                        + realm.getConfiguration().getRealmFileName());

                        }

                        @Override
                        public void onError(ObjectServerError error) {
                            Log.e("Login Error", error.toString());
                            toast = Toast.makeText(c, "The provided credentials are invalid or the user does not exist.", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
                            toast.show();
                        }
                    });

        } catch(Exception e) {
            e.printStackTrace();
            toast = Toast.makeText(this, "Cannot login to the server!", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
            toast.show();
        }finally{
            MyRealm.logoutUser();
        }
    }

    @Click(R.id.bSignUp)
    public void bSignUp(){
        RegisterActivity_.intent(this).start();
    }

    @Override
    protected void onResume(){
        super.onResume();
        SharedPreferences prefs = getSharedPreferences("UserData",MODE_PRIVATE);
        if (prefs.getBoolean("RememberMe", false)) {
            etEmail.setText(prefs.getString("LastUser", null));
            etPassword.setText(prefs.getString("LastPass", null));
            cbRememberMe.setChecked(true);
        }
        else{
            etEmail.setText("");
            etPassword.setText("");
            cbRememberMe.setChecked(false);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        realm.close();
    }

    private void GoToNextScreen(){
        realm = MyRealm.getRealm(SyncUser.current());

        user = realm.where(User.class)
                .equalTo("email", email)
                .and()
                .equalTo("password",pword)
                .findAllAsync();

        user.addChangeListener(new RealmChangeListener<RealmResults<User>>() {
            @Override
            public void onChange(RealmResults<User> userg) {
                Log.e("Updating User", "Changes detected.");

                Log.d("User Valid?",user.isValid() + "");
                boolean found = false;
                for(User u: userg) {
                    if (u.isValid()) {
                        switch (u.getRole()) {
                            case "Reporter":
                                ReporterMapActivity_.intent(c).uid(u.getUid()).start();
                                found = true;
                                break;
                            case "Responder":
                                ReporterMapActivity_.intent(c).uid(u.getUid()).start();
                                found = true;
                                break;
                            case "News Agency":
                                // TODO: Add a news agency map activity
                                found = true;
                                break;
                        }
                        if(found){
                            break;
                        }
                    }
                }

            }
        });
    }

}
