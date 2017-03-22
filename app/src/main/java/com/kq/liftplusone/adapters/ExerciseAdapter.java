package com.kq.liftplusone.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.kq.liftplusone.R;
import com.kq.liftplusone.activities.SetsActivity;
import com.kq.liftplusone.database.RoutineDatabase;
import com.kq.liftplusone.models.Exercise;
import com.kq.liftplusone.models.Routine;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.kq.liftplusone.helpers.Constants.DATABASE_NAME;
import static com.kq.liftplusone.helpers.Constants.EXERCISE_ADAPTER_LOG_TAG;

public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ViewHolder>  {

    private Context mContext;
    private RoutineDatabase mDbHelper;

    private Routine mRoutine;
    private ArrayList<Exercise> mExercises;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        @Bind(R.id.upper_text) TextView mExerciseName;
        @Bind(R.id.lower_text) TextView mSetDescription;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);

            mDbHelper = new RoutineDatabase(view.getContext(), DATABASE_NAME);
        }

        @Override
        public void onClick(View v) {
            Exercise exercise = mExercises.get(getAdapterPosition()); // get Routine from list
            Log.d(EXERCISE_ADAPTER_LOG_TAG, exercise.toString());

            Intent intent = new Intent(mContext, SetsActivity.class);
            intent.putExtra("Routine", mRoutine.getRoutineName());
            intent.putExtra("Exercise", exercise.getExerciseName());
            mContext.startActivity(intent);
        }

        @Override
        public boolean onLongClick(View v) {
            final int pos = getAdapterPosition(); // gets item position
            new MaterialDialog.Builder(mContext)
                .content(v.getResources().getString(R.string.delete_exercise, mExercises.get(pos).getExerciseName()))
                .positiveText(android.R.string.yes)
                .negativeText(android.R.string.cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Exercise ex = mExercises.get(pos);
                        mRoutine.removeExercise(ex);
                        mDbHelper.update(mRoutine);
                        updateData(mDbHelper.get(mRoutine.getRoutineName()));
                        notifyDataSetChanged();
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .show();
            return true;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ExerciseAdapter(Context context, Routine routine) {
        mRoutine = routine;
        mExercises = routine.getExercises();
        mContext = context;
    }

    @Override
    public ExerciseAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View exerciseView = inflater.inflate(R.layout.recycler_item, parent, false);

        // Return a new holder instance
        return new ViewHolder(exerciseView);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        // Get the data model based on position
        Exercise exercise = mExercises.get(position);

        // Set item views based on your views and data model
        TextView exerciseName = viewHolder.mExerciseName;
        exerciseName.setText(exercise.getExerciseName());

        TextView exerciseDescription = viewHolder.mSetDescription;
        exerciseDescription.setText(exercise.setsAsString());

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mExercises.size();
    }

    public void updateData(Routine routine){
        mRoutine = routine;
        mExercises = mRoutine.getExercises();
    }

}