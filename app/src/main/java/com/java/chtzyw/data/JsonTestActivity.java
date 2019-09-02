package com.java.chtzyw.data;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.java.chtzyw.R;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import okhttp3.Call;
import okhttp3.Response;

public class JsonTestActivity extends AppCompatActivity implements View.OnClickListener{
    public static final String TAG="JsonTestActivity";
    TextView article;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_json_test);

        Button sendRequest=(Button)findViewById(R.id.send_request);
        Button readJson=(Button)findViewById(R.id.read_json);
        article=(TextView)findViewById(R.id.article_text);
        sendRequest.setOnClickListener(this);
        readJson.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.send_request) {
            String address="https://api2.newsminer.net/svc/news/queryNewsList?size=15&startDate=2019-07-01&endDate=2019-07-03&words=特朗普&categories=科技";
            HttpUtil.sendOkHttpRequest(address, new okhttp3.Callback() {
                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    String responseData = response.body().string();
                    parseNewsJson(responseData);
                }
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                }
            });
        }
        else if(view.getId()==R.id.read_json){
            showArticle(fileLoad("201907030137e6a3d6e14b7642539bf3df0daf3ba2c4"));
//            imageView=(ImageView)findViewById(R.id.image_view);
//            imageView.setImageURI(Uri.fromFile(new File("C:/Users/lenovo/Desktop/img.jpeg")));
        }
    }

    private void showArticle(final String text){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                article.setText(text);
            }
        });
    }


    private void fileSave(String filename,String inputText){
        FileOutputStream out=null;
        BufferedWriter writer=null;
        try {
            out=openFileOutput(filename, Context.MODE_PRIVATE);
            writer=new BufferedWriter(new OutputStreamWriter(out));
            writer.write(inputText);
        }
        catch (IOException e){
            e.printStackTrace();
        }
        finally {
            try{
                if(writer!=null)
                    writer.close();
                Log.d(TAG, filename);
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    private String fileLoad(String filename){
        FileInputStream in=null;
        BufferedReader reader=null;
        StringBuilder content=new StringBuilder();
        try{
            in=openFileInput(filename);
            reader=new BufferedReader(new InputStreamReader(in));
            String line="";
            while((line=reader.readLine())!=null)
                content.append(line);
        }
        catch (IOException e){
            e.printStackTrace();
        }
        finally {
            try{
                if(reader!=null)
                    reader.close();
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
        return content.toString();
    }

    /*输入Json数据，解析NewsJson类*/
    private NewsJson parseNewsJson(String jsonData){
        Gson gson=new Gson();
        NewsJson newsJson=gson.fromJson(jsonData, NewsJson.class);
        Log.d(TAG, "pageSize: "+newsJson.getPageSize());
        Log.d(TAG, "total: "+newsJson.getTotal());
        /*存储新闻*/
        for(News news:newsJson.getData()) {
            Gson jsonFile = new Gson();
            String jsonString = jsonFile.toJson(news);
            fileSave(news.getNewsID(), jsonString);
        }
        return newsJson;
    }




    /*输入新闻ID，返回新闻类*/
    private News parseNewsFile(String filename){
        Gson gson=new Gson();
        News news=gson.fromJson(fileLoad(filename), News.class);
        return news;
    }
}
