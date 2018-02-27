package com.example.flickrdemoapp.ui;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

import java.lang.ref.WeakReference;

/**
 * This class is a wrapper around BitmapDrawable. It maintains reference of task associated
 * with the ImageView.
 */
public class AsyncDrawable extends BitmapDrawable {
    private final WeakReference<PhotoFetcherAsyncTask> mPhotoFetcherTaskWeakReference;

    public AsyncDrawable(Resources res, Bitmap bitmap, PhotoFetcherAsyncTask photoFetcherAsyncTask) {
        super(res, bitmap);
        mPhotoFetcherTaskWeakReference = new WeakReference<>(photoFetcherAsyncTask);
    }

    public PhotoFetcherAsyncTask getPhotoFetcherTask() {
        return mPhotoFetcherTaskWeakReference.get();
    }
}
