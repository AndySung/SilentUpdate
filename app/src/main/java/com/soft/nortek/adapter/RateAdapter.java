package com.soft.nortek.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.soft.nortek.bean.WifiModelRateBean;
import com.soft.nortek.silentupdate.R;

import java.util.ArrayList;
import java.util.List;

public class RateAdapter extends BaseAdapter {
    private Context context;
    private List<WifiModelRateBean.RateBean> rateList = new ArrayList<>();
    private String RateName;
    private String RateSetValue;
    private LayoutInflater mInflater;

    public void setRateList(List<WifiModelRateBean.RateBean> rateList) {
        this.rateList = rateList;
        notifyDataSetChanged();
    }

    public RateAdapter(Context context) {
        this.context = context;
        //初始化布局加载器
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return rateList.size();
    }

    @Override
    public Object getItem(int position) {
        return rateList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public String getName(){
        return RateName;
    }

    public String getRateSetValue(){
        return RateSetValue;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View layout = mInflater.inflate(R.layout.spiner_item_layout,null);
        //初始化布局中的元素
        TextView tv = layout.findViewById(R.id.tv_name);
        tv.setText(rateList.get(position).getRateName());
        RateName = rateList.get(position).getRateName();
        RateSetValue = rateList.get(position).getGetRateValue();
        return layout;
    }
}
