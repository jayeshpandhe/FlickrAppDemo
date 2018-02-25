package com.example.flickrdemoapp.ui;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

import java.lang.ref.WeakReference;

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
