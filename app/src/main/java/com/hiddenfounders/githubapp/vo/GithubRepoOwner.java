package com.hiddenfounders.githubapp.vo;


import android.arch.persistence.room.Ignore;

import com.google.gson.annotations.SerializedName;

public class GithubRepoOwner {

    @SerializedName("login")
    private String ownerName;

    @SerializedName("avatar_url")
    private String avatarUrl;

    public GithubRepoOwner() {

    }

    @Ignore
    public GithubRepoOwner(String ownerName, String avatarUrl) {
        this.ownerName = ownerName;
        this.avatarUrl = avatarUrl;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

}
