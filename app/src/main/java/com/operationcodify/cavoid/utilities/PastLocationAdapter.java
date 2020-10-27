package com.operationcodify.cavoid.utilities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.operationcodify.cavoid.R;

import java.util.ArrayList;

public class PastLocationAdapter extends RecyclerView.Adapter<PastLocationViewHolder>{
    PastLocationViewHolder vh;
    ArrayList<String> pastLocationMessages;
    Context c;

    //The adapter constructor
    public PastLocationAdapter(Context c, ArrayList<String> data) {
        this.c = c;
        pastLocationMessages = data;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public PastLocationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        CardView v = (CardView)LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row, parent, false);
        vh = new PastLocationViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull PastLocationViewHolder vh, int position) {
        if(position > 0  && (position%2 !=0)){
            position++;
        }
        if(position+1 < pastLocationMessages.size()) {
            vh.pastCasesTextView.setText(pastLocationMessages.get(position));
            vh.pastDeathsTextView.setText(pastLocationMessages.get(position + 1));
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if(pastLocationMessages != null) {
            return pastLocationMessages.size();
        }
        else {
            return 0;
        }
    }

}
