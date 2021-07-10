/*
 * Copyright 2017 Zhihu Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zpj.shouji.market.imagepicker.widget;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.fragment.app.FragmentActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;

import com.zpj.recyclerview.EasyRecyclerView;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.imagepicker.SelectionManager;
import com.zpj.shouji.market.imagepicker.entity.Album;
import com.zpj.shouji.market.imagepicker.loader.AlbumLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Main Activity to display albums and media content (images/videos) in each album
 * and also support media selecting operations.
 */
public class AlbumListLayout extends RecyclerView implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_ID = 1;

    private final List<Album> albumList = new ArrayList<>();

    private final EasyRecyclerView<Album> recyclerLayout;

    private LoaderManager mLoaderManager;

    private OnAlbumSelectListener onAlbumSelectListener;

    private int selectPosition;

    private boolean mLoadFinished;

    public AlbumListLayout(@NonNull Context context) {
        this(context, null);
    }

    public AlbumListLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AlbumListLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        recyclerLayout = new EasyRecyclerView<>(this);
        recyclerLayout.setItemRes(R.layout.item_album_linear)
                .setData(albumList)
//                .setLayoutManager(new GridLayoutManager(context, 2))
                .onBindViewHolder((holder, list, position, payloads) -> {
                    Album album = list.get(position);

                    holder.getItemView().setBackgroundColor(Color.TRANSPARENT);

                    holder.setText(R.id.tv_title, album.getDisplayName(context));
                    holder.setText(R.id.tv_count, "共" + album.getCount() + "张图片");

                    // do not need to load animated Gif
                    SelectionManager.getInstance().imageEngine.loadThumbnail(
                            context,
                            holder.getImageView(R.id.album_cover),
                            Uri.fromFile(new File(album.getCoverPath()))
                    );

                    if (selectPosition == position) {
                        holder.setVisible(R.id.iv_current, true);
                    } else {
                        holder.setInVisible(R.id.iv_current);
                    }

                })
                .onItemClick((holder, view1, album) -> {
                    selectPosition = holder.getAdapterPosition();
                    recyclerLayout.notifyDataSetChanged();
                    if (onAlbumSelectListener != null) {
                        onAlbumSelectListener.onSelect(album);
                    }
//                    onAlbumSelected(album);
                })
                .build();
//        recyclerLayout.getEasyRecyclerView().getRecyclerView().setHasFixedSize(true);
        recyclerLayout.showLoading();
    }

    public void init(FragmentActivity activity, Bundle savedInstanceState) {
        mLoaderManager = LoaderManager.getInstance(activity);
    }

    public Album getCurrentAlbum() {
        return albumList.get(selectPosition);
    }

    public void loadAlbums() {
        albumList.clear();
        recyclerLayout.showLoading();
        mLoaderManager.initLoader(LOADER_ID, null, this);

    }

    public void onDestroy() {
        if (mLoaderManager != null) {
            mLoaderManager.destroyLoader(LOADER_ID);
        }
    }

    public void setOnAlbumSelectListener(OnAlbumSelectListener onAlbumSelectListener) {
        this.onAlbumSelectListener = onAlbumSelectListener;
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
        mLoadFinished = false;
        return AlbumLoader.newInstance(getContext());
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        if (!mLoadFinished) {
            mLoadFinished = true;
            cursor.moveToFirst();
            do {
                albumList.add(Album.valueOf(cursor));
            } while (cursor.moveToNext());
//        recyclerLayout.notifyDataSetChanged();
            if (albumList.isEmpty()) {
                recyclerLayout.showEmpty();
            } else {
                if (onAlbumSelectListener != null) {
                    onAlbumSelectListener.onSelect(albumList.get(0));
                }
                selectPosition = 0;
                recyclerLayout.showContent();
            }
            mLoaderManager.destroyLoader(LOADER_ID);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        albumList.clear();
        recyclerLayout.notifyDataSetChanged();
    }

    public interface OnAlbumSelectListener {
        void onSelect(Album album);
    }

}
