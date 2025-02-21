package com.zpj.shouji.market.glide.apk;


import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.signature.ObjectKey;
import com.zpj.shouji.market.model.InstalledAppInfo;

import java.io.InputStream;

public class ApkIconModelLoader implements ModelLoader<InstalledAppInfo, InputStream> {

    private final Context context;

    public ApkIconModelLoader(Context context){
        this.context = context;
    }

    @Nullable
    @Override
    public LoadData<InputStream> buildLoadData(@NonNull InstalledAppInfo installedAppInfo, int width, int height, @NonNull Options options) {
        return new LoadData<>(new ObjectKey(installedAppInfo.getPackageName()), new ApkIconFetcher(context,installedAppInfo));
    }

    @Override
    public boolean handles(@NonNull InstalledAppInfo installedAppInfo) {
        return true;
    }
}