package com.example.flickrdemoapp.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.GridView;

import com.bgcomm.models.Response;
import com.bgcomm.ui.DialogicProgressBackgroundCommFragment;
import com.bgcomm.utils.NetworkUtils;
import com.example.flickrdemoapp.R;
import com.web.flickr.FlickrWebServicesImpl;
import com.web.flickr.models.response.FlickrPhotoInfo;
import com.web.flickr.models.response.GetRecentPhotosResponse;

import java.util.ArrayList;
import java.util.List;

public class FlickrPhotoGalleryFragment extends DialogicProgressBackgroundCommFragment implements View.OnClickListener, AbsListView.OnScrollListener {
    private static String TAG = FlickrPhotoGalleryFragment.class.getSimpleName();
    private final int GET_RECENT_IMAGES_REQUEST_ID = 1;
    private int pageToLoad = 1;
    private int currentPage = 0;
    private int totalPages = 0;
    private GridView mFlickrPhotoGridView;
    private Button mLoadMoreButton;
    private PhotoAdapter mPhotoAdapter;
    private List<FlickrPhotoInfo> mPhotoInfoList = new ArrayList<>();
    private PhotoFetcher mPhotoFetcher;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.flickr_photo_gallery_screen, container, false);
        mFlickrPhotoGridView = v.findViewById(R.id.photo_grid_view);
        mLoadMoreButton = v.findViewById(R.id.load_more);
        mPhotoFetcher = new PhotoFetcher(getActivity(), R.drawable.white_bg);
        mPhotoAdapter = new PhotoAdapter(getActivity(), mPhotoInfoList, mPhotoFetcher);
        mFlickrPhotoGridView.setAdapter(mPhotoAdapter);
        attachListeners();
        getRecentPhotosFromFlicker();
        return v;
    }

    private void getRecentPhotosFromFlicker() {
        if (!NetworkUtils.isNetworkPresent(getActivity())) {
            showAlertDialog(getString(R.string.network_not_available), null);
        } else if (pageToLoad == 1) {
            execute(GET_RECENT_IMAGES_REQUEST_ID);
        } else if (pageToLoad < totalPages) {
            pageToLoad++;
            execute(GET_RECENT_IMAGES_REQUEST_ID);
        } else {
            Log.e(TAG, "We have already reached last page");
        }
    }

    private void attachListeners() {
        mFlickrPhotoGridView.setOnScrollListener(this);
        mLoadMoreButton.setOnClickListener(this);
    }

    @Override
    public Response executeRequest(Object requestType) throws Exception {
        Integer requestTyp = (Integer) requestType;
        switch (requestTyp) {
            case GET_RECENT_IMAGES_REQUEST_ID:
                return executeGetRecentImageRequest();
            default:
                return null;
        }
    }

    private Response executeGetRecentImageRequest() throws Exception {
        Log.d(TAG, "Load page: " + pageToLoad);
        FlickrWebServicesImpl flickrWebServices = new FlickrWebServicesImpl();
        return flickrWebServices.executeGetRecentPhotosRequest(pageToLoad);
    }

    @Override
    public void onSuccess(Object requestType, Response response) {
        Integer requestTyp = (Integer) requestType;
        switch (requestTyp) {
            case GET_RECENT_IMAGES_REQUEST_ID:
                onGetRecentImageSuccess(response);
                break;
        }
    }

    private void onGetRecentImageSuccess(Response response) {
        // No need to null check, it is already handled by AsyncTask
        GetRecentPhotosResponse getRecentPhotosResponse = (GetRecentPhotosResponse) response;
        currentPage = getRecentPhotosResponse.getPage();
        totalPages = getRecentPhotosResponse.getPages();
        Log.d(TAG, "Current page: " + currentPage);
        Log.d(TAG, "Total pages: " + totalPages);
        List<FlickrPhotoInfo> photoInfoList = getRecentPhotosResponse.getPhotosInfo();
        updateUI(photoInfoList);
    }

    private void updateUI(List<FlickrPhotoInfo> photoInfoList) {
        if (photoInfoList != null && photoInfoList.size() > 0) {
            mPhotoInfoList.addAll(photoInfoList);
            mPhotoAdapter.notifyDataSetChanged();
        }
        mLoadMoreButton.setEnabled(currentPage < totalPages);
    }

    @Override
    public void onError(Object requestType, Response response) {
        Integer requestTyp = (Integer) requestType;
        switch (requestTyp) {
            case GET_RECENT_IMAGES_REQUEST_ID:
                showErrorMsg(response);
                break;
        }
    }

    private void showErrorMsg(Response response) {
        String msg;
        if (response != null && !TextUtils.isEmpty(response.getMessage())) {
            msg = response.getMessage();
        } else {
            msg = getString(R.string.generic_error_message);
        }
        showAlertDialog(msg, null);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.load_more:
                onLoadMoreButtonClicked();
                break;
        }
    }

    private void onLoadMoreButtonClicked() {
        getRecentPhotosFromFlicker();
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int scrollState) {
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
            onGridViewScrollStarted();
        } else {
            onGridViewScrollFinished();
        }
    }

    private void onGridViewScrollStarted() {
        mPhotoFetcher.setPauseWork(true);
    }

    private void onGridViewScrollFinished() {
        mPhotoFetcher.setPauseWork(false);
    }

    @Override
    public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        boolean reachedEnd = (firstVisibleItem + visibleItemCount >= totalItemCount);
        Log.d(TAG, "Reached end");
        mLoadMoreButton.setVisibility(reachedEnd ? View.VISIBLE : View.GONE);
    }
}
