package com.example.guesstheshow;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class ChoicesActivity extends AppCompatActivity {
    String category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choices);

        Intent intent = getIntent();
        category = intent.getStringExtra("category");
    }

    public void showRounds(View view){
        String tag = view.getTag().toString();
        Intent intent = new Intent(ChoicesActivity.this, RoundsActivity.class);
        intent.putExtra("category",category);
        intent.putExtra("choice",tag);
        startActivity(intent);
    }
}