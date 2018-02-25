package com.example.flickrdemoapp.ui;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import com.example.flickrdemoapp.R;
import com.web.flickr.models.response.FlickrPhotoInfo;

public class FlickrPhotoDetailsFragment extends Fragment {
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.flickr_photo_details_fragment_layout, container, false);
        Intent intent = getActivity().getIntent();

        if(intent.hasExtra(FlickrPhotoGalleryFragment.FLICKER_PHOTO_DETAILS)) {
            FlickrPhotoInfo flickrPhotoInfo = intent.getParcelableExtra(FlickrPhotoGalleryFragment.FLICKER_PHOTO_DETAILS);
            TextView idTextView = v.findViewById(R.id.photo_id);
            TextView ownerTextView = v.findViewById(R.id.photo_owner);
            TextView serverTextView = v.findViewById(R.id.photo_server);
            TextView titleTextView = v.findViewById(R.id.photo_title);
            TextView urlTextView = v.findViewById(R.id.photo_url);
            idTextView.setText(flickrPhotoInfo.getId());
            ownerTextView.setText(flickrPhotoInfo.getOwner());
            serverTextView.setText(flickrPhotoInfo.getServer());
            titleTextView.setText(flickrPhotoInfo.getTitle());
            String url = constructPhotoURL(flickrPhotoInfo);
            urlTextView.setText(url);
            WebView webView = v.findViewById(R.id.flickr_photo_web_view);
            webView.clearCache(true);
            webView.loadUrl(url);
        } else {
            getActivity().finish();
        }
        return v;
    }

    private String constructPhotoURL(FlickrPhotoInfo flickrPhotoInfo) {
        String farm = "" + flickrPhotoInfo.getFarm();
        String server = flickrPhotoInfo.getServer();
        String id_secret = flickrPhotoInfo.getId() + "_" + flickrPhotoInfo.getSecret();
        // URL format: https://farm{farm-id}.staticflickr.com/{server-id}/{id}_{secret}.jpg
        return getString(R.string.flickr_photo_url, farm, server, id_secret);
    }
}
