package sample;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;


public class EventHanglerForRectangles implements EventHandler<MouseEvent> {
    private Rectangle rectangle;
    private boolean isChanged;
    EventHanglerForRectangles(Rectangle rectangle){
        this.rectangle = rectangle;
        isChanged = false;
    }
    @Override
    public void handle(MouseEvent mouseEvent) {
        if (isChanged) {
            rectangle.setFill(Color.web("0x2F4F4F"));
            isChanged = false;
        }
        else {
            isChanged = true;
            rectangle.setFill(Color.LIGHTGRAY);
        }
    }
}
