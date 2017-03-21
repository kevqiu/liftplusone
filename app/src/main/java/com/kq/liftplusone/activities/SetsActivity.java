package com.kq.liftplusone.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.kq.liftplusone.R;
import com.kq.liftplusone.adapters.ExerciseAdapter;
import com.kq.liftplusone.adapters.SetsAdapter;
import com.kq.liftplusone.database.RoutineDatabase;
import com.kq.liftplusone.models.Exercise;
import com.kq.liftplusone.models.ExerciseSet;
import com.kq.liftplusone.models.Routine;

import java.util.ArrayList;

import static com.kq.liftplusone.helpers.Constants.DATABASE_NAME;

public class SetsActivity extends AnimationBaseActivity {
    private RecyclerView mRecyclerView;
    private SetsAdapter mSetsAdapter;
    private RoutineDatabase mRoutineDb;

    private String mRoutineName;
    private String mExerciseName;
    private Routine mRoutine;
    private Exercise mExercise;
    ArrayList<ExerciseSet> mSets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set);

        // set up toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // initialize database
        mRoutineDb = new RoutineDatabase(getBaseContext(), DATABASE_NAME);

        // get Intent data passed from Exercise activity
        Intent intent = getIntent();
        mRoutineName = intent.getStringExtra("Routine");
        mRoutine = mRoutineDb.get(mRoutineName);
        mExerciseName = intent.getStringExtra("Exercise");
        mExercise = mRoutine.getExercise(mExerciseName);

        // initialize adapter, populate in onResume
        mSetsAdapter = new SetsAdapter(this, mRoutine, mExercise);

        // set title to Routine name
        getSupportActionBar().setTitle(mExerciseName);

        // get RecyclerView and attach adapter
        mRecyclerView = (RecyclerView) findViewById(R.id.set_recycler_view);
        mRecyclerView.setAdapter(mSetsAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void updateVariables() {
        mRoutine = mRoutineDb.get(mRoutineName);
        mExercise = mRoutine.getExercise(mExerciseName);
        mSets = mExercise.getSets();
        if(mSetsAdapter != null)
            mSetsAdapter.updateData(mSets);
    }
}
