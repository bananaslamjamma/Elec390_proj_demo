package com.example.elec390_proj_demo;


import android.text.InputFilter;
import android.text.Spanned;

public class InputSanitizer implements InputFilter {
    private int minInt, minMax;

    public InputSanitizer ( int minValue , int maxValue) {
        this.minInt = minValue;
        this.minMax = maxValue;
    }

    @Override
    public CharSequence filter(CharSequence charSequence, int startIndex, int endIndex, Spanned spanned, int d_start, int d_end) {
        try {
            int input = Integer. parseInt (spanned.toString() + charSequence.toString()) ;
            if (isInRange(minInt, minMax, input))
                return null;
        } catch (NumberFormatException e) {
            e.printStackTrace() ;
        }
        return "" ;
    }
    private boolean isInRange ( int a , int b , int c) {
        return b > a ? c >= a && c <= b : c >= b && c <= a ;
    }
}
