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

public class RFChannelAdapter extends BaseAdapter {
    private Context context;
    private List<WifiSetChannelBean.RFChannelBean> rfChannelList = new ArrayList<>();
    private String RFChannelName;
    private String RFChannelSetValue;
    private LayoutInflater mInflater;

    public void setRFChannelList(List<WifiSetChannelBean.RFChannelBean> rfChannelList) {
        this.rfChannelList = rfChannelList;
        notifyDataSetChanged();
    }

    public RFChannelAdapter(Context context) {
        this.context = context;
        //初始化布局加载器
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return rfChannelList.size();
    }

    @Override
    public Object getItem(int position) {
        return rfChannelList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public String getName(){
        return RFChannelName;
    }

    public String getRFChannelSetValue(){
        return RFChannelSetValue;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View layout = mInflater.inflate(R.layout.spiner_item_layout,null);
        //初始化布局中的元素
        TextView tv = layout.findViewById(R.id.tv_name);
        tv.setText(rfChannelList.get(position).getRfChannelName());
        RFChannelName = rfChannelList.get(position).getRfChannelName();
        RFChannelSetValue = rfChannelList.get(position).getChannelSetValue();
        return layout;
    }
}
