package com.hiddenfounders.githubapp.api;


import android.arch.lifecycle.LiveData;

import com.hiddenfounders.githubapp.vo.GithubRepo;
import com.hiddenfounders.githubapp.vo.GithubRepoResponse;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Query;


public interface GithubApi {

    @GET("search/repositories?q=created:>2017-10-22&sort=stars&order=desc")
    LiveData<ApiResponse<GithubRepoResponse>> getRepos(
        @Query("page") int page
    );
}

