package com.example.cavoid;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class APIWorker extends Worker {
    public APIWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Data data = getInputData();
        String title = data.getString("title");
        String message = data.getString("message");
        AppNotificationHandler.deliverNotification(getApplicationContext(), title, message);
        return Result.success();
    }
}
