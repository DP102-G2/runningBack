package com.g2.runningback.Admin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.g2.runningback.LoginActivity;
import com.g2.runningback.MainActivity;
import com.g2.runningback.R;

import static android.content.ContentValues.TAG;

public class AdminActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.option_menu, menu);
        menu.removeItem(R.id.opAdmin);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.opMain:
                intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                this.finish();
                return true;

            case R.id.opLogout:
                SharedPreferences pref = getSharedPreferences("preference",
                        MODE_PRIVATE);
                pref.edit().putBoolean("isSignIn", false).apply();
                Log.d(TAG, "AdminActivity 已登出");
                intent = new Intent(AdminActivity.this, LoginActivity.class);
                startActivity(intent);
                return true;
        }
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        // 從偏好設定檔中取得登入狀態來決定是否顯示「登出」
        SharedPreferences pref = getSharedPreferences("preference", MODE_PRIVATE);
        boolean login = pref.getBoolean("isSignIn", false);
        if (!login) {
            Log.d(TAG, "AdminActivity onResume 一開始檢查未登入");
            Intent intent = new Intent(AdminActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    }


}
