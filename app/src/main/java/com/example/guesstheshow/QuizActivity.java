package com.example.guesstheshow;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.guesstheshow.databinding.ActivityQuizBinding;

import java.util.ArrayList;
import java.util.List;


public class QuizActivity extends AppCompatActivity {
    private ActivityQuizBinding binding;
    private QuizViewModel viewModel;
    private AlertDialog dialog;
    private Button dialogButton, answerButton;
    private Button[] buttonArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQuizBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();
        setContentView(root);

        Intent intent = getIntent();
        buttonArray = new Button[]{binding.answer1, binding.answer2, binding.answer3, binding.answer4};

        viewModel = new ViewModelProvider(this).get(QuizViewModel.class);
        viewModel.setInitial(getApplicationContext());
        viewModel.category = intent.getStringExtra("category");
        viewModel.tag = intent.getStringExtra("choice");
        viewModel.width = binding.imageView.getWidth();
        viewModel.height = binding.imageView.getHeight();



        if(!viewModel.state){
            createFetchingDialog();
            dialog.show();
            dialog.setCanceledOnTouchOutside(false);
            dialogButton = dialog.getButton(AlertDialog.BUTTON_NEUTRAL);
            dialogButton.setEnabled(false);
            viewModel.startRequest();
        }

        viewModel.getImage().observe(this, new Observer<Bitmap>() {
            @Override
            public void onChanged(Bitmap bitmap) {
                binding.imageView.setImageBitmap(bitmap);
            }
        });

        viewModel.checkRounds().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(final Integer integer) {
                createRoundsDialog();

                new CountDownTimer(1000, 500) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                    }

                    @Override
                    public void onFinish() {
                        String val;
                        int[] scores = viewModel.getQuizScores();
                        if(integer == 1){
                            viewModel.stopTimer();
                            val = getString(R.string.progressMessage);
                            val = String.format(val, scores[0], scores[1], scores[2] );
                            dialog.setMessage(val);
                            dialog.show();
                        }else if(integer == 2){
                            viewModel.stopTimer();
                            val = getString(R.string.endMessage);
                            val = String.format(val, scores[0], scores[1], scores[2] );
                            dialog.setMessage(val);
                            dialog.show();
                        }
                        dialog.setCanceledOnTouchOutside(false);
                    }
                }.start();

            }
        });

        viewModel.getTimer().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if(integer == -1){
                    setQuizDetails();
                }else{
                    binding.countdownText.setText(Integer.toString(integer));
                }
            }
        });

        viewModel.isFetchingData().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean fetching) {
                if(!fetching){
                    dialogButton.setEnabled(true);
                }
            }
        });

        viewModel.checkEnd().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean){
                    new CountDownTimer(1000, 500) {
                        @Override
                        public void onTick(long millisUntilFinished) {

                        }

                        @Override
                        public void onFinish() {
                            moveToResults();
                        }
                    }.start();
                }
            }
        });

    }

    public void checkAnswer(View view){
        answerButton = (Button) view;

        for(Button b : buttonArray){
            b.setEnabled(false);
        }

        boolean check = viewModel.checkAnswer(answerButton.getText().toString());
        if(check){
            answerButton.setBackgroundColor(getColor(R.color.correct));
        }else{
            answerButton.setBackgroundColor(getColor(R.color.wrong));
        }

        if(!viewModel.isRound()){
            new CountDownTimer(1000, 500) {
                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    refreshAnswers(answerButton);
                }
            }.start();
        }
    }

    public void refreshAnswers(Button button){
        for(Button b : buttonArray){
            b.setEnabled(true);
        }
        viewModel.stopTimer();
        button.setBackgroundColor(getColor(R.color.options));
        binding.imageView.setImageDrawable(getDrawable(R.drawable.ic_launcher_foreground));
        setQuizDetails();
    }

    public void createFetchingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(R.string.fetchingDataTitle)
                .setMessage(R.string.fetchingDataMessage)
                .setNeutralButton(R.string.dialogButton, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setQuizDetails();
                        dialog.dismiss();
                    }
                });

        dialog = builder.create();
    }

    public void createRoundsDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(R.string.progressTitle)
                .setNeutralButton(R.string.dialogButton, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        refreshAnswers(answerButton);
                        dialog.dismiss();
                    }
                });

        dialog = builder.create();
    }

    public void setQuizDetails(){
        int answer = (int) Math.floor(Math.random() * 4);
        ArrayList<String> details = viewModel.getCurrentQuizData();
        List<String> subDetails = details.subList(1,4);

        for(int i = 0; i < buttonArray.length; i++){
            if(i != answer){
                buttonArray[i].setText(subDetails.get(0));
                subDetails.remove(0);
            }
        }
        buttonArray[answer].setText(details.get(0));

    }

    public void moveToResults(){
        checkCharactersUnlock();

        Intent intent = new Intent(this, ResultsActivity.class);
        intent.putExtra("scores",viewModel.getQuizScores());
        intent.putExtra("category",viewModel.category);
        startActivity(intent);
        finish();
    }

    public void checkCharactersUnlock(){
        if(viewModel.category.equals("anime")){
            SharedPreferences preferences = QuizActivity.this.getSharedPreferences("characterMode",MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            int[] scores = viewModel.getQuizScores();
            if(scores[0] >= 3 && scores[2] >= 10 && preferences.getInt("lockMode", 0) == 0){
                editor.putInt("lockMode",1);
                editor.apply();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        viewModel.stopRequests();
        checkCharactersUnlock();

        Intent intent = new Intent(QuizActivity.this,ChoicesActivity.class);
        intent.putExtra("category",viewModel.category);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        checkCharactersUnlock();
    }
}
