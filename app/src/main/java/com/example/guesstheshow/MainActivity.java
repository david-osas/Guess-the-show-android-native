package com.example.guesstheshow;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }



    public void showChoices(View view){
        String tag = view.getTag().toString();
        Intent intent = new Intent(MainActivity.this,ChoicesActivity.class);
        intent.putExtra("category",tag);
        startActivity(intent);
    }
}