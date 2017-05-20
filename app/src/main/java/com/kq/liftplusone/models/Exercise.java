package com.kq.liftplusone.models;

import com.google.gson.Gson;
import com.kq.liftplusone.helpers.MeasurementHelper;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Kevin on 5/14/2017.
 */

public class Exercise {

    private String mExerciseName;
    private Map<String, ExerciseSet> mSets;

    public Exercise(String exerciseName, LinkedHashMap<String, ExerciseSet> sets) {
        mExerciseName = exerciseName;
        mSets = sets;
    }

    public String setsAsString(Measurement m) {
        String measurementString = MeasurementHelper.getString(m);
        StringBuilder sb = new StringBuilder();
        ArrayList<ExerciseSet> sets = new ArrayList(mSets.values());
        for (ExerciseSet s : sets) {
            sb.append(s.getWeight() + measurementString);
            if (sets.indexOf(s) != mSets.size() - 1) { // delimit with commas except for last one
                sb.append(" | ");
            }
        }
        return sb.toString();
    }

    public String getExerciseName() {
        return mExerciseName;
    }

    public ArrayList<ExerciseSet> getSets() {return new ArrayList<>(mSets.values());}

    public void putSet(ExerciseSet es) {
        mSets.put(es.getId(), es);
    }

    public void removeSet(ExerciseSet es) {
        mSets.remove(es);
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

}
