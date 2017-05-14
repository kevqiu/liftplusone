package com.kq.liftplusone.models;

import com.google.gson.Gson;
import com.kq.liftplusone.helpers.MeasurementHelper;

import java.util.ArrayList;

/**
 * Created by Kevin on 5/14/2017.
 */

public class Exercise {

    private Equipment mEquipment;
    private String mExerciseName;
    private ArrayList<ExerciseSet> mSets;

    public Exercise(String exerciseName, Equipment equipment, ArrayList<ExerciseSet> sets) {
        mExerciseName = exerciseName;
        mSets = sets;
        mEquipment = equipment;
    }

    public String setsAsString(Measurement m) {
        String measurementString = MeasurementHelper.getString(m);
        StringBuilder sb = new StringBuilder();
        for (Object s : mSets) {
            if (s instanceof ExerciseSet) {
                ExerciseSet es = (ExerciseSet) s;
                sb.append(es.getWeight() + measurementString);
                if (mSets.indexOf(es) != mSets.size() - 1) { // delimit with commas except for last one
                    sb.append(" | ");
                }
            }
        }
        return sb.toString();
    }

    public String getExerciseName() {
        return mExerciseName;
    }

    public ArrayList<ExerciseSet> getSets() {return mSets;}

    public void addSet(ExerciseSet es) {
        ArrayList<ExerciseSet> sets = (ArrayList<ExerciseSet>) mSets;
        sets.add((ExerciseSet) es);
        mSets = sets;
    }

    public void removeSet(ExerciseSet es) {
        mSets.remove(es);
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

}
