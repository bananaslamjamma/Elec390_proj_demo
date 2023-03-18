package com.example.elec390_proj_demo;

import java.util.HashMap;
import java.util.Map;

public class Plants {

    public String name, date, nick_name;
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
    }


    public Plants(String name, String date, int flag) {
        this.name = name;
        this.date = date;
        this.flag = flag;
        this.watering_amount = 0;
        this.target_moisture = 0;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNick_name() {
        return nick_name;
    }

    public void setNick_name(String nick_name) {
        this.nick_name = nick_name;
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

    @Override
    public String toString() {
        return "Plants{" +
                "name='" + name + '\'' +
                ", date='" + date + '\'' +
                ", nick_name='" + nick_name + '\'' +
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
        result.put("watering_amount",  watering_amount);
        result.put("target_moisture", target_moisture);
        return result;
    }

}


