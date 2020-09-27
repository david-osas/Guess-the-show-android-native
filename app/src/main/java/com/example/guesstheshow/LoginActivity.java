package com.example.guesstheshow;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.guesstheshow.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();
        setContentView(root);
    }

    public void toggleState(View view){
        Button button = (Button) view;

        if(button.getText().toString().equalsIgnoreCase(getString(R.string.signupButton))){
            binding.signup.setBackgroundColor(getColor(R.color.options));
            binding.login.setBackgroundColor(getColor(R.color.grey));
            binding.username.setVisibility(View.VISIBLE);
        }else{
            binding.signup.setBackgroundColor(getColor(R.color.grey));
            binding.login.setBackgroundColor(getColor(R.color.options));
            binding.username.setVisibility(View.INVISIBLE);
        }
    }
}