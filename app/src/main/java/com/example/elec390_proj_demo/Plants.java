package com.example.elec390_proj_demo;

import java.util.HashMap;
import java.util.Map;
/**
 * ELEC-390 Soil Sense
 * Created by Evan Yu on 04/01/2023.
 * @Plants.class
 * Plants datamodel for importing/exporting from app/database
 */

public class Plants {

    public String name, date, scientific_name, sunlight, watering_freq, plant_url;
    public int watering_amount, target_moisture;
    public int flag;

    public Plants(String name, String date, int flag, int watering_amount, int target_moisture) {
        this.name = name;
        this.date = date;
        this.flag = flag;
        this.watering_amount = watering_amount;
        this.target_moisture = target_moisture;
    }

    public Plants() {
        this.name = "";
        this.date = "";
        this.flag = 0;
        this.scientific_name = "";
        this.watering_amount = 0;
        this.target_moisture = 0;
        this.sunlight = "";
        this.watering_freq = "";
        this.plant_url = "";
    }


    public Plants(String name, String date, int flag) {
        this.name = name;
        this.date = date;
        this.flag = flag;
        this.scientific_name = "";
        this.watering_amount = 0;
        this.target_moisture = 0;
        this.sunlight = "";
        this.watering_freq = "";
        this.plant_url = "";
    }

    public Plants(String name, String date, String sun, String wf, String url, String nick) {
        this.name = name;
        this.date = date;
        this.flag = 0;
        this.scientific_name = nick;
        this.watering_amount = 0;
        this.target_moisture = 0;
        this.sunlight = sun;
        this.watering_freq = wf;
        this.plant_url = url;
    }

    public String getScientific_name() {
        return scientific_name;
    }

    public void setScientific_name(String scientific_name) {
        this.scientific_name = scientific_name;
    }

    public String getSunlight() {
        return sunlight;
    }

    public void setSunlight(String sunlight) {
        this.sunlight = sunlight;
    }

    public String getWatering_freq() {
        return watering_freq;
    }

    public void setWatering_freq(String watering_freq) {
        this.watering_freq = watering_freq;
    }

    public String getPlant_url() {
        return plant_url;
    }

    public void setPlant_url(String plant_url) {
        this.plant_url = plant_url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getWatering_amount() {
        return watering_amount;
    }

    public void setWatering_amount(int watering_amount) {
        this.watering_amount = watering_amount;
    }

    public int getTarget_moisture() {
        return target_moisture;
    }

    public void setTarget_moisture(int target_moisture) {
        this.target_moisture = target_moisture;
    }

    public int calcCurrentMoisture() {
        int rMoisture = this.target_moisture;
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

    @Override
    public String toString() {
        return "Plants{" +
                "name='" + name + '\'' +
                ", date='" + date + '\'' +
                ", scientific_name='" + scientific_name + '\'' +
                ", sunlight='" + sunlight + '\'' +
                ", watering_freq='" + watering_freq + '\'' +
                ", plant_url='" + plant_url + '\'' +
                ", watering_amount=" + watering_amount +
                ", target_moisture=" + target_moisture +
                ", flag=" + flag +
                '}';
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("date", date);
        result.put("flag", flag);
        result.put("scientific_name", scientific_name);
        result.put("sunlight",  sunlight);
        result.put("watering_freq",  watering_freq);
        result.put("plant_url",  plant_url);
        result.put("watering_amount",  watering_amount);
        result.put("target_moisture", target_moisture);
        return result;
    }

}


