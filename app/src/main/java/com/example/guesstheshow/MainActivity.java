package com.example.guesstheshow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.guesstheshow.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.user,menu);

        MenuItem highscores = menu.findItem(R.id.highscores);

        highscores.setVisible(false);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId() == R.id.highscores){
            Intent intent = new Intent(this, HighscoresActivity.class);
            startActivity(intent);

        }else if(item.getItemId() == R.id.login && !item.getTitle().toString().equals("Logout")){
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);

        }else if(item.getTitle().toString().equals("Logout")){

        }

        return true;
    }

    public void showChoices(View view){
        String tag = view.getTag().toString();
        Intent intent = new Intent(MainActivity.this,ChoicesActivity.class);
        intent.putExtra("category",tag);
        startActivity(intent);
    }
}