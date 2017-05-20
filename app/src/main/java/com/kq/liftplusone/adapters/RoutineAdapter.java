package com.kq.liftplusone.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.kq.liftplusone.R;
import com.kq.liftplusone.activities.ExerciseActivity;
import com.kq.liftplusone.database.RoutineDatabase;
import com.kq.liftplusone.models.Routine;

import java.util.ArrayList;

import butterknife.*;

import static com.kq.liftplusone.helpers.Constants.*;

public class RoutineAdapter extends RecyclerView.Adapter<RoutineAdapter.ViewHolder>  {
    private Context mContext;
    private ArrayList<Routine> mRoutines;
    private RoutineDatabase mDbHelper;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        @Bind(R.id.upper_text) TextView mRoutineName;
        @Bind(R.id.lower_text) TextView mExerciseList;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);

            mDbHelper = new RoutineDatabase(view.getContext(), DATABASE_NAME);
        }

        @Override
        public void onClick(View v) {
            Routine routine = mRoutines.get(getAdapterPosition()); // get Routine from list
            Log.d(ROUTINE_ADAPTER_LOG_TAG, routine.toString());

            Intent intent = new Intent(mContext, ExerciseActivity.class);
            intent.putExtra("Routine", routine.getRoutineName());
            mContext.startActivity(intent);
        }

        @Override
        public boolean onLongClick(View v) {
            final int pos = getAdapterPosition(); // gets item position
            new MaterialDialog.Builder(mContext)
                .content(v.getResources().getString(R.string.delete_routine, mRoutines.get(pos).getRoutineName()))
                .positiveText(android.R.string.yes)
                .negativeText(android.R.string.cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Routine routine = mRoutines.get(pos);
                        mDbHelper.remove(routine.getRoutineName());
                        updateData(mDbHelper.getAll());
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
    public RoutineAdapter(Context context, ArrayList<Routine> routines) {
        mRoutines = routines;
        mContext = context;
    }

    @Override
    public RoutineAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View routineView = inflater.inflate(R.layout.activity_recycler_item, parent, false);

        // Return a new holder instance
        return new ViewHolder(routineView);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        // Get the data model based on position
        Routine routine = mRoutines.get(position);

        // Set item views based on your views and data model
        TextView routineName = viewHolder.mRoutineName;
        routineName.setText(routine.getRoutineName());

        TextView exerciseListText = viewHolder.mExerciseList;
        exerciseListText.setText(routine.exercisesAsString());

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mRoutines.size();
    }

    public void updateData(ArrayList<Routine> list){
        mRoutines = list;
        notifyDataSetChanged();
    }
}