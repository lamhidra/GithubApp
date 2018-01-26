package com.hiddenfounders.githubapp;

import android.arch.lifecycle.ViewModelProviders;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.hiddenfounders.githubapp.api.GithubApi;
import com.hiddenfounders.githubapp.common.GithubClient;
import com.hiddenfounders.githubapp.db.AppDatabase;
import com.hiddenfounders.githubapp.db.GithubRepoDao;
import com.hiddenfounders.githubapp.repository.GithubRepoRepository;
import com.hiddenfounders.githubapp.ui.GithubRepoAdapter;
import com.hiddenfounders.githubapp.ui.GithubRepoViewModel;
import com.hiddenfounders.githubapp.vo.Status;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new GithubRepoAdapter(new ArrayList<>(), this);

        GithubRepoDao repoDao =
                AppDatabase.getAppDatabase(getApplicationContext()).githubRepoDao();

        GithubApi githubApi = GithubClient.createService(GithubApi.class);

        GithubRepoViewModel model = ViewModelProviders
                .of(this)
                .get(GithubRepoViewModel.class);

        model.init(new GithubRepoRepository(new AppExecutors(), githubApi, repoDao));


        model.loadRepos().observe(this,
                listResource -> {
            Log.e("MainActivity", "success");
            if (listResource.status == Status.SUCCESS) {
                ((GithubRepoAdapter) mAdapter).setRepoList(listResource.data.getGithubRepos());
                mAdapter.notifyDataSetChanged();
            }


        });

        mRecyclerView.setAdapter(mAdapter);
    }
}
