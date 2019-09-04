package com.java.chtzyw.main;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;
import com.java.chtzyw.R;
import com.java.chtzyw.news.NewsFragment;

// 主活动，管理导航栏和工具栏的响应事件
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private int currNavigationId = R.id.nav_home;   // 当前导航栏选中页面的id
    private Toolbar mToolBar;                       // 工具栏
    private Fragment mNews, mFavourite, mSetting;   // 导航栏的各种不同页面
    private NavigationView mNavigationView;         // 导航栏

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 导航栏布局自动生成的代码
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

        // 设置导航栏切换的回调函数
        mNavigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 回到主页面时切换到当前的页面
        switchNavigation(currNavigationId);
    }

    // 返回键回退导航栏
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    // 创建右上角的菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    // 右上角菜单的响应事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            Toast.makeText(this, "你点击了右上角的菜单！\n但是现在还没有实现功能！",
//                    Toast.LENGTH_LONG).show();
//        }

        return super.onOptionsItemSelected(item);
    }

    // 导航栏的响应事件
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // 切换到响应页面
        int id = item.getItemId();
        switchNavigation(id);

        // 回退导航栏
//        DrawerLayout drawer = findViewById(R.id.drawer_layout);
//        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    // 切换页面的一些辅助函数
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
        if (mSetting == null)
            mSetting = SettingFragment.newInstance();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_content, mSetting).commit();
    }

    private void switchToFavourite() {
        switchTo(R.id.nav_favourite, "收藏");
        if (mFavourite == null)
            mFavourite = FavouriteFragment.newInstance("favourite", "demo");
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_content, mFavourite).commit();
    }

    private void switchTo(int id, String title) {
        mToolBar.setTitle(title);
        mNavigationView.setCheckedItem(id);
    }

}
