package com.motondon.imagedownloader_service.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.motondon.imagedownloader_service.MainFragment;
import com.motondon.imagedownloader_service.thread.ImageDownloaderTask;

public class ImageDownloaderService extends Service implements ImageDownloaderTask.ThreadCallback {

    private static final String TAG = ImageDownloaderService.class.getSimpleName();

    private LocalBroadcastManager mLocalBroadcastManager;

    private Thread mImageDownloaderThread;
    private ImageDownloaderTask imageDownloaderTask;

    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate");
        super.onCreate();

        // Get the localBroadcastManager instance, so that it can communicate with the fragment
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(getApplicationContext());
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "onBind");

        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand");

        Bundle data = intent.getExtras();
        String downloadUrl = data.getString(MainFragment.DOWNLOAD_URI);
        imageDownloaderTask = new ImageDownloaderTask(this, downloadUrl);
        mImageDownloaderThread = new Thread(imageDownloaderTask);

        // Request the ImageDownloaderTask to start the download
        mImageDownloaderThread.start();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy");

        // We should here clean up everything we used.

        super.onDestroy();
    }

    @Override
    public void taskStarted(String fileName) {
        Log.d(TAG, "taskStarted() - Sending to the  " + MainFragment.class.getSimpleName() + " TASK_STARTED action so that it can have a chance to update its GUI with the file (name) being downloaded.");
        Intent i = new Intent(MainFragment.TASK_STARTED);
        i.putExtra(MainFragment.CURRENT_FILE_NAME, fileName);
        mLocalBroadcastManager.sendBroadcast(i);
    }

    @Override
    public void taskFinished(Bitmap bitmap) {
        Log.d(TAG, "taskFinished() - Sending to the  " + MainFragment.class.getSimpleName() + " TASK_FINISHED action in order for it to update its imageFrame with just downloaded image.");
        Intent i = new Intent(MainFragment.TASK_FINISHED);
        i.putExtra(MainFragment.DOWNLOADED_IMAGE, bitmap);
        mLocalBroadcastManager.sendBroadcast(i);

        // When download is finished, call stopSelf() in order to finish this service. If we do not do it, service will never finish
        stopSelf();
    }
}
