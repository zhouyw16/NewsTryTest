package com.java.chtzyw.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.java.chtzyw.R;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private int currNavigationId = R.id.nav_home;
    private Toolbar mToolBar;
    private Fragment mNews, mFavourite, mSetting;
    private NavigationView mNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToolBar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolBar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        mNavigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mToolBar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        mNavigationView.setNavigationItemSelectedListener(this);


    }

    @Override
    protected void onResume() {
        super.onResume();
        switchNavigation(currNavigationId);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switchNavigation(id);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void switchNavigation(int id) {
        currNavigationId = id;
        switch (id) {
            case R.id.nav_home:       switchToNews();       break;
            case R.id.nav_favourite:  switchToFavourite();  break;
            case R.id.nav_setting:    switchToSetting();    break;
            default:    break;
        }
    }

    private void switchToNews() {
        switchTo(R.id.nav_home, "新闻");
        if (mNews == null)
            mNews = NewsFragment.newInstance();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_content, mNews).commit();
    }

    private void switchToSetting() {
        switchTo(R.id.nav_setting, "设置");
    }

    private void switchToFavourite() {
        switchTo(R.id.nav_favourite, "收藏");
    }

    private void switchTo(int id, String title) {
        mToolBar.setTitle(title);
        mNavigationView.setCheckedItem(id);
    }

}
