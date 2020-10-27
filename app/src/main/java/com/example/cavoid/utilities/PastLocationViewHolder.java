package com.example.cavoid.utilities;

import android.view.View;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.example.cavoid.R;

public class PastLocationViewHolder extends ViewHolder {
    TextView pastCasesTextView,pastDeathsTextView;
    public PastLocationViewHolder(View v){
        super(v);
        this.pastCasesTextView = v.findViewById(R.id.pastCasesTextView);
        this.pastDeathsTextView = v.findViewById(R.id.pastDeathsTextView);
    }
}
