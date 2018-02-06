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

import javax.security.auth.callback.Callback;

public abstract class NetworkBoundResource<ResultType, RequestType> {
    private final AppExecutors mAppExecutors;

    public final MediatorLiveData<Resource<ResultType>> liveResult = new MediatorLiveData<>();
    public final LiveDataPager liveDataPager = new LiveDataPager();

    @MainThread
    NetworkBoundResource(AppExecutors appExecutors) {
        this.mAppExecutors = appExecutors;

        this.liveDataPager.observeForever((data) -> loadData());
    }

    @MainThread
    private void setValue(Resource<ResultType> newValue) {
        if (!Objects.equals(liveResult.getValue(), newValue)) {
            liveResult.setValue(newValue);
        }
    }

    private void fetchFromNetwork(final LiveData<ResultType> dbSource) {
        LiveData<ApiResponse<RequestType>> apiResponse = createCall();

        // we re-attach dbSource as a new source, it will dispatch its latest value quickly
        liveResult.addSource(dbSource, newData -> {
            liveResult.setValue(Resource.loading(newData));
            liveResult.removeSource(dbSource);
        });

        liveResult.addSource(apiResponse, response -> {
            liveResult.removeSource(apiResponse);

            if (response.isSuccessful()) {
                mAppExecutors.diskIO().execute(() -> {
                    saveCallResult(processResponse(response));
                    mAppExecutors.mainThread().execute(() -> {
                        // we specially request a new live data,
                        // otherwise we will get immediately last cached value,
                        // which may not be updated with lasted results received from network.
                        final LiveData<ResultType> newDbSource = loadFromDb();
                        liveResult.addSource(newDbSource,
                                newData -> {
                                    liveResult.setValue(Resource.success(newData));
                                    liveResult.removeSource(newDbSource);
                                });
                    });
                });
            } else {
                onFetchFailed();

                // We don't have to send data with the error state
                // dbSource contains the data represented in the UI. loadFromdb() retrieve the new data.
                liveResult.addSource(dbSource,  newData -> {
                    liveResult.setValue(Resource.error(response.errorMessage, null));
                    liveResult.removeSource(dbSource);
                });
            }
        });
    }



    protected void onFetchFailed() {

    }

    // TODO:: doesn't need to know about page.
    // TODO Maybe forcing implementing those two callbacks is better.
    // TODO:: Should accept two callbacks as paramaters. (loadFromDb and createCall)
    private void loadData() {

        liveResult.setValue(Resource.loading(null));

        final LiveData<ResultType> dbSource = loadFromDb();

        liveResult.addSource(dbSource, data -> {
            liveResult.removeSource(dbSource);
            if(shouldFetch(data)) { // Check if database contains data
                fetchFromNetwork(dbSource);
            } else {
                liveResult.addSource(dbSource, newData -> {
                    liveResult.setValue(Resource.success(newData));
                    liveResult.removeSource(dbSource);
                });
            }
        });
    }

    public LiveData<Resource<ResultType>> asLiveData() {
        return liveResult;
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
    protected abstract LiveData<ApiResponse<RequestType>> createCall();

    @WorkerThread
    protected abstract void saveCallResult(@NonNull RequestType item);

}
