package com.example.guesstheshow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.guesstheshow.databinding.ActivityMainBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    @Override
    protected void onResume() {
        super.onResume();

        invalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.user,menu);



        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        auth = FirebaseAuth.getInstance();
        super.onPrepareOptionsMenu(menu);
        MenuItem highscores = menu.findItem(R.id.highscores);
        MenuItem user = menu.findItem(R.id.login);

        if(auth.getCurrentUser() == null){
            highscores.setVisible(false);
            user.setTitle(getString(R.string.login));
        }else{
            highscores.setVisible(true);
            user.setTitle(getString(R.string.logout));
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        auth = FirebaseAuth.getInstance();

        if(item.getItemId() == R.id.highscores){
            Intent intent = new Intent(this, HighscoresActivity.class);
            startActivity(intent);

        }else if(item.getItemId() == R.id.login && !item.getTitle().toString().equals("Logout")){
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);

        }else if(item.getTitle().toString().equalsIgnoreCase("Logout")){
            auth.signOut();
            Toast.makeText(MainActivity.this,getString(R.string.logoutMessage),Toast.LENGTH_SHORT).show();
            invalidateOptionsMenu();
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