package com.ltgd.vsliderexample;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.ltgd.vslider.Slider;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Slider slider = (Slider) findViewById(R.id.slider);
        slider.setOnSliderChangeListener(sliderChangeListener);

        Slider slider2 = (Slider) findViewById(R.id.slider2);
        slider2.setOnSliderChangeListener(sliderChangeListener);

    }

    Slider.OnSliderChangeListener sliderChangeListener = new Slider.OnSliderChangeListener() {
        @Override
        public void onProgressChanged(Slider slider, int progress, boolean fromUser) {
            Log.d(TAG, "onProgressChanged: " + progress + " fromUser: " + fromUser);
        }

        @Override
        public void onStartTrackingTouch(Slider slider) {
            Log.d(TAG, "onStartTrackingTouch: ");
        }

        @Override
        public void onStopTrackingTouch(Slider slider) {
            Log.d(TAG, "onStopTrackingTouch: ");
        }
    };
}