package com.example.guesstheshow;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.guesstheshow.databinding.ActivityChoicesBinding;

public class ChoicesActivity extends AppCompatActivity {
    private String category;
    private ActivityChoicesBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChoicesBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();
        setContentView(root);

        Intent intent = getIntent();
        category = intent.getStringExtra("category");

        if(!category.equals("anime")){
            binding.charactersCard.setVisibility(View.INVISIBLE);
        }
    }

    public void showRounds(View view){
        String tag = view.getTag().toString();
        Intent intent = new Intent(ChoicesActivity.this, QuizActivity.class);
        intent.putExtra("category",category);
        intent.putExtra("choice",tag);
        startActivity(intent);
    }
}