package com.example.elec390_proj_demo;

public class Users {


    public String user_name;
    public String u_id;



    public Users(String us, String u ) {
        this.user_name = us;
        this.u_id = u;
    }

    @Override
    public String toString() {
        return "Users{" +
                "user_name='" + user_name + '\'' +
                '}';
    }
}


