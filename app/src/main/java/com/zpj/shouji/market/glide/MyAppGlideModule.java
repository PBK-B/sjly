package com.zpj.shouji.market.glide;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.ImageHeaderParser;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.stream.HttpGlideUrlLoader;
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.load.resource.gif.StreamGifDecoder;
import com.bumptech.glide.module.AppGlideModule;
import com.zpj.shouji.market.download.AppDownloadMission;
import com.zpj.shouji.market.glide.apk.ApkModelLoaderFactory;
import com.zpj.shouji.market.glide.combine.CombineImage;
import com.zpj.shouji.market.glide.combine.CombineImageModelLoaderFactory;
import com.zpj.shouji.market.glide.mission.MissionModelLoaderFactory;
import com.zpj.shouji.market.model.InstalledAppInfo;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.List;

@GlideModule
public class MyAppGlideModule extends AppGlideModule {

    @Override
    public void applyOptions(@NonNull Context context, @NonNull GlideBuilder builder) {
        super.applyOptions(context, builder);
        builder.setDefaultTransitionOptions(Drawable.class, DrawableTransitionOptions.withCrossFade(500));
        builder.setDefaultTransitionOptions(Bitmap.class, BitmapTransitionOptions.withCrossFade(500));
//        builder.setDefaultRequestOptions(new RequestOptions().skipMemoryCache(true));

    }

    @Override
    public void registerComponents(@NonNull Context context, @NonNull Glide glide, @NonNull Registry registry) {
        super.registerComponents(context, glide, registry);
        List<ImageHeaderParser> imageHeaderParsers = registry.getImageHeaderParsers();

        // 解决Glide加载格式不完整（丢帧、压缩等）的gif动图出错的问题
        com.spx.gifdecoder.ByteBufferGifDecoder byteBufferGifDecoder =
                new com.spx.gifdecoder.ByteBufferGifDecoder(context, imageHeaderParsers, glide.getBitmapPool(), glide.getArrayPool());
        registry.prepend(Registry.BUCKET_GIF, ByteBuffer.class, GifDrawable.class, byteBufferGifDecoder);
//        registry.append(Registry.BUCKET_GIF,
//                InputStream.class,
//                FrameSequenceDrawable.class, new GifDecoder(glide.getBitmapPool()));

        registry.prepend(Registry.BUCKET_GIF,
                InputStream.class,
                GifDrawable.class, new StreamGifDecoder(imageHeaderParsers, byteBufferGifDecoder, glide.getArrayPool()));

        registry.prepend(InstalledAppInfo.class, InputStream.class, new ApkModelLoaderFactory(context));
        registry.prepend(AppDownloadMission.class, InputStream.class, new MissionModelLoaderFactory(context));
        registry.prepend(CombineImage.class, InputStream.class, new CombineImageModelLoaderFactory(context));
//        registry.prepend(GlideInputStreamData.class, InputStream.class, new CustomModelLoaderFactory<>(context));
//        registry.prepend(GlideBitmapData.class, Bitmap.class, new CustomModelLoaderFactory<>(context));
//        registry.prepend(GlideDrawableData.class, Drawable.class, new CustomModelLoaderFactory<>(context));


//        registry.replace(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory(ProgressManager.getOkHttpClient()));
    }
}
