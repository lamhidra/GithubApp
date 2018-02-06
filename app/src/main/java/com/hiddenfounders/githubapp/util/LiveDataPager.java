package com.hiddenfounders.githubapp.util;

import android.arch.lifecycle.MutableLiveData;


public class LiveDataPager
        extends MutableLiveData<PaginationInfo> {

    public void loadNextPage(PaginationInfo paginationInfo) {
        setValue(paginationInfo);
    }
}
