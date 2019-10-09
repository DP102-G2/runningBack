package com.g2.runningback;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.g2.runningback.Common.Common;
import com.g2.runningback.Common.CommonTask;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import static android.content.ContentValues.TAG;
public class LoginActivity extends AppCompatActivity {

    private EditText etId,etPassword;
    private Button loginbtLogin;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle("登入頁面");
        etId = findViewById(R.id.etId);
        etPassword = findViewById(R.id.etPassword);
        loginbtLogin = findViewById(R.id.loginbtLogin);
        loginbtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = etId.getText().toString();
                String password = etPassword.getText().toString();

                if(Common.networkConnected(LoginActivity.this)){
                    String url = Common.URL_SERVER+"LoginServlet";
                    Login login = new Login(id,password);
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("action","getLogin");
                    jsonObject.addProperty("login",new Gson().toJson(login));

                    if (password.length() <=0 || id.length() <=0) {
                        Common.showToast(LoginActivity.this, "Must not blank ");
                        return;
                    }

                    Login mLogin = null;
                    try{
                        String result = new CommonTask(url, jsonObject.toString()).execute().get();
                        mLogin = new Gson().fromJson(result, Login.class);
                    } catch(Exception e){
                        Log.e(TAG, e.getMessage());
                    }

                    if(mLogin ==null){
                        Common.showToast(LoginActivity.this, "Login Failed");
                    }
                    else{
                        Common.showToast(LoginActivity.this, "Login Success");

                        SharedPreferences pref = getSharedPreferences("preference",
                                MODE_PRIVATE);

                        pref.edit()
                                .putBoolean("isSignIn", true)
                                .apply();

                        Log.d(TAG, "登入成功，此頁消失");
                        LoginActivity.this.finish();// finish() 讓視窗（頁面）消失

                    }
                }
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
        return super.onCreateView(name, context, attrs);
    }


}