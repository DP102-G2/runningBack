package com.g2.runningback;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView btbar;
    Intent intent;

    @Override
    public void onStart() {
        super.onStart();
        // 從偏好設定檔中取得登入狀態來決定是否顯示「登出」
        SharedPreferences pref = getSharedPreferences("preference", MODE_PRIVATE);
        boolean login = pref.getBoolean("isSignIn", false);
        Log.d(TAG, "Main Activity onStart 的isSignIn前");
        if (!login) {
            Log.d(TAG, "Main Activity onResume 一開始檢查未登入");
            intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        holdView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.option_menu, menu);
        menu.removeItem(R.id.opMain);
        menu.removeItem(R.id.opLogout);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
       Intent intent;
        switch (item.getItemId()) {
            case R.id.opAdmin:
                intent = new Intent(this, AdminActivity.class);
                startActivity(intent);
                return true;
        }
        return true;
    }

    private void holdView(){
        btbar = findViewById(R.id.Main_btbar);
        NavController navCtr = Navigation.findNavController(MainActivity.this,R.id.Main_fg);
        NavigationUI.setupWithNavController(btbar,navCtr);

    }




}
