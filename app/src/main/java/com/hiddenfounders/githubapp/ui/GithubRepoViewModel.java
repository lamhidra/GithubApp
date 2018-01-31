package com.hiddenfounders.githubapp.ui;


import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.hiddenfounders.githubapp.AppExecutors;
import com.hiddenfounders.githubapp.api.GithubApi;
import com.hiddenfounders.githubapp.common.GithubClient;
import com.hiddenfounders.githubapp.db.AppDatabase;
import com.hiddenfounders.githubapp.db.GithubRepoDao;
import com.hiddenfounders.githubapp.repository.GithubRepoRepository;
import com.hiddenfounders.githubapp.repository.NetworkBoundResource;
import com.hiddenfounders.githubapp.util.LiveDataPager;
import com.hiddenfounders.githubapp.vo.GithubRepoResponse;
import com.hiddenfounders.githubapp.vo.Resource;

public class GithubRepoViewModel extends AndroidViewModel {

    private GithubRepoRepository githubRepoRepository;
    private GithubRepoDao repoDao;
    private GithubApi githubApi;
    private AppExecutors appExecutors;

    private NetworkBoundResource repoManager;
    public LiveData<Resource<GithubRepoResponse>> LiveRepos;
    public LiveDataPager LiveDataPager;

    public GithubRepoViewModel(@NonNull Application application) {
        super(application);

        repoDao = AppDatabase
                .getAppDatabase(application.getApplicationContext()).githubRepoDao();
        githubApi = GithubClient.createService(GithubApi.class);
        appExecutors = new AppExecutors();

        githubRepoRepository = new GithubRepoRepository(appExecutors, githubApi, repoDao);

        this.repoManager = githubRepoRepository.getRepos();
        this.LiveDataPager = repoManager.fetchNextPage();
        this.LiveRepos = repoManager.asLiveData();
    }

    public int getNextPage() {
        return (githubRepoRepository.getReposCount() / 30) + 1;
    }

    @Override
    protected void onCleared() {

    }
}
