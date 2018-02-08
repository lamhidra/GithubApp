package com.hiddenfounders.githubapp.ui;


import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hiddenfounders.githubapp.R;
import com.hiddenfounders.githubapp.util.PaginationAdapterCallback;
import com.hiddenfounders.githubapp.util.PaginationInfo;
import com.hiddenfounders.githubapp.util.Utils;
import com.hiddenfounders.githubapp.vo.GithubRepo;
import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;
import java.util.List;

import timber.log.Timber;

public class GithubRepoAdapter extends
        RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ITEM = 0;
    private static final int LOADING = 1;
    private static final int RETRY = 2;
    private static final int THRESHOLD = 5;

    private List<GithubRepo> mRepoList;
    private RecyclerView mRecyclerView;
    private WeakReference<Context> mContext;
    private boolean mIsLoading = false;
    private boolean mRetryDisplayed = false;

    public GithubRepoAdapter(Context context,
                             RecyclerView recyclerView,
                             List<GithubRepo> repoList) {

        if (!(context instanceof PaginationAdapterCallback))
            throw new UnsupportedOperationException("PaginationAdapterCallback not implemented. ");

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

                if (dy > 0 && !mRetryDisplayed) {
                    LinearLayoutManager mLayoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
                    int firstVisibleItem = mLayoutManager.findFirstVisibleItemPosition();
                    int itemsCount = getItemCount();

                    if ((itemsCount - firstVisibleItem) <= THRESHOLD) {
                        PaginationInfo paginationInfo = new PaginationInfo(Utils.PAGE_SIZE,
                                GithubRepoAdapter.this.getItemCount() - 1);
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
                        .inflate(R.layout.item_repo, parent, false);
                return new GithubRepoViewHolder(dataView);

            case LOADING:
                View progressView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_repo_progress, parent, false);
                return new ProgressViewHolder(progressView);

            default:
                View retryView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_repo_retry, parent, false);
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

    public void removeAll() {
        mRepoList.clear();
    }

    /**
     *
     * @param repo
     */
    public void add(GithubRepo repo) {
        mRepoList.add(repo);
        notifyItemInserted(mRepoList.size() - 1);
    }

    /**
     *
     * @param githubRepos
     */
    public void addAll(List<GithubRepo> githubRepos) {
        for (GithubRepo repo : githubRepos) {
            add(repo);
        }
    }

    /**
     *
     */
    public void addLoadingFooter() {
        if (!mIsLoading) {
            mIsLoading = true;
            add(new GithubRepo());
        }
    }

    /**
     *
     */
    public void addRetryFooter() {
        if (!mRetryDisplayed) {
            mRetryDisplayed = true;
            add(new GithubRepo());
        }
    }

    /**
     *
     */
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

    /**
     *
     */
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

    /**
     *
     */
    public void retryLoadingPage() {
        if (mRetryDisplayed) {
            PaginationInfo paginationInfo = new PaginationInfo(Utils.PAGE_SIZE,
                    GithubRepoAdapter.this.getItemCount() - 1);
            ((PaginationAdapterCallback) mContext.get()).loadPage(paginationInfo);
        }
    }

    /**
     *  Provide a reference to the views for each repository item.
     */
    class GithubRepoViewHolder
            extends RecyclerView.ViewHolder {

        private TextView mName;
        private TextView mDescription;
        private TextView mStars;
        private TextView mOwnerName;
        private ImageView mOwnerAvatar;

        public GithubRepoViewHolder(View v) {
            super(v);

            mName = v.findViewById(R.id.textview_repoitem_name);
            mDescription = v.findViewById(R.id.textview_repoitem_desc);
            mOwnerName = v.findViewById(R.id.textview_repoitem_ownername);
            mStars = v.findViewById(R.id.textview_repoitem_numberofstars);
            mOwnerAvatar = v.findViewById(R.id.imageview_repoitem_avatar);
        }

        public void bindTo(GithubRepo repo) {
            mName.setText(repo.getName());
            mDescription.setText(repo.getDescription());
            mOwnerName.setText(repo.getOwner().getOwnerName());
            mStars.setText(String.valueOf(Utils.formatStars(repo.getStars())));
            try {
                Picasso.with(itemView.getContext())
                        .load(repo.getOwner().getAvatarUrl())
                        .into(mOwnerAvatar);
            } catch (IllegalArgumentException ex) {
                Timber.e(ex, "Avatar url is not valid.");
            }
        }
    }

    /**
     *  Provide a reference to the progressbar view item.
     *  When the data is being loaded from the data source,
     *  This view item will be appended to the bottom of the list.
     */
    class ProgressViewHolder extends RecyclerView.ViewHolder{
        private ProgressBar mProgressBar;
        public ProgressViewHolder(View itemView) {
            super(itemView);
            mProgressBar = itemView.findViewById(R.id.progressbar_repoitem_bottombp);
        }
    }


    /**
     *  Provide a reference to the retry view item.
     *  In an error case this view item will be appended to the bottom of the list.
     */
    class RetryViewHolder
            extends RecyclerView.ViewHolder
            implements View.OnClickListener{

        private ImageButton mRetryButton;

        public RetryViewHolder(View itemView) {
            super(itemView);

            mRetryButton = itemView.findViewById(R.id.imagebutton_repoitem_retry);
            itemView.setOnClickListener(this);
            mRetryButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            retryLoadingPage();
        }
    }
}
