/*
 * Copyright (C) 2014 nohana, Inc.
 * Copyright 2017 Zhihu Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an &quot;AS IS&quot; BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zpj.shouji.market.imagepicker.loader;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.MergeCursor;
import android.net.Uri;
import android.provider.MediaStore;


import androidx.loader.content.CursorLoader;

import com.zpj.shouji.market.imagepicker.entity.Album;

/**
 * Load all albums (grouped by bucket_id) into a single cursor.
 */
public class AlbumLoader extends CursorLoader {
    public static final String COLUMN_COUNT = "count";
    private static final Uri QUERY_URI = MediaStore.Files.getContentUri("external");
    private static final String[] COLUMNS = {
            MediaStore.Files.FileColumns._ID,
            "bucket_id",
            "bucket_display_name",
            MediaStore.MediaColumns.DATA,
            COLUMN_COUNT};
    private static final String[] PROJECTION = {
            MediaStore.Files.FileColumns._ID,
            "bucket_id",
            "bucket_display_name",
            MediaStore.MediaColumns.DATA,
            "COUNT(*) AS " + COLUMN_COUNT};

    // === params for showSingleMediaType: true ===
    private static final String SELECTION_FOR_SINGLE_MEDIA_TYPE =
            MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                    + " AND " + MediaStore.MediaColumns.SIZE + ">0"
                    + ") GROUP BY (bucket_id";

    private static final String BUCKET_ORDER_BY = "datetaken DESC";

    private AlbumLoader(Context context, String selection, String[] selectionArgs) {
        super(context, QUERY_URI, PROJECTION, selection, selectionArgs, BUCKET_ORDER_BY);
    }

    public static CursorLoader newInstance(Context context) {
        return new AlbumLoader(context, SELECTION_FOR_SINGLE_MEDIA_TYPE,
                new String[]{String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE)});
    }

    @Override
    public Cursor loadInBackground() {
        Cursor albums = super.loadInBackground();
        MatrixCursor allAlbum = new MatrixCursor(COLUMNS);
        int totalCount = 0;
        String allAlbumCoverPath = "";
        if (albums != null) {
            while (albums.moveToNext()) {
                totalCount += albums.getInt(albums.getColumnIndex(COLUMN_COUNT));
            }
            if (albums.moveToFirst()) {
                allAlbumCoverPath = albums.getString(albums.getColumnIndex(MediaStore.MediaColumns.DATA));
            }
        }
        allAlbum.addRow(new String[]{Album.ALBUM_ID_ALL, Album.ALBUM_ID_ALL, Album.ALBUM_NAME_ALL, allAlbumCoverPath,
                String.valueOf(totalCount)});

        return new MergeCursor(new Cursor[]{allAlbum, albums});
    }

    @Override
    public void onContentChanged() {
        // FIXME a dirty way to fix loading multiple times
    }
}