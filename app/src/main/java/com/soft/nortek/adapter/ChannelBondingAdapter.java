package com.soft.nortek.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.soft.nortek.bean.WifiSetChannelBean;
import com.soft.nortek.silentupdate.R;

import java.util.ArrayList;
import java.util.List;

public class ChannelBondingAdapter extends BaseAdapter {
    private Context context;
    //布局加载器：将xml转为View对象RelativeLayout
    private LayoutInflater mInflater;

    public ChannelBondingAdapter(Context context) {
        this.context = context;
        //初始化布局加载器
        mInflater = LayoutInflater.from(context);
    }

    private List<WifiSetChannelBean.ChannelBean> mChannelBeanList = new ArrayList<>();

    public void setChannelBondingList(List<WifiSetChannelBean.ChannelBean> mChannelBeanList) {
        this.mChannelBeanList = mChannelBeanList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mChannelBeanList.size();
    }

    @Override
    public Object getItem(int position) {
        return mChannelBeanList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View layout = mInflater.inflate(R.layout.spiner_item_layout,null);
        //初始化布局中的元素
        TextView tv = layout.findViewById(R.id.tv_name);
        tv.setText(mChannelBeanList.get(position).getChannelName());
        return layout;
    }
}
