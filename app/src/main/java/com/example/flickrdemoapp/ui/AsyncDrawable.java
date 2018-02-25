package com.example.flickrdemoapp.ui;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

import java.lang.ref.WeakReference;
import java.util.concurrent.Future;

public class AsyncDrawable extends BitmapDrawable {
    private final WeakReference<Future> mPhotoFetcherTaskWeakReference;

    public AsyncDrawable(Resources res, Bitmap bitmap, Future photoFetcherTask) {
        super(res, bitmap);
        mPhotoFetcherTaskWeakReference = new WeakReference<>(photoFetcherTask);
    }

    public Future getPhotoFetcherTask() {
        return mPhotoFetcherTaskWeakReference.get();
    }
}
