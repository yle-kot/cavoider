package com.example.cavoid.workers;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.cavoid.utilities.Utilities;

import java.util.concurrent.TimeUnit;

public class GetWorker extends Worker {
    public GetWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        long delay = Utilities.getMilliSecondsUntilTime(8);
        WorkManager mWorkManager = WorkManager.getInstance(getApplicationContext());
        OneTimeWorkRequest GetRequest = new OneTimeWorkRequest.Builder(GetWorker.class)
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .build();
        mWorkManager.enqueue(GetRequest);
        return Result.success();
    }
}
