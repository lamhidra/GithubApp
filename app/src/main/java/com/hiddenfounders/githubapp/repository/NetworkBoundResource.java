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
    private final AppExecutors appExecutors;

    private final MediatorLiveData<Resource<ResultType>> result = new MediatorLiveData<>();
    private final LiveDataPager load = new LiveDataPager();

    @MainThread
    NetworkBoundResource(AppExecutors appExecutors) {
        this.appExecutors = appExecutors;

        this.load.observeForever((data) -> loadData());
    }

    @MainThread
    private void setValue(Resource<ResultType> newValue) {
        if (!Objects.equals(result.getValue(), newValue)) {
            result.setValue(newValue);
        }
    }


    private void fetchFromNetwork(final LiveData<ResultType> dbSource) {
        LiveData<ApiResponse<RequestType>> apiResponse = createCall();

        // we re-attach dbSource as a new source, it will dispatch its latest value quickly
        result.addSource(apiResponse, response -> {
            result.removeSource(apiResponse);
            // usefulness !!!
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

                // We don't have to send data with the error state
                // dbSource contains the data represented in the UI. loadFromdb() retrieve the new data.
                result.addSource(dbSource,  newData -> setValue(Resource.error(response.errorMessage, null)));
            }
        });
    }



    protected void onFetchFailed() {

    }


    // TODO:: doesn't need to know about page.
    // TODO Maybe forcing implementing those two callbacks is better.
    // TODO:: Should accept two callbacks as paramaters. (loadFromDb and createCall)
    private void loadData() {
        final LiveData<ResultType> dbSource = loadFromDb();
        result.addSource(dbSource, data -> {
            result.removeSource(dbSource);
            if(shouldFetch(data)) { // Check if database contains data
                fetchFromNetwork(dbSource);
            } else {
                result.addSource(dbSource, newData -> setValue(Resource.success(newData)));
            }
            // Remember to handle Complete state
        });
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
    protected abstract LiveData<ApiResponse<RequestType>> createCall();

    @WorkerThread
    protected abstract void saveCallResult(@NonNull RequestType item);

}
