package com.example.guesstheshow;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.guesstheshow.databinding.ActivityResultsBinding;

public class ResultsActivity extends AppCompatActivity {
    private ActivityResultsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityResultsBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();
        setContentView(root);

        Intent intent = getIntent();
        setDetails(intent.getIntArrayExtra("scores"));

    }

    public void setDetails(int[] scores){
        String rounds = String.format(getString(R.string.resultsRounds), scores[0]);
        String questions = String.format(getString(R.string.resultsQuestions), scores[1]);
        String number = String.format(getString(R.string.resultsCorrectNumber), scores[2]);
        binding.rounds.setText(rounds);
        binding.questions.setText(questions);
        binding.number.setText(number);
        if(scores[2] >= scores[1]/2){
            binding.number.setTextColor(getColor(R.color.correct));
        }else{
            binding.number.setTextColor(getColor(R.color.wrong));
        }

    }
}