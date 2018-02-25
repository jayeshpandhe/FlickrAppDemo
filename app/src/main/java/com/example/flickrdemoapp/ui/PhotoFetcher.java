package com.example.flickrdemoapp.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import com.bgcomm.BackgroundWorker;
import com.example.flickrdemoapp.R;
import com.web.flickr.OnPhotoFetchedListener;
import com.web.flickr.models.response.FlickrPhotoInfo;

import java.util.concurrent.Future;

public class PhotoFetcher {
    private static final String TAG = PhotoFetcher.class.getSimpleName();
    private final Object mPauseWorkLock = new Object();
    private boolean pauseWork;
    private Context mContext;
    private Bitmap mDefaultBitmap;

    public PhotoFetcher(Context context, int defaultBitmapResId) {
        mContext = context.getApplicationContext();
        mDefaultBitmap = BitmapFactory.decodeResource(mContext.getResources(), defaultBitmapResId);
    }

    public void fetchImage(FlickrPhotoInfo flickrPhotoInfo, View view, OnPhotoFetchedListener onPhotoFetchedListener) {
        String imageId = flickrPhotoInfo.getId();
        BitmapDrawable bitmapDrawable = PhotoMemCache.get(imageId);
        if (bitmapDrawable == null) {
            ImageView imageView = view.findViewById(R.id.flickr_photo_image_view);
            if(isPreviousPhotoFetcherTaskInProgress(imageView)) {
                cancelPreviousPhotoFetcherTask(imageView);
            }
            PhotoFetcherTask photoDownloadTask = new PhotoFetcherTask(mContext, this, flickrPhotoInfo, view, onPhotoFetchedListener);
            Future futurePhotoFetcherTask = BackgroundWorker.submitRunnable(photoDownloadTask);
            bitmapDrawable = new AsyncDrawable(mContext.getResources(), mDefaultBitmap, futurePhotoFetcherTask);
        }
        if(onPhotoFetchedListener != null) {
            onPhotoFetchedListener.onBitmapDrawableFetchedForView(view, bitmapDrawable);
        }
    }

    private boolean isPreviousPhotoFetcherTaskInProgress(ImageView imageView) {
        Future photoFetcherTask = getPhotoFetcherTask(imageView);
        return photoFetcherTask != null;
    }

    private void cancelPreviousPhotoFetcherTask(ImageView imageView) {
        Future photoFetcherTask = getPhotoFetcherTask(imageView);
        if(photoFetcherTask != null) {
            photoFetcherTask.cancel(true);
        }
    }

    private Future getPhotoFetcherTask(ImageView imageView) {
        Future photoFetcherTask = null;
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
}
