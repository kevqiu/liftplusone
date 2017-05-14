package com.kq.liftplusone.helpers;

import com.kq.liftplusone.models.Measurement;

public class MeasurementHelper {
    public static String getString(Measurement m) {
        return m == Measurement.Pound ? "lb" : "kg";
    }

    public static double getRatio(Measurement m) {
        return m == Measurement.Pound ? 1 : 0.4536;
    }
}
