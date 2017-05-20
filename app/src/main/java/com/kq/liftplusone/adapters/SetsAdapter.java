package com.kq.liftplusone.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.kq.liftplusone.R;
import com.kq.liftplusone.database.RoutineDatabase;
import com.kq.liftplusone.models.Exercise;
import com.kq.liftplusone.models.ExerciseSet;
import com.kq.liftplusone.models.Measurement;
import com.kq.liftplusone.models.Routine;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.kq.liftplusone.helpers.Constants.DATABASE_NAME;
import static com.kq.liftplusone.helpers.Constants.SET_ADAPTER_LOG_TAG;

public class SetsAdapter extends RecyclerView.Adapter<SetsAdapter.ViewHolder>  {

    private Context mContext;
    private Routine mRoutine;
    private RoutineDatabase mDbHelper;
    private SharedPreferences sharedPrefs;

    private Exercise mExercise;
    private ArrayList<ExerciseSet> mSets;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        @Bind(R.id.left_text) TextView mSetText;
        @Bind(R.id.right_text) TextView mWeight;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);

            mDbHelper = new RoutineDatabase(view.getContext(), DATABASE_NAME);
        }

        @Override
        public void onClick(View v) {
            final ExerciseSet set = mSets.get(getAdapterPosition()); // get Routine from list
            MaterialDialog dialog = new MaterialDialog.Builder(mContext)
                    .title("Set " + (getAdapterPosition() + 1))
                    .positiveText(android.R.string.ok)
                    .negativeText(android.R.string.cancel)
                    .customView(R.layout.exercise_dialog_set_recycler_item, true)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            String weightInput = ((EditText) dialog.getCustomView().findViewById(R.id.weight_edit_text)).getText().toString();
                            String repsInput = ((EditText) dialog.getCustomView().findViewById(R.id.reps_edit_text)).getText().toString();
                            if(!weightInput.isEmpty() && weightInput != null) {
                                float newWeight = Float.parseFloat(weightInput);
                                set.setWeight(newWeight);
                                mExercise.putSet(set);
                                mRoutine.putExercise(mExercise);
                                mDbHelper.update(mRoutine);
                                updateData(mExercise);
                            }
                            if(!repsInput.isEmpty() && repsInput != null) {
                                int newReps = Integer.parseInt(repsInput);
                                set.setReps(newReps);
                                mExercise.putSet(set);
                                mRoutine.putExercise(mExercise);
                                mDbHelper.update(mRoutine);
                                updateData(mExercise);
                            }
                        }
                    })
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            dialog.dismiss();
                        }
                    })
                    .build();

            // hide remove button
            dialog.getCustomView().findViewById(R.id.remove_button).setVisibility(View.INVISIBLE);

            // attach text change listeners to inputs
            EditText weightInput = (EditText) dialog.getCustomView().findViewById(R.id.weight_edit_text);
            EditText repsInput = (EditText) dialog.getCustomView().findViewById(R.id.reps_edit_text);

            weightInput.setText(Float.toString(set.getWeight()));
            repsInput.setText(Integer.toString(set.getReps()));

            weightInput.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if(count > 0 && s.charAt(s.length()-1) != '.') {
                        set.setWeight(Float.parseFloat(s.toString()));
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
            repsInput.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if(count > 0) {
                        set.setReps(Integer.parseInt(s.toString()));
                    }
                }
                @Override
                public void afterTextChanged(Editable s) {}
            });
            dialog.show();
        }

        @Override
        public boolean onLongClick(View v) {
            final int pos = getAdapterPosition(); // gets item position
            AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
            dialog.setMessage(v.getResources().getString(R.string.delete_set))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ExerciseSet set = mSets.get(pos);
                        mExercise.removeSet(set);
                        mRoutine.putExercise(mExercise);
                        mDbHelper.update(mRoutine);
                        updateData(mDbHelper.get(mRoutine.getRoutineName()).getExercise(mExercise.getExerciseName()));
                        notifyDataSetChanged();
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .show();
            return true;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public SetsAdapter(Context context, Routine routine, Exercise exercise) {
        mRoutine = routine;
        mExercise = exercise;
        mSets = exercise.getSets();
        mContext = context;
    }

    @Override
    public SetsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View routineView = inflater.inflate(R.layout.set_recycler_item, parent, false);

        // Return a new holder instance
        return new ViewHolder(routineView);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        // Get the data model based on position
        ExerciseSet set = mSets.get(position);

        // Set item views based on your views and data model
        TextView setName = viewHolder.mSetText;
        int setNumber = position + 1;
        setName.setText("Set " + setNumber);

        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        Measurement measurement = sharedPrefs.getString("measurement", "lb").equals("lb") ? Measurement.Pound : Measurement.Kilogram;
        TextView setWeight = viewHolder.mWeight;
        setWeight.setText(set.setAsString(measurement));

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mSets.size();
    }

    public void updateData(Exercise ex){
        mExercise = ex;
        mSets = ex.getSets();
        notifyDataSetChanged();
    }
}