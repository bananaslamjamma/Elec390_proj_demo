package com.example.elec390_proj_demo;

import java.util.HashMap;
import java.util.Map;

public class ActivePlant {

    public String address;
    public boolean manualFlag, arduinoRefresh, reservoirFlag;

    public int current_moisture;

    public ActivePlant(String address, boolean manualFlag, boolean arduinoRefresh, boolean reservoirFlag, int current_moisture) {
        this.address = address;
        this.manualFlag = manualFlag;
        this.arduinoRefresh = arduinoRefresh;
        this.reservoirFlag = reservoirFlag;
        this.current_moisture = current_moisture;
    }
    public ActivePlant() {
        this.address = "dummy";
        this.manualFlag = false;
        this.arduinoRefresh = false;
        this.reservoirFlag = false;
        this.current_moisture = 0;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isManualFlag() {
        return manualFlag;
    }

    public void setManualFlag(boolean manualFlag) {
        this.manualFlag = manualFlag;
    }

    public boolean isArduinoRefresh() {
        return arduinoRefresh;
    }

    public void setArduinoRefresh(boolean arduinoRefresh) {
        this.arduinoRefresh = arduinoRefresh;
    }

    public boolean isReservoirFlag() {
        return reservoirFlag;
    }

    public void setReservoirFlag(boolean reservoirFlag) {
        this.reservoirFlag = reservoirFlag;
    }

    public int getCurrent_moisture() {
        return current_moisture;
    }

    public void wipeProfile(){
        this.address = "dummy";
        this.manualFlag = false;
        this.arduinoRefresh = false;
        this.reservoirFlag = false;
        this.current_moisture = 0;
    }

    public void setCurrent_moisture(int current_moisture) {
        this.current_moisture = current_moisture;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("address", this.address);
        result.put("current_moisture", this.current_moisture);
        result.put("manualFlag", this.manualFlag);
        result.put("arduinoRefresh", this.arduinoRefresh);
        result.put("reservoirFlag", this.reservoirFlag);
        return result;
    }
}


