package sample;

import javafx.scene.control.Slider;

public class SliderThread extends Thread{
    private Slider slider;
    private int value;

    SliderThread(Slider slider, int value)
    {
        this.slider = slider;
        this.value = value;
    }

    @Override
    public void run()
    {
        while (!Thread.interrupted())
            slider.setValue(value);
    }
}
