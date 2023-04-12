package com.example.elec390_proj_demo;

import java.util.ArrayList;

/**
 * ELEC-390 Soil Sense
 * Created by Evan Yu on 04/01/2023.
 * @AsyncResponse.java interface for PerenualHandler
 */

public interface AsyncResponse {
    void processFinish(ArrayList<Plants> output);

    void beforeProcess(ArrayList<Plants> apiPlantsList);
}
