package com.example.wuyou.yibujiazai;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.LruCache;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.wuyou.yibujiazai.Adapter.NewsAdapter;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by wuyou on 2018/11/6.
 */

public class ImageLoader {

    private ImageView mImageView;
    private String mUrl;
    private LruCache<String, Bitmap> mLruCache;
    private ListView mListView;
    private Set<NewsAsyncTask> mTask;

    public ImageLoader(ListView listView) {
        mListView = listView;
        mTask = new HashSet<>();
        int MaxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = MaxMemory / 4;
        mLruCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }
        };
    }


    public void addBitmapToCache(String url, Bitmap bitmap) {
        if (getBitmapFromCache(url) == null) {
            mLruCache.put(url, bitmap);
        }
    }

    public Bitmap getBitmapFromCache(String url) {
        return mLruCache.get(url);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mImageView.getTag().equals(mUrl)) {
                mImageView.setImageBitmap((Bitmap) msg.obj);
            }
        }
    };

    public void showImage(ImageView imageView, final String url) {
        mImageView = imageView;
        mUrl = url;
        new Thread() {
            @Override
            public void run() {
                super.run();
                Bitmap bitmap = getBitmap(url);
                Message message = Message.obtain();
                message.obj = bitmap;
                handler.sendMessage(message);
            }
        }.start();
    }

    public Bitmap getBitmap(String urlString) {
        Bitmap bitmap;
        InputStream is = null;
        try {
            URL url = new URL(urlString);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            is = new BufferedInputStream(httpURLConnection.getInputStream());
            bitmap = BitmapFactory.decodeStream(is);
            httpURLConnection.disconnect();
            return bitmap;
        } catch (java.io.IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public void showImageAsncTask(ImageView imageView, String url) {
        Bitmap bitmap = getBitmapFromCache(url);
        if (bitmap == null) {
            imageView.setImageResource(R.mipmap.ic_launcher);
        } else {
            imageView.setImageBitmap(bitmap);
        }
    }

    public void loadImage(int start, int end){
        for (int i = start; i < end; i++) {
            String url = NewsAdapter.URLS[i];
            Bitmap bitmap = getBitmapFromCache(url);
            if (bitmap == null){
                NewsAsyncTask task = new NewsAsyncTask(url);
                task.execute(url);
                mTask.add(task);
            }else {
                ImageView imageView = (ImageView) mListView.findViewWithTag(url);
                imageView.setImageBitmap(bitmap);
            }
        }
    }

    public void cancelAllTasks() {
        if (mTask != null){
            for (NewsAsyncTask newsAsyncTask : mTask){
                newsAsyncTask.cancel(false);
            }
        }
    }

    private class NewsAsyncTask extends AsyncTask<String, Void, Bitmap> {

//        private ImageView mImageView;
        private String mUrl;

        public NewsAsyncTask(String url) {
//            mImageView = imageView;
            mUrl = url;
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            String url = strings[0];
            Bitmap bitmap = getBitmap(url);
            if (bitmap != null) {
                addBitmapToCache(url, bitmap);
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            ImageView imageView = (ImageView) mListView.findViewWithTag(mUrl);
            if (imageView != null && bitmap != null){
                imageView.setImageBitmap(bitmap);
            }
            mTask.remove(this);
        }
    }
}
