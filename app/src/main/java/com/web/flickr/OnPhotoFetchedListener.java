package com.web.flickr;

import android.graphics.drawable.BitmapDrawable;
import android.view.View;

public interface OnPhotoFetchedListener {
    void onBitmapDrawableFetchedForView(View view, BitmapDrawable bitmapDrawable);
}
