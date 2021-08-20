package com.ltgd.vslider;

/*
    Create for listening every slider OnSliderChangeListener
*/
public class GlobalSliderListener {

    private static GlobalSliderListener instance = null;

    public static GlobalSliderListener getInstance() {
        if (instance == null) {
            instance = new GlobalSliderListener();
        }
        return (instance);
    }

    Slider.OnSliderChangeListener onSliderChangeListener;

    private GlobalSliderListener() {}

    public void setOnSliderChangeListener(Slider.OnSliderChangeListener onSliderChangeListener) {
        this.onSliderChangeListener = onSliderChangeListener;
    }

}
