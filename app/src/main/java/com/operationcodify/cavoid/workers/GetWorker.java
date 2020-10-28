package com.operationcodify.cavoid.workers;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.operationcodify.cavoid.utilities.GeneralUtilities;

import java.util.concurrent.TimeUnit;

public class GetWorker extends Worker {
    public GetWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        // TODO implement worker!

        /* Create next instance of the worker, ~12 hours from now! */
        long delay = GeneralUtilities.getSecondsUntilHour(8);
        WorkManager mWorkManager = WorkManager.getInstance(getApplicationContext());
        OneTimeWorkRequest GetRequest = new OneTimeWorkRequest.Builder(GetWorker.class)
                .setInitialDelay(delay, TimeUnit.SECONDS)
                .build();
        mWorkManager.enqueue(GetRequest);
        return Result.success();
    }
}
