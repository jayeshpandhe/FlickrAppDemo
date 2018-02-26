package com.example.flickrdemoapp.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.flickrdemoapp.R;
import com.web.FlickrURLBuilder;
import com.web.flickr.OnPhotoFetchedListener;
import com.web.flickr.models.response.FlickrPhotoInfo;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

public class PhotoFetcherAsyncTask extends AsyncTask<Void, Void, BitmapDrawable> {
    private static final String TAG = PhotoFetcherAsyncTask.class.getSimpleName();
    private PhotoFetcher mPhotoFetcher;
    private FlickrPhotoInfo mFlickrPhotoInfo;
    private WeakReference<View> mViewReference;
    private OnPhotoFetchedListener mOnPhotoFetchedListener;
    private PhotoDiskCache mPhotoDiskCache;

    public PhotoFetcherAsyncTask(PhotoFetcher photoFetcher, FlickrPhotoInfo flickrPhotoInfo, View view, OnPhotoFetchedListener onPhotoFetchedListener) {
        mPhotoFetcher = photoFetcher;
        mFlickrPhotoInfo = flickrPhotoInfo;
        mViewReference = new WeakReference<>(view);
        mOnPhotoFetchedListener = onPhotoFetchedListener;
        mPhotoDiskCache = mPhotoFetcher.getPhotoDiskCache();
    }

    @Override
    protected BitmapDrawable doInBackground(Void... voids) {
        final String imageId = mFlickrPhotoInfo.getId();
        Bitmap bitmap = null;
        BitmapDrawable bitmapDrawable = null;
        synchronized (mPhotoFetcher.getPauseWorkLock()) {
            while(mPhotoFetcher.isPauseWork() && !isCancelled()) {
                try {
                    mPhotoFetcher.getPauseWorkLock().wait();
                } catch(InterruptedException e) {}
            }
        }

        if(mPhotoDiskCache != null && !isCancelled() && getAttachedImageView() != null) {
            bitmapDrawable = mPhotoDiskCache.get(imageId);
        }

        if(bitmapDrawable == null && !isCancelled() && getAttachedImageView() != null) {
            bitmap = downloadBitmap(FlickrURLBuilder.buildPhotoThumbnailURL(mFlickrPhotoInfo));
        }

        if(bitmap != null) {
            bitmapDrawable = new BitmapDrawable(bitmap);
            cacheBitmap(imageId, bitmapDrawable);
        }
        return bitmapDrawable;
    }

    @Override
    protected void onPostExecute(BitmapDrawable bitmapDrawable) {
        super.onPostExecute(bitmapDrawable);
        if(!isCancelled()) {
            mOnPhotoFetchedListener.onBitmapDrawableFetchedForView(mViewReference.get(), bitmapDrawable);
        }
    }

    private void cacheBitmap(String imageId, BitmapDrawable bitmapDrawable) {
        if(!isCancelled()) {
            PhotoMemCache.put(imageId, bitmapDrawable);
            mPhotoDiskCache.put(imageId, bitmapDrawable);
        }
    }

    private ImageView getAttachedImageView() {
        View v = mViewReference.get();
        return v.findViewById(R.id.flickr_photo_image_view);
    }

    private Bitmap downloadBitmap(String photoURL) {
        Bitmap bitmap = null;
        HttpURLConnection urlConnection = null;
        BufferedInputStream in = null;
        try {
            final URL url = new URL(photoURL);
            urlConnection = (HttpURLConnection) url.openConnection();
            in = new BufferedInputStream(urlConnection.getInputStream());
            if(!isCancelled()) {
                bitmap = BitmapFactory.decodeStream(in);
            }
        } catch (final Exception e) {
            Log.e(TAG, "Error in downloadBitmap - " + e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            try {
                if (in != null) {
                    in.close();
                }
            } catch (final IOException e) {
            }
        }
        return bitmap;
    }

    @Override
    protected void onCancelled(BitmapDrawable bitmapDrawable) {
        super.onCancelled(bitmapDrawable);
        synchronized (mPhotoFetcher.getPauseWorkLock()) {
            mPhotoFetcher.getPauseWorkLock().notifyAll();
        }
    }
}
