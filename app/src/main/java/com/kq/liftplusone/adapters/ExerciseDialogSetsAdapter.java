package com.kq.liftplusone.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.kq.liftplusone.R;
import com.kq.liftplusone.models.ExerciseSet;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ExerciseDialogSetsAdapter extends RecyclerView.Adapter<ExerciseDialogSetsAdapter.ViewHolder>  {
    private Context mContext;
    private ArrayList<ExerciseSet> mSets;

    public class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.weight_edit_text) EditText weight;
        @Bind(R.id.reps_edit_text) EditText repsInput;
        @Bind(R.id.remove_button) ImageButton removeButton;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ExerciseDialogSetsAdapter(Context context) {
        mContext = context;
        mSets = new ArrayList<>();
        addItem();
    }

    @Override
    public ExerciseDialogSetsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View routineView = inflater.inflate(R.layout.exercise_dialog_set_recycler_item, parent, false);

        // Return a new holder instance
        return new ViewHolder(routineView);
    }

    int weightPrevLength;
    int repsPrevLength;
    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        ImageButton removeButton = viewHolder.removeButton;
        EditText weightInput = viewHolder.weight;
        EditText repsInput = viewHolder.repsInput;

        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSets.remove(position);
                notifyDataSetChanged();
            }
        });
        weightInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { weightPrevLength = s.length(); }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count > 0 && s.charAt(s.length()-1) != '.') {
                    ExerciseSet set = mSets.get(position);
                    set.setWeight(Float.parseFloat(s.toString()));
                    mSets.set(position, set);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (weightPrevLength > s.length()) {
                    ExerciseSet set = mSets.get(position);
                    set.setWeight(s.length() > 0 ? Float.parseFloat(s.toString()) : 0);
                    mSets.set(position, set);
                }
            }
        });

        repsInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { repsPrevLength = s.length(); }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(count > 0) {
                    ExerciseSet set = mSets.get(position);
                    set.setReps(Integer.parseInt(s.toString()));
                    mSets.set(position, set);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(repsPrevLength > s.length() && s.length() > 0) {
                    ExerciseSet set = mSets.get(position);
                    set.setReps(s.length() > 0 ? Integer.parseInt(s.toString()) : 0);
                    mSets.set(position, set);
            }}
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mSets.size();
    }

    public void addItem() {
        mSets.add(new ExerciseSet(0,0));
        notifyItemInserted(getItemCount());
    }

    public ArrayList<ExerciseSet> getSets() {
        return mSets;
    }

}