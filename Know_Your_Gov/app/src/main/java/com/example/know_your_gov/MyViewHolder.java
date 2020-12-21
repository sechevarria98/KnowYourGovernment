package com.example.know_your_gov;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyViewHolder extends RecyclerView.ViewHolder {

    TextView name;
    TextView party;
    TextView office;

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);

        name = itemView.findViewById(R.id.nameVH);
        party = itemView.findViewById(R.id.partyVH);
        office = itemView.findViewById(R.id.officeVH);
    }
}
