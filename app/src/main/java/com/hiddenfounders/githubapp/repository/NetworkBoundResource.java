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
 * Provide the capability to implement Offline first
 * Take the database as the single source of truth ... load the data gradually
 * Implementing two way communication with the viewmodel through
 * the reposipru Using two live data instances liveResult
 * for notifying the view model of new uodats
 * and the livedatapager for requesting new page.
 *
 * @param <ResultType>
 * @param <RequestType>
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
     * Fetch a new page from the network and save it to the database
     * notify the observers with the new updates. (loading, succes, error)
     *
     * @param dbSource
     */
    private void fetchFromNetwork(final LiveData<ResultType> dbSource) {
        LiveData<ApiResponse<RequestType>> apiResponse = createCall();

        // Todo:: remove this line.
        // we re-attach dbSource as a new source, it will dispatch its latest value quickly
        /*liveResult.addSource(dbSource, newData -> {
            liveResult.setValue(Resource.loading(newData));
            liveResult.removeSource(dbSource);
        });*/

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

    /**
     *  database is the single source of truth
     *  look first into the database
     *  look for the data in the network if it doesn't exist in the database
     */
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
