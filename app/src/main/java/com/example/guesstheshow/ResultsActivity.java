package com.example.guesstheshow;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.guesstheshow.databinding.ActivityResultsBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class ResultsActivity extends AppCompatActivity {
    private ActivityResultsBinding binding;
    private String category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityResultsBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();
        setContentView(root);

        Intent intent = getIntent();
        category = intent.getStringExtra("category");
        setDetails(intent.getIntArrayExtra("scores"));
        addToCloudDB(intent.getIntArrayExtra("scores")[2],category);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(ResultsActivity.this,ChoicesActivity.class);
        intent.putExtra("category",category);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void addToCloudDB(int score, String category){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if(auth.getCurrentUser() != null){
            String email = auth.getCurrentUser().getEmail();
            String[] emailSplit = email.split("@");

            HashMap<String,Object> data = new HashMap<>();
            data.put("title",emailSplit[0]);
            data.put("category",category);
            data.put("score",score);

            db.collection("leaderBoard")
                    .add(data)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Toast.makeText(ResultsActivity.this,getString(R.string.cloudDBSuccess),Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    public void setDetails(int[] scores){
        String rounds = String.format(getString(R.string.resultsRounds), scores[0]);
        String questions = String.format(getString(R.string.resultsQuestions), scores[1]);
        String number = String.format(getString(R.string.resultsCorrectNumber), scores[2]);
        binding.rounds.setText(rounds);
        binding.questions.setText(questions);
        binding.number.setText(number);
        if(scores[2] >= scores[1]/2 && scores[2] != 0){
            binding.number.setTextColor(getColor(R.color.correct));
        }else{
            binding.number.setTextColor(getColor(R.color.wrong));
        }

    }
}