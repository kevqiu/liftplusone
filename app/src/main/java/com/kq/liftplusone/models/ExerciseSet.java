package com.kq.liftplusone.models;

import com.kq.liftplusone.helpers.MeasurementHelper;

public class ExerciseSet {
    private int mReps;
    private float mWeight;

    public ExerciseSet(int reps, float weight) {
        mReps = reps;
        mWeight = weight;
    }

    public int getReps() {
        return mReps;
    }

    public void setReps(int mReps) {
        this.mReps = mReps;
    }

    public float getWeight() {
        return mWeight;
    }

    public void setWeight(float mWeight) {
        this.mWeight = mWeight;
    }

    public String setAsString(Measurement m) {
        String measurementString = MeasurementHelper.getString(m);
        double ratio = MeasurementHelper.getRatio(m);
        StringBuilder sb = new StringBuilder();

        double weight = getWeight() * ratio;
        sb.append(getReps() + " reps @ " + String.format("%.1f", weight) + measurementString);

        return sb.toString();
    }

}
