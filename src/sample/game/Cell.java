package sample.game;

import com.sun.istack.internal.NotNull;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class Cell {

    @NotNull
    public int value;
    private String cell_id;
    private String btn_id;
    private static int size_width;
    private static int size_height;
    private String image;
    private static Button btn = new Button("");

    public Cell (Button button) {
        btn = button;
    }

    /*** Methods ***/
    public void setValue(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setCell_id(String id) {
        this.cell_id = id;
    }

    public String getCell_id() {
        return cell_id;
    }

    public void setBtn_id(String id) {
        this.btn_id = id;
        btn.setId(id);
    }

    public String getBtn_id() {
        return btn_id;
    }

    public void setSize(int width, int height) {
        btn.setMinSize(width, height);
        btn.setPrefSize(width, height);
        btn.setMaxSize(width, height);
        this.size_width = width;
        this.size_height = height;
    }

    public void setImage(String imageName) {
        this.image = imageName;
        btn.setGraphic(new ImageView("img/" + imageName + ".png"));
    }

    public void setOnAction(EventHandler<ActionEvent> action) {
        btn.setOnAction(action);
    }

    public  void setOnMousePressed(EventHandler<MouseEvent> action) {
        btn.setOnMousePressed(action);
    }

    public void disableCell() {
        btn.setDisable(true);
        btn.setStyle("-fx-background-color: #ffffff");
        btn.setOpacity(1);
    }

    public Button button() {
        return btn;
    }

    /*** Types Cell ***/
    public static int UNDETECTED = 0;
    public static int NO_BOMB = 1;
    public static int BOMBED = 2;
    public static int FLAGGED = 3;
    public static int NUMBER = 4;
}
