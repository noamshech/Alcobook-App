package com.example.alcobook.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.alcobook.R;
import com.example.alcobook.model.helper.AuthHelper;

import java.util.function.Consumer;

public class LoginActivity extends AppCompatActivity {
    private final static String TAG="LoginActivity";

    protected boolean mIsLoginMode=true;
    protected EditText mUsernameEt;
    protected EditText mEmailEt;
    protected EditText mPasswordEt;
    protected EditText mPasswordAgainEt;
    protected Button mSwitchBtn;
    protected Button mConfirmBtn;
    protected ProgressBar mProgressBar;

    final private Runnable ON_SUCCESS=()->{
        startActivity(new Intent(this,MainActivity.class));
        finish();
    };

    final private Consumer<String> ON_FAILURE=(s)->{
        mProgressBar.setVisibility(View.INVISIBLE);
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
        mConfirmBtn.setEnabled(true);
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SkipIfSignedIn();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initViews();
    }

    private void SkipIfSignedIn() {
        if(AuthHelper.isLoggedIn()){
            startActivity(new Intent(this,MainActivity.class));
            finish();
        }
    }

    protected void initViews(){
        mUsernameEt=findViewById(R.id.login_username_et);
        mEmailEt=findViewById(R.id.login_email_et);
        mPasswordEt=findViewById(R.id.login_password_et);
        mPasswordAgainEt=findViewById(R.id.login_password_again_et);
        mSwitchBtn=findViewById(R.id.login_switch_btn);
        mConfirmBtn=findViewById(R.id.login_confirm_btn);
        mProgressBar=findViewById(R.id.login_progress_bar);

        mSwitchBtn.setOnClickListener((View v)->{
            mIsLoginMode=!mIsLoginMode;
            if(mIsLoginMode){
                showUsernameAndPassAgain(false);
                mConfirmBtn.setText("Sign in");
            } else {
                showUsernameAndPassAgain(true);
                mConfirmBtn.setText("Register");
            }
        });

        mConfirmBtn.setOnClickListener((View v)->{
            if(!areAllFieldsFilled()){
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }
            if(mIsLoginMode){
                mConfirmBtn.setEnabled(false);
                mProgressBar.setVisibility(View.VISIBLE);
                login();
            } else {
                if(doPasswordsMatch()){
                    mConfirmBtn.setEnabled(false);
                    mProgressBar.setVisibility(View.VISIBLE);
                    register();
                } else {
                    Toast.makeText(this, "Passwords must match", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    protected void login(){
        String email=mEmailEt.getText().toString();
        String password=mPasswordEt.getText().toString();
        Log.d(TAG, "login: Email="+email+" Pass="+password);
        AuthHelper.login(email,password,ON_SUCCESS,ON_FAILURE);
    }

    protected void register(){
        String username=mUsernameEt.getText().toString();
        String email=mEmailEt.getText().toString();
        String password=mPasswordAgainEt.getText().toString();
        AuthHelper.register(email,password,username,ON_SUCCESS,ON_FAILURE);
    }

    protected boolean areAllFieldsFilled(){
        if(isNullOrEmpty(mEmailEt.getText().toString()))
            return false;
        if(isNullOrEmpty(mPasswordEt.getText().toString()))
            return false;
        if(!mIsLoginMode){
            if(isNullOrEmpty(mUsernameEt.getText().toString()))
                return false;
            return !isNullOrEmpty(mPasswordAgainEt.getText().toString());
        }
        return true;
    }

    protected void showUsernameAndPassAgain(boolean visible){
        int visibility= visible ? View.VISIBLE : View.GONE;
        mPasswordAgainEt.setVisibility(visibility);
        mUsernameEt.setVisibility(visibility);
        mSwitchBtn.setText(visible ? "Switch to login" : "Switch to register");
    }


    private boolean isNullOrEmpty(String s){
        return s==null || s.trim().isEmpty();
    }

    private boolean doPasswordsMatch() {
        return mPasswordEt.getText().toString().equals(mPasswordAgainEt.getText().toString());
    }
}