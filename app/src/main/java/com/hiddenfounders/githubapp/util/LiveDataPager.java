package com.hiddenfounders.githubapp.util;

import android.arch.lifecycle.MutableLiveData;
import android.graphics.PaintFlagsDrawFilter;


public class LiveDataPager extends MutableLiveData<PaginationInfo> {

    public void fetchNextPage(PaginationInfo paginationInfo) {
        setValue(paginationInfo);
    }
}
