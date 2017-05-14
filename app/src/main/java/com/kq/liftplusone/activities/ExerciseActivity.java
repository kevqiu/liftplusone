package com.kq.liftplusone.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.kq.liftplusone.R;
import com.kq.liftplusone.adapters.ExerciseAdapter;
import com.kq.liftplusone.adapters.ExerciseDialogSetsAdapter;
import com.kq.liftplusone.database.RoutineDatabase;
import com.kq.liftplusone.helpers.EquipmentHelper;
import com.kq.liftplusone.models.Equipment;
import com.kq.liftplusone.models.Exercise;
import com.kq.liftplusone.models.Routine;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

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

    Equipment equipment = Equipment.Barbell;
    @OnClick(R.id.fab)
    public void addExercise(View view) {
        final ExerciseDialogSetsAdapter setsAdapter = new ExerciseDialogSetsAdapter(this);

        // construct Dialog
        final MaterialDialog dialog = new MaterialDialog.Builder(view.getContext())
            .title(R.string.enter_exercise)
            .positiveText(android.R.string.ok)
            .negativeText(android.R.string.cancel)
            .customView(R.layout.exercise_dialog, true)
            .onPositive(new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    String input = ((EditText) dialog.findViewById(R.id.routine_name)).getText().toString();
                    Exercise exercise = new Exercise(input, equipment, setsAdapter.getSets());
                    mRoutine.putExercise(exercise);
                    mRoutineDb.update(mRoutine);
                    insertOnAdapter(mExerciseAdapter.getItemCount());
                    updateMessage();
                }
            })
            .onNegative(new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    dialog.dismiss();
                }
            })
            .build();

        // disabled by default, seek validation first
        dialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);

        // Validate exercise name
        ((TextView) dialog.getCustomView().findViewById(R.id.routine_name)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                dialog.getActionButton(DialogAction.POSITIVE).setEnabled(count > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        final Spinner equipmentSpinner = (Spinner) dialog.findViewById(R.id.equipment_spinner);
        final RecyclerView setRecyclerView = (RecyclerView) dialog.findViewById(R.id.exercise_dialog_set_recycler_view);
        final Button addSetButton = (Button) dialog.findViewById(R.id.add_button);

        ArrayAdapter<CharSequence> equipmentAdapter = ArrayAdapter.createFromResource(this,
                    R.array.equipment_array, android.R.layout.simple_spinner_item);
        equipmentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        equipmentSpinner.setAdapter(equipmentAdapter);
        equipmentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> arg0, View v, int position, long id)
            {
                equipment = EquipmentHelper.StringToEquipment(equipmentSpinner.getItemAtPosition(position).toString());
            }

            public void onNothingSelected(AdapterView<?> arg0)
            {
                Log.d(EXERCISE_ACTIVITY_LOG_TAG, "nothing selected");
            }
        });

        // attach adapter to Sets collection
        setRecyclerView.setAdapter(setsAdapter);
        setRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        addSetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setsAdapter.addItem();
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
