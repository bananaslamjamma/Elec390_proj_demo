package com.example.elec390_proj_demo.ui.login;

/**
 * Class exposing authenticated user details to the UI.
 */
public class LoggedInUserView {
    private String displayName;
    //... other data fields that may be accessible to the UI

    LoggedInUserView(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}