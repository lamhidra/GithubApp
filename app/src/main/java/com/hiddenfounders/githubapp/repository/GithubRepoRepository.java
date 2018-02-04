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

public class GithubRepoRepository {
    private final GithubApi githubApi;
    private final AppExecutors appExecutors;
    private final GithubRepoDao repoDao;

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

                int count = fetchNextPage().getValue().getCount();
                int offset = fetchNextPage().getValue().getOffset();

                // load range
                return Transformations.map(repoDao.loadRepos(count, offset),
                        githubRepo ->
                    new GithubRepoResponse(githubRepo)
                );
            }

            @Override
            protected boolean shouldFetch(@Nullable GithubRepoResponse data) {
                return data.getGithubRepos().size() == 0;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<GithubRepoResponse>> createCall() {
                return githubApi.getRepos(getNextPage());
            }

            @Override
            protected void saveCallResult(@NonNull GithubRepoResponse repos) {
                // Add current page for pagination.
                repoDao.InsertRepos(repos.getGithubRepos());
            }
        };
    }

    public int getReposCount() {
        return repoDao.reposCount();
    }

    public int getNextPage() {
        return (getReposCount() / 30) + 1;
    }
}