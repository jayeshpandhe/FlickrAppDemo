package com.example.flickrdemoapp.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;

import com.example.flickrdemoapp.R;
import com.web.flickr.OnPhotoFetchedListener;
import com.web.flickr.models.response.FlickrPhotoInfo;

public class PhotoFetcher {
    private static final String TAG = PhotoFetcher.class.getSimpleName();
    private final Object mPauseWorkLock = new Object();
    private boolean pauseWork;
    private Context mContext;
    private Bitmap mDefaultBitmap;
    private PhotoDiskCache mPhotoDiskCache;

    public PhotoFetcher(Context context, int defaultBitmapResId) {
        mContext = context.getApplicationContext();
        mDefaultBitmap = BitmapFactory.decodeResource(mContext.getResources(), defaultBitmapResId);
        mPhotoDiskCache = PhotoDiskCache.getInstance(mContext);
    }

    public void fetchImage(FlickrPhotoInfo flickrPhotoInfo, View view, OnPhotoFetchedListener onPhotoFetchedListener) {
        ImageView imageView = view.findViewById(R.id.flickr_photo_image_view);
        if(isPreviousPhotoFetcherTaskInProgress(imageView)) {
            cancelPreviousPhotoFetcherTask(imageView);
        }

        String imageId = flickrPhotoInfo.getId();
        BitmapDrawable bitmapDrawable = PhotoMemCache.get(imageId);
        if(bitmapDrawable == null) {
            PhotoFetcherAsyncTask photoFetcherAsyncTask = new PhotoFetcherAsyncTask(this, flickrPhotoInfo, view, onPhotoFetchedListener);
            bitmapDrawable = new AsyncDrawable(mContext.getResources(), mDefaultBitmap, photoFetcherAsyncTask);
            photoFetcherAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        if(onPhotoFetchedListener != null) {
            onPhotoFetchedListener.onBitmapDrawableFetchedForView(view, bitmapDrawable);
        }
    }

    private boolean isPreviousPhotoFetcherTaskInProgress(ImageView imageView) {
        PhotoFetcherAsyncTask photoFetcherTask = getPhotoFetcherTask(imageView);
        return photoFetcherTask != null;
    }

    private void cancelPreviousPhotoFetcherTask(ImageView imageView) {
        PhotoFetcherAsyncTask photoFetcherTask = getPhotoFetcherTask(imageView);
        if(photoFetcherTask != null) {
            photoFetcherTask.cancel(true);
        }
    }

    private static PhotoFetcherAsyncTask getPhotoFetcherTask(ImageView imageView) {
        PhotoFetcherAsyncTask photoFetcherTask = null;
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                photoFetcherTask = asyncDrawable.getPhotoFetcherTask();
            }
        }
        return photoFetcherTask;
    }

    public boolean isPauseWork() {
        return pauseWork;
    }

    public void setPauseWork(boolean pauseWork) {
        synchronized (mPauseWorkLock) {
            this.pauseWork = pauseWork;
            if(!pauseWork) {
                mPauseWorkLock.notifyAll();
            }
        }
    }

    public Object getPauseWorkLock() {
        return mPauseWorkLock;
    }

    public PhotoDiskCache getPhotoDiskCache() {
        return mPhotoDiskCache;
    }
}
