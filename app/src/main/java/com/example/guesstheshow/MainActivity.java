package com.example.guesstheshow;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.guesstheshow.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();
        setContentView(root);


    }

    public void showChoices(View view){
        String tag = view.getTag().toString();
        Intent intent = new Intent(MainActivity.this,ChoicesActivity.class);
        intent.putExtra("category",tag);
        startActivity(intent);
    }
}