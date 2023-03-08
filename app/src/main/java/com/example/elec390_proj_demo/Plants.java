package com.example.elec390_proj_demo;

public class Plants {


    public String name;
    public String nick_name;
    public int Flag;
    public String date;


    public Plants(String name, String date, int Flag) {
        this.name = name;
        this.date = date;
        this.Flag = Flag;
    }

    @Override
    public String toString() {
        return "Plants{" +
                "name='" + name + '\'' +
                ", nick_name='" + nick_name + '\'' +
                ", Flag=" + Flag +
                ", date='" + date + '\'' +
                '}';
    }
}


