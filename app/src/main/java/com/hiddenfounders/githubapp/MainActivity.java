package com.hiddenfounders.githubapp;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private GithubRepoAdapter mAdapter;

    private LinearLayout mErrorLayout;
    private ProgressBar mProgressBar;
    private TextView mTextError;
    private GithubRepoViewModel model;

    private boolean isLoading = false;
    private boolean isConfChange = false;
    private int count = 0;
    private int scrolling_position = 0;
    private static final String ITEMS_COUNT_KEY = "items_count";
    private static final String LIST_OFFSET_KEY = "list_offset";


    private Observer<Resource<GithubRepoResponse>> liveReposResponse =
            new Observer<Resource<GithubRepoResponse>>() {
        @Override
        public void onChanged(@Nullable Resource<GithubRepoResponse> listResource) {

                Log.e("MainActivity", "success");
                if (listResource.status == Status.SUCCESS) {
                    // Check if configuration changes and reload again
                    if (isConfChange) {
                        PaginationInfo paginationInfo = new PaginationInfo(count, 0);
                        loadPage(paginationInfo);
                        isConfChange = false;
                        return;
                    }


                    isLoading = false;

                    // TODO :: change name to attach instead of addAll
                    mAdapter.addAll(listResource.data.getGithubRepos());

                    Toast.makeText(getApplicationContext(),
                            "pos: " + scrolling_position, Toast.LENGTH_SHORT).show();

                    if (scrolling_position > 0
                            && scrolling_position <= mAdapter.getItemCount()) {
                        mRecyclerView.scrollToPosition(scrolling_position);
                        scrolling_position = 0;
                    }

                    // TODO:: swapAdapter and notify dataset changed.
                } else if (listResource.status == Status.ERROR) {
                    // TODO:: Check if list is empty (1)
                    // TODO:: Display the error accordingly to (1). To strategies (Strategy design pattern).
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
        mTextError =   findViewById(R.id.error_txt_cause);

        if (savedInstanceState != null) {
            count = savedInstanceState.getInt(ITEMS_COUNT_KEY);
            scrolling_position = savedInstanceState.getInt(LIST_OFFSET_KEY);
        }

        mAdapter = new GithubRepoAdapter(new ArrayList<GithubRepo>(), mRecyclerView, this);

        mRecyclerView.setAdapter(mAdapter);

        model = ViewModelProviders.of(this).get(GithubRepoViewModel.class);

        if (model.getReposListener().getValue() != null) {
            if (count > model.getReposListener().getValue()
                    .data.getGithubRepos().size());
            isConfChange = true;
        }

        model.getReposListener().observe(
                    this,
                    liveReposResponse);


        Log.e("Mfirst time", "" + model.getReposListener().getValue());
    }

    @Override
    public void onStart() {
        super.onStart();

        // first time
        if (model.getReposListener().getValue() == null) {
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
        // Should save ItemsCount and the offset

        // call superclass to save any view hierarchy
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
        // One solution is to add it to saveInstanceState
        if (!isLoading) {
            isLoading = true;
            model.getReposPager().fetchNextPage(paginationInfo);
        }
    }

    private void hideErrorView() {
        if (mErrorLayout.getVisibility() == View.VISIBLE) {
            mErrorLayout.setVisibility(View.GONE);
        }
    }

    private void showErrorView() {
        if (mErrorLayout.getVisibility() == View.GONE) {
            mErrorLayout.setVisibility(View.VISIBLE);
        }
    }

}
