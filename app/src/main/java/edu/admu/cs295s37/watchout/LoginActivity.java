package edu.admu.cs295s37.watchout;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import io.realm.Realm;
import io.realm.RealmResults;

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

    @AfterViews
    public void init(){
        realm = Realm.getDefaultInstance();
        SharedPreferences prefs = getSharedPreferences("UserData",MODE_PRIVATE);
        if (prefs.getBoolean("RememberMe", false)) {
            etEmail.setText(prefs.getString("LastUser", null));
            etPassword.setText(prefs.getString("LastPass", null));
            cbRememberMe.setChecked(true);
        }
    }

    @Click(R.id.bSignIn)
    public void bSingIn(){
        String email = etEmail.getText().toString();
        String pword = etPassword.getText().toString();

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

        RealmResults<User> users = realm.where(User.class).equalTo("email", email).findAll();
        if (users.size()==0) {
            toast = Toast.makeText(this, "Sorry, wrong username!", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
            toast.show();
        }
        else if(!(users.first().getPassword().equals(pword))) {
            toast = Toast.makeText(this, "Sorry, wrong password!", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
            toast.show();
        }
        else {
            if(users.first().getRole().equals("Reporter")){
                ReporterMapActivity_.intent(this).uid(users.first().getUid()).start();
            }
            else if (users.first().getRole().equals("Responder")){
                // TODO: Add a responder map activity
            }
            else if (users.first().getRole().equals("News Agency")){
                // TODO: Add a news agency map activity
            }
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
    protected void onDestroy(){
        super.onDestroy();
        realm.close();
    }

}
