package com.operationcodify.cavoid.activities;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.operationcodify.cavoid.R;

public class PastLocationViewHolder extends ViewHolder {
    TextView pastCasesTextView, pastDeathsTextView;

    public PastLocationViewHolder(View v) {
        super(v);
        this.pastCasesTextView = v.findViewById(R.id.pastCasesTextView);
        this.pastDeathsTextView = v.findViewById(R.id.pastDeathsTextView);
    }
}
