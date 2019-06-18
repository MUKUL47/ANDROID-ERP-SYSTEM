package com.example.erpp;

import android.support.annotation.NonNull;

import androidx.work.Worker;

import javax.xml.transform.Result;

public class WorkManage extends Worker {
    @Override
    public Worker.WorkerResult doWork() {
        new NotificationCenter().checkNotification();
        return WorkerResult.SUCCESS;
    }
}
