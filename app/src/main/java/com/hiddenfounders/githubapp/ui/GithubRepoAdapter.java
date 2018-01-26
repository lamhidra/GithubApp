package com.hiddenfounders.githubapp.ui;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hiddenfounders.githubapp.R;
import com.hiddenfounders.githubapp.vo.GithubRepo;
import com.squareup.picasso.Picasso;

import java.util.List;

public class GithubRepoAdapter extends
        RecyclerView.Adapter<GithubRepoAdapter.GithubRepoViewHolder>{

    private List<GithubRepo> repoList;
    private Context context;

    public GithubRepoAdapter(List<GithubRepo> repoList, Context context) {
        this.repoList = repoList;
        this.context = context;
    }

    public List<GithubRepo> getRepoList() {
        return repoList;
    }

    public void setRepoList(List<GithubRepo> repoList) {
        this.repoList = repoList;
    }


    @Override
    public GithubRepoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.repo_item_view, parent, false);

        return new GithubRepoViewHolder(v);
    }

    @Override
    public void onBindViewHolder(GithubRepoViewHolder holder, int position) {

        holder.mName.setText(repoList.get(position).getName());
        holder.mDescription.setText(repoList.get(position).getDescription());
        holder.mOwnerName.setText(repoList.get(position).getOwner().getOwnerName());
        holder.mStars.setText(String.valueOf(repoList.get(position).getStars()));
        Picasso.with(context)
                .load(repoList.get(position).getOwner().getAvatarUrl())
                .into(holder.mOwnerAvatar);
    }

    @Override
    public int getItemCount() {
        return repoList != null ? repoList.size(): 0;
    }

     static class GithubRepoViewHolder extends RecyclerView.ViewHolder {

        private TextView  mName;
        private TextView mDescription;
        private TextView mStars;
        private TextView mOwnerName;
        private ImageView mOwnerAvatar;

        public GithubRepoViewHolder(View v) {
            super(v);

            mName = v.findViewById(R.id.tv_reponame);
            mDescription = v.findViewById(R.id.tv_repodesc);
            mOwnerName = v.findViewById(R.id.tv_ownername);
            mStars = v.findViewById(R.id.tv_numberofstars);
            mOwnerAvatar = v.findViewById(R.id.img_avatar);
        }
    }
}
