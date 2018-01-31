package com.hiddenfounders.githubapp.repository;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
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
    private static GithubRepoRepository INSTANCE;

    public GithubRepoRepository(AppExecutors appExecutors,
                                GithubApi githubApi,
                                GithubRepoDao repoDao) {
        this.githubApi = githubApi;
        this.appExecutors = appExecutors;
        this.repoDao = repoDao;
    }

    public NetworkBoundResource<GithubRepoResponse, GithubRepoResponse> getRepos() {
        return new NetworkBoundResource<GithubRepoResponse, GithubRepoResponse>(appExecutors) {
            @NonNull
            @Override
            protected LiveData<GithubRepoResponse> loadFromDb() {
                // load range
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
            protected LiveData<ApiResponse<GithubRepoResponse>> createCall(int page) {
                return githubApi.getRepos(page);

            }

            @Override
            protected void saveCallResult(@NonNull GithubRepoResponse repos) {
                repoDao.InsertRepos(repos.getGithubRepos());
            }
        }.initialLoad(1);
    }

    public int getReposCount() {
        return repoDao.reposCount();
    }
}



