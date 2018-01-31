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

    private Observer<Resource<GithubRepoResponse>> liveReposResponse =
            new Observer<Resource<GithubRepoResponse>>() {
        @Override
        public void onChanged(@Nullable Resource<GithubRepoResponse> listResource) {
                // Start Loading

                Log.e("MainActivity", "success");
                if (listResource.status == Status.SUCCESS) {
                    hideErrorView();
                    mProgressBar.setVisibility(View.GONE);

                    // Not the first page.
                    //if (mAdapter.getItemCount() > 0) mAdapter.removeLoadingFooter();

                    // swap adapter
                    mRecyclerView.setAdapter(
                            new GithubRepoAdapter(listResource.data.getGithubRepos(),
                                    mRecyclerView,
                                    MainActivity.this));
                    mAdapter.notifyDataSetChanged();

                } else if (listResource.status == Status.ERROR) {
                    mProgressBar.setVisibility(View.GONE);

                    if (listResource.data == null ||
                            (listResource.data != null
                                    && listResource.data.getGithubRepos().size() == 0)) {
                        showErrorView();
                    } else {
                        /*mRecyclerView.setAdapter(
                                new GithubRepoAdapter(listResource.data.getGithubRepos(),
                                        mRecyclerView,
                                        MainActivity.this));
                        mAdapter.notifyDataSetChanged();*/
                        //Toast.makeText(MainActivity.this, "Couldn't connect to network, Try again", Toast.LENGTH_SHORT).show();
                        // message and show retry footer.
                        //mAdapter.showRetry(true, listResource.message);
                    }
                } else if (listResource.status == Status.LOADING) {
                    hideErrorView();
                    mProgressBar.setVisibility(View.VISIBLE);

                    /*if (listResource.data != null) {
                        GithubRepoResponse response = listResource.data;
                        if (response.getGithubRepos().size() == 0)
                            mProgressBar.setVisibility(View.VISIBLE);
                    }*/

                    // If recycler view still computing a layout or scrolling this will throw an exception.
                    // mAdapter.addLoadingFooter();
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

        mAdapter = new GithubRepoAdapter(new ArrayList<GithubRepo>(), mRecyclerView, this);

        model = ViewModelProviders.of(this).get(GithubRepoViewModel.class);

        model.LiveRepos.observe(this, liveReposResponse);

        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void retryPageLoad() {
        model.LiveDataPager.fetchPage(model.getNextPage());
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
