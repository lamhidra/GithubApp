package com.hiddenfounders.githubapp.db;


import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.hiddenfounders.githubapp.vo.GithubRepo;

@Database(entities = {GithubRepo.class}, version = 3)
public abstract class AppDatabase extends RoomDatabase{
    public abstract GithubRepoDao githubRepoDao();

    private static AppDatabase INSTANCE;

    public static AppDatabase getAppDatabase(Context context) {

        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context, AppDatabase.class, "github-db")
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }

        return INSTANCE;
    }
}
