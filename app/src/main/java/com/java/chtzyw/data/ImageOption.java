package com.java.chtzyw.data;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.java.chtzyw.R;

public class ImageOption {
    private static RequestOptions miniOption = new RequestOptions()
            .placeholder(R.drawable.sample_pic)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .override(320, 180);
    public static RequestOptions miniImgOption() {return miniOption;}
}