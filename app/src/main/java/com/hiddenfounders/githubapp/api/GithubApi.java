package com.hiddenfounders.githubapp.api;


import android.arch.lifecycle.LiveData;

import com.hiddenfounders.githubapp.util.ApiResponse;
import com.hiddenfounders.githubapp.vo.GithubRepoResponse;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface GithubApi {
/**
 * @GET declares an HTTP GET request
 * @Path("user") annotation on the userId parameter marks it as a
 * replacement for the {user} placeholder in the @GET path
 */
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

