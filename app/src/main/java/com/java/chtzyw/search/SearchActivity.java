package com.java.chtzyw.search;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.java.chtzyw.R;
import com.java.chtzyw.data.News;
import com.java.chtzyw.data.NewsHandler;
import com.java.chtzyw.data.ResultListener;
import com.java.chtzyw.main.MainActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import java.util.LinkedList;


public class SearchActivity extends AppCompatActivity {

    private HistoryFragment mHistory;
    private ResultFragment mResult;
    private int fragId = 0;                     //状态

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = findViewById(R.id.toolbar_search);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        switchToHistory();
    }

    @Override
    protected void onDestroy() {                            //退出前
        NewsHandler.getHandler().sendHistorySaveRequest();  //保存历史记录
        super.onDestroy();
    }

    // 创建右上角的菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_toolbar, menu);
        MenuItem item=menu.findItem(R.id.action_search);
        SearchView searchView=(SearchView)item.getActionView();
        searchView.setIconifiedByDefault(false);
        searchView.setQueryHint("搜索");
//        searchView.setMaxWidth(900);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                onSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

    public void onSearch(String query) {
        NewsHandler.getHandler().sendHistoryAddRequest(query);
        NewsHandler.getHandler().sendSearchResultClearRequest();
        if(fragId==0){
            fragId=1;
            switchToResult();
        }
        NewsHandler.getHandler().sendSearchRequest(query, 15, new ResultListener() {
            @Override
            public void onSuccess(LinkedList<News> newsList, int newsNum) {
                runOnUiThread(() -> {
                    mResult.refreshView();
                    Toast.makeText(SearchActivity.this, "已刷新" + newsNum + "条新闻", Toast.LENGTH_SHORT).show();
                });
            }
            @Override
            public void onFailure(int code) {
                runOnUiThread(() -> {
                    Toast.makeText(SearchActivity.this, "搜索失败", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    // 右上角菜单的响应事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id==android.R.id.home || id == R.id.action_cancel) {

            if(fragId==0){
                finish();
            }
            else if(fragId==1){
                fragId=0;
                switchToHistory();
                NewsHandler.getHandler().sendSearchResultClearRequest();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void switchToHistory(){
        if (mHistory == null){
            mHistory = HistoryFragment.newInstance();
            mHistory.setOnSearchListen((query) -> onSearch(query));
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_content, mHistory).commit();
    }
    private void switchToResult(){
        if (mResult == null)
            mResult = ResultFragment.newInstance();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_content, mResult).commit();
    }
}
