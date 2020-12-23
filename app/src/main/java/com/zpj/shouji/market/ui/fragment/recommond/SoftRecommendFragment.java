package com.zpj.shouji.market.ui.fragment.recommond;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.zpj.toast.ZToast;
import com.zpj.http.ZHttp;
import com.zpj.http.parser.html.nodes.Element;
import com.zpj.http.parser.html.select.Elements;
import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.recyclerview.MultiData;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.api.PreloadApi;
import com.zpj.shouji.market.model.AppInfo;
import com.zpj.shouji.market.ui.fragment.ToolBarAppListFragment;
import com.zpj.shouji.market.ui.fragment.collection.CollectionRecommendListFragment;
import com.zpj.shouji.market.ui.fragment.homepage.multi.AppInfoMultiData;
import com.zpj.shouji.market.ui.fragment.homepage.multi.CollectionMultiData;
import com.zpj.shouji.market.ui.fragment.homepage.multi.TutorialMultiData;

import java.util.ArrayList;
import java.util.List;

public class SoftRecommendFragment extends BaseRecommendFragment implements View.OnClickListener {

    private static final String TAG = "RecommendFragment";
    private static final String[] TITLES = {"软件新闻", "软件评测", "软件教程", "软件周刊"};

    @Override
    protected int getHeaderLayoutId() {
        return R.layout.layout_header_recommend_soft;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);
//        view.findViewById(R.id.tv_necessary).setOnClickListener(this);
//        view.findViewById(R.id.tv_collection).setOnClickListener(this);
//        view.findViewById(R.id.tv_rank).setOnClickListener(this);
//        view.findViewById(R.id.tv_classification).setOnClickListener(this);
    }

    @Override
    public void toolbarLeftTextView(@NonNull TextView view) {
        super.toolbarLeftTextView(view);
        view.setText(R.string.title_soft);
    }

    @Override
    public void initHeader(EasyViewHolder holder) {
        holder.setOnClickListener(R.id.tv_necessary, this);
        holder.setOnClickListener(R.id.tv_collection, this);
        holder.setOnClickListener(R.id.tv_rank, this);
        holder.setOnClickListener(R.id.tv_classification, this);

        ZHttp.get("https://soft.shouji.com.cn")
                .toHtml()
                .onSuccess(data -> {
                    List<AppInfo> list = new ArrayList<>();
                    Elements recommends = data.selectFirst("body > div:nth-child(5) > div.boutique.fl > ul").select("li");
                    for (Element recommend : recommends) {
                        Element a = recommend.selectFirst("a");
                        AppInfo info = new AppInfo();
                        info.setAppId(a.attr("href")
                                .replace("/down/", "")
                                .replace(".html", ""));
                        info.setAppIcon(a.selectFirst("img").attr("src"));
                        info.setAppTitle(a.attr("title"));
                        info.setAppType("soft");
                        info.setAppSize(a.selectFirst("p").text().replace("大小：", ""));
                        list.add(info);
                    }
                    initData(list);
                })
                .onError(throwable -> {
                    throwable.printStackTrace();
                    ZToast.error("出错了！" + throwable.getMessage());
                })
                .subscribe();

    }

    @Override
    protected void initMultiData(List<MultiData> list) {
        list.add(new AppInfoMultiData("最近更新") {
            @Override
            public void onHeaderClick() {
                ToolBarAppListFragment.startUpdateSoftList();
            }

            @Override
            public PreloadApi getKey() {
                return PreloadApi.UPDATE_SOFT;
            }
        });

        list.add(new CollectionMultiData());

        list.add(new AppInfoMultiData("常用应用") {
            @Override
            public void onHeaderClick() {
                ToolBarAppListFragment.startRecommendSoftList();
            }

            @Override
            public PreloadApi getKey() {
                return PreloadApi.HOME_SOFT;
            }
        });

        for (int i = 0; i < TITLES.length; i++) {
            list.add(new TutorialMultiData(TITLES[i], "soft", i + 1));
        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_necessary:
                ToolBarAppListFragment.startNecessarySoftList();
                break;
            case R.id.tv_collection:
                CollectionRecommendListFragment.start();
                break;
            case R.id.tv_rank:
                AppRankFragment.startSoft();
                break;
            case R.id.tv_classification:
                AppClassificationFragment.startSoft();
                break;
        }
    }


}
