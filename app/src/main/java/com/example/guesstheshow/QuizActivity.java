package com.example.guesstheshow;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.guesstheshow.databinding.ActivityQuizBinding;


public class QuizActivity extends AppCompatActivity {
    private ActivityQuizBinding binding;
    private QuizViewModel viewModel;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQuizBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();
        setContentView(root);

        Intent intent = getIntent();

        viewModel = new ViewModelProvider(this).get(QuizViewModel.class);
        viewModel.setInitial(getApplicationContext());
        viewModel.setCategory(intent.getStringExtra("category"));
        viewModel.setTag(intent.getStringExtra("choice"));

        createFetchingDialog();

        if(!viewModel.state){
            dialog.show();
            viewModel.startRequest();
        }

        viewModel.checkRounds().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                createRoundsDialog();
                int[] scores = viewModel.getQuizScores();
                String val;
                if(integer == 1){
                    val = getString(R.string.progressMessage);
                    val = String.format(val, scores[0], scores[1], scores[2] );
                    dialog.setMessage(val);
                    dialog.show();
                }else if(integer == 2){
                    val = getString(R.string.endMessage);
                    val = String.format(val, scores[0], scores[1], scores[2] );
                    dialog.setMessage(val);
                    dialog.show();
                }
            }
        });

        viewModel.getTimer().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                binding.countdownText.setText(Integer.toString(integer));
            }
        });

        viewModel.isFetchingData().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean fetching) {
                if(!fetching){
                    dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setEnabled(true);
                }

            }
        });

    }

    public void checkAnswer(View view){
        Button button = (Button) view;

        Button[] buttonArray = {binding.answer1, binding.answer2, binding.answer3, binding.answer4};
        for(Button b : buttonArray){
            b.setEnabled(false);
        }

        boolean check = viewModel.checkAnswer(button.getText().toString());
        if(check){
            button.setBackgroundColor(getColor(R.color.correct));
        }else{
            button.setBackgroundColor(getColor(R.color.wrong));
        }

        viewModel.startTimer();
    }

    public void createFetchingDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(R.string.fetchingDataTitle)
                .setMessage(R.string.fetchingDataMessage)
                .setNeutralButton(R.string.dialogButton, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        dialog = builder.create();
        dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setEnabled(false);
    }

    public void createRoundsDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(R.string.progressTitle)
                .setNeutralButton(R.string.dialogButton, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        dialog = builder.create();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        viewModel.stopRequests();
    }
}