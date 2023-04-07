package com.example.irrigation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;

import com.example.irrigation.fragments.HomeFragment;
import com.example.irrigation.fragments.PredictionFragment;
import com.example.irrigation.fragments.WeatherFragment;

public class MainActivity extends AppCompatActivity {

    ViewPager2 myViewPager2;
    ViewPagerAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myViewPager2 = findViewById(R.id.viewPager);

        myAdapter = new ViewPagerAdapter(getSupportFragmentManager(), getLifecycle());

        // add Fragments in your ViewPagerFragmentAdapter class
        myAdapter.addFragment(new WeatherFragment());
        myAdapter.addFragment(new HomeFragment());
        myAdapter.addFragment(new PredictionFragment());

        // set Orientation in your ViewPager2
        myViewPager2.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);

        myViewPager2.setAdapter(myAdapter);
        myViewPager2.setCurrentItem(1);

    }
}