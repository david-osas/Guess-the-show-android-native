package com.example.guesstheshow;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class QuizViewModel extends ViewModel {
    public String category = "", tag = "";
    private int[] tmdbPages = {1,2,3,4,5}, animePages = {1,2};
    private String requestTag = "quiz_data";
    private String tmdbShowsBaseUrl = "https://api.themoviedb.org/3/%s/popular?api_key=%s&language=en-US&page=%d";
    private String tmdbCharactersBaseUrl = "https://api.themoviedb.org/3/movie/%d/credits?api_key=%s";
    private String animeShowsBaseUrl = "https://api.jikan.moe/v3/top/anime/%d/tv";
    private String animeCharactersBaseUrl = "https://api.jikan.moe/v3/top/characters/%d";
    private RequestQueue requestQueue;
    private ArrayList<String[]> dataPairs = new ArrayList<>();
    private ArrayList<String> dataTitles = new ArrayList<>();

    QuizViewModel(Context c){
        requestQueue = Volley.newRequestQueue(c);
    }


    public void startRequest(String category, String tag){
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
                        String characterUrl = String.format(tmdbCharactersBaseUrl, id, BuildConfig.TMDB_KEY);
                        JsonObjectRequest newObjectRequest = getTMDBCharactersObject(characterUrl);
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
            url = String.format(tmdbShowsBaseUrl, category, BuildConfig.TMDB_KEY);
            
        }

        return url;
    }

    private  void makeApiCall(String category, String tag, String url){
        JsonObjectRequest data;

        if(category.equals("anime")){
            for (int animePage : animePages) {
                url = String.format(url, animePage);
                data = getAnimeObject(url);
                data.setTag(requestTag);
                requestQueue.add(data);
            }
        }else if(tag.equals("shows")){
            for (int tmdbPage : tmdbPages) {
                url = String.format(url, tmdbPage);
                data = getTMDBShowsObject(url);
                data.setTag(requestTag);
                requestQueue.add(data);
            }
        }

    }

    private JsonObjectRequest getAnimeObject(String url){
        JsonObjectRequest data = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray array = response.getJSONArray("top");
                    for(int j = 0; j < array.length(); j++){
                        JSONObject object = array.getJSONObject(j);
                        String[] pairs = {object.getString("title"), object.getString("image_url")};
                        dataPairs.add(pairs);
                        dataTitles.add(object.getString("title"));
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

    private JsonObjectRequest getTMDBShowsObject(String url){
        JsonObjectRequest data = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray array = response.getJSONArray("results");
                    for(int j = 0; j < array.length(); j++){
                        JSONObject object = array.getJSONObject(j);
                        String[] pairs = {object.getString("title"), object.getString("poster_path")};
                        dataPairs.add(pairs);
                        dataTitles.add(object.getString("title"));
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

    private JsonObjectRequest getTMDBCharactersObject(String url){
        JsonObjectRequest data = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray array = response.getJSONArray("cast");
                    int length = Math.min(array.length(), 20);
                    for(int j = 0; j < length; j++){
                        JSONObject object = array.getJSONObject(j);
                        String[] pairs = {object.getString("character"), object.getString("profile_path")};
                        dataPairs.add(pairs);
                        dataTitles.add(object.getString("character"));
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
