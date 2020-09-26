package com.example.guesstheshow;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class HighscoresAdapter extends RecyclerView.Adapter<HighscoresAdapter.HighscoresViewHolder> {

    public class HighscoresViewHolder extends RecyclerView.ViewHolder {
        public TextView name, category, score;
        public HighscoresViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            category = itemView.findViewById(R.id.category);
            score = itemView.findViewById(R.id.score);
        }
    }

    public void setDataset(){

    }

    @NonNull
    @Override
    public HighscoresViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.highscore_item,parent,false);

        HighscoresViewHolder viewHolder = new HighscoresViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull HighscoresViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }


}
