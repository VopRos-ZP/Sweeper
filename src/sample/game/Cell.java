package sample.game;

import com.sun.istack.internal.NotNull;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class Cell {

    @NotNull
    private String value;
    private String type;
    private String cell_id;
    private String btn_id;
    private Button btn;

    public Cell (Button button) {
        btn = button;
    }

    /*** Methods ***/
    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
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
    }

    public void setImage(String imageName) {
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

    public void setButton(Button btn) {
        this.btn = btn;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Cell) {
            Cell c = (Cell) o;
            return this.getCell_id().equals(c.getCell_id());
        }
        return super.equals(o);
    }

    /*** Types Cell ***/
    public static String ZERO = "num0";
    public static String BOMBED = "bomb";
    public static String FLAGGED = "flagged";
    public static String NUMBER = "num";
}