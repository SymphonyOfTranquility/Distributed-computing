package sample;

import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;


public class MyRectangles {
    private Rectangle rectangle;
    private int curColor;
    private static Color[] colors = {Color.web("0x2F4F4F"), Color.CORAL, Color.YELLOW, Color.DODGERBLUE, Color.VIOLET};
    MyRectangles(int size){
        rectangle = new Rectangle(size, size);
        rectangle.setStrokeType(StrokeType.INSIDE);
        rectangle.setStroke(Color.BLACK);
        rectangle.setFill(colors[0]);
        rectangle.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY)
                changeColor();
            if(event.getButton() == MouseButton.SECONDARY)
                reverseChangeColor();
        });
        curColor = 0;
    }

    private void reverseChangeColor() {
        curColor = (curColor + 4)%5;
        rectangle.setFill(colors[curColor]);
    }

    private void changeColor(){
        curColor = (curColor + 1)%5;
        rectangle.setFill(colors[curColor]);
    }

    Rectangle getRectangle(){
        return rectangle;
    }

    int getColor(){
        return curColor;
    }

    void setColor(int curColor){
        if (curColor != this.curColor) {
            this.curColor = curColor;
            rectangle.setFill(colors[curColor]);
        }
    }
}
