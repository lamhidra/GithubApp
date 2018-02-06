package com.hiddenfounders.githubapp.ui;


import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import com.hiddenfounders.githubapp.repository.GithubRepoRepository;
import com.hiddenfounders.githubapp.repository.NetworkBoundResource;
import com.hiddenfounders.githubapp.util.LiveDataPager;
import com.hiddenfounders.githubapp.vo.GithubRepoResponse;
import com.hiddenfounders.githubapp.vo.Resource;

public class GithubRepoViewModel extends AndroidViewModel {

    private GithubRepoRepository mGithubRepoRepository;

    private NetworkBoundResource mRepoManager;
    public LiveData<Resource<GithubRepoResponse>> liveRepos;
    public LiveDataPager liveDataPager;

    public GithubRepoViewModel(@NonNull Application application) {
        super(application);

        mGithubRepoRepository = new GithubRepoRepository(application);
        this.mRepoManager = mGithubRepoRepository.getRepos();
    }

    public LiveData<Resource<GithubRepoResponse>> getReposListener() {
        if (liveRepos == null) {
            liveRepos = mRepoManager.liveResult;
        }
        return liveRepos;
    }

    public LiveDataPager getReposPager() {
        if (liveDataPager == null) {
            liveDataPager = mRepoManager.liveDataPager;
        }
        return liveDataPager;
    }

    @Override
    protected void onCleared() {
        // repoManager.asLiveData().removeObserver(liveReposResponse);
    }
}
