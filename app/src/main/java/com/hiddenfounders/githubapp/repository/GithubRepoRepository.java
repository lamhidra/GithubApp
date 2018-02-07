package com.hiddenfounders.githubapp.repository;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.hiddenfounders.githubapp.AppExecutors;
import com.hiddenfounders.githubapp.util.ApiResponse;
import com.hiddenfounders.githubapp.api.GithubApi;
import com.hiddenfounders.githubapp.api.GithubClient;
import com.hiddenfounders.githubapp.db.AppDatabase;
import com.hiddenfounders.githubapp.db.GithubRepoDao;

import com.hiddenfounders.githubapp.util.Utils;
import com.hiddenfounders.githubapp.vo.GithubRepoResponse;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Works as a mediator between the viewmodels and datasources
 * It abstract the access to data from the viewmodel
 *
 */
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

    /**
     * Gets github repositories from data source.
     *
     * @return A new Instance of NetworkBoundResource.
     */
    public NetworkBoundResource<GithubRepoResponse, GithubRepoResponse> getRepos() {
        return new NetworkBoundResource<GithubRepoResponse, GithubRepoResponse>(mAppExecutors) {

            /**
             * Query the database asynchronously for a specific range of data.
             * The query result always counts between 0 and 30 items.
             *
             * @return An observable used to subscribe to get database results.
             */
            @NonNull
            @Override
            protected LiveData<GithubRepoResponse> loadFromDb() {

                int count = liveDataPager.getValue() != null ?
                        liveDataPager.getValue().getCount() : Utils.PAGE_SIZE;
                int offset = liveDataPager.getValue() != null ?
                    liveDataPager.getValue().getOffset() : Utils.DEFAULT_STARTING_OFFSET;

                return Transformations.map(mRepoDao.loadRepos(count, offset),
                        githubRepo -> new GithubRepoResponse(githubRepo)
                );

            }

            /**
             * Use the query result returned from the database to decide
             * whether or not to look for the next page from the network.
             *
             * @param data The database query result (loadFromDb).
             * @return False or True.
             */
            @Override
            protected boolean shouldFetch(@Nullable GithubRepoResponse data) {
                return (data.getGithubRepos() != null) && (data.getGithubRepos().size() == 0);
            }

            /**
             *
             *
             * @return livedata
             */
            @NonNull
            @Override
            protected LiveData<ApiResponse<GithubRepoResponse>> createCall() {
                return mGithubApi.getRepos(getNextPageUrl());
            }

            /**
             * Saves the list of repositories into the database.
             *
             * @param repos The list of repositories retrieved from the network.
             */
            @Override
            protected void saveCallResult(@NonNull GithubRepoResponse repos) {
                mRepoDao.InsertRepos(repos.getGithubRepos());
            }
        };
    }

    /**
     * Query the database for the number of repositories.
     *
     * @return The number of repositories stored in the database.
     */
    private int getReposCount() {
        return mRepoDao.reposCount();
    }

    /**
     * Gets the next page index.
     *
     * @return The next page index.
     */
    private int getNextPage() {
        return (getReposCount() / Utils.PAGE_SIZE) + 1;
    }

    /**
     * Gets the next page url.
     *
     * @return The next page url.
     */
    private String getNextPageUrl() {
        Date today = new Date();
        today = Utils.subtractDays(today, 30);
        SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
        String date = DATE_FORMAT.format(today);

         return "search/repositories?q=created:>" +
                 date +
                 "&sort=stars&order=desc&page=" +
                 getNextPage();
    }
}