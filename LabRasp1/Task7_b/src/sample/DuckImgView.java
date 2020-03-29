package sample;


import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Random;

public class DuckImgView extends ImageView {
    int id;
    Random random = new Random();

    public DuckImgView(int id){
        super();
        this.id = id;
    }

    public void setRandomStart() {
        boolean temp = (random.nextInt(2) == 0);
        if (temp) {
            setX(-random.nextInt(50) - 70);
            setY(50.0 + random.nextDouble()*300.0);
            setImage(new Image("sample/img/redRight.gif"));
        }
        else {
            setX(870 + random.nextInt(50));
            setY(50.0 + random.nextDouble()*300.0);
            setImage(new Image("sample/img/redLeft.gif"));
        }
    }
}
