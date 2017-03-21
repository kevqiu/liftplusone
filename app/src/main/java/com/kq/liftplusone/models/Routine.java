package com.kq.liftplusone.models;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Routine {
    private String mRoutineName;
    private Map<String, Exercise> mExercises;

    public Routine(String routineName) {
        mRoutineName = routineName;
        mExercises = new HashMap<>();
    }

    public String getRoutineName() {
        return mRoutineName;
    }

    public ArrayList<Exercise> getExercises() {
        return new ArrayList<>(mExercises.values());
    }

    public void putExercise(Exercise ex) {
        mExercises.put(ex.getExerciseName(), ex);
    }

    public void removeExercise(Exercise ex) {
        mExercises.remove(ex.getExerciseName());
    }

    public Exercise getExercise(String name) {
        return mExercises.get(name);
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    public String exercisesAsString() {
        StringBuilder sb = new StringBuilder();

        for (Exercise e : getExercises()) {
            sb.append(e.getExerciseName());
            if (getExercises().indexOf(e) != mExercises.size()-1) { // delimit with commas except for last one
                sb.append(" | ");
            }
        }
        return sb.toString();
    }
}
