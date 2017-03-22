package com.kq.liftplusone.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.kq.liftplusone.R;
import com.kq.liftplusone.adapters.RoutineAdapter;
import com.kq.liftplusone.database.RoutineDatabase;
import com.kq.liftplusone.models.Routine;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.kq.liftplusone.helpers.Constants.DATABASE_NAME;

public class RoutineActivity extends AnimationBaseActivity {
    // bind views
    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.routine_recycler_view) RecyclerView mRecyclerView;

    // content providers
    private RoutineAdapter mRoutineAdapter;
    private RoutineDatabase mRoutineDb;

    // activity variables
    private ArrayList<Routine> mRoutines;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // initialize view
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routine);
        ButterKnife.bind(this);

        // set up toolbar
        setSupportActionBar(toolbar);

        // initialize database
        mRoutineDb = new RoutineDatabase(getBaseContext(), DATABASE_NAME);
        //mRoutineDb.dropTable();
        mRoutines = mRoutineDb.getAll();

        // initialize adapter, populate in onResume
        mRoutineAdapter = new RoutineAdapter(this, mRoutines);

        // get RecyclerView and attach adapter
        mRecyclerView.setAdapter(mRoutineAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    // update adapter
    @Override
    protected void onResume() {
        updateVariables();
        updateMessage();
        mRoutineAdapter.notifyDataSetChanged();
        super.onResume();
    }

    @OnClick(R.id.fab)
    public void addRoutine(View view) {
        new MaterialDialog.Builder(view.getContext())
            .title(R.string.enter_routine)
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
                        Routine routine = new Routine(input);
                        mRoutineDb.add(routine);
                        insertOnAdapter(mRoutineAdapter.getItemCount());
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

    private void insertOnAdapter(int pos) {
        updateVariables();
        mRoutineAdapter.notifyItemInserted(pos);
    }

    // update activity variables and adapter if constructed
    private void updateVariables() {
        mRoutines = mRoutineDb.getAll();
        if(mRoutineAdapter != null)
            mRoutineAdapter.updateData(mRoutines);
    }

    // hide "No Routines" messages if there is at least one routine
    private void updateMessage() {
        TextView noRoutineMsg = (TextView) findViewById(R.id.no_routines);
        if (mRoutines.isEmpty())
            noRoutineMsg.setVisibility(View.VISIBLE);
        else
            noRoutineMsg.setVisibility(View.INVISIBLE);
    }
}