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

/**
 *
 */
public class GithubRepoViewModel extends AndroidViewModel {

    private NetworkBoundResource mRepoManager;
    private LiveData<Resource<GithubRepoResponse>> liveRepos;
    private LiveDataPager liveDataPager;

    public GithubRepoViewModel(@NonNull Application application) {
        super(application);


        GithubRepoRepository mGithubRepoRepository = new GithubRepoRepository(application);
        this.mRepoManager = mGithubRepoRepository.getRepos();
    }

    /**
     *
     * @return
     */
    public LiveData<Resource<GithubRepoResponse>> getReposListener() {
        if (liveRepos == null) {
            liveRepos = mRepoManager.liveResult;
        }
        return liveRepos;
    }

    /**
     *
     * @return
     */
    public LiveDataPager getReposPager() {
        if (liveDataPager == null) {
            liveDataPager = mRepoManager.liveDataPager;
        }
        return liveDataPager;
    }
}
