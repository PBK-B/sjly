package com.zpj.shouji.market.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.zpj.http.core.IHttp;
import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.api.HttpApi;
import com.zpj.shouji.market.model.DiscoverInfo;
import com.zpj.shouji.market.ui.adapter.DiscoverBinder;
import com.zpj.shouji.market.ui.fragment.base.BaseSwipeBackFragment;
import com.zpj.shouji.market.ui.widget.flowlayout.FlowLayout;
import com.zpj.toast.ZToast;

import java.util.ArrayList;
import java.util.List;

public class ReportFragment extends BaseSwipeBackFragment {

    public static ReportFragment newInstance(DiscoverInfo discoverInfo) {
        ReportFragment fragment = new ReportFragment();
        fragment.discoverInfo = discoverInfo;
        return fragment;
    }

    public static void start(DiscoverInfo discoverInfo) {
        start(newInstance(discoverInfo));
    }

    private DiscoverInfo discoverInfo;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_report;
    }

    @Override
    public CharSequence getToolbarTitle(Context context) {
        return "举报动态";
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {


        View themeLayout = findViewById(R.id.layout_theme);
        EasyViewHolder holder = new EasyViewHolder(themeLayout);
        DiscoverBinder binder = new DiscoverBinder(false, false);
        List<DiscoverInfo> discoverInfoList = new ArrayList<>();
        discoverInfoList.add(discoverInfo);
        binder.onBindViewHolder(holder, discoverInfoList, 0, new ArrayList<>(0));


        FlowLayout flowLayout = findViewById(R.id.fl_tags);
        flowLayout.setMultiSelectMode(true);
        String[] tags = getResources().getStringArray(R.array.default_report_reason);
        flowLayout.addItems(tags);

        AppCompatEditText etContent = findViewById(R.id.et_content);

        TextView tvSubmit = findViewById(R.id.tv_submit);
        tvSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flowLayout.getSelectedItem().isEmpty() && TextUtils.isEmpty(etContent.getText())) {
                    ZToast.warning("请选择举报原因！");
                    return;
                }
                String reasons = "";
                for (String reason : flowLayout.getSelectedItem()) {
                    if (!TextUtils.isEmpty(reasons)) {
                        reasons += ",--";
                    }
                    reasons += reason;
                }
                if (!TextUtils.isEmpty(etContent.getText())) {
                    if (!TextUtils.isEmpty(reasons)) {
                        reasons += ",--";
                    }
                    reasons += etContent.getText();
                }
//                ZToast.normal("reasons=" + reasons);
                HttpApi.reportApi(discoverInfo.getId(), discoverInfo.getContentType(), reasons)
                        .bindToLife(ReportFragment.this)
                        .onSuccess(data -> {
                            String info = data.selectFirst("info").text();
                            if ("success".equals(data.selectFirst("result").text())) {
                                ZToast.success(info);
                                pop();
                            } else {
                                ZToast.error(info);
                            }
                        })
                        .onError(new IHttp.OnErrorListener() {
                            @Override
                            public void onError(Throwable throwable) {
                                ZToast.error(throwable.getMessage());
                            }
                        })
                        .subscribe();
            }
        });

//        ElasticScrollView scrollView = findViewById(R.id.scroll_view);
//        KeyboardUtils.registerSoftInputChangedListener(_mActivity, view, height -> {
//            tvSubmit.setTranslationY(-height);
//            scrollView.setTranslationY(-height);
//        });


    }

}
