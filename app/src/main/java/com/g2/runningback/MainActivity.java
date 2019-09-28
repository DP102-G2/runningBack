package com.g2.runningback;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView btbar;

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
            case R.id.opAdmin:
                intent = new Intent(this, AdminActivity.class);
                startActivity(intent);
                this.finish();
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
