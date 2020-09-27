package com.example.guesstheshow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;

import com.example.guesstheshow.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();
        setContentView(root);

        auth = FirebaseAuth.getInstance();
    }

    public void authenticate(final View view){
        final String email = binding.email.getText().toString();
        final String password = binding.password.getText().toString();
        if(email.isEmpty() || password.isEmpty()){
            Snackbar.make(view,getString(R.string.emtpyDetails),Snackbar.LENGTH_SHORT).show();
        }else{
            binding.progressBar.setVisibility(View.VISIBLE);
            auth.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                binding.progressBar.setVisibility(View.INVISIBLE);
                                showSuccess(view);
                            }else{
                                signup(email,password, view);
                            }
                        }
                    });
        }
    }

    public  void signup(String email, String password, final View view){
        auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            binding.progressBar.setVisibility(View.INVISIBLE);
                            showSuccess(view);
                        }else{
                            binding.progressBar.setVisibility(View.INVISIBLE);
                            Snackbar.make(view,getString(R.string.failure),Snackbar.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public void showSuccess(View view){
        Snackbar.make(view,getString(R.string.success),Snackbar.LENGTH_LONG).show();
        new CountDownTimer(1000, 500) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                finish();
            }
        }.start();
    }
}