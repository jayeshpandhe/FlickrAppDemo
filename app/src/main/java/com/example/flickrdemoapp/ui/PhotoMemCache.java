package com.example.flickrdemoapp.ui;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.util.LruCache;
import android.text.TextUtils;

/**
 * This class is a wrapper around LruCache. It caches downloaded bitmap in the memory.
 */
public class PhotoMemCache {
    private static final String TAG = PhotoMemCache.class.getSimpleName();
    private static PhotoMemCache mInstance;
    private LruCache<String, BitmapDrawable> mImageCache;
    // Default memory cache size in kilobytes
    private static final int DEFAULT_MEM_CACHE_SIZE = 4 * 1024 * 1024; // 4MiB

    static  {
       mInstance = new PhotoMemCache();
    }

    private PhotoMemCache() {
        init();
    }

    private void init() {
        mImageCache = new LruCache<String, BitmapDrawable>(DEFAULT_MEM_CACHE_SIZE) {
            @Override
            protected int sizeOf(String key, BitmapDrawable value) {
                final int bitmapSize = getBitmapSize(value) / 1024;
                return bitmapSize == 0 ? 1 : bitmapSize;
            }
        };
    }

    public static int getBitmapSize(BitmapDrawable value) {
        Bitmap bitmap = value.getBitmap();
        return bitmap.getRowBytes() * bitmap.getHeight();
    }

    public static BitmapDrawable get(String key) {
        return mInstance.mImageCache.get(key);
    }

    public static void put(String key, BitmapDrawable value) {
        if(!TextUtils.isEmpty(key) && value != null) {
            mInstance.mImageCache.put(key, value);
        }
    }

    public static void clearCache() {
        if(mInstance.mImageCache != null)
            mInstance.mImageCache.evictAll();
    }
}
