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
 * Holds and manages the observables (LiveData),persisting them over activity configuration changes.
 */
public class GithubRepoViewModel extends AndroidViewModel {

    private NetworkBoundResource mRepoManager;
    private LiveData<Resource<GithubRepoResponse>> liveRepos;
    private LiveDataPager liveDataPager;
    private GithubRepoRepository mGithubRepoRepository;

    public GithubRepoViewModel(@NonNull Application application) {
        super(application);
        mGithubRepoRepository = new GithubRepoRepository(application);
        this.mRepoManager = mGithubRepoRepository.getRepos();
    }

    /**
     * Notify the UI for new updates.
     * @return LiveData A observable
     */
    public LiveData<Resource<GithubRepoResponse>> getReposListener() {
        if (liveRepos == null) {
            liveRepos = mRepoManager.liveResult;
        }
        return liveRepos;
    }

    /**
     * Used to query the data sources for new pages.
     * @return LivaDataPager an observable
     */
    public LiveDataPager getReposPager() {
        if (liveDataPager == null) {
            liveDataPager = mRepoManager.liveDataPager;
        }
        return liveDataPager;
    }

    public void deleteAllRepos() {
        mGithubRepoRepository.deleteAllRepos();
        
    }
}
