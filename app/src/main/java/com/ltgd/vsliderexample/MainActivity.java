package com.ltgd.vsliderexample;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ltgd.vslider.Slider;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Slider slider = (Slider) findViewById(R.id.slider);
        slider.setOnSliderChangeListener(sliderChangeListener);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false));
        recyclerView.setAdapter(new CustomAdapter(new String[]{"test", "test", "test", "test", "test", "test"}));
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


    public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

        private String[] localDataSet;

        public class ViewHolder extends RecyclerView.ViewHolder {
            private final Slider slider;

            public ViewHolder(View view) {
                super(view);

                slider = (Slider) view.findViewById(R.id.slider50);
                slider.setOnSliderChangeListener(sliderChangeListener);
            }

            public Slider getSlider() {
                return slider;
            }
        }

        public CustomAdapter(String[] dataSet) {
            localDataSet = dataSet;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.recycleview_item, viewGroup, false);

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        }

        @Override
        public int getItemCount() {
            return localDataSet.length;
        }
    }

}