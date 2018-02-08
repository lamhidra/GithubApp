package com.hiddenfounders.githubapp.repository;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import com.hiddenfounders.githubapp.AppExecutors;
import com.hiddenfounders.githubapp.util.ApiResponse;
import com.hiddenfounders.githubapp.util.LiveDataPager;
import com.hiddenfounders.githubapp.vo.Resource;

import java.util.Objects;

/**
 * A generic class that can provide a resource backed by both a Local data source and the network.
 *
 * @param <ResultType> Type for the Resource data
 * @param <RequestType> Type for the API response
 */
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

    /**
     * Fetches data from the network and save it to the local data source.
     * Returns the response if the network is reachable or error if is not.
     * */
    private void fetchFromNetwork() {
        LiveData<ApiResponse<RequestType>> apiResponse = createCall();

        liveResult.addSource(apiResponse, response -> {
            liveResult.removeSource(apiResponse);

            if (response.isSuccessful()) {
                mAppExecutors.diskIO().execute(() -> {
                    saveCallResult(processResponse(response));
                    mAppExecutors.mainThread().execute(() -> {
                        // we specially request a new live data,
                        // otherwise we will get immediately last cached value,
                        // which may not be updated with lasted results received from network.
                        final LiveData<ResultType> newDbSource = loadFromLocalDS();
                        liveResult.addSource(newDbSource,
                                newData -> {
                                    setValue(Resource.success(newData));
                                    liveResult.removeSource(newDbSource);
                                });
                    });
                });
            } else {
                onFetchFailed();
                setValue(Resource.error(response.errorMessage, null));

            }
        });
    }



    protected void onFetchFailed() {

    }

    /**
     * Tries to load data from the local data source and checks whether the result is good enough
     * to be dispatched or it should be fetched from the network.
     */
    private void loadData() {

        liveResult.setValue(Resource.loading(null));

        final LiveData<ResultType> dbSource = loadFromLocalDS();

        liveResult.addSource(dbSource, data -> {
            liveResult.removeSource(dbSource);
            if(shouldFetch(data)) {
                fetchFromNetwork();
            } else {
                liveResult.addSource(dbSource, newData -> {
                    setValue(Resource.success(newData));
                    liveResult.removeSource(dbSource);
                });
            }
        });
    }

    /**
     * Utility method that takes the network response and return the body.
     *
     * @param response  Holds the code, data and error message of the network response.
     * @return The body of the network response.
     */
    @WorkerThread
    protected RequestType processResponse(ApiResponse<RequestType> response) {
        return response.body;
    }

    /**
     * Loads data from the local data source.
     * @return LiveData That notifies observers when the data is available.
     */
    @NonNull
    @MainThread
    protected abstract LiveData<ResultType> loadFromLocalDS();

    /**
     * Checks if the data loaded from the local data source meets certain conditions.
     * @param data the data loaded from the local data source.
     * @return True or False.
     */
    @MainThread
    protected abstract boolean shouldFetch(@Nullable ResultType data);

    /**
     * Responsible for creating a remote server call.
     *
     * @return LiveData
     */
    @NonNull
    @MainThread
    protected abstract LiveData<ApiResponse<RequestType>> createCall();

    /**
     * Saves the data fetched from the network to the local data source,
     */
    @WorkerThread
    protected abstract void saveCallResult(@NonNull RequestType item);

}
