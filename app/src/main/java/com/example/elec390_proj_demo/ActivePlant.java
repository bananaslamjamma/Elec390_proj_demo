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

    public int calcCurrentMoisture() {
        int rMoisture = this.getCurrent_moisture();
        int result = 0;

        if (rMoisture >= 656) {
            // soil dry, needs watering
            result = 1;
        } else if (rMoisture < 656 && rMoisture >= 519) {
            // soil somewhat watered
            result = 2;
        } else if (rMoisture < 519 && rMoisture >= 473) {
            // soil moderately watered
            result = 3;
        } else if (rMoisture < 473 && rMoisture >= 420) {
            // soil very well watered
            result = 4;
        } else if (rMoisture <= 356) {
            //soil doesn't need watering
            result = 5;
        }
        return result;
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


