package com.hiddenfounders.githubapp;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
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
    private static final int PAGE_SIZE = 30;


    private RecyclerView mRecyclerView;
    private GithubRepoAdapter mAdapter;

    private LinearLayout mErrorLayout;
    private ProgressBar mProgressBar;
    private GithubRepoViewModel mRepoViewModel;

    private boolean mIsLoading = false;
    private int mScrolling_position = 0;
    private boolean mIsRetrying = false;


    /**
     *
     */
    private Observer<Resource<GithubRepoResponse>> mLiveReposResponse =
            new Observer<Resource<GithubRepoResponse>>() {
        @Override
        public void onChanged(@Nullable Resource<GithubRepoResponse> listResource) {
                if (listResource == null) return;

                if (listResource.status == Status.SUCCESS) {
                    hideErrorView();
                    hideProgressBar();

                    if (listResource.data == null) return;
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

        mRecyclerView = findViewById(R.id.recyclervie_repos);
        mRecyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager mLayoutManager= new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mErrorLayout = findViewById(R.id.layout_error);
        mProgressBar = findViewById(R.id.progressbar_initial);
        Button mRetryButton = findViewById(R.id.button_layouterror_retry);

        mRetryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PaginationInfo paginationInfo = new PaginationInfo(PAGE_SIZE, 0);
                loadPage(paginationInfo);
            }
        });

        mAdapter = new GithubRepoAdapter(this, mRecyclerView, new ArrayList<>());

        mRecyclerView.setAdapter(mAdapter);

        mRepoViewModel = ViewModelProviders.of(this).get(GithubRepoViewModel.class);

        if (savedInstanceState != null) {
            int count = savedInstanceState.getInt(ITEMS_COUNT_KEY);
            mScrolling_position = savedInstanceState.getInt(LIST_OFFSET_KEY);

            PaginationInfo paginationInfo = new PaginationInfo(count, 0);
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

    /**
     *
     * @param paginationInfo
     */
    @Override
    public void loadPage(PaginationInfo paginationInfo)
    {
        if (!mIsLoading) mRepoViewModel.getReposPager().loadNextPage(paginationInfo);
    }

    /**
     *
     */
    private void displayProgressBar() {
        if (!mIsLoading) {
            if (mAdapter.getItemCount() > 0) mAdapter.addLoadingFooter();
            else if (mAdapter.getItemCount() == 0) mProgressBar.setVisibility(View.VISIBLE);

            mIsLoading = true;
        }

    }

    /**
     *
     */
    private void hideProgressBar() {
        if (mIsLoading) {
            mProgressBar.setVisibility(View.INVISIBLE);
            mAdapter.removeLoadingFooter();
            mIsLoading = false;
        }

    }

    /**
     *
     */
    private void hideErrorView() {
        if (mIsRetrying) {
            mErrorLayout.setVisibility(View.GONE);
            mAdapter.removeRetryFooter();
            mIsRetrying = false;
        }
    }

    /**
     *
     */
    private void showErrorView() {
        if (!mIsRetrying) {
            if (mAdapter.getItemCount() > 0) mAdapter.addRetryFooter();
            else if (mAdapter.getItemCount() == 0) mErrorLayout.setVisibility(View.VISIBLE);

            mIsRetrying = true;
        }

    }

}
