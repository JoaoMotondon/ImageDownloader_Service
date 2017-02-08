package com.motondon.imagedownloader_service.thread;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Prior to start a download, this class will call a ThreadCallback interface method. This will  make
 * ImageDownloaderService class (which implements ThreadCallback interface) to send and intent which will be
 * received by the MainFragment. Then, MainFragment will update its GUI with the fileName for the file being
 * downloaded
 *
 * After the download, another TreadCallback method will be called with the image just downloaded. This will
 * make MainFragment to update its GUI with the image.
 *
 */
public class ImageDownloaderTask implements Runnable {

    private static final String TAG = ImageDownloaderTask.class.getSimpleName();

    // Implemented by the ImageDownloaderService
    public interface ThreadCallback {
        void taskStarted(String fileName);
        void taskFinished(Bitmap bitmap);
    }

    private ThreadCallback mCallback;
    private String mUrl;

    public ImageDownloaderTask(ThreadCallback callback, String url) {
        Log.v(TAG, "Constructor");

        this.mCallback = callback;
        this.mUrl = url;
    }

    @Override
    public void run() {
        Log.v(TAG, "run() - Begin");

        try {
            String fileName = Uri.parse(mUrl).getLastPathSegment();

            // Inform ImageDownloaderService that a download will be started.
            mCallback.taskStarted(fileName);

            Log.v(TAG, "run() - Downloading image...");
            Bitmap bitmap = downloadBitmap(mUrl);
            Log.v(TAG, "run() - Download finished");

            // Inform ImageDownloaderService that a download has been finished.
            mCallback.taskFinished(bitmap);

        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "run() - Exception while trying to download image from url: " + mUrl + ". Message: " + e.getMessage());
        }

        Log.v(TAG, "run() - End");
    }

    /**
     * Download image here
     *
     * @param strUrl
     * @return
     * @throws IOException
     */
    private Bitmap downloadBitmap(String strUrl) throws IOException {
        Bitmap bitmap=null;
        InputStream iStream = null;
        try{
            URL url = new URL(strUrl);
            /** Creating an http connection to communicate with url */
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            /** Connecting to url */
            urlConnection.connect();

            /** Reading data from url */
            iStream = urlConnection.getInputStream();

            /** Creating a bitmap from the stream returned from the url */
            bitmap = BitmapFactory.decodeStream(iStream);

        }catch(Exception e){
            Log.d(TAG, "Exception while downloading url: " + strUrl + ". Error: " + e.toString());
        }finally{
            if (iStream != null) {
                iStream.close();
            }
        }
        return bitmap;
    }
}
