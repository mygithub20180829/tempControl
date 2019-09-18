package com.kunlan.game.hellochart;

import android.graphics.Paint;

public class TemperatureBean {
    private String data;
    private float maxTemp;
    private float minTemp;


    public TemperatureBean(String data, float maxTemp, float minTemp) {
       this.maxTemp = maxTemp;
       this.minTemp = minTemp;
       this.data = data;
        }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public float getMaxTemp() {
        return maxTemp;
    }

    public void setMaxTemp(float maxTemp) {
        this.maxTemp = maxTemp;
    }

    public float getMinTemp() {
        return minTemp;
    }

    public void setMinTemp(float minTemp) {
        this.minTemp = minTemp;
    }

    @Override
    public String toString() {
           return "TemperatureBean{" + "maxTemp=" + maxTemp + ", minTemp=" + minTemp + ", data='" + data + '\'' + '}';
        }

}
