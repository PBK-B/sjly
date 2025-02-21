package com.zpj.shouji.market.ui.fragment.profile;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;

import com.zpj.http.parser.html.nodes.Element;
import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.constant.Keys;
import com.zpj.shouji.market.model.UserDownloadedAppInfo;
import com.zpj.shouji.market.ui.fragment.base.NextUrlFragment;
import com.zpj.shouji.market.ui.fragment.detail.AppDetailFragment;
import com.zpj.shouji.market.utils.BeanUtils;

import java.util.List;

public class UserDownloadedFragment extends NextUrlFragment<UserDownloadedAppInfo> {

//    private static final String DEFAULT_URL = "http://tt.shouji.com.cn/app/view_member_down_xml_v2.jsp?id=5636865";

    public static UserDownloadedFragment newInstance(String id) {
        Bundle args = new Bundle();
        args.putString(Keys.ID, id);
        UserDownloadedFragment fragment = new UserDownloadedFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        defaultUrl = DEFAULT_URL;
//        nextUrl = DEFAULT_URL;
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.item_app_user_downloaded;
    }

    @Override
    protected void handleArguments(Bundle arguments) {
        defaultUrl = "/app/view_member_down_xml_v2.jsp?id=" + arguments.getString(Keys.ID, "");
        nextUrl = defaultUrl;
    }

    @Override
    public void onClick(EasyViewHolder holder, View view, UserDownloadedAppInfo data) {
        AppDetailFragment.start(data);
    }

    @Override
    public UserDownloadedAppInfo createData(Element element) {
        return BeanUtils.createBean(element, UserDownloadedAppInfo.class);
//        UserDownloadedAppInfo appInfo = new UserDownloadedAppInfo();
//        appInfo.setId(element.selectFirst("id").text());
//        appInfo.setTitle(element.selectFirst("title").text());
//        appInfo.setDownId(element.selectFirst("downid").text());
//        appInfo.setAppType(element.selectFirst("apptype").text());
//        appInfo.setPackageName(element.selectFirst("package").text());
//        appInfo.setAppSize(element.selectFirst("m").text());
//        appInfo.setDownloadTime(element.selectFirst("r").text());
//        return appInfo;
    }

    @Override
    public void onBindViewHolder(EasyViewHolder holder, List<UserDownloadedAppInfo> list, int position, List<Object> payloads) {
        UserDownloadedAppInfo appInfo = list.get(position);
        holder.getTextView(R.id.text_title).setText(appInfo.getTitle());
        holder.getTextView(R.id.text_package_name).setText(appInfo.getPackageName());
        holder.getTextView(R.id.text_info).setText(appInfo.getAppSize() + " | 于" + appInfo.getDownloadTime() + "下载");
    }
}
