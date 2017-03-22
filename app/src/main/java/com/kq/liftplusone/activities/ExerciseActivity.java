package com.kq.liftplusone.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.kq.liftplusone.R;
import com.kq.liftplusone.adapters.ExerciseAdapter;
import com.kq.liftplusone.database.RoutineDatabase;
import com.kq.liftplusone.models.Exercise;
import com.kq.liftplusone.models.Routine;

import java.util.ArrayList;

import butterknife.*;

import static com.kq.liftplusone.helpers.Constants.DATABASE_NAME;
import static com.kq.liftplusone.helpers.Constants.EXERCISE_ACTIVITY_LOG_TAG;

public class ExerciseActivity extends AnimationBaseActivity {
    // bind views
    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.exercise_recycler_view) RecyclerView mRecyclerView;

    // content providers
    private ExerciseAdapter mExerciseAdapter;
    private RoutineDatabase mRoutineDb;

    // activity variables
    private String mRoutineName;
    private Routine mRoutine;
    private ArrayList<Exercise> mExercises;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);
        ButterKnife.bind(this);

        // set up toolbar with back button
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // initialize database
        mRoutineDb = new RoutineDatabase(getBaseContext(), DATABASE_NAME);

        // get Intent data passed from Routine activity
        Intent intent = getIntent();
        mRoutineName = intent.getStringExtra("Routine");
        updateVariables();

        // set title to Routine name
        getSupportActionBar().setTitle(mRoutineName);

        // initialize adapter, populate in onResume
        mExerciseAdapter = new ExerciseAdapter(this, mRoutine);

        // get RecyclerView and attach adapter
        mRecyclerView.setAdapter(mExerciseAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    // update adapter
    @Override
    protected void onResume() {
        updateVariables();
        updateMessage();
        mExerciseAdapter.notifyDataSetChanged();
        super.onResume();
    }

    @OnClick(R.id.fab)
    public void addExercise(View view) {
        MaterialDialog dialog = new MaterialDialog.Builder(view.getContext())
            .title(R.string.enter_exercise)
            .positiveText(android.R.string.ok)
            .negativeText(android.R.string.cancel)
            .customView(R.layout.exercise_dialog, true)
            .onPositive(new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                    String input = dialog.getInputEditText().getText().toString();
//                    if(!input.isEmpty() && input != null) {
//                        Exercise exercise = new Exercise(input);
//                        mRoutine.putExercise(exercise);
//                        mRoutineDb.update(mRoutine);
//                        insertOnAdapter(mExerciseAdapter.getItemCount());
//                        updateMessage();
//                    }
                }
            })
            .onNegative(new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    dialog.dismiss();
                }
            }).build();

            final Spinner equipmentSpinner = (Spinner) dialog.findViewById(R.id.equipment_spinner);
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                    R.array.equipment_array, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            equipmentSpinner.setAdapter(adapter);
            equipmentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
            {
                public void onItemSelected(AdapterView<?> arg0, View v, int position, long id)
                {
                    Log.d(EXERCISE_ACTIVITY_LOG_TAG, equipmentSpinner.getItemAtPosition(position).toString());
                }

                public void onNothingSelected(AdapterView<?> arg0)
                {
                    Log.d(EXERCISE_ACTIVITY_LOG_TAG, "nothing selected");
                }
            });
            dialog.show();
    }

    private void insertOnAdapter(int pos) {
        updateVariables();
        mExerciseAdapter.notifyItemInserted(pos);
    }

    // update activity variables and adapter if constructed
    private void updateVariables() {
        mRoutine = mRoutineDb.get(mRoutineName);
        mExercises = mRoutine.getExercises();
        if(mExerciseAdapter != null)
            mExerciseAdapter.updateData(mRoutine);
    }

    // hide "No Exercises" messages if there is at least one routine
    private void updateMessage() {
        TextView noExerciseMsg = (TextView) findViewById(R.id.no_exercises);
        if (mExercises.isEmpty())
            noExerciseMsg.setVisibility(View.VISIBLE);
        else
            noExerciseMsg.setVisibility(View.INVISIBLE);
    }

}
