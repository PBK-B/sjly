//package com.zpj.shouji.market.ui.fragment.profile;
//
//import android.os.Bundle;
//import androidx.annotation.Nullable;
//import android.view.View;
//
//import com.bumptech.glide.Glide;
//import com.zpj.toast.ZToast;
//import com.zpj.http.parser.html.nodes.Element;
//import com.zpj.recyclerview.EasyViewHolder;
//import com.zpj.shouji.market.R;
//import com.zpj.shouji.market.api.HttpApi;
//import com.zpj.shouji.market.constant.Keys;
//import com.zpj.shouji.market.constant.UpdateFlagAction;
//import com.zpj.shouji.market.event.StartFragmentEvent;
//import com.zpj.shouji.market.model.BlacklistInfo;
//import com.zpj.shouji.market.ui.fragment.WebFragment;
//import com.zpj.shouji.market.ui.fragment.base.NextUrlFragment;
//import com.zpj.shouji.market.ui.fragment.theme.ThemeListFragment;
//import com.zpj.shouji.market.ui.widget.popup.BottomListPopupMenu;
//
//import java.util.List;
//
//public class MyAtFragment extends ThemeListFragment {
//
//    public static void start() {
//        Bundle args = new Bundle();
//        args.putString(Keys.DEFAULT_URL, "http://tt.tljpxm.com/app/user_content_aite_xml_v2.jsp");
//        MyAtFragment fragment = new MyAtFragment();
//        fragment.setArguments(args);
//        StartFragmentEvent.start(fragment);
//    }
//
//    @Override
//    protected int getLayoutId() {
//        return R.layout.fragment_list_with_toolbar;
//    }
//
//    @Override
//    protected boolean supportSwipeBack() {
//        return true;
//    }
//
//    @Override
//    protected void initView(View view, @Nullable Bundle savedInstanceState) {
//        super.initView(view, savedInstanceState);
//        setToolbarTitle("@我的");
//    }
//
//    @Override
//    public void onDestroy() {
//        HttpApi.updateFlagApi(UpdateFlagAction.AT);
//        super.onDestroy();
//    }
//
//}
