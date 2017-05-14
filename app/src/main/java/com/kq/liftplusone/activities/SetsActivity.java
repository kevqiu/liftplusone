package com.kq.liftplusone.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
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
        final ExerciseSet set = new ExerciseSet(0,0);
        MaterialDialog dialog = new MaterialDialog.Builder(view.getContext())
                .title(R.string.enter_set)
                .positiveText(android.R.string.ok)
                .negativeText(android.R.string.cancel)
                .customView(R.layout.exercise_dialog_set_recycler_item, true)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        mExercise.addSet(set);
                        mRoutine.putExercise(mExercise);
                        mRoutineDb.update(mRoutine);
                        insertOnAdapter(mSetsAdapter.getItemCount());
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

    // update adapter
    @Override
    protected void onResume() {
        super.onResume();
    }

    private void insertOnAdapter(int pos) {
        updateVariables();
        mSetsAdapter.notifyItemInserted(pos);
    }

    // update activity variables and adapter if constructed
    private void updateVariables() {
        mRoutine = mRoutineDb.get(mRoutineName);
        mExercise = mRoutine.getExercise(mExerciseName);
        mSets = mExercise.getSets();
        if(mSetsAdapter != null)
            mSetsAdapter.updateData(mExercise );
    }
}
