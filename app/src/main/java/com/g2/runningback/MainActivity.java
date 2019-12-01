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
import com.g2.runningback.Admin.AdminActivity;
import com.g2.runningback.Login.LoginActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {

    public BottomNavigationView btbar;
    private NavController navCtr;
    Intent intent;
    @Override
    public void onStart() {
        super.onStart();
        // 從偏好設定檔中取得登入狀態來決定是否顯示「登出」
        SharedPreferences pref = getSharedPreferences("preference", MODE_PRIVATE);
        boolean login = pref.getBoolean("isSignIn", false);
        int permission = pref.getInt("job_no",0);
        Log.d(TAG, "Main Activity onStart 的isSignIn前");
        Menu menu = btbar.getMenu();
        if (!login) {
            Log.d(TAG, "Main Activity onResume 一開始檢查未登入");
            intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
        else{
            switch(permission){
                case 2:
                    menu.removeItem(R.id.productFragment);
                    menu.removeItem(R.id.adFragment);
                    menu.removeItem(R.id.saleFragment);
                    navCtr.navigate(R.id.userFragment);
                    break;
                case 3:
                    menu.removeItem(R.id.userFragment);
                    menu.removeItem(R.id.orderFragment);
                    break;
            }
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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
       Intent intent;
        switch (item.getItemId()) {
            case R.id.opLogout:
                SharedPreferences pref = getSharedPreferences("preference",
                        MODE_PRIVATE);
                pref.edit().putBoolean("isSignIn", false).apply();
                Log.d(TAG, "AdminActivity 已登出");
                intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                return true;
            case R.id.opAdmin:
                intent = new Intent(this, AdminActivity.class);
                startActivity(intent);
                return true;
        }
        return true;
    }

    private void holdView(){
        btbar = findViewById(R.id.Main_btbar);
        navCtr = Navigation.findNavController(MainActivity.this,R.id.Main_fg);
        NavigationUI.setupWithNavController(btbar,navCtr);


    }
}
