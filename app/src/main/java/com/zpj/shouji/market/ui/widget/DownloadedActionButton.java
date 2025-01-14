package com.zpj.shouji.market.ui.widget;

import android.content.Context;
import androidx.appcompat.widget.AppCompatTextView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.zpj.rxbus.RxBus;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.download.AppDownloadMission;
import com.zpj.shouji.market.utils.EventBus;
import com.zpj.toast.ZToast;
import com.zpj.utils.AppUtils;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class DownloadedActionButton extends AppCompatTextView
        implements View.OnClickListener {

    private static final String TAG = "DownloadButton";

    private int textId;

    private AppDownloadMission mission;

    public DownloadedActionButton(Context context) {
        this(context, null);
    }

    public DownloadedActionButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DownloadedActionButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void bindMission(AppDownloadMission mission) {
        this.mission = mission;
        boolean isFinished = mission.isFinished();
        if (isFinished) {
            setVisibility(View.VISIBLE);

//            Binder binder = new Binder(mission);
//            binder.run();
            init(mission);

//            setText(textId);
//            setOnClickListener(this);
        } else {
            setVisibility(View.GONE);
            setOnClickListener(null);
            this.mission = null;
        }

    }

    @Override
    public void onClick(View v) {
        if (mission == null) {
            return;
        }
        if (mission.isInstalled()) {
            AppUtils.runApp(v.getContext(), mission.getPackageName());
        } else if (mission.getFile().exists()) {
//            if (mission.isInstalled()) {
//                String apkVersion = AppUtils.getApkVersionName(getContext(), mission.getFilePath());
//                String appVersion = AppUtils.getAppVersionName(getContext(), mission.getPackageName());
//                if (TextUtils.equals(apkVersion, appVersion)) {
//                    AppUtils.runApp(v.getContext(), mission.getPackageName());
//                } else {
//                    mission.install();
//                }
//            } else {
//                mission.install();
//            }
            mission.install();
        } else {
            ZToast.warning(R.string.text_retry);
            mission.restart();
        }

//        switch (textId) {
//            case R.string.text_upgrade:
//            case R.string.text_install:
//                mission.install();
//                break;
//            case R.string.text_open:
//                AppUtils.runApp(v.getContext(), mission.getPackageName());
//                break;
//            case R.string.text_retry:
//                ZToast.warning(R.string.text_retry);
//                break;
//        }
    }

    private void init(final AppDownloadMission mission) {
        Observable.create(
                (ObservableOnSubscribe<Integer>) emitter -> {
                    final int textId;
                    if (mission.isInstalled()) {
                        textId = R.string.text_open;
                    } else if (mission.getFile().exists()) {
                        if (mission.isUpgrade()) {
                            textId = R.string.text_upgrade;
                        } else {
                            textId = R.string.text_install;
                        }
                    } else {
                        textId = R.string.text_retry;
                    }
                    emitter.onNext(textId);
                    emitter.onComplete();
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(integer -> {
                    if (mission == DownloadedActionButton.this.mission) {
                        DownloadedActionButton.this.textId = integer;
                        setText(integer);
                        setOnClickListener(DownloadedActionButton.this);
                    }
                })
                .subscribe();
    }

}
