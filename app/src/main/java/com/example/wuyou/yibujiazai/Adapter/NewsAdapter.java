package com.example.wuyou.yibujiazai.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.example.wuyou.yibujiazai.Bean.NewsBean;
import com.example.wuyou.yibujiazai.ImageLoader;
import com.example.wuyou.yibujiazai.R;
import java.util.List;

/**
 * Created by wuyou on 2018/11/6.
 */

public class NewsAdapter extends BaseAdapter implements AbsListView.OnScrollListener{

    private List<NewsBean> mList;
    private LayoutInflater mLayoutInflater;
    private ImageLoader mImageLoader;
    public static String[] URLS;
    private int mStart,mEnd;
    private boolean mFirstIn;

    public NewsAdapter(Context context, List<NewsBean> data, ListView listView){
        mList = data;
        mLayoutInflater = LayoutInflater.from(context);
        mImageLoader = new ImageLoader(listView);
        URLS = new String[data.size()];
        for (int i = 0; i < data.size(); i++) {
            URLS[i] = data.get(i).newsIconUrl;
        }
        mFirstIn = true;
        listView.setOnScrollListener(this);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int i) {
        return mList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if (view == null){
            viewHolder = new ViewHolder();
            view = mLayoutInflater.inflate(R.layout.list_item,null);
            viewHolder.ivIcon = (ImageView) view.findViewById(R.id.iv_icon);
            viewHolder.tvTitle = (TextView) view.findViewById(R.id.tv_title);
            viewHolder.tvContent = (TextView) view.findViewById(R.id.tv_content);
            view.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.ivIcon.setImageResource(R.mipmap.ic_launcher);
        String url = mList.get(i).newsIconUrl;
        viewHolder.ivIcon.setTag(url);
//        new ImageLoader().showImage(viewHolder.ivIcon,url);
        mImageLoader.showImageAsncTask(viewHolder.ivIcon,url);
        viewHolder.tvTitle.setText(mList.get(i).newsTitle);
        viewHolder.tvContent.setText(mList.get(i).newsContent);
        return view;
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {
        if (i == SCROLL_STATE_IDLE){
            mImageLoader.loadImage(mStart,mEnd);
        }else {
            mImageLoader.cancelAllTasks();
        }
    }

    @Override
    public void onScroll(AbsListView absListView, int i, int i1, int i2) {
        mStart = i;
        mEnd = i + i1;
        if (mFirstIn && i1 > 0){
            mImageLoader.loadImage(mStart,mEnd);
            mFirstIn = false;
        }
    }


    class ViewHolder{
        public TextView tvTitle,tvContent;
        public ImageView ivIcon;
    }

}
