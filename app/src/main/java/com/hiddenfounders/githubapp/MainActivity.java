package com.hiddenfounders.githubapp;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.hiddenfounders.githubapp.ui.GithubRepoAdapter;
import com.hiddenfounders.githubapp.ui.GithubRepoViewModel;
import com.hiddenfounders.githubapp.util.PaginationAdapterCallback;
import com.hiddenfounders.githubapp.util.PaginationInfo;
import com.hiddenfounders.githubapp.vo.GithubRepo;
import com.hiddenfounders.githubapp.vo.GithubRepoResponse;
import com.hiddenfounders.githubapp.vo.Resource;
import com.hiddenfounders.githubapp.vo.Status;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity
        implements PaginationAdapterCallback {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String ITEMS_COUNT_KEY = "items_count";
    private static final String LIST_OFFSET_KEY = "list_offset";


    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private GithubRepoAdapter mAdapter;

    private LinearLayout mErrorLayout;
    private ProgressBar mProgressBar;
    private GithubRepoViewModel mRepoViewModel;

    private boolean mIsLoading = false;
    private int mCount = 0;
    private int mScrolling_position = 0;
    private boolean mIsRetrying = false;


    private Observer<Resource<GithubRepoResponse>> mLiveReposResponse =
            new Observer<Resource<GithubRepoResponse>>() {
        @Override
        public void onChanged(@Nullable Resource<GithubRepoResponse> listResource) {

                if (listResource.status == Status.SUCCESS) {
                    hideErrorView();
                    hideProgressBar();

                    mAdapter.addAll(listResource.data.getGithubRepos());

                    if (mScrolling_position > 0
                            && mScrolling_position <= mAdapter.getItemCount()) {
                        mRecyclerView.scrollToPosition(mScrolling_position);
                        mScrolling_position = 0;
                    }

                } else if (listResource.status == Status.ERROR) {
                    showErrorView();
                    hideProgressBar();
                } else if (listResource.status == Status.LOADING) {
                    hideErrorView();
                    displayProgressBar();
                }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mErrorLayout = findViewById(R.id.error_layout);
        mProgressBar = findViewById(R.id.pb_initial);


        mAdapter = new GithubRepoAdapter(new ArrayList<GithubRepo>(), mRecyclerView, this);

        mRecyclerView.setAdapter(mAdapter);

        mRepoViewModel = ViewModelProviders.of(this).get(GithubRepoViewModel.class);

        if (savedInstanceState != null) {
            mCount = savedInstanceState.getInt(ITEMS_COUNT_KEY);
            mScrolling_position = savedInstanceState.getInt(LIST_OFFSET_KEY);

            PaginationInfo paginationInfo = new PaginationInfo(mCount, 0);
            loadPage(paginationInfo);
        }

        mRepoViewModel.getReposListener().observe(
                    this,
                    mLiveReposResponse);

    }

    @Override
    public void onStart() {
        super.onStart();
        if (mRecyclerView.getAdapter().getItemCount() == 0) {
            PaginationInfo paginationInfo = new PaginationInfo(30, 0);
            loadPage(paginationInfo);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        LinearLayoutManager linearLayoutManager =
                (LinearLayoutManager) mRecyclerView.getLayoutManager();

        outState.putInt(ITEMS_COUNT_KEY, mAdapter.getItemCount());
        outState.putInt(LIST_OFFSET_KEY,
                linearLayoutManager.findFirstCompletelyVisibleItemPosition());

    }

    @Override
    public void loadPage(PaginationInfo paginationInfo)
    {
        if (!mIsLoading) mRepoViewModel.getReposPager().loadNextPage(paginationInfo);
    }

    private void displayProgressBar() {
        if (!mIsLoading) {
            if (mAdapter.getItemCount() > 0) mAdapter.addLoadingFooter();
            else if (mAdapter.getItemCount() == 0) mProgressBar.setVisibility(View.VISIBLE);

            mIsLoading = true;
        }

    }

    private void hideProgressBar() {
        if (mIsLoading) {
            mProgressBar.setVisibility(View.INVISIBLE);
            mAdapter.removeLoadingFooter();
            mIsLoading = false;
        }

    }

    private void hideErrorView() {
        if (mIsRetrying) {
            mErrorLayout.setVisibility(View.GONE);
            mAdapter.removeRetryFooter();
            mIsRetrying = false;
        }
    }

    private void showErrorView() {
        if (!mIsRetrying) {
            if (mAdapter.getItemCount() > 0) mAdapter.addRetryFooter();
            else if (mAdapter.getItemCount() == 0) mErrorLayout.setVisibility(View.VISIBLE);

            mIsRetrying = true;
        }
    }

}
