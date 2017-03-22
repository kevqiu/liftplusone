package com.kq.liftplusone.models;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Set;

public class Exercise {
    private String mExercise;
    private MuscleGroup mMuscleGroup;
    private Equipment mEquipment;
    private ArrayList<ExerciseSet> mSets;

    public Exercise(String exercise, MuscleGroup muscleGroup, Equipment equipment) {
        mExercise = exercise;
        mMuscleGroup = muscleGroup;
        mEquipment = equipment;
        mSets = new ArrayList<>();
        for (int i = 0; i < 3; i++)
            mSets.add(new ExerciseSet(5,5));
    }

    public String getExerciseName() {
        return mExercise;
    }

    public ArrayList<ExerciseSet> getSets() {
        return mSets;
    }

    public MuscleGroup getMuscleGroup() {
        return mMuscleGroup;
    }

    public Equipment getEquipment() {
        return mEquipment;
    }

    public void removeSet(ExerciseSet s) {
        mSets.remove(s);
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    public String setsAsString() {
        StringBuilder sb = new StringBuilder();
        for (ExerciseSet s : mSets) {
            sb.append(s.getReps() + " reps @ " + s.getWeight());
            if (mSets.indexOf(s) != mSets.size() - 1) { // delimit with commas except for last one
                sb.append(" | ");
            }
        }
        return sb.toString();
    }
}
