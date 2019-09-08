package com.java.chtzyw.news;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.bumptech.glide.Glide;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.java.chtzyw.R;
import com.java.chtzyw.data.*;

import java.util.regex.Pattern;

import cn.jzvd.JzvdStd;

public class NewsDetailActivity extends AppCompatActivity {

    private News news;
    private boolean isFavoured;
    private Menu mMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();

        news=(News)getIntent().getSerializableExtra("news_detail");
        setNews();
        isFavoured = NewsHandler.getHandler().sendIsFavouredRequest(news);
    }

    private void setNews() {
        System.out.println(news.getVideo());

        TextView textTitle=findViewById(R.id.text_title);
        TextView textIntro=findViewById(R.id.text_intro);
        TextView textContent=findViewById(R.id.text_content);
        textTitle.setText(news.getTitle());
        textIntro.setText(news.getPublisher()+" "+news.getPublishTime());

        StringBuilder content = new StringBuilder();
        for (String line : news.getContent().split("\n")) {
            if (line.length() == 0 || Pattern.matches("\\s+", line)) continue;
            if (news.getPublisher().equals("搜狐新闻")) {
                if (line.startsWith("原标题")) continue;
                if (line.startsWith("责任编辑")) continue;
                if (line.startsWith("返回搜狐，查看更多")) continue;
            }
            content.append(line).append("\n\n");
        }

        textContent.setText(content);
        ImageView imageStart= findViewById(R.id.image_start);
        ImageView imageEnd= findViewById(R.id.image_end);
        JzvdStd videoView = findViewById(R.id.news_video);
        String[] images=news.getImages();
        if (ImageOption.noImage) {
            videoView.setVisibility(View.GONE);
            imageStart.setVisibility(View.GONE);
            imageEnd.setVisibility(View.GONE);
        }
        else if (!news.getVideo().isEmpty()) {
            videoView.setVisibility(View.VISIBLE);
            imageStart.setVisibility(View.GONE);
            imageEnd.setVisibility(View.GONE);
            videoView.setUp(news.getVideo(), news.getTitle());
        }
        else if(images==null){
            imageStart.setVisibility(View.GONE);
            imageEnd.setVisibility(View.GONE);
        }
        else if(images.length==1) {
            imageStart.setVisibility(View.VISIBLE);
            imageEnd.setVisibility(View.GONE);
            Glide.with(this).load(images[0]).apply(ImageOption.fitImgOption()).into(imageStart);
        }
        else{
            imageStart.setVisibility(View.VISIBLE);
            imageEnd.setVisibility(View.VISIBLE);
            Glide.with(this).load(images[0]).apply(ImageOption.fitImgOption()).into(imageStart);
            Glide.with(this).load(images[1]).apply(ImageOption.fitImgOption()).into(imageEnd);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_toolbar, menu);
        if (isFavoured) {
            menu.findItem(R.id.action_favor).setIcon(getDrawable(R.drawable.ic_favorite));
        }
        else {
            menu.findItem(R.id.action_favor).setIcon(getDrawable(R.drawable.ic_favorite_border));
        }
        mMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id==android.R.id.home){
            finish();
        }
        else if (id == R.id.action_favor) {
            isFavoured = !isFavoured;
            if (isFavoured) {
                NewsHandler.getHandler().sendFavorSaveRequest(news);
                Toast.makeText(this, "收藏成功", Toast.LENGTH_SHORT).show();
                mMenu.findItem(R.id.action_favor).setIcon(getDrawable(R.drawable.ic_favorite));
            }
            else {
                NewsHandler.getHandler().sendFavorDeleteRequest(news);
                Toast.makeText(this, "取消收藏", Toast.LENGTH_SHORT).show();
                mMenu.findItem(R.id.action_favor).setIcon(getDrawable(R.drawable.ic_favorite_border));
            }
        }
        else if (id==R.id.action_share) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            String imgPath=news.getCover();
            if (imgPath == null || imgPath.equals("")) {
                intent.setType("text/plain"); // 纯文本
            }
            else {
                intent.setType("image/jpg");
                Uri uri=NewsHandler.getHandler().sendUrl2UriRequest(news);
                intent.putExtra(Intent.EXTRA_STREAM, uri);
            }
            intent.putExtra(Intent.EXTRA_SUBJECT, "Share News");
            intent.putExtra(Intent.EXTRA_TEXT,
                    news.getTitle()+"\n\n"+news.getPublisher()+"\n\n"+news.getContent());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(Intent.createChooser(intent, "ShareActivity"));
            return true;
            }
        return true;
    }
}
