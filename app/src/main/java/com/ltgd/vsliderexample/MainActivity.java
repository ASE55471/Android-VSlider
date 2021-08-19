package com.ltgd.vsliderexample;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.ltgd.vslider.Slider;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    Slider horizontalSlider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        horizontalSlider = (Slider) findViewById(R.id.slider);
        horizontalSlider.setOnSliderChangeListener(horizontalSliderChangeListener);
        horizontalSlider.setOnClickListener(onClickListener);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(new CustomAdapter(new int[]{0, 10, 20, 30, 40, 50, 60, 70, 80, 90
                , 100, 90, 70, 50, 30, 10}));

        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                horizontalSlider.setProgress(30);
            }
        });

    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Toast.makeText(MainActivity.this, "onClick", Toast.LENGTH_SHORT).show();
        }
    };

    Slider.OnSliderChangeListener horizontalSliderChangeListener = new Slider.OnSliderChangeListener() {
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

        private int[] localDataSet;

        public class ViewHolder extends RecyclerView.ViewHolder {
            private final Slider slider;

            public ViewHolder(View view) {
                super(view);

                slider = (Slider) view.findViewById(R.id.slider50);
                slider.setOnClickListener(onClickListener);
            }

            public Slider getSlider() {
                return slider;
            }
        }

        public CustomAdapter(int[] dataSet) {
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
            Log.d(TAG, "onBindViewHolder: " + localDataSet[position] + " position: " + position);
            viewHolder.getSlider().setProgress(localDataSet[position]);
            viewHolder.getSlider().setOnSliderChangeListener(new Slider.OnSliderChangeListener() {
                @Override
                public void onProgressChanged(Slider slider, int progress, boolean fromUser) {
                    Log.d(TAG, "onProgressChanged: " + progress + " position: "
                            + position + " fromUser: " + fromUser);
                    if (fromUser)
                        localDataSet[position] = progress;
                }

                @Override
                public void onStartTrackingTouch(Slider slider) {
                    //Didn't find a proper way to re allow InterceptTouchEvent
                    slider.getParent().requestDisallowInterceptTouchEvent(true);
                }

                @Override
                public void onStopTrackingTouch(Slider slider) {

                }
            });
        }

        @Override
        public int getItemCount() {
            return localDataSet.length;
        }

    }

}