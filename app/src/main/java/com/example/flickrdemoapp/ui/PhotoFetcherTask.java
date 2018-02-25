package com.example.flickrdemoapp.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.util.Log;
import android.view.View;

import com.example.flickrdemoapp.R;
import com.foregroundcomm.UIThreadHandler;
import com.web.flickr.FlickrAPIConstants;
import com.web.flickr.OnPhotoFetchedListener;
import com.web.flickr.models.response.FlickrPhotoInfo;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

public class PhotoFetcherTask implements Runnable {
    private static final String TAG = PhotoFetcherTask.class.getSimpleName();
    private Context mContext;
    private PhotoFetcher mPhotoFetcher;
    private FlickrPhotoInfo mFlickrPhotoInfo;
    private final WeakReference<View> mViewReference;
    private OnPhotoFetchedListener mOnPhotoFetchedListener;

    public PhotoFetcherTask(Context context, PhotoFetcher photoFetcher, FlickrPhotoInfo flickrPhotoInfo, View view, OnPhotoFetchedListener onPhotoFetchedListener) {
        mContext = context.getApplicationContext();
        mPhotoFetcher = photoFetcher;
        mFlickrPhotoInfo = flickrPhotoInfo;
        mViewReference = new WeakReference<>(view);
        mOnPhotoFetchedListener = onPhotoFetchedListener;
    }

    @Override
    public void run() {
        synchronized (mPhotoFetcher.getPauseWorkLock()) {
            while(mPhotoFetcher.isPauseWork()) {
                try {
                    Log.d(TAG, "Scrolling started do not fetch images");
                    mPhotoFetcher.getPauseWorkLock().wait();
                } catch (InterruptedException e) {}
            }
        }

        PhotoDiskCache photoDiskCache = PhotoDiskCache.getInstance(mContext);
        String imageId = mFlickrPhotoInfo.getId();
        BitmapDrawable bitmapDrawable = photoDiskCache.get(imageId);
        if (bitmapDrawable == null) {
            Log.d(TAG, "Image not cached on disk, so downloading it: " + imageId);
            String flickrPhotoURL = constructPhotoURL();
            Bitmap bitmap = downloadBitmap(flickrPhotoURL);
            if(bitmap != null) {
                bitmapDrawable = new BitmapDrawable(bitmap);
                PhotoMemCache.put(imageId, bitmapDrawable);
                photoDiskCache.put(imageId, bitmapDrawable);
            }
        } else {
            Log.d(TAG, "Image cached on disk: " + imageId);
        }
        onPostExecute(bitmapDrawable);
    }

    private void onPostExecute(final BitmapDrawable bitmapDrawable) {
        UIThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                onBitmapDrawableFetchedForView(bitmapDrawable);
            }
        });
    }

    private void onBitmapDrawableFetchedForView(BitmapDrawable bitmapDrawable) {
        if(mOnPhotoFetchedListener != null) {
            View view = mViewReference.get();
            mOnPhotoFetchedListener.onBitmapDrawableFetchedForView(view, bitmapDrawable);
        }
    }

    private String constructPhotoURL() {
        String farm = "" + mFlickrPhotoInfo.getFarm();
        String server = mFlickrPhotoInfo.getServer();
        String id_secret = mFlickrPhotoInfo.getId() + "_" + mFlickrPhotoInfo.getSecret() + "_z";
        // URL format: https://farm{farm-id}.staticflickr.com/{server-id}/{id}_{secret}.jpg
        return mContext.getString(R.string.flickr_photo_url, farm, server, id_secret);
    }

    private Bitmap downloadBitmap(String photoURL) {
        Bitmap bitmap = null;
        HttpURLConnection urlConnection = null;
        BufferedInputStream in = null;
        try {
            final URL url = new URL(photoURL);
            urlConnection = (HttpURLConnection) url.openConnection();
            in = new BufferedInputStream(urlConnection.getInputStream());
            bitmap = BitmapFactory.decodeStream(in);
        } catch (final IOException e) {
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
}
