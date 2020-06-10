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

public class WifiModelAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater mInflater;
    public WifiModelAdapter(Context context) {
        this.context = context;
        //初始化布局加载器
        mInflater = LayoutInflater.from(context);
    }

    private List<WifiModelRateBean.ModelBean> mModelBeans = new ArrayList<>();

    public void setModelList(List<WifiModelRateBean.ModelBean> mModelBeanList) {
        this.mModelBeans = mModelBeanList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mModelBeans.size();
    }

    @Override
    public Object getItem(int position) {
        return mModelBeans.get(position);
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
        tv.setText(mModelBeans.get(position).getModelName());
        return layout;
    }
}
