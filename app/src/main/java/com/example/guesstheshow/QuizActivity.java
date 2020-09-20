package com.example.guesstheshow;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

public class QuizActivity extends AppCompatActivity {
    private QuizViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        viewModel = new ViewModelProvider(this).get(QuizViewModel.class);

        if(viewModel.category.isEmpty()){
            Intent intent = getIntent();
            viewModel.category = intent.getStringExtra("category");
            viewModel.tag = intent.getStringExtra("tag");
        }
    }
}