package com.soft.nortek.bean;

import java.util.List;

public class WifiModelRateBean {
    @com.google.gson.annotations.SerializedName("SetModel")
    private List<WifiModelRateBean.ModelBean> wifiModelList;
//    private List<WifiModelRateBean.RateBean>  wifiRateList;
    public List<WifiModelRateBean.ModelBean> getModelList(){
        return wifiModelList;
    }
    public void setModelList(List<WifiModelRateBean.ModelBean> ModelList){
        this.wifiModelList = ModelList;
    }
//
//    public List<WifiModelRateBean.RateBean> getRateList(){
//        return wifiRateList;
//    }
//    public void setRateList(List<WifiModelRateBean.RateBean> RateList){
//        this.wifiRateList = RateList;
//    }

    public static class ModelBean {
        private String id;
        @com.google.gson.annotations.SerializedName("Model")
        private String ModelName;
        @com.google.gson.annotations.SerializedName("Rate")
        private List<WifiModelRateBean.RateBean> mRates;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getModelName() {
            return ModelName;
        }

        public void setModelName(String name) {
            this.ModelName = name;
        }

        public List<WifiModelRateBean.RateBean> getRates() {
            return mRates;
        }

        public void setRates(List<WifiModelRateBean.RateBean> Rates) {
            this.mRates = Rates;
        }

        @Override
        public String toString() {
            return ModelName;
        }
    }

    public static class RateBean {
        private String id;
        @com.google.gson.annotations.SerializedName("RateValue")
        private String rateName;

        @com.google.gson.annotations.SerializedName("getRateValue")
        private String getRateValue;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getRateName() {
            return rateName;
        }

        public void setRateName(String name) {
            this.rateName = name;
        }

        public String getGetRateValue() {
            return getRateValue;
        }

        public void setGetRateValue(String getRateValue) {
            this.getRateValue = getRateValue;
        }
    }

}
