package com.example.guesstheshow;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.CountDownTimer;
import android.widget.ImageView;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;

public class QuizViewModel extends ViewModel {
    public String category, tag;
    private int[] tmdbPages = {1,2,3,4,5}, animePages = {1,2};
    private String requestTag = "quiz_data";
    private String tmdbShowsBaseUrl = "https://api.themoviedb.org/3/%s/popular?api_key=%s&language=en-US&page=";
    private String animeShowsBaseUrl = "https://api.jikan.moe/v3/top/anime/%d/tv";
    private String animeCharactersBaseUrl = "https://api.jikan.moe/v3/top/characters/%d";
    private String imageBaseUrl = "https://image.tmdb.org/t/p/w185";
    private RequestQueue requestQueue;
    private ArrayList<String[]> generalDataPairs = new ArrayList<>();
    private String answer;
    private int index = 0;
    private MutableLiveData<Boolean> fetchingData = new MutableLiveData<>();
    private MutableLiveData<Integer> rounds = new MutableLiveData<>();
    private MutableLiveData<Integer> timer = new MutableLiveData<>();
    private MutableLiveData<Bitmap> image = new MutableLiveData<>();
    private MutableLiveData<Boolean> end = new MutableLiveData<>();
    private int questions = 0, correct = 0, completedRounds = 0;
    private CountDownTimer countDownTimer;
    public int width, height;
    public boolean state = false;


    public void setInitial(Context c){
        if(requestQueue == null){
            requestQueue = Volley.newRequestQueue(c);
            fetchingData.setValue(true);
            rounds.setValue(0);
            end.setValue(false);
        }
    }

    public LiveData<Boolean> isFetchingData(){
        return fetchingData;
    }

    public LiveData<Integer> checkRounds(){
        return rounds;
    }

    public LiveData<Integer> getTimer(){
        return timer;
    }

    public LiveData<Bitmap> getImage(){
        return image;
    }

    public LiveData<Boolean> checkEnd(){
        return end;
    }

    public int getMaxDataSize(){
        return generalDataPairs.size();
    }

    public int[] getQuizScores(){
        return new int[]{completedRounds, questions, correct};
    }

    public void startTimer(){
        countDownTimer = new CountDownTimer(40000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    timer.setValue((int) (millisUntilFinished/1000));
                }

                @Override
                public void onFinish() {
                    handleRounds();
                    if(!isRound()){
                        timer.setValue(-1);
                    }
                }
            };
        countDownTimer.start();
    }

    public void stopTimer(){
        countDownTimer.cancel();
    }

    public boolean isRound(){
        return rounds.getValue() > 0;
    }


    public boolean checkAnswer(String choice){
        boolean val;
        if(choice.equalsIgnoreCase(answer)){
            correct++;
            val = true;
        }else{
            val = false;
        }
        handleRounds();

        return val;
    }

    private void handleRounds(){
        if((index)%10 == 0 && index != generalDataPairs.size()){
            if(generalDataPairs.size() - index <= 10){
                rounds.setValue(2);
            }else{
                rounds.setValue(1);
            }
            completedRounds++;
        }else{
            rounds.setValue(0);
        }
        questions++;
        if(index == generalDataPairs.size()){
            end.setValue(true);
        }
    }

    public ArrayList<String> getCurrentQuizData(){
        ImageRequest request;
        ArrayList<String> values = new ArrayList<>();
        String[] current = generalDataPairs.get(index);
        answer = current[0];

        if(category.equals("anime")){
            request = getImageRequest(current[1]);
        }else{
            request = getImageRequest(imageBaseUrl+current[1]);
        }
        request.setTag(requestTag);
        requestQueue.add(request);

        values.add(current[0]);

        HashSet<Integer> randoms = getRandomIndexes(generalDataPairs.size());
        for(int r : randoms){
            String[] others = generalDataPairs.get(r);
            values.add(others[0]);
        }
        index++;

        return values;
    }
    
    private HashSet<Integer> getRandomIndexes(int range){
        HashSet<Integer> randoms = new HashSet<>();

        while(randoms.size() < 3){
            int val = (int) Math.floor(Math.random() * range);
            if(val != index){
                randoms.add(val);
            }
        }
        return randoms;

    }


    public void startRequest(){
        state = true;
        String url;

        if(category.equals("anime")){
            url = buildUrl("anime",tag);
            
        }else if(category.equals("movies")){
                url = buildUrl("movie", tag);

        }else{
            url = buildUrl("tv", tag);

        }
        makeApiCall(url);
    }

    public void stopRequests(){
        if(requestQueue != null){
            requestQueue.cancelAll(requestTag);
        }
    }


    private String buildUrl(String category, String tag){
        String url;

        if(category.equals("anime")){
            if(tag.equals("shows")){
                url = animeShowsBaseUrl;
            }else{
                url = animeCharactersBaseUrl;
            }
        }else{
            url = String.format(tmdbShowsBaseUrl, category, BuildConfig.TMDB_KEY) + "%d";
        }

        return url;
    }

    private  void makeApiCall(String url){
        JsonObjectRequest data;

        if(category.equals("anime")){
            for (int animePage : animePages) {
                String tempUrl = String.format(url, animePage);
                data = getAnimeObject(tempUrl, animePage);
                data.setTag(requestTag);
                requestQueue.add(data);
            }
        }else{
            for (int tmdbPage : tmdbPages) {
                String tempUrl = String.format(url, tmdbPage);
                data = getTMDBShowsObject(tempUrl, tmdbPage);
                data.setTag(requestTag);
                requestQueue.add(data);
            }
        }

    }

    private JsonObjectRequest getAnimeObject(String url, final int page){
        JsonObjectRequest data = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray array = response.getJSONArray("top");
                    for(int j = 0; j < array.length(); j++){
                        JSONObject object = array.getJSONObject(j);
                        String[] pairs = {object.getString("title"), object.getString("image_url")};
                        generalDataPairs.add(pairs);
                        if(j == array.length()-1 && page == 2){
                            fetchingData.postValue(false);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        return data;
    }

    private JsonObjectRequest getTMDBShowsObject(String url,final int page){
        JsonObjectRequest data = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray array = response.getJSONArray("results");
                    for(int j = 0; j < array.length(); j++){
                        JSONObject object = array.getJSONObject(j);
                        String title = category.equals("movies")? object.getString("title") : object.getString("name");
                        String[] pairs = {title, object.getString("poster_path")};
                        generalDataPairs.add(pairs);
                        if(j == array.length()-1 && page == 5){
                            fetchingData.postValue(false);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        return data;
    }

    private ImageRequest getImageRequest(String url){
        ImageRequest request = new ImageRequest(url, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                image.postValue(response);
                startTimer();
            }
        }, width, height, ImageView.ScaleType.FIT_XY, null
                , new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        return request;
    }

}
