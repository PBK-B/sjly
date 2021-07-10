package com.zpj.shouji.market.glide.combine;


import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.signature.ObjectKey;

import java.io.InputStream;

public class CombineImageModelLoader implements ModelLoader<CombineImage, InputStream> {

    private Context context;

    public CombineImageModelLoader(Context context){
        this.context = context;
    }

    @Nullable
    @Override
    public LoadData<InputStream> buildLoadData(@NonNull CombineImage combineImage, int width, int height, @NonNull Options options) {
        return new LoadData<>(new ObjectKey(combineImage.getKey()), new CombineImageFetcher(context, combineImage));
    }

    @Override
    public boolean handles(@NonNull CombineImage installedAppInfo) {
        return true;
    }
}