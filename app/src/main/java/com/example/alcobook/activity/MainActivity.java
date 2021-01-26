package com.example.alcobook.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.alcobook.R;
import com.example.alcobook.model.helper.AuthHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    NavController mNavCtrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavCtrl= Navigation.findNavController(this,R.id.main_nav_host);

        BottomNavigationView bottomNavigationView=findViewById(R.id.main_bottom_nav);
        NavigationUI.setupWithNavController(bottomNavigationView,mNavCtrl);
        NavigationUI.setupActionBarWithNavController(this,mNavCtrl);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.default_actionbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home){
            mNavCtrl.navigateUp();
            return true;
        } else if (item.getItemId()==R.id.default_actionbar_signout) {
            signout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void signout(){
        AuthHelper.signout();
        startActivity(new Intent(this,LoginActivity.class));
        finish();
    }

    public void setActionBarTitle(String title){
        if(getSupportActionBar()!=null){
            getSupportActionBar().setTitle(title);
        }
    }
}