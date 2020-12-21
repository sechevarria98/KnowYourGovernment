package com.example.know_your_gov;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PoliticianAdapter extends RecyclerView.Adapter<MyViewHolder> {

    private List<Politician> politicians;
    private MainActivity mainAct;

    PoliticianAdapter(List<Politician> politicians, MainActivity ma) {
        this.politicians = politicians;
        this.mainAct = ma;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.politician_list, parent, false);
        itemView.setOnClickListener(mainAct);
        return new MyViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Politician politician = politicians.get(position);

        holder.name.setText(politician.getName());
        holder.office.setText(politician.getOffice());
        holder.party.setText("(" + politician.getParty() + ")");
    }

    @Override
    public int getItemCount() {
        return politicians.size();
    }
}
