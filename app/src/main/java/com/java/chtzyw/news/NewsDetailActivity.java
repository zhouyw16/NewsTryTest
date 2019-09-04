package com.java.chtzyw.news;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.java.chtzyw.R;
import com.java.chtzyw.data.*;

import cn.sharesdk.onekeyshare.OnekeyShare;

public class NewsDetailActivity extends AppCompatActivity {

    private News news;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        news=(News)getIntent().getSerializableExtra("news_detail");
        TextView textTitle=(TextView)findViewById(R.id.text_title);
        TextView textIntro=(TextView)findViewById(R.id.text_intro);
        TextView textContent=(TextView)findViewById(R.id.text_content);
        textTitle.setText(news.getTitle());
        textIntro.setText(news.getPublisher()+"\t"+news.getPublishTime());
        textContent.setText(news.getContent());
        ImageView imageStart=(ImageView)findViewById(R.id.image_start);
        ImageView imageEnd=(ImageView)findViewById(R.id.image_end);
        String[] images=news.getImages();
        if(images==null){
            imageStart.setVisibility(View.GONE);
            imageEnd.setVisibility(View.GONE);
        }
        else if(images.length==1) {
            imageStart.setVisibility(View.GONE);
            imageEnd.setVisibility(View.VISIBLE);
            Glide.with(this).load(images[0]).apply(ImageOption.fitImgOption()).into(imageEnd);
        }
        else{
            imageStart.setVisibility(View.VISIBLE);
            imageEnd.setVisibility(View.VISIBLE);
            Glide.with(this).load(images[0]).apply(ImageOption.fitImgOption()).into(imageStart);
            Glide.with(this).load(images[1]).apply(ImageOption.fitImgOption()).into(imageEnd);
        }
    }

    /*显示右上方三点按钮*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /*三点按钮点击事件*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_favor) {
            NewsHandler.getHandler().sendFavorSaveRequest(news);
            Toast.makeText(this,"收藏成功", Toast.LENGTH_SHORT).show();
            return true;
        }
        else if(id==R.id.action_share){
            OnekeyShare oks = new OnekeyShare();
            // title标题，微信、QQ和QQ空间等平台使用
            oks.setTitle(news.getTitle());
            // titleUrl QQ和QQ空间跳转链接
            oks.setTitleUrl("http://sharesdk.cn");
            // text是分享文本，所有平台都需要这个字段
            oks.setText("我是分享文本");
            // imagePath是图片的本地路径，确保SDcard下面存在此张图片
            oks.setImagePath("/sdcard/test.jpg");
//            oks.setImagePath("/sdcard/NewsAppPicture/1.png");
            // url在微信、Facebook等平台中使用
            oks.setUrl("http://sharesdk.cn");
            // 启动分享GUI
            oks.show(this);
        }
        return true;
    }
}
