package com.example.irrigation.fragments;

import static android.content.Context.LOCALE_SERVICE;
import static android.content.Context.LOCATION_SERVICE;
import static androidx.core.content.ContextCompat.getSystemService;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.irrigation.R;
import com.example.irrigation.WeatherRVAdapter;
import com.example.irrigation.WeatherRVModel;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class WeatherFragment extends Fragment {

    private ConstraintLayout home;
    private TextView citytxt, temptxt, tempconditiontxt;
    private ImageView backimg, tempimg;
    private RecyclerView RVforcast;
    private ArrayList<WeatherRVModel> weatherRVModelArrayList;
    private WeatherRVAdapter weatherRVAdapter;
    private LocationManager locationManager;
    private int PERMISSION_CODE = 1;
    private String cityName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        home = getView().findViewById(R.id.CLHome);
        citytxt = getView().findViewById(R.id.citytxt);
        temptxt = getView().findViewById(R.id.temptxt);
        tempconditiontxt = getView().findViewById(R.id.tempconditiontxt);
        backimg = getView().findViewById(R.id.backimg);
        tempimg = getView().findViewById(R.id.tempimg);
        RVforcast = getView().findViewById(R.id.RVforcast);
        weatherRVModelArrayList = new ArrayList<>();
        weatherRVAdapter = new WeatherRVAdapter(getContext(), weatherRVModelArrayList);
        RVforcast.setAdapter(weatherRVAdapter);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if(ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions((Activity) getContext(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_CODE);
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        cityName = getCityName(location.getLongitude(), location.getLatitude());
        getWeatherInfo(cityName);

    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
            Toast.makeText(getContext(), "Permission granted...", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getContext(), "Please provide permissions...", Toast.LENGTH_SHORT).show();
        }
    }

    private String getCityName(double longitude, double latitude){
        String cityName = "NOT FOUND";
        Geocoder gcd = new Geocoder(getContext(), Locale.getDefault());
        try{
            List<Address> addresses = gcd.getFromLocation(latitude, longitude, 10);
            for(Address add : addresses){
                if(add!=null){
                    String city = add.getLocality();
                    if(city!=null && !city.equals("")){
                        cityName = city;
                    }else {
                        Log.d("TAG", "CITY NOT fOUND");
                        //Toast.makeText(this, "City not found...", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        } catch (IOException e){
            e.printStackTrace();
        }
        return cityName;
    }

    private void getWeatherInfo(String cityName){
        String url = "http://api.weatherapi.com/v1/forecast.json?key=53c14852a4ca406c84194454230104&q="+cityName+"&days=1&aqi=yes&alerts=yes";
        citytxt.setText(cityName);
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                weatherRVModelArrayList.clear();
                try {
                    String temperature = response.getJSONObject("current").getString("temp_c");
                    temptxt.setText(temperature+"℃");
                    int isDay = response.getJSONObject("current").getInt("is_day");
                    String condition = response.getJSONObject("current").getJSONObject("condition").getString("text");
                    String conditionIcon = response.getJSONObject("current").getJSONObject("condition").getString("icon");
                    Picasso.get().load("http:".concat(conditionIcon)).into(tempimg);
                    tempconditiontxt.setText(condition);
                    if(isDay==1){
                        Picasso.get().load("https://wallpapercave.com/wp/wp8092186.jpg").into(backimg);
                    }else{
                        Picasso.get().load("https://th.bing.com/th/id/OIP._9cYcXIelBBjQMcGWJ-R9QHaHa?pid=ImgDet&rs=1").into(backimg);
                    }
                    JSONObject forcastObj = response.getJSONObject("forecast");
                    JSONObject forcastO = forcastObj.getJSONArray("forecastday").getJSONObject(0);
                    JSONArray hourArray = forcastO.getJSONArray("hour");
                    for(int i = 0; i < hourArray.length(); i++){
                        JSONObject hourObj = hourArray.getJSONObject(i);
                        String time = hourObj.getString("time");
                        String temp = hourObj.getString("temp_c");
                        String img = hourObj.getJSONObject("condition").getString("icon");
                        String wind =hourObj.getString("wind_kph");
                        weatherRVModelArrayList.add(new WeatherRVModel(time, temp, img, wind));
                    }
                    weatherRVAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    Log.d("Error", String.valueOf(e));
                    throw new RuntimeException(e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "Please enter valid city name...", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_weather, container, false);
    }
}