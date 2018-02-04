package com.hiddenfounders.githubapp.util;

import android.os.Parcelable;

/**
 * Created by ayoub on 04/02/2018.
 */

public class PaginationInfo {

    private int count;
    private int offset;

    public PaginationInfo() {}

    public PaginationInfo(int count, int offset) {
        this.count = count;
        this.offset = offset;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }
}
