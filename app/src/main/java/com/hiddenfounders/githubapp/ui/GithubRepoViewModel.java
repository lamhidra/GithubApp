package com.hiddenfounders.githubapp.ui;


import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.IntegerRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

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

// TODO ::
// TODO :: Handle error cases
// TODO :: add cache capabilities to database.
public class GithubRepoViewModel extends AndroidViewModel {

    private GithubRepoRepository githubRepoRepository;
    private GithubRepoDao repoDao;
    private GithubApi githubApi;
    private AppExecutors appExecutors;

    /*Observer<Resource<GithubRepoResponse>> liveReposResponse =
            new Observer<Resource<GithubRepoResponse>>() {
                @Override
                public void onChanged(@Nullable Resource<GithubRepoResponse> listResource) {
                    liveRepos.setValue(listResource);
                }
            }; */

    private NetworkBoundResource repoManager;
    public LiveData<Resource<GithubRepoResponse>> liveRepos;
    public LiveDataPager liveDataPager;

    public GithubRepoViewModel(@NonNull Application application) {
        super(application);

        repoDao = AppDatabase
                .getAppDatabase(application.getApplicationContext()).githubRepoDao();
        githubApi = GithubClient.createService(GithubApi.class);
        appExecutors = new AppExecutors();

        githubRepoRepository = new GithubRepoRepository(appExecutors, githubApi, repoDao);

        this.repoManager = githubRepoRepository.getRepos();
    }

    public LiveData<Resource<GithubRepoResponse>> getReposListener() {
        if (liveRepos == null) {
            liveRepos = repoManager.asLiveData();
        }
        return liveRepos;
    }

    public LiveDataPager getReposPager() {
        if (liveDataPager == null) {
            liveDataPager = repoManager.fetchNextPage();
        }
        return liveDataPager;
    }

    // TODO:: Repository doesn't have to know exactly what's gonna do, it has to just execute
    @Override
    protected void onCleared() {
        // repoManager.asLiveData().removeObserver(liveReposResponse);
    }


}
