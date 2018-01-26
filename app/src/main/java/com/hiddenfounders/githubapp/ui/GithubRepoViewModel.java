package com.hiddenfounders.githubapp.ui;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.hiddenfounders.githubapp.repository.GithubRepoRepository;
import com.hiddenfounders.githubapp.vo.GithubRepo;
import com.hiddenfounders.githubapp.vo.GithubRepoResponse;
import com.hiddenfounders.githubapp.vo.Resource;

import java.util.List;

public class GithubRepoViewModel extends ViewModel {

    private GithubRepoRepository githubRepoRepository;

    public void init(GithubRepoRepository githubRepoRepository) {
        this.githubRepoRepository = githubRepoRepository;
    }

    public LiveData<Resource<GithubRepoResponse>> loadRepos() {
        return githubRepoRepository.getRepos();
    }
}
