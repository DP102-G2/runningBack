package com.g2.runningback.Common;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.g2.runningback.R;
import com.google.gson.JsonObject;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageTask extends AsyncTask<Object, Integer, Bitmap> {

    private final static String TAG = "ImageTask";
    private String url;
    private int id, imageSize,ad_no;
    private String pro_no;
    /* ImageTask的屬性strong參照到SpotListFragment內的imageView不好，
    會導致SpotListFragment進入背景時imageView被參照而無法被釋放，
    而且imageView會參照到Context，也會導致Activity無法被回收。
    改採weak參照就不會阻止imageView被回收 */
    private WeakReference<ImageView> imageViewWeakReference;

    public ImageTask(String url, String pro_no, int imageSize) {
        this.url = url;
        this.pro_no = pro_no;
        this.imageSize = imageSize;
    }

    public ImageTask(String url, int ad_no, int imageSize) {
        this(url, ad_no, imageSize, null);
    }

    public ImageTask(String url, int ad_no, int imageSize, ImageView imageView) {
        this.url = url;
        this.ad_no = ad_no;
        this.imageSize = imageSize;
        this.imageViewWeakReference = new WeakReference<>(imageView);

    }

    public ImageTask(String url, String pro_no, int imageSize, ImageView imageView) {
        this.url = url;
        this.pro_no = pro_no;
        this.imageSize = imageSize;
        this.imageViewWeakReference = new WeakReference<>(imageView);
    }


    @Override
    protected Bitmap doInBackground(Object... objects) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("action", "getImage");
        jsonObject.addProperty("id", id);
        jsonObject.addProperty("pro_no",pro_no);
        jsonObject.addProperty("ad_no",ad_no);
        jsonObject.addProperty("imageSize", imageSize);
        return getRemoteImage(url, jsonObject.toString());
    }

    private Bitmap getRemoteImage(String url, String jsonOut) {

        HttpURLConnection connection ;
        Bitmap bitmap = null;

        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setRequestMethod("POST");
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
            bw.write(jsonOut);

            Log.e(TAG, "Output: " + jsonOut);
            bw.close();

            int responseCode = connection.getResponseCode();

            if (responseCode == 200) {
                bitmap = BitmapFactory.decodeStream(new BufferedInputStream(connection.getInputStream()));
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {

        ImageView imageView = imageViewWeakReference.get();
        if (isCancelled() || imageView == null) {
            return;
        }
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else {
            imageView.setImageResource(R.drawable.pro_image2);
        }

    }
}
