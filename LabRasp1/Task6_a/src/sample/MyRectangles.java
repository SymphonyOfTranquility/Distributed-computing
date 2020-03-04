package sample;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;


public class MyRectangles {
    private Rectangle rectangle;
    private boolean isChanged;
    MyRectangles(int size){
        rectangle = new Rectangle(size, size);
        rectangle.setStrokeType(StrokeType.INSIDE);
        rectangle.setStroke(Color.BLACK);
        rectangle.setFill(Color.web("0x2F4F4F"));
        rectangle.setOnMouseClicked(event -> {
            changeColor();
        });
        isChanged = false;
    }

    private void changeColor(){
        if (isChanged) {
            rectangle.setFill(Color.web("0x2F4F4F"));
            isChanged = false;
        }
        else {
            isChanged = true;
            rectangle.setFill(Color.LIGHTGRAY);
        }
    }

    Rectangle getRectangle(){
        return rectangle;
    }

    boolean getChanged(){
        return isChanged;
    }

    void setChanged(boolean isChanged){
        if (isChanged != this.isChanged)
            changeColor();
    }
}
