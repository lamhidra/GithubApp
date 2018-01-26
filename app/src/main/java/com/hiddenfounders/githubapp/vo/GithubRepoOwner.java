package com.hiddenfounders.githubapp.vo;


import com.google.gson.annotations.SerializedName;

public class GithubRepoOwner {

    @SerializedName("login")
    private String ownerName;

    @SerializedName("avatar_url")
    private String avatarUrl;

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
