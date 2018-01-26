package com.hiddenfounders.githubapp.vo;


import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "repos",
        primaryKeys = {"id"})
public class GithubRepo {

    @SerializedName("id")
    private long id;

    @SerializedName("name")
    private String name;

    @SerializedName("description")
    private String description;

    @Embedded
    private GithubRepoOwner owner;

    @SerializedName("stargazers_count")
    private double stars;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getStars() {
        return stars;
    }

    public void setStars(double stars) {
        this.stars = stars;
    }

    public GithubRepoOwner getOwner() {
        return owner;
    }

    public void setOwner(GithubRepoOwner owner) {
        this.owner = owner;
    }

}
