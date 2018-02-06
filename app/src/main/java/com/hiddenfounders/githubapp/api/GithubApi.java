package com.hiddenfounders.githubapp.api;


import android.arch.lifecycle.LiveData;

import com.hiddenfounders.githubapp.vo.GithubRepo;
import com.hiddenfounders.githubapp.vo.GithubRepoResponse;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;


public interface GithubApi {

    @GET
    LiveData<ApiResponse<GithubRepoResponse>> getRepos(
        @Url String url
    );
}

