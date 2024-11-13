package com.example.smartpills;

import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MainRecyclerAdapter extends RecyclerView.Adapter<MainRecyclerAdapter.ViewHolder> {

    private ArrayList<String> medicaments;
    private ArrayList<String> datesEtHeures;





    public MainRecyclerAdapter(ArrayList<String> medicaments, ArrayList<String> datesEtHeures){
        this.medicaments = medicaments;
        this.datesEtHeures = datesEtHeures;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_main_historique,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.medicineText.setText(medicaments.get(position));
        String h = datesEtHeures.get(position);
        if (h.equals("Alerte")){
            holder.dateText.setError(h);
            holder.dateText.setTypeface(Typeface.DEFAULT_BOLD);
            holder.dateText.setTextColor(Color.RED);
        } else if (h.equals("En attente")){
            holder.dateText.setTextColor(Color.RED);
        }
        holder.dateText.setText(h);

    }

    @Override
    public int getItemCount() {
        return datesEtHeures.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView medicineText;
        TextView dateText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.historique_view);
            medicineText = itemView.findViewById(R.id.date);
            dateText = itemView.findViewById(R.id.medicament);
        }
    }
}
