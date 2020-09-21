package com.example.guesstheshow;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

public class QuizActivity extends AppCompatActivity {
    private QuizViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        Intent intent = getIntent();

        viewModel = new ViewModelProvider(this).get(QuizViewModel.class);
        viewModel.setContext(getApplicationContext());
        viewModel.setCategory(intent.getStringExtra("category"));
        viewModel.setTag(intent.getStringExtra("choice"));

        if(!viewModel.state){
            viewModel.startRequest();
        }

        viewModel.getDataPairs().observe(this, new Observer<ArrayList<String[]>>() {
            @Override
            public void onChanged(ArrayList<String[]> strings) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        viewModel.stopRequests();
    }
}