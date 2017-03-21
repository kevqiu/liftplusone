package com.kq.liftplusone.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.kq.liftplusone.R;
import com.kq.liftplusone.adapters.ExerciseAdapter;
import com.kq.liftplusone.database.RoutineDatabase;
import com.kq.liftplusone.models.Exercise;
import com.kq.liftplusone.models.Routine;

import java.util.ArrayList;

import static com.kq.liftplusone.helpers.Constants.DATABASE_NAME;
import static com.kq.liftplusone.helpers.Constants.EXERCISE_ADAPTER_LOG_TAG;

public class ExerciseActivity extends AnimationBaseActivity {
    private RecyclerView mRecyclerView;
    private ExerciseAdapter mExerciseAdapter;
    private RoutineDatabase mRoutineDb;

    private String mRoutineName;
    private Routine mRoutine;
    ArrayList<Exercise> mExercises;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);

        // set up toolbar with back button
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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
        mRecyclerView = (RecyclerView) findViewById(R.id.exercise_recycler_view);
        mRecyclerView.setAdapter(mExerciseAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialDialog.Builder(view.getContext())
                    .content(R.string.enter_exercise)
                    .positiveText(android.R.string.ok)
                    .negativeText(android.R.string.cancel)
                    .input(null, null, new MaterialDialog.InputCallback() {
                        @Override
                        public void onInput(MaterialDialog dialog, CharSequence input) {
                        }
                    })
                    .inputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            String input = dialog.getInputEditText().getText().toString();
                            if(!input.isEmpty() && input != null) {
                                Exercise exercise = new Exercise(input);
                                mRoutine.putExercise(exercise);
                                mRoutineDb.update(mRoutine);
                                insertOnAdapter(mExerciseAdapter.getItemCount());
                                updateMessage();
                            }
                        }
                    })
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
            }
        });
    }

    @Override
    protected void onResume() {
        updateVariables();
        updateMessage();
        mExerciseAdapter.notifyDataSetChanged();
        super.onResume();
    }

    private void insertOnAdapter(int pos) {
        updateVariables();
        mExerciseAdapter.notifyItemInserted(pos);
    }

    private void updateVariables() {
        mRoutine = mRoutineDb.get(mRoutineName);
        mExercises = mRoutine.getExercises();
        if(mExerciseAdapter != null)
            mExerciseAdapter.updateData(mRoutine);
    }

    private void updateMessage() {
        // hide "No Exercises" messages if there is at least one routine
        TextView noExerciseMsg = (TextView) findViewById(R.id.no_exercises);
        if (mExercises.isEmpty())
            noExerciseMsg.setVisibility(View.VISIBLE);
        else
            noExerciseMsg.setVisibility(View.INVISIBLE);
    }

}
