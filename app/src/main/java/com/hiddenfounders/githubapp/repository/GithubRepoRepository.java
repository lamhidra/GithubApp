package com.hiddenfounders.githubapp.repository;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.hiddenfounders.githubapp.AppExecutors;
import com.hiddenfounders.githubapp.api.ApiResponse;
import com.hiddenfounders.githubapp.api.GithubApi;
import com.hiddenfounders.githubapp.db.GithubRepoDao;

import com.hiddenfounders.githubapp.vo.GithubRepoResponse;
import com.hiddenfounders.githubapp.vo.Resource;


public class GithubRepoRepository {

    private final GithubApi githubApi;
    private final AppExecutors appExecutors;
    private final GithubRepoDao repoDao;

    public GithubRepoRepository(AppExecutors appExecutors, GithubApi githubApi, GithubRepoDao repoDao) {
        this.githubApi = githubApi;
        this.appExecutors = appExecutors;
        this.repoDao = repoDao;
    }

    public LiveData<Resource<GithubRepoResponse>> getRepos() {
        return new NetworkBoundResource<GithubRepoResponse, GithubRepoResponse>(appExecutors) {
            @NonNull
            @Override
            protected LiveData<GithubRepoResponse> loadFromDb() {
                return Transformations.map(repoDao.loadRepos(), githubRepo ->
                    new GithubRepoResponse(githubRepo)
                );
            }

            @Override
            protected boolean shouldFetch(@Nullable GithubRepoResponse data) {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<GithubRepoResponse>> createCall() {
                return githubApi.getRepos();

            }

            @Override
            protected void saveCallResult(@NonNull GithubRepoResponse repos) {
                repoDao.InsertRepos(repos.getGithubRepos());
            }
        }.asLiveData();
    }
}



