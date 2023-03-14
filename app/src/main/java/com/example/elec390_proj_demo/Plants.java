package com.example.elec390_proj_demo;

import java.util.HashMap;
import java.util.Map;

public class Plants {

    public String name;
    public String nick_name;
    public int flag;
    public String date;


    public Plants(String name, String date, int flag) {
        this.name = name;
        this.date = date;
        this.flag = flag;
    }

    public Plants() {
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

    @Override
    public String toString() {
        return "Plants{" +
                "name='" + name + '\'' +
                ", nick_name='" + nick_name + '\'' +
                ", Flag=" + flag +
                ", date='" + date + '\'' +
                '}';
    }
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("date", date);
        result.put("flag", flag);
        return result;
    }

}


