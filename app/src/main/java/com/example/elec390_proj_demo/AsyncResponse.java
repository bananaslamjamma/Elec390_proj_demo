package com.example.elec390_proj_demo;

import java.util.ArrayList;

public interface AsyncResponse {
    void processFinish(ArrayList<Plants> output);

    void onPreExecute();
}
