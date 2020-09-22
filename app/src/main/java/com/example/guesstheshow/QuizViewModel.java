package com.example.guesstheshow;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;

public class QuizViewModel extends ViewModel {
    private String category, tag;
    private int[] tmdbPages = {1,2,3,4,5}, animePages = {1,2};
    private String requestTag = "quiz_data";
    private String tmdbShowsBaseUrl = "https://api.themoviedb.org/3/%s/popular?api_key=%s&language=en-US&page=";
    private String tmdbCharactersBaseUrl = "https://api.themoviedb.org/3/%s/%d/credits?api_key=%s";
    private String animeShowsBaseUrl = "https://api.jikan.moe/v3/top/anime/%d/tv";
    private String animeCharactersBaseUrl = "https://api.jikan.moe/v3/top/characters/%d";
    private RequestQueue requestQueue;
    private ArrayList<String[]> generalDataPairs = new ArrayList<>();
    private ArrayList<String[]> charactersDataPairs = new ArrayList<>();
    private String[] answer;
    private int index = 0;
    private MutableLiveData<Boolean> fetchingData = new MutableLiveData<>();
    private MutableLiveData<Integer> rounds = new MutableLiveData<>();
    public boolean state = false;


    public void setInitial(Context c){
        if(requestQueue == null){
            requestQueue = Volley.newRequestQueue(c);
            fetchingData.setValue(true);
            rounds.setValue(0);
        }
    }

    public void setCategory(String s){
        category = s;
    }
    public void setTag(String s){
        tag = s;
    }

    public ArrayList<String[]> getGeneralDataPairs() {
        return generalDataPairs;
    }
    public ArrayList<String[]> getCharactersDataPairs() {
        return charactersDataPairs;
    }

    public LiveData<Boolean> isFetchingData(){
        return fetchingData;
    }

    public LiveData<Integer> checkRounds(){
        return rounds;
    }


    public boolean checkAnswer(String choice){
        if((index + 1)%10 == 0 && index > 0){
            if(selectData().size() - (index +1) <= 10){
                rounds.setValue(2);
            }else{
                rounds.setValue(1);
            }
        }
        return choice.equalsIgnoreCase(answer[0]);
    }

    public ArrayList<String> getCurrentQuizData(){
        ArrayList<String> values = new ArrayList<>();

        String[] current = selectData().get(index);
        answer = current;
        values.add(current[0]);

        HashSet<Integer> randoms = getRandomIndexes(generalDataPairs.size());
        for(int r : randoms){
            String[] others = selectData().get(r);
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

    private ArrayList<String[]> selectData(){
        if(!category.equals("anime") && tag.equals("characters")){
            return charactersDataPairs;
        }else{
            return generalDataPairs;
        }
    }


    public void startRequest(){
        state = true;
        String url = "";

        if(category.equals("anime")){
            url = buildUrl("anime",tag);
            
        }else{
            if(category.equals("movies")){
                url = buildUrl("movie", tag);

            }else if(category.equals("series")){
                url = buildUrl("tv", tag);

            }

            if(tag.equals("characters")){
                url = String.format(url, 1);
                JsonObjectRequest idsRequest = getIdsObjectRequest(url);
                idsRequest.setTag(requestTag);
                requestQueue.add(idsRequest);
            }
        }
        makeApiCall(category, tag, url);

    }

    public void stopRequests(){
        if(requestQueue != null){
            requestQueue.cancelAll(requestTag);
        }
    }

    private JsonObjectRequest getIdsObjectRequest(String url){
        JsonObjectRequest idsRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray array = response.getJSONArray("results");
                    int i = 0;
                    while(i < array.length()){
                        JSONObject object = array.getJSONObject(i);
                        int id = object.getInt("id");
                        String title = category.equals("movies")? object.getString("title") : object.getString("name");
                        String characterUrl = category.equals("movies") ? String.format(tmdbCharactersBaseUrl, "movie", id, BuildConfig.TMDB_KEY)
                                : String.format(tmdbCharactersBaseUrl, "tv", id, BuildConfig.TMDB_KEY);
                        JsonObjectRequest newObjectRequest = getTMDBCharactersObject(characterUrl, title, i);
                        newObjectRequest.setTag(requestTag);
                        requestQueue.add(newObjectRequest);

                        i++;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("id error", error.getMessage());
            }
        });

        return idsRequest;
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

    private  void makeApiCall(String category, String tag, String url){
        JsonObjectRequest data;

        if(category.equals("anime")){
            for (int animePage : animePages) {
                String tempUrl = String.format(url, animePage);
                data = getAnimeObject(tempUrl, animePage);
                data.setTag(requestTag);
                requestQueue.add(data);
            }
        }else if(tag.equals("shows")){
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
                Log.i("id error", error.getMessage());
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
                Log.i("id error", error.getMessage());
            }
        });

        return data;
    }

    private JsonObjectRequest getTMDBCharactersObject(String url, final String showTitle, final int page){
        JsonObjectRequest data = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray array = response.getJSONArray("cast");
                    int length = Math.min(array.length(), 20);
                    for(int j = 0; j < length; j++){
                        JSONObject object = array.getJSONObject(j);
                        String[] pairs = {object.getString("character"), object.getString("profile_path"), showTitle};
                        charactersDataPairs.add(pairs);
                        if(j == array.length()-1 && page == 19){
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
                Log.i("id error", error.getMessage());
            }
        });

        return data;
    }

}
