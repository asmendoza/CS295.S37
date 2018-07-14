package edu.admu.cs295s37.watchout;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

@EActivity
class LoginActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }
}
