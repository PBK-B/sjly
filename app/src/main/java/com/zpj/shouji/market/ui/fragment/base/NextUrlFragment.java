package com.zpj.shouji.market.ui.fragment.base;

import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zpj.http.core.HttpObserver;
import com.zpj.http.core.IHttp;
import com.zpj.http.parser.html.nodes.Document;
import com.zpj.http.parser.html.nodes.Element;
import com.zpj.http.parser.html.select.Elements;
import com.zpj.recyclerview.EasyAdapter;
import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.recyclerview.footer.IFooterViewHolder;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.api.HttpApi;
import com.zpj.shouji.market.constant.Keys;

import java.util.List;

import io.reactivex.ObservableEmitter;

public abstract class NextUrlFragment<T> extends RecyclerLayoutFragment<T>
        implements IHttp.OnSuccessListener<Integer>, IHttp.OnErrorListener {

    protected String defaultUrl;
    protected String nextUrl;
    private boolean flag = false;
    protected boolean refresh;

    @Override
    protected void handleArguments(Bundle arguments) {
        defaultUrl = arguments.getString(Keys.DEFAULT_URL, "");
        nextUrl = defaultUrl;
    }

    @Override
    public void onRefresh() {
        nextUrl = defaultUrl;
//        super.onRefresh();
//        data.clear();
//        recyclerLayout.notifyDataSetChanged();
//        if (data.isEmpty()) {
//            refresh = false;
//            recyclerLayout.showContent();
//        } else {
//            refresh = true;
//            getData();
//        }

        if (data.isEmpty()) {
            recyclerLayout.showLoading();
        }
        refresh = true;
        getData();
    }

    @Override
    public void onClick(EasyViewHolder holder, View view, T data) {

    }

    @Override
    public boolean onLongClick(EasyViewHolder holder, View view, T data) {
        return false;
    }

    @Override
    public boolean onLoadMore(EasyAdapter.Enabled enabled, int currentPage) {
        Log.d("NextUrlFragment", "onLoadMore flag=" + flag + " data.isEmpty()=" + data.isEmpty() + " refresh=" + refresh + " nextUrl=" + nextUrl);
        if (TextUtils.isEmpty(nextUrl)) {
            return false;
        }
//        if (data.isEmpty()) {
//            recyclerLayout.showLoading();
//        }
//        getData();

        if (data.isEmpty() && !refresh) {
            if (flag) {
                return false;
            }
            flag = true;
//            postOnEnterAnimationEnd(this::getData);
            getData();
        } else {
            getData();
        }
//        refresh = false;
        return true;
    }

    @Override
    public void onEnterAnimationEnd(Bundle savedInstanceState) {
        super.onEnterAnimationEnd(savedInstanceState);
        flag = false;
    }

    @Override
    public void onSuccess(final Integer s) throws Exception {
        postOnEnterAnimationEnd(new Runnable() {
            @Override
            public void run() {
                int start = s;
////                Log.d("getData", "doc=" + doc);
//                nextUrl = doc.selectFirst("nextUrl").text();
//                if (refresh) {
//                    data.clear();
//                }
//                int start = data.size();
//                try {
//                    onGetDocument(doc);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    onError(e);
//                    return;
//                }
                int end = data.size();

                int last = -1;
                int first = -1;
                if (recyclerLayout.getLayoutManager() instanceof LinearLayoutManager) {
                    last = ((LinearLayoutManager) recyclerLayout.getLayoutManager()).findLastVisibleItemPosition();
                    first = ((LinearLayoutManager) recyclerLayout.getLayoutManager()).findFirstVisibleItemPosition();
                }

//                Log.d("getData", "last=" + last);
//
//                Log.d("getData", "start=" + start + " end=" + end + " count=" + (end - start));
//                Log.d("getData", "getHeaderView=" + recyclerLayout.getAdapter().getHeaderView());
//                Log.d("getData", "getFooterView=" + recyclerLayout.getAdapter().getFooterView());
                if (start == 0 || first == 0) { // last <= start
                    recyclerLayout.notifyDataSetChanged();
                } else {
                    if (start < end) {
//            recyclerLayout.notifyItemRangeChanged(start, end);
                        int count = end - start;
                        if (recyclerLayout.getAdapter().getHeaderView() != null) {
                            start += 1;
                        }
//                        Log.d("getData222222222222", "start=" + start + " count=" + count);
                        recyclerLayout.notifyItemRangeChanged(start, count);
                    } else {
                        IFooterViewHolder viewHolder = recyclerLayout.getAdapter().getFooterViewHolder();
                        if (viewHolder != null) {
                            viewHolder.onShowHasNoMore();
                        }
//                        View footerView = recyclerLayout.getAdapter().getFooterView();
//                        if (footerView != null) {
//                            LinearLayout llContainerProgress = footerView.findViewById(R.id.ll_container_progress);
//                            TextView tvMsg = footerView.findViewById(R.id.tv_msg);
//                            if (llContainerProgress != null) {
//                                llContainerProgress.setVisibility(View.GONE);
//                            }
//                            if (tvMsg != null) {
//                                tvMsg.setVisibility(View.VISIBLE);
//                                tvMsg.setText(R.string.easy_has_no_more);
//                            }
//                        }
                        return;
                    }
                }
                refresh = false;
                if (data.size() == 0) {
                    if (recyclerLayout.getAdapter().getHeaderView() == null) {
                        recyclerLayout.showEmpty();
                    } else {
//                        View footerView = recyclerLayout.getAdapter().getFooterView();
//                        if (footerView != null) {
//                            LinearLayout llContainerProgress = footerView.findViewById(R.id.ll_container_progress);
//                            TextView tvMsg = footerView.findViewById(R.id.tv_msg);
//                            if (llContainerProgress != null) {
//                                llContainerProgress.setVisibility(View.GONE);
//                            }
//                            if (tvMsg != null) {
//                                tvMsg.setVisibility(View.VISIBLE);
//                                tvMsg.setText(R.string.easy_has_no_more);
//                            }
//                        }
                        IFooterViewHolder viewHolder = recyclerLayout.getAdapter().getFooterViewHolder();
                        if (viewHolder != null) {
                            viewHolder.onShowHasNoMore();
                        }
                        recyclerLayout.showContent();
                    }
                } else {
                    recyclerLayout.showContent();
                }
//        recyclerLayout.notifyDataSetChanged();
            }
        });
    }

    protected void onGetDocument(Document doc) throws Exception {
        Elements items = doc.select("item");
        if (items.isEmpty()) {
            nextUrl = "";
        } else {
            for (Element element : doc.select("item")) {
                T item = createData(element);
                if (item == null) {
                    continue;
                }
                data.add(item);
            }
        }
    }

    @Override
    public void onError(Throwable throwable) {
        recyclerLayout.showErrorView(throwable.getMessage());
    }

    protected void getData() {
        Log.d("NextUrlFragment", "getData nextUrl=" + nextUrl);
        HttpApi.getXml(nextUrl)
                .bindToLife(this)
                .flatMap((HttpObserver.OnFlatMapListener<Document, Integer>) (doc, emitter) -> {
                    nextUrl = doc.selectFirst("nextUrl").text();
                    if (refresh) {
                        data.clear();
                    }
                    int start = data.size();
                    try {
                        onGetDocument(doc);
                    } catch (Exception e) {
                        e.printStackTrace();
                        onError(e);
                        return;
                    }
                    emitter.onNext(start);
                    emitter.onComplete();
                })
                .onSuccess(this)
                .onError(this)
                .subscribe();
    }

    public abstract T createData(Element element);

}
