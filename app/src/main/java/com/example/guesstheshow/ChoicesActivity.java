package com.example.guesstheshow;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
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
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Intent intent = getIntent();
        category = intent.getStringExtra("category");

        animeUISettings();
    }



    public void animeUISettings(){
        SharedPreferences preferences =  ChoicesActivity.this.getSharedPreferences("characterMode",MODE_PRIVATE);
        if(!category.equals("anime")){
            binding.charactersCard.setVisibility(View.INVISIBLE);
            binding.lockMode.setVisibility(View.INVISIBLE);
        }else{
            if(preferences.getInt("lockMode", 0) == 1){
                binding.lockMode.setText(getString(R.string.unlocked));
                binding.charactersCard.setCardBackgroundColor(getColor(R.color.colorPrimary));
                binding.charactersCard.setEnabled(true);
            }else{
                binding.lockMode.setText(getString(R.string.locked));
                binding.charactersCard.setCardBackgroundColor(getColor(R.color.options));
                binding.charactersCard.setEnabled(false);
            }
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