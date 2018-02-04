package com.hiddenfounders.githubapp.ui;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hiddenfounders.githubapp.R;
import com.hiddenfounders.githubapp.vo.GithubRepo;
import com.squareup.picasso.Picasso;

public class GithubRepoViewHolder
        extends RecyclerView.ViewHolder {

    private TextView mName;
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

    public void bindTo(GithubRepo repo) {

        mName.setText(repo.getName());
        mDescription.setText(repo.getDescription());
        mOwnerName.setText(repo.getOwner().getOwnerName());
        mStars.setText(String.valueOf(repo.getStars()));
        Picasso.with(itemView.getContext())
                .load(repo.getOwner().getAvatarUrl())
                .into(mOwnerAvatar);
    }

}
