package com.example.elec390_proj_demo;

/**
 * ELEC-390 Soil Sense
 * Created by Evan Yu on 04/01/2023.
 * @Users.class
 * Users datamodel for importing/exporting from app/database
 */

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


