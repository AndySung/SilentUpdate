package com.soft.nortek.bean;

import java.util.List;

public class WifiSetChannelBean {
    @com.google.gson.annotations.SerializedName("SetChannel")
    private List<ChannelBean> ChannelBondingList;
    public List<ChannelBean> getChannelBondingList(){
        return ChannelBondingList;
    }

    public void setChannelBondingList(List<ChannelBean> channelBondingList){
        this.ChannelBondingList = channelBondingList;
    }


    public static class ChannelBean {
        private String id;
        @com.google.gson.annotations.SerializedName("ChannelBonding")              //channel bonding
        private String ChannelName;
        @com.google.gson.annotations.SerializedName("RFChannel")           //RF Channel
        private List<RFChannelBean> mRFChannels;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getChannelName() {
            return ChannelName;
        }

        public void setChannelName(String name) {
            this.ChannelName = name;
        }

        public List<RFChannelBean> getRFChannels() {
            return mRFChannels;
        }

        public void setRFChannels(List<RFChannelBean> RFChannels) {
            this.mRFChannels = RFChannels;
        }

        @Override
        public String toString() {
            return ChannelName;
        }
    }

    public static class RFChannelBean {
        private String id;
        @com.google.gson.annotations.SerializedName("RF_Channel")
        private String rfChannelName;

        @com.google.gson.annotations.SerializedName("channelValue")             //获取Channel的值，用于设置RFChannel
        private String channelSetValue;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getRfChannelName() {
            return rfChannelName;
        }

        public void setRfChannelName(String name) {
            this.rfChannelName = name;
        }

        public String getChannelSetValue() {
            return channelSetValue;
        }

        public void setChannelSetValue(String channelSetValue) {
            this.channelSetValue = channelSetValue;
        }
    }
}
