package com.hiddenfounders.githubapp.util;

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
