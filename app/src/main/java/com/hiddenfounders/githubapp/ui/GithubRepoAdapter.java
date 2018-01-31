package com.hiddenfounders.githubapp.ui;


import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.hiddenfounders.githubapp.R;
import com.hiddenfounders.githubapp.util.PaginationAdapterCallback;
import com.hiddenfounders.githubapp.vo.GithubRepo;

import java.util.List;

public class GithubRepoAdapter extends
        RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<GithubRepo> repoList;
    private RecyclerView mRecyclerView;
    private boolean isLoadingAdded = false;
    private boolean retryPageLoad = false;
    private String errorMsg;
    private Context context;

    public GithubRepoAdapter(List<GithubRepo> repoList,
                             RecyclerView recyclerView,
                             Context context
    ) {

        if (!(context instanceof PaginationAdapterCallback))
            throw new UnsupportedOperationException("Must implement PaginationAdapterCallback");

        this.repoList = repoList;
        this.mRecyclerView = recyclerView;
        this.context = context;
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                // check if dy is positive.
                if (dy > 0) {
                    LinearLayoutManager mLayoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
                    //Log.e("Adapter", "itemCOunt: " + mLayoutManager.findFirstVisibleItemPosition());
                    int firstVisibleItem = mLayoutManager.findFirstVisibleItemPosition();
                    int itemsCount = getItemCount();

                    if ((itemsCount - firstVisibleItem) == 30) {
                        Log.e("Adapter", "itemCOunt: " + "Load");
                        ((PaginationAdapterCallback) context).retryPageLoad();
                    }
                }
            }
        });
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        switch (viewType) {
            case 1:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.repo_item_view, parent, false);
                return new GithubRepoErrViewHolder(v);
            default:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.repo_item_view, parent, false);
                return new GithubRepoViewHolder(v);
        }
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case 0:
                GithubRepo repo = repoList.get(position);
                if (repo != null) {
                    ((GithubRepoViewHolder) holder).bindTo(repo);
                }
                break;

            case 1:
                GithubRepoErrViewHolder errVH = (GithubRepoErrViewHolder) holder;
                if (retryPageLoad) {
                    errVH.setErrLayoutVisibility(View.VISIBLE);
                    errVH.setPbVisibility(View.GONE);

                    errVH.updateErrMessage(
                            errorMsg != null ? errorMsg :
                                    context.getString(R.string.error_msg_unknown)
                    );
                } else {
                    errVH.setErrLayoutVisibility(View.GONE);
                    errVH.setPbVisibility(View.VISIBLE);
                    ((PaginationAdapterCallback) context).retryPageLoad();
                }
                break;
        }

    }

    @Override
    public int getItemViewType(int position) {
        //return (position == repoList.size() - 1 && isLoadingAdded) ? 1 : 0;
        return 0;
    }

    @Override
    public int getItemCount() {
        return repoList != null ? repoList.size() : 0;
    }

    public void showRetry(boolean show, @Nullable String errorMsg) {
        retryPageLoad = show;
        notifyItemChanged(repoList.size() - 1);

        if (errorMsg != null)
            this.errorMsg = errorMsg;
    }

    public void add(GithubRepo r) {
        repoList.add(r);
        notifyItemInserted(repoList.size() - 1);
    }

    public void addAll(List<GithubRepo> moveRepos) {
        for (GithubRepo repo : moveRepos) {
            add(repo);
        }
    }


    public boolean isEmpty() {
        return getItemCount() == 0;
    }

    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new GithubRepo());
    }

    public void remove(GithubRepo repo) {
        int position = repoList.indexOf(repo);
        if (position > -1) {
            repoList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        isLoadingAdded = false;
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }

    public GithubRepo getItem(int position) {
        return repoList.get(position);
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = repoList.size() - 1;
        GithubRepo repo = getItem(position);

        if (repo != null) {
            repoList.remove(position);
            notifyItemRemoved(position);
        }
    }
}
