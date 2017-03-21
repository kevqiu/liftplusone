package com.kq.liftplusone.models;

public class ExerciseSet {
    private int mReps;
    private int mWeight;

    public ExerciseSet(int reps, int weight) {
        mReps = reps;
        mWeight = weight;
    }

    public int getReps() {
        return mReps;
    }

    public void setReps(int mReps) {
        this.mReps = mReps;
    }

    public int getWeight() {
        return mWeight;
    }

    public void setWeight(int mWeight) {
        this.mWeight = mWeight;
    }
}
