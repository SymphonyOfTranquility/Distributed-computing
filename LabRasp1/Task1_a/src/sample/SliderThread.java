package sample;

import javafx.scene.control.Slider;

import static java.lang.Integer.min;
import static java.lang.Integer.max;

public class SliderThread extends Thread{
    private Slider slider;
    private int value;
    private int step;

    SliderThread(Slider slider, int value, int step)
    {
        this.slider = slider;
        this.value = value;
        this.step = step;
    }

    @Override
    public void run()
    {
        while (!Thread.interrupted()){

            synchronized (slider) {

                int currentValue = (int) slider.getValue();
                if (currentValue <= 90 && value == 90)
                    slider.setValue(min(currentValue + step, 90));
                if (currentValue >= 10 && value == 10)
                    slider.setValue(max(currentValue + step, 10));
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}
