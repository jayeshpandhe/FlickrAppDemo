package com.example.flickrdemoapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.bgcomm.BackgroundWorker;
import com.bgcomm.models.Response;
import com.bgcomm.ui.DialogicProgressBackgroundCommFragment;
import com.bgcomm.utils.NetworkUtils;
import com.example.flickrdemoapp.R;
import com.web.flickr.FlickrWebServicesImpl;
import com.web.flickr.models.response.FlickrPhotoInfo;
import com.web.flickr.models.response.GetRecentPhotosResponse;

import java.util.ArrayList;
import java.util.List;

public class FlickrPhotoGalleryFragment extends DialogicProgressBackgroundCommFragment implements View.OnClickListener, AbsListView.OnScrollListener, AdapterView.OnItemClickListener {
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
    public static final String FLICKER_PHOTO_DETAILS = "FLICKER_PHOTO_DETAILS";
    private boolean startAutoScroll;
    private boolean scrollBarAtStart;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

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
        } else if (pageToLoad == 1 || pageToLoad <= totalPages) {
            execute(GET_RECENT_IMAGES_REQUEST_ID);
        } else {
            Log.d(TAG, "We have already reached last page");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mPhotoFetcher.setPauseWork(false);
        startAutoScroll = false;
    }

    private void attachListeners() {
        mFlickrPhotoGridView.setOnScrollListener(this);
        mFlickrPhotoGridView.setOnItemClickListener(this);
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
        pageToLoad++;
        Log.d(TAG, "Current page: " + currentPage);
        Log.d(TAG, "Total pages: " + totalPages);
        List<FlickrPhotoInfo> photoInfoList = getRecentPhotosResponse.getPhotosInfo();
        updateUI(photoInfoList);
    }

    private void updateUI(List<FlickrPhotoInfo> photoInfoList) {
        Toast.makeText(getActivity(), getString(R.string.page_loaded_msg, currentPage, totalPages), Toast.LENGTH_LONG).show();
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
        startAutoScroll = false;
    }

    @Override
    public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        boolean scrollBarAtEnd = (firstVisibleItem + visibleItemCount >= totalItemCount);
        scrollBarAtStart = (firstVisibleItem == 0);
        mLoadMoreButton.setVisibility(scrollBarAtEnd ? View.VISIBLE : View.GONE);
        if(scrollBarAtStart) {
            onScrollBarReachedStart();
        }
        if(scrollBarAtEnd) {
            onScrollBarReachedEnd();
        }
    }

    private void onScrollBarReachedStart() {
        if(startAutoScroll) {
            mFlickrPhotoGridView.smoothScrollByOffset(mPhotoAdapter.getCount() - 1);
        }
    }

    private void onScrollBarReachedEnd() {
        if(startAutoScroll) {
            mFlickrPhotoGridView.smoothScrollToPosition(0);
        }
    }
    
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        FlickrPhotoInfo flickrPhotoInfo = mPhotoInfoList.get(i);
        Intent intent = new Intent(getActivity(), FlickrPhotoDetailsActivity.class);
        intent.putExtra(FLICKER_PHOTO_DETAILS, flickrPhotoInfo);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.clear_disk_cache_menu:
                clearDiskCache();
                return true;

            case R.id.clear_mem_cache_menu:
                clearMemCache();
                return true;

            case R.id.start_auto_scroll_menu:
                startAutoScroll();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void startAutoScroll() {
        startAutoScroll = true;
        if(scrollBarAtStart) {
            mFlickrPhotoGridView.smoothScrollToPosition(mPhotoAdapter.getCount() - 1);
        } else {
            mFlickrPhotoGridView.smoothScrollToPosition(0);
        }
    }

    private void clearDiskCache() {
        BackgroundWorker.submitRunnable(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "Clear disk cache");
                PhotoDiskCache.getInstance(getActivity()).clearCache(getActivity());
            }
        });
    }

    private void clearMemCache() {
        Log.d(TAG, "Clear mem cache");
        PhotoMemCache.clearCache();
    }
}
