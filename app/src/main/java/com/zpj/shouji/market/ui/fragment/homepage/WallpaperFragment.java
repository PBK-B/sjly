package com.zpj.shouji.market.ui.fragment.homepage;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;

import com.kongzue.stacklabelview.interfaces.OnLabelClickListener;
import com.zpj.http.parser.html.nodes.Document;
import com.zpj.http.parser.html.nodes.Element;
import com.zpj.http.parser.html.select.Elements;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.model.WallpaperTag;
import com.zpj.shouji.market.ui.adapter.FragmentsPagerAdapter;
import com.zpj.shouji.market.ui.fragment.ImageListFragment;
import com.zpj.shouji.market.ui.fragment.base.BaseFragment;
import com.zpj.shouji.market.ui.widget.WallpaperTagPopup;
import com.zpj.shouji.market.utils.ExecutorHelper;
import com.zpj.shouji.market.utils.HttpUtil;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class WallpaperFragment extends BaseFragment implements View.OnClickListener {

    private final AtomicBoolean isInitTags = new AtomicBoolean(false);

    private final List<ImageListFragment> fragments = new ArrayList<>(0);
    private final List<WallpaperTag> wallpaperTags = new ArrayList<>(0);

    private ViewPager viewPager;
    private MagicIndicator magicIndicator;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_wallpaper;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        isInitTags.set(false);
        viewPager = view.findViewById(R.id.vp);
        magicIndicator = view.findViewById(R.id.magic_indicator);
        ImageView expand = view.findViewById(R.id.iv_expand);
        expand.setOnClickListener(this);
        initWallpaperTags();
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        if (!isInitTags.get()) {
            initWallpaperTags();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_expand) {
            WallpaperTagPopup.with(context)
                    .setLabels(wallpaperTags)
                    .setSelectLabel(wallpaperTags.get(viewPager.getCurrentItem()).getName())
                    .setOnLabelClickListener(new OnLabelClickListener() {
                        @Override
                        public void onClick(int index, View v, String s) {
                            viewPager.setCurrentItem(index);
                        }
                    })
                    .show(v);
        }
    }

    private void initWallpaperTags() {
        ExecutorHelper.submit(() -> {
            try {
                Document doc = HttpUtil.getDocument("http://tt.shouji.com.cn/app/bizhi_tags.jsp?versioncode=198");
                Elements elements = doc.select("item");
                wallpaperTags.clear();
                for (Element item : elements) {
                    wallpaperTags.add(WallpaperTag.create(item));
                }
                post(this::initMagicIndicator);
            } catch (IOException e) {
                e.printStackTrace();
                // TODO 加载默认分类
            }
        });
    }

    private void initMagicIndicator() {
        isInitTags.set(true);
        fragments.clear();
        for (WallpaperTag tag : wallpaperTags) {
            fragments.add(ImageListFragment.newInstance(tag));
        }
        FragmentsPagerAdapter adapter = new FragmentsPagerAdapter(getChildFragmentManager(), fragments, null);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(fragments.size());
        CommonNavigator navigator = new CommonNavigator(getContext());
        navigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return wallpaperTags.size();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, int index) {
                ColorTransitionPagerTitleView titleView = new ColorTransitionPagerTitleView(context);
                titleView.setNormalColor(Color.GRAY);
                titleView.setSelectedColor(getResources().getColor(R.color.colorPrimary));
                titleView.setTextSize(14);
                titleView.setText(wallpaperTags.get(index).getName());
                titleView.setOnClickListener(view -> viewPager.setCurrentItem(index));
                return titleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator indicator = new LinePagerIndicator(context);
                indicator.setColors(getResources().getColor(R.color.colorPrimary));
                return new LinePagerIndicator(context);
            }
        });
        magicIndicator.setNavigator(navigator);
        ViewPagerHelper.bind(magicIndicator, viewPager);
    }
}