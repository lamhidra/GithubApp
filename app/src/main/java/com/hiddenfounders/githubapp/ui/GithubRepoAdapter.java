package com.hiddenfounders.githubapp.ui;


import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hiddenfounders.githubapp.R;
import com.hiddenfounders.githubapp.util.PaginationAdapterCallback;
import com.hiddenfounders.githubapp.util.PaginationInfo;
import com.hiddenfounders.githubapp.vo.GithubRepo;

import java.lang.ref.WeakReference;
import java.util.List;

public class GithubRepoAdapter extends
        RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<GithubRepo> mRepoList;
    private RecyclerView mRecyclerView;
    private WeakReference<Context> mContext;
    private boolean mIsLoading = false;
    private boolean mRetryDisplayed = false;
    private static final int ITEM = 0;
    private static final int LOADING = 1;
    private static final int RETRY = 2;

    public GithubRepoAdapter(List<GithubRepo> repoList,
                             RecyclerView recyclerView,
                             Context context
    ) {

        if (!(context instanceof PaginationAdapterCallback))
            throw new UnsupportedOperationException("Must implement PaginationAdapterCallback");

        this.mRepoList = repoList;
        this.mRecyclerView = recyclerView;
        this.mContext = new WeakReference<>(context);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                // check if dy is positive.
                if (dy > 0 && !mRetryDisplayed) {
                    LinearLayoutManager mLayoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
                    int firstVisibleItem = mLayoutManager.findFirstVisibleItemPosition();
                    int itemsCount = getItemCount();

                    if ((itemsCount - firstVisibleItem) <= 5) {
                        Log.e("Adapter", "itemCOunt: " + GithubRepoAdapter.this.getItemCount());
                        PaginationInfo paginationInfo = new PaginationInfo(30,
                                GithubRepoAdapter.this.getItemCount());
                        ((PaginationAdapterCallback)
                                GithubRepoAdapter.this.mContext.get()).loadPage(paginationInfo);
                    }
                }
            }
        });
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        switch (viewType) {
            case ITEM:
                View dataView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.repo_item_view, parent, false);
                return new GithubRepoViewHolder(dataView);

            case LOADING:
                View progressView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.repo_prog_view, parent, false);
                return new ProgressViewHolder(progressView);

            default:
                View retryView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.retry_repo_view, parent, false);
                return new RetryViewHolder(retryView);
        }
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (!mIsLoading && !mRetryDisplayed) {
            GithubRepo repo = mRepoList.get(position);
            if (repo != null) {
                ((GithubRepoViewHolder) holder).bindTo(repo);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        return (position == (mRepoList.size() - 1)
                && (mIsLoading || mRetryDisplayed)) ? (mIsLoading ? LOADING: RETRY) : ITEM;
    }

    @Override
    public int getItemCount() {
        return mRepoList != null ? mRepoList.size() : 0;
    }

    public void add(GithubRepo r) {
        mRepoList.add(r);
        notifyItemInserted(mRepoList.size() - 1);
    }

    public void addAll(List<GithubRepo> moveRepos) {
        for (GithubRepo repo : moveRepos) {
            add(repo);
        }
    }

    public void addLoadingFooter() {
        if (!mIsLoading) {
            mIsLoading = true;
            add(new GithubRepo());
        }
    }

    public void addRetryFooter() {
        if (!mRetryDisplayed) {
            mRetryDisplayed = true;
            add(new GithubRepo());
        }
    }

    public void removeRetryFooter() {
        if (mRetryDisplayed) {
            mRetryDisplayed = false;
            int position = mRepoList.size() - 1;
            GithubRepo repo = mRepoList.get(position);

            if (repo != null) {
                mRepoList.remove(position);
                notifyItemRemoved(position);
            }
        }
    }

    public void removeLoadingFooter() {
        if (mIsLoading) {
            mIsLoading = false;

            int position = mRepoList.size() - 1;
            GithubRepo repo = mRepoList.get(position);

            if (repo != null) {
                mRepoList.remove(position);
                notifyItemRemoved(position);
            }
        }
    }

    public void retryLoadingPage() {
        if (mRetryDisplayed) {
            PaginationInfo paginationInfo = new PaginationInfo(30,
                    GithubRepoAdapter.this.getItemCount());
            ((PaginationAdapterCallback) mContext.get()).loadPage(paginationInfo);
        }
    }


    class ProgressViewHolder extends RecyclerView.ViewHolder{


        private ProgressBar mProgressBar;

        public ProgressViewHolder(View itemView) {
            super(itemView);

            mProgressBar = itemView.findViewById(R.id.pb_repo);
            //mProgressBar.setVisibility(View.VISIBLE);
        }

        public ProgressBar getmProgressBar() {
            return mProgressBar;
        }

        public void setmProgressBar(ProgressBar mProgressBar) {
            this.mProgressBar = mProgressBar;
        }
    }

    class RetryViewHolder
            extends RecyclerView.ViewHolder
            implements View.OnClickListener{

        private ImageButton mRetryButton;

        public RetryViewHolder(View itemView) {
            super(itemView);

            mRetryButton = itemView.findViewById(R.id.loadmore_retry);
            itemView.setOnClickListener(this);
            mRetryButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            retryLoadingPage();
        }
    }
}
