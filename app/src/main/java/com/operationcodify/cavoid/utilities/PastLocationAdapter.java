package com.operationcodify.cavoid.utilities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.operationcodify.cavoid.R;
import com.operationcodify.cavoid.activities.ParsedPastLocationReport;

import java.util.TreeSet;

public class PastLocationAdapter extends RecyclerView.Adapter<PastLocationViewHolder>{
    PastLocationViewHolder vh;
    TreeSet<ParsedPastLocationReport> pastLocationReports;
    Context c;

    //The adapter constructor
    public PastLocationAdapter(Context c, TreeSet<ParsedPastLocationReport> data) {
        this.c = c;
        pastLocationReports = data;
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
        for (ParsedPastLocationReport report : pastLocationReports){
            setPastCasesTextView(report);
            setPastDeathsTextView(report);
        }
    }

    private void setPastDeathsTextView(ParsedPastLocationReport r) {
        String message = r.countyName + " New deaths: " + r.newDeathNumber + " Total Deaths: " + r.totalDeaths;
        vh.pastDeathsTextView.setText(message);
    }

    private void setPastCasesTextView(ParsedPastLocationReport r) {
        String message = r.countyName + " New cases: " + r.newCaseNumber + "  Active Cases: " + r.activeCasesEst + " Total Cases: " + r.totalCases;
        vh.pastCasesTextView.setText(message);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if(pastLocationReports != null) {
            return pastLocationReports.size();
        }
        else {
            return 0;
        }
    }

}
