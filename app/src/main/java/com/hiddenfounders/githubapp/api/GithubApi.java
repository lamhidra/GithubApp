package com.hiddenfounders.githubapp.api;


import android.arch.lifecycle.LiveData;

import com.hiddenfounders.githubapp.util.ApiResponse;
import com.hiddenfounders.githubapp.vo.GithubRepoResponse;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface GithubApi {

    /**
     *
     * @param url
     * @return
     */
    @GET
    LiveData<ApiResponse<GithubRepoResponse>> getRepos(
        @Url String url
    );
}

