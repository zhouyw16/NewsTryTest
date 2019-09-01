package com.example.newstrytest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import okhttp3.Call;
import okhttp3.Response;

public class JsonTestActivity extends AppCompatActivity {
    public static final String TAG="JsonTestActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_json_test);

        Button sendRequest=(Button)findViewById(R.id.send_request);
        sendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getId() == R.id.send_request) {
                    String address="https://api2.newsminer.net/svc/news/queryNewsList?size=15&startDate=2019-07-01&endDate=2019-07-03&words=特朗普&categories=科技";
                    HttpUtil.sendOkHttpRequest(address, new okhttp3.Callback() {
                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                            String responseData = response.body().string();
                            fileSave(responseData);
                            parseJSONWithGSON(responseData);
                        }

                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                            Log.d(TAG, "onFailure: http");
                        }
                    });
                }
            }
        });
    }

    private void fileSave(String inputText){
        FileOutputStream out=null;
        BufferedWriter writer=null;
        try {
            out=openFileOutput("data", Context.MODE_PRIVATE);
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
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    private void parseJSONWithGSON(String jsonData){
        Gson gson=new Gson();
        NewsJson newsJson=gson.fromJson(jsonData,NewsJson.class);
        Log.d(TAG, "pageSize: "+newsJson.getPageSize());
        Log.d(TAG, "total: "+newsJson.getTotal());
        for(News news:newsJson.getData()) {
            Log.d(TAG, "title: " + news.getTitle());
            Log.d(TAG, "content: " + news.getContent());
        }
    }
}
