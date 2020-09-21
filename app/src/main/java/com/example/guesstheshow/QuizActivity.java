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

        Intent intent = getIntent();

        viewModel = new ViewModelProvider(this).get(QuizViewModel.class);
        viewModel.setInitial(getApplicationContext());
        viewModel.setCategory(intent.getStringExtra("category"));
        viewModel.setTag(intent.getStringExtra("choice"));

        if(!viewModel.state){
            viewModel.startRequest();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        viewModel.stopRequests();
    }
}