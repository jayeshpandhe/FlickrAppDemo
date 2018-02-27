package com.example.flickrdemoapp.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * This class is a wrapper around DiskCache. It caches downloaded bitmap in the device cache memory.
 */
public class PhotoDiskCache {
    private static final String TAG = PhotoDiskCache.class.getSimpleName();
    private static final Object DISK_CACHE_LOCK = new Object();
    private static final int DISK_CACHE_SIZE = 1024 * 1024 * 10; // 10MB
    private static final String PHOTO_DISK_CACHE_DIR = "thumbnails";
    private static final int DISK_CACHE_INDEX = 0;
    private static final int DEFAULT_COMPRESS_QUALITY = 100;
    private static final Object LOCK = new Object();
    private static PhotoDiskCache mInstance;
    private DiskLruCache mDiskLruCache;

    public static PhotoDiskCache getInstance(Context context) {
        if (mInstance == null) {
            synchronized (LOCK) {
                if (mInstance == null) {
                    mInstance = new PhotoDiskCache(context);
                }
            }
        }
        return mInstance;
    }

    private PhotoDiskCache(Context context) {
        initDiskCache(context.getApplicationContext());
    }

    private void initDiskCache(Context context) {
        File diskCacheDir = getDiskCacheDir(context, PHOTO_DISK_CACHE_DIR);
        try {
            mDiskLruCache = DiskLruCache.open(diskCacheDir, 1, 1, DISK_CACHE_SIZE);
        } catch (IOException e) {
            Log.e(TAG, "Exception opening cache file", e);
        }
    }

    public BitmapDrawable get(String key) {
        synchronized (DISK_CACHE_LOCK) {
            BitmapDrawable bitmapDrawable = null;
            if (!TextUtils.isEmpty(key)) {
                key = hashKeyForDisk(key);
                Log.d(TAG, "Hash key: " + key);
                InputStream in = null;
                try {
                    final DiskLruCache.Snapshot snapshot = mDiskLruCache.get(key);
                    if (snapshot != null) {
                        in = snapshot.getInputStream(DISK_CACHE_INDEX);
                        if (in != null) {
                            FileDescriptor fd = ((FileInputStream) in).getFD();
                            Bitmap bitmap = BitmapFactory.decodeFileDescriptor(fd);
                            bitmapDrawable = new BitmapDrawable(bitmap);
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Exception: ", e);
                } finally {
                    if (in != null) {
                        try {
                            in.close();
                        } catch (IOException e) {}
                    }
                }
            }
            return bitmapDrawable;
        }
    }

    public void put(String key, BitmapDrawable value) {
        synchronized (DISK_CACHE_LOCK) {
            if (!TextUtils.isEmpty(key) && value != null && value.getBitmap() != null) {
                key = hashKeyForDisk(key);
                OutputStream out = null;
                try {
                    DiskLruCache.Snapshot snapshot = mDiskLruCache.get(key);
                    if (snapshot == null) {
                        final DiskLruCache.Editor editor = mDiskLruCache.edit(key);
                        if (editor != null) {
                            out = editor.newOutputStream(DISK_CACHE_INDEX);
                            Log.d(TAG, "Putting image for key: " + key + ". Value bitmap: " + value.getBitmap());
                            value.getBitmap().compress(Bitmap.CompressFormat.JPEG, DEFAULT_COMPRESS_QUALITY, out);
                            editor.commit();
                            out.close();
                        }
                    } else {
                        snapshot.getInputStream(DISK_CACHE_INDEX).close();
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Exception: ", e);
                    if (out != null) {
                        try {
                            out.close();
                        } catch (IOException e1) {}
                    }
                }
            }
        }
    }

    public void clearCache(Context context) {
        synchronized (DISK_CACHE_LOCK) {
            //mDiskCacheStarting = true;
            if (mDiskLruCache != null && !mDiskLruCache.isClosed()) {
                try {
                    mDiskLruCache.delete();
                    Log.d(TAG, "Disk cache cleared");
                } catch (IOException e) {
                    Log.e(TAG, "clearCache - " + e);
                }
                mDiskLruCache = null;
                initDiskCache(context.getApplicationContext());
            }
        }
    }

    public static String hashKeyForDisk(String key) {
        String cacheKey;
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(key.getBytes());
            cacheKey = bytesToHexString(mDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(key.hashCode());
        }
        return cacheKey;
    }

    private static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    /**
     * Get a usable cache directory (external if available, internal otherwise).
     *
     * @param context    The context to use
     * @param uniqueName A unique directory name to append to the cache dir
     * @return The cache dir
     */
    public static File getDiskCacheDir(Context context, String uniqueName) {
        // Check if media is mounted or storage is built-in, if so, try and use external cache dir
        // otherwise use internal cache dir
        final String cachePath =
                Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) ||
                        !isExternalStorageRemovable() ? getExternalCacheDir(context).getPath() :
                        context.getCacheDir().getPath();

        return new File(cachePath + File.separator + uniqueName);
    }

    public static File getExternalCacheDir(Context context) {
        return context.getExternalCacheDir();
    }


    public static boolean isExternalStorageRemovable() {
        return Environment.isExternalStorageRemovable();
    }
}
