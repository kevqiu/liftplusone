package com.kq.liftplusone.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kq.liftplusone.R;
import com.kq.liftplusone.database.RoutineDatabase;
import com.kq.liftplusone.models.Exercise;
import com.kq.liftplusone.models.ExerciseSet;
import com.kq.liftplusone.models.Routine;

import java.util.ArrayList;

import static com.kq.liftplusone.helpers.Constants.DATABASE_NAME;
import static com.kq.liftplusone.helpers.Constants.SET_ADAPTER_LOG_TAG;

public class SetsAdapter extends RecyclerView.Adapter<SetsAdapter.ViewHolder>  {

    private Context mContext;
    private Routine mRoutine;
    private Exercise mExercise;
    private ArrayList<ExerciseSet> mSets;
    private RoutineDatabase mDbHelper;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private TextView mSetText;
        private TextView mWeight;

        public ViewHolder(View view) {
            super(view);
            mSetText = (TextView) itemView.findViewById(R.id.left_text);
            mWeight = (TextView) itemView.findViewById(R.id.right_text);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);

            mDbHelper = new RoutineDatabase(view.getContext(), DATABASE_NAME);
        }

        @Override
        public void onClick(View v) {
            ExerciseSet set = mSets.get(getAdapterPosition()); // get Routine from list
            Log.d(SET_ADAPTER_LOG_TAG, set.toString());
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
                        updateData(mDbHelper.get(mRoutine.getRoutineName()).getExercise(mExercise.getExerciseName()).getSets());
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

        TextView setWeight = viewHolder.mWeight;
        setWeight.setText(set.getReps() + " reps @ " + set.getWeight());

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mSets.size();
    }

    public void updateData(ArrayList<ExerciseSet> list){
        mSets = list;
    }
}