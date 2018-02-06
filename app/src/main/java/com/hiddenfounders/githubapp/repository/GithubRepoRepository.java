package com.hiddenfounders.githubapp.repository;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.hiddenfounders.githubapp.AppExecutors;
import com.hiddenfounders.githubapp.api.ApiResponse;
import com.hiddenfounders.githubapp.api.GithubApi;
import com.hiddenfounders.githubapp.common.GithubClient;
import com.hiddenfounders.githubapp.db.AppDatabase;
import com.hiddenfounders.githubapp.db.GithubRepoDao;

import com.hiddenfounders.githubapp.util.Utils;
import com.hiddenfounders.githubapp.vo.GithubRepoResponse;

import java.text.SimpleDateFormat;
import java.util.Date;

public class GithubRepoRepository {
    private final GithubApi mGithubApi;
    private final AppExecutors mAppExecutors;
    private final GithubRepoDao mRepoDao;

    public GithubRepoRepository(Application application) {

        this.mRepoDao = AppDatabase
                .getAppDatabase(application.getApplicationContext()).githubRepoDao();
        this.mGithubApi = GithubClient.createService(GithubApi.class);
        this.mAppExecutors = new AppExecutors();
    }

    public NetworkBoundResource<GithubRepoResponse, GithubRepoResponse> getRepos() {
        return new NetworkBoundResource<GithubRepoResponse, GithubRepoResponse>(mAppExecutors) {
            @NonNull
            @Override
            protected LiveData<GithubRepoResponse> loadFromDb() {

                int count = liveDataPager.getValue().getCount();
                int offset = liveDataPager.getValue().getOffset();

                // load range
                LiveData<GithubRepoResponse> liveRepo =
                        Transformations.map(mRepoDao.loadRepos(count, offset),
                        githubRepo -> new GithubRepoResponse(githubRepo)
                );

                return liveRepo;
            }

            @Override
            protected boolean shouldFetch(@Nullable GithubRepoResponse data) {
                return data.getGithubRepos().size() == 0;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<GithubRepoResponse>> createCall() {
                Date today = new Date();
                today = Utils.subtractDays(today, 30);
                SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
                String date = DATE_FORMAT.format(today);
                String selection = "search/repositories?q=created:>" + date + "&sort=stars&order=desc&page=" + getNextPage();
                return mGithubApi.getRepos(selection);
            }

            @Override
            protected void saveCallResult(@NonNull GithubRepoResponse repos) {
                mRepoDao.InsertRepos(repos.getGithubRepos());
            }
        };
    }

    private int getReposCount() {
        return mRepoDao.reposCount();
    }

    private int getNextPage() {
        return (getReposCount() / 30) + 1;
    }

}