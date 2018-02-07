package com.hiddenfounders.githubapp.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.hiddenfounders.githubapp.vo.GithubRepo;
import com.hiddenfounders.githubapp.vo.GithubRepoResponse;

import java.util.List;

@Dao
public interface GithubRepoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void InsertRepos(List<GithubRepo> repos);

    @Query("SELECT * FROM repos ORDER BY stars desc LIMIT :count OFFSET :offset")
    LiveData<List<GithubRepo>> loadRepos(int count, int offset);

    @Query("SELECT COUNT(*) from repos")
    int reposCount();
}
