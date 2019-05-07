package com.example.kvitter.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.kvitter.R;
import com.example.kvitter.Util.CurrentId;
import com.example.kvitter.Util.CurrentReceipt;
import com.example.kvitter.Util.CurrentUser;

public class NavigationActivity extends AppCompatActivity {
    private DrawerLayout dl;
    private ActionBarDrawerToggle t;
    private NavigationView nv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void runNavigation(int id) {
        dl = (DrawerLayout)findViewById(id);
        t = new ActionBarDrawerToggle(this, dl,R.string.common_open_on_phone, R.string.app_name);
        dl.addDrawerListener(t);
        t.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
        getSupportActionBar().setTitle(Html.fromHtml("<font color=\"black\">" + getString(R.string.title_navigation) + "</font>"));
        nv = (NavigationView)findViewById(R.id.nv);
        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch(id)
                {
                    case R.id.myAccount:
                        Toast.makeText(getApplicationContext(), "Mina sidor",Toast.LENGTH_SHORT).show();
                        Intent myAccount = new Intent(getApplicationContext(), MyAccountActivity.class);
                        startActivity(myAccount);
                        break;
                    case R.id.myReciepts:
                        Toast.makeText(getApplicationContext(), "Mina kvitton",Toast.LENGTH_SHORT).show();
                        Intent myReciept = new Intent(getApplicationContext(), MyReceiptActivity.class);
                        startActivity(myReciept);
                        break;
                    case R.id.newReceipt:
                        Toast.makeText(getApplicationContext(), "Nytt kvitto",Toast.LENGTH_SHORT).show();
                        Intent newReciept = new Intent(getApplicationContext(), AddReceiptActivity.class);
                        startActivity(newReciept);
                        break;
                    case R.id.logOut:
                        Toast.makeText(getApplicationContext(), "Loggar ut..",Toast.LENGTH_SHORT).show();
                        Intent logOut  = new Intent(getApplicationContext(), LoginActivity.class);
                        CurrentId.setUserId(null);
                        CurrentUser.setUser(null);
                        CurrentReceipt.setReceipt(null);
                        startActivity(logOut);
                        break;
                    default:
                        return true;
                }
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(t.onOptionsItemSelected(item)) {
            return true;
        }
            if(item.getItemId()== R.id.loggOut) {
            Toast.makeText(getApplicationContext(), "Loggar ut..", Toast.LENGTH_SHORT).show();
            Intent logOut = new Intent(getApplicationContext(), LoginActivity.class);
            CurrentId.setUserId(null);
            CurrentUser.setUser(null);
            CurrentReceipt.setReceipt(null);
            startActivity(logOut);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.nav_meny,menu);
        return true;
    }
}