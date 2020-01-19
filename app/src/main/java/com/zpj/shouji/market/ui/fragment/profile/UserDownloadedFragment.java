package com.zpj.shouji.market.ui.fragment.profile;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.zpj.http.parser.html.nodes.Document;
import com.zpj.http.parser.html.nodes.Element;
import com.zpj.http.parser.html.select.Elements;
import com.zpj.recyclerview.loadmore.LoadMoreWrapper;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.model.UserDownloadedAppInfo;
import com.zpj.shouji.market.ui.adapter.UserDownloadedAdapter;
import com.zpj.shouji.market.ui.fragment.detail.AppDetailFragment;
import com.zpj.shouji.market.ui.fragment.base.BaseFragment;
import com.zpj.shouji.market.utils.HttpUtil;
import com.zpj.shouji.market.utils.ExecutorHelper;

import java.util.ArrayList;
import java.util.List;

public class UserDownloadedFragment extends BaseFragment {

    private static final String DEFAULT_URL = "http://tt.shouji.com.cn/app/view_member_down_xml_v2.jsp?versioncode=198&id=5636865";

    private RecyclerView recyclerView;
    private UserDownloadedAdapter adapter;
    private final List<UserDownloadedAppInfo> appInfoList = new ArrayList<>();
    private String nextUrl = DEFAULT_URL;

    private Runnable getDataRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                Document doc = HttpUtil.getDocument(nextUrl);
                nextUrl = doc.select("nextUrl").get(0).text();
                Elements items = doc.select("item");
                for (Element item : items) {
                    UserDownloadedAppInfo appInfo = new UserDownloadedAppInfo();
                    appInfo.setId(item.selectFirst("id").text());
                    appInfo.setTitle(item.selectFirst("title").text());
                    appInfo.setDownId(item.selectFirst("downid").text());
                    appInfo.setAppType(item.selectFirst("apptype").text());
                    appInfo.setPackageName(item.selectFirst("package").text());
                    appInfo.setAppSize(item.selectFirst("m").text());
                    appInfo.setDownloadTime(item.selectFirst("r").text());
                    appInfoList.add(appInfo);
                }
                post(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_recycler_view;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new UserDownloadedAdapter(appInfoList);
        adapter.setItemClickListener(new UserDownloadedAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(UserDownloadedAdapter.ViewHolder holder, int position, UserDownloadedAppInfo item) {
                _mActivity.start(AppDetailFragment.newInstance(item));
            }
        });
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        LoadMoreWrapper.with(adapter)
                .setLoadMoreEnabled(true)
                .setListener(enabled -> ExecutorHelper.submit(getDataRunnable))
                .into(recyclerView);
    }
}
