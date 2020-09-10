package com.zpj.shouji.market;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.ViewTarget;
import com.felix.atoast.library.AToast;
import com.lqr.emoji.IImageLoader;
import com.lqr.emoji.LQREmotionKit;
import com.maning.librarycrashmonitor.MCrashMonitor;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.zpj.downloader.ZDownloader;
import com.zpj.downloader.config.DownloaderConfig;
import com.zpj.shouji.market.download.AppDownloadMission;
import com.zpj.utils.PrefsHelper;

import io.reactivex.functions.Consumer;
import io.reactivex.plugins.RxJavaPlugins;
import per.goweii.burred.Blurred;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
//        DoraemonKit.install(this);

        long start = System.currentTimeMillis();

        //        MFileUtils.setCrashLogPath(Environment.getExternalStorageDirectory().getPath() + "/sjly/Crash/Log");
//        MFileUtils.setCrashPicPath(Environment.getExternalStorageDirectory().getPath() + "/sjly/Crash/ScreenShoot");
        MCrashMonitor.init(this, true, file -> {
//                MCrashMonitor.startCrashShowPage(getContext());
        });
        FlowManager.init(this);

//        UMConfigure.init(this,"5f53cf523739314483bc4020"
//                ,"umeng",UMConfigure.DEVICE_TYPE_PHONE,"");

        AToast.onInit(this);
//        ZDownloader.init(this);
        ZDownloader.init(
                DownloaderConfig.with(this)
                        .setUserAgent("Sjly(3.0)")
                        .setDownloadPath(
                                PrefsHelper.with()
                                        .getString(
                                                "download_directory",
                                                Environment.getExternalStorageDirectory().getAbsolutePath()
                                                        + "/sjly/ProDownload/")),
                AppDownloadMission.class
        );
        LQREmotionKit.init(this, new IImageLoader() {
            @Override
            public void displayImage(Context context, String path, ImageView imageView) {
                Glide.with(context).load(path).centerCrop().into(imageView);
            }
        });
        Blurred.init(this);

        ViewTarget.setTagId(R.id.glide_tag_id);
//        Glide.with(getApplicationContext())
//                .setDefaultRequestOptions(GlideUtils.REQUEST_OPTIONS).applyDefaultRequestOptions()

        RxJavaPlugins.setErrorHandler(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                throwable.printStackTrace();
            }
        });

        Log.d("AppAppApp", "duration=" + (System.currentTimeMillis() - start));
    }

}
