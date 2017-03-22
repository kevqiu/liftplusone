package com.kq.liftplusone.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.kq.liftplusone.R;
import com.kq.liftplusone.adapters.SetsAdapter;
import com.kq.liftplusone.database.RoutineDatabase;
import com.kq.liftplusone.models.Exercise;
import com.kq.liftplusone.models.ExerciseSet;
import com.kq.liftplusone.models.Routine;

import java.util.ArrayList;

import butterknife.*;

import static com.kq.liftplusone.helpers.Constants.DATABASE_NAME;

public class SetsActivity extends AnimationBaseActivity {
    // bind views
    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.set_recycler_view) RecyclerView mRecyclerView;

    // content providers
    private SetsAdapter mSetsAdapter;
    private RoutineDatabase mRoutineDb;

    // activity variables
    private String mRoutineName;
    private String mExerciseName;
    private Routine mRoutine;
    private Exercise mExercise;
    private ArrayList<ExerciseSet> mSets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set);
        ButterKnife.bind(this);

        // set up toolbar
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
        mRecyclerView.setAdapter(mSetsAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @OnClick(R.id.fab)
    public void addSet(View view) {
        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    // update adapter
    @Override
    protected void onResume() {
        super.onResume();
    }

    // update activity variables and adapter if constructed
    private void updateVariables() {
        mRoutine = mRoutineDb.get(mRoutineName);
        mExercise = mRoutine.getExercise(mExerciseName);
        mSets = mExercise.getSets();
        if(mSetsAdapter != null)
            mSetsAdapter.updateData(mSets);
    }
}
