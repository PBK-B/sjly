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
import android.net.Uri;
import android.provider.MediaStore;
import androidx.loader.content.CursorLoader;

import com.zpj.shouji.market.imagepicker.entity.Album;

/**
 * Load images and videos into a single cursor.
 */
public class AlbumMediaLoader extends CursorLoader {
    private static final Uri QUERY_URI = MediaStore.Files.getContentUri("external");
    private static final String[] PROJECTION = {
            MediaStore.Files.FileColumns._ID,
            MediaStore.MediaColumns.DISPLAY_NAME,
            MediaStore.MediaColumns.MIME_TYPE,
            MediaStore.MediaColumns.SIZE,
            "duration"};

    // === params for album ALL && showSingleMediaType: true ===
    private static final String SELECTION_ALL_FOR_SINGLE_MEDIA_TYPE =
            MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                    + " AND " + MediaStore.MediaColumns.SIZE + ">0";
    // =========================================================

    // === params for ordinary album && showSingleMediaType: true ===
    private static final String SELECTION_ALBUM_FOR_SINGLE_MEDIA_TYPE =
            MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                    + " AND "
                    + " bucket_id=?"
                    + " AND " + MediaStore.MediaColumns.SIZE + ">0";


    private static final String ORDER_BY = MediaStore.Images.Media.DATE_TAKEN + " DESC";

    private AlbumMediaLoader(Context context, String selection, String[] selectionArgs, boolean capture) {
        super(context, QUERY_URI, PROJECTION, selection, selectionArgs, ORDER_BY);
    }

    public static CursorLoader newInstance(Context context, Album album, boolean capture) {
        String selection;
        String[] selectionArgs;
        boolean enableCapture;

        if (album.isAll()) {
            selection = SELECTION_ALL_FOR_SINGLE_MEDIA_TYPE;
            selectionArgs = new String[]{String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE)};
            enableCapture = capture;
        } else {
            selection = SELECTION_ALBUM_FOR_SINGLE_MEDIA_TYPE;
            selectionArgs = new String[]{String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE), album.getId()};
            enableCapture = false;
        }
        return new AlbumMediaLoader(context, selection, selectionArgs, enableCapture);
    }

    @Override
    public Cursor loadInBackground() {
        return super.loadInBackground();
    }

    @Override
    public void onContentChanged() {
        // FIXME a dirty way to fix loading multiple times
    }
}
