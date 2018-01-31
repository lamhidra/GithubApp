package com.hiddenfounders.githubapp.vo;


import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GithubRepoResponse {
    @SerializedName("items")
    private List<GithubRepo> githubRepos;

    public GithubRepoResponse(List<GithubRepo> githubRepos) {
        this.githubRepos = githubRepos;
    }

    public List<GithubRepo> getGithubRepos() {
        return githubRepos;
    }
}
