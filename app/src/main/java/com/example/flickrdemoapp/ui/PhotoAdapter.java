package com.example.flickrdemoapp.ui;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.example.flickrdemoapp.R;
import com.web.flickr.OnPhotoFetchedListener;
import com.web.flickr.models.response.FlickrPhotoInfo;

import java.util.List;

public class PhotoAdapter extends BaseAdapter implements OnPhotoFetchedListener {
    private static final int CELL_WIDTH = 500, CELL_HEIGHT = 500;
    private final List<FlickrPhotoInfo> mPhotoInfoList;
    private RelativeLayout.LayoutParams mImageViewLayoutParams;
    private LayoutInflater mInflater;
    private PhotoFetcher mPhotoFetcher;
    private BitmapDrawable mErrorBitmapDrawable;

    public PhotoAdapter(Context context, List<FlickrPhotoInfo> photoInfoList, PhotoFetcher photoFetcher) {
        context = context.getApplicationContext();
        mPhotoInfoList = photoInfoList;
        mPhotoFetcher = photoFetcher;
        mImageViewLayoutParams = new RelativeLayout.LayoutParams(CELL_WIDTH, CELL_HEIGHT);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mErrorBitmapDrawable = new BitmapDrawable(BitmapFactory.decodeResource(context.getResources(), R.drawable.error_download));
    }

    @Override
    public int getCount() {
        return mPhotoInfoList.size();
    }

    @Override
    public Object getItem(int i) {
        return mPhotoInfoList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.grid_view_item, viewGroup, false);
            ImageView imageView = convertView.findViewById(R.id.flickr_photo_image_view);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setLayoutParams(mImageViewLayoutParams);
        }
        FlickrPhotoInfo flickrPhotoInfo = mPhotoInfoList.get(i);
        mPhotoFetcher.fetchImage(flickrPhotoInfo, convertView, this);
        return convertView;
    }

    @Override
    public void onBitmapDrawableFetchedForView(View view, BitmapDrawable bitmapDrawable) {
        setImageBitmap(view, bitmapDrawable);
    }

    private void setImageBitmap(final View view, final BitmapDrawable bitmapDrawable) {
        if(view != null) {
            updateView(view, bitmapDrawable);
        }
    }

    private void updateView(View view, BitmapDrawable bitmapDrawable) {
        ImageView imageView = view.findViewById(R.id.flickr_photo_image_view);
        if(imageView != null) {
            updateImageView(imageView, bitmapDrawable);
        }
        ProgressBar progressBar = view.findViewById(R.id.progress_bar);
        if(progressBar != null) {
            progressBar.setVisibility((bitmapDrawable != null && bitmapDrawable instanceof AsyncDrawable) ? View.VISIBLE : View.GONE);
        }
    }

    private void updateImageView(ImageView imageView, BitmapDrawable bitmapDrawable) {
        if(bitmapDrawable != null && bitmapDrawable.getBitmap() != null) {
            imageView.setImageDrawable(bitmapDrawable);
        } else {
            imageView.setImageDrawable(mErrorBitmapDrawable);
        }
    }
}
