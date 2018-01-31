package com.hiddenfounders.githubapp.repository;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.util.Log;
import android.widget.Toast;

import com.hiddenfounders.githubapp.AppExecutors;
import com.hiddenfounders.githubapp.api.ApiResponse;
import com.hiddenfounders.githubapp.util.LiveDataPager;
import com.hiddenfounders.githubapp.vo.Resource;

import java.util.Objects;

public abstract class NetworkBoundResource<ResultType, RequestType> {
    private final AppExecutors appExecutors;

    private final MediatorLiveData<Resource<ResultType>> result = new MediatorLiveData<>();
    private final LiveDataPager load = new LiveDataPager();

    @MainThread
    NetworkBoundResource(AppExecutors appExecutors) {
        this.appExecutors = appExecutors;

        this.load.observeForever((page) -> loadData(page));
    }

    @MainThread
    private void setValue(Resource<ResultType> newValue) {
        if (!Objects.equals(result.getValue(), newValue)) {
            result.setValue(newValue);
        }
    }

    private void fetchFromNetwork(final LiveData<ResultType> dbSource, int page) {
        LiveData<ApiResponse<RequestType>> apiResponse = createCall(page);

        // we re-attach dbSource as a new source, it will dispatch its latest value quickly
        //result.addSource(dbSource, newData -> setValue(Resource.loading(newData)));
        result.addSource(apiResponse, response -> {
            result.removeSource(apiResponse);
            result.removeSource(dbSource);
            // noinspection ConstantConditions
            if (response.isSuccessful()) {
                appExecutors.diskIO().execute(() -> {
                    saveCallResult(processResponse(response));
                    appExecutors.mainThread().execute(() -> {
                        // we specially request a new live data,
                        // otherwise we will get immediately last cached value,
                        // which may not be updated with lasted results received from network.
                        result.addSource(loadFromDb(), newData -> setValue(Resource.success(newData)));
                    });
                });
            } else {
                onFetchFailed();
                result.addSource(dbSource,  newData -> setValue(Resource.error(response.errorMessage, newData)));
            }
        });
    }

    protected void onFetchFailed() {

    }

    private void loadData(int page) {
        result.setValue(Resource.loading(null));
        final LiveData<ResultType> dbSource = loadFromDb();
        result.addSource(dbSource, data -> {
            result.removeSource(dbSource);
            if (shouldFetch(data)) {
                fetchFromNetwork(dbSource, page);
            } else {
                result.addSource(dbSource, newData -> setValue(Resource.success(newData)));
            }
        });
    }

    public NetworkBoundResource<ResultType, RequestType> initialLoad(int firstPage) {
        loadData(firstPage);
        return this;
    }

    public LiveData<Resource<ResultType>> asLiveData() {
        return result;
    }
    public LiveDataPager fetchNextPage() {
        return load;
    }

    @WorkerThread
    protected RequestType processResponse(ApiResponse<RequestType> response) {
        return response.body;
    }

    @NonNull
    @MainThread
    protected abstract LiveData<ResultType> loadFromDb();

    @MainThread
    protected abstract boolean shouldFetch(@Nullable ResultType data);

    @NonNull
    @MainThread
    protected abstract LiveData<ApiResponse<RequestType>> createCall(int page);

    @WorkerThread
    protected abstract void saveCallResult(@NonNull RequestType item);

}
