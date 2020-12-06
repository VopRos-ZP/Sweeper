package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import java.util.Random;

public class Controller {

    @FXML
    private int paneSize;
    private final int COLS = 50;
    public Pane pane;
    public ProgressBar progress;
    public Label time;
    public Font nowTime;
    public Label txt;
    private Button[][] buttons;
    private Button[] bombs;

    // инициализация окна и его размера
    public void initStage(int size) {
        this.paneSize = size;
        buttons = new Button[paneSize][paneSize];
        paintGridPane();
        initBombs();
    }

    // создание игрового поля
    private void paintGridPane() {
        GridPane gp = new GridPane();
        gp.setGridLinesVisible(false);
        gp.setVisible(true);
        gp.setLayoutX(20);
        gp.setLayoutY(130);
        for (int i = 0; i < paneSize; i++) {
            for (int j = 0; j < paneSize; j++) {
                gp.addColumn(i, initButton(i, j));
                gp.addRow(j);
            }
        }
        pane.getChildren().add(gp);
    }

    // инициализация кнопок
    private Button initButton(int i, int j) {
        Button btn = new Button();
        btn.setText("");
        btn.setId(i + "_" + j);
        btn.setGraphic(new ImageView("img/closed.png"));
        btn.setMinSize(COLS, COLS);
        btn.setPrefSize(COLS, COLS);
        btn.setMaxSize(COLS, COLS);
        btn.setOnAction(this::onClick);
        btn.setOnMousePressed(this::onMouseClick);
        buttons[i][j] = btn;
        return btn;
    }

    // инициализация бомб
    private void initBombs() {
        Random r = new Random();
        bombs = new Button[countBomb()];
        for (int x = 0; x < countBomb(); x++) {
            int rx = r.nextInt(paneSize);
            int ry = r.nextInt(paneSize);
            if (!checkCountBombs(rx, ry))
                bombs[x] = buttons[rx][ry];
            else x--;
        }
        setValueBombs();
    }

    // установка "значения" бомбам для их определения
    private void setValueBombs() {
        for (Button btn : bombs)
            btn.setId(btn.getId().split("_")[0] + "_" +
                      btn.getId().split("_")[1] + "/bomb");
    }

    // проверка на повтор координат одной бомбы
    private boolean checkCountBombs(int randX, int randY) {
        for (Button bomb : bombs)
            if (buttons[randX][randY].equals(bomb))
                return true;
        return false;
    }

    // получение кол-ва бомб
    private int countBomb() {
        int answer = 25;
        if (paneSize == 15)
            answer = 40;
        return answer;
    }

    // проверка "значений" для показа игроку
    private void checkValue(Button btn) {
        String value = btn.getId();
        try {
            value = value.split("/")[1];
        } catch (IndexOutOfBoundsException index) {
            soutNum(btn);
        }
        switch (value) {
            case "bomb":
                soutBombs();
                break;
            case "nobomb":
                soutNum(btn);
                break;
        }
    }

    // показ бомб (если нажал на бомбу)
    private void soutBombs() {
        for (Button bomb : bombs) {
            bomb.setGraphic(new ImageView("img/bomb.png"));
            bomb.setDisable(true);
            bomb.setStyle("-fx-background-color: #ffffff");
            bomb.setOpacity(1);
        }
        for (Button[] btns : buttons)
            for (Button btn : btns) {
                btn.setDisable(true);
                btn.setStyle("-fx-background-color: #ffffff");
                btn.setOpacity(1);
                checkValueGameOver(btn);
            }
    }

    // проверка "значений" при проигрыше (показ неправильного определения флажков)
    private void checkValueGameOver(Button btn) {
        try {
            String value = btn.getId().split("/")[1];
            if (value.equals("nobomb"))
                btn.setGraphic(new ImageView("img/nobomb.png"));
        } catch (Exception ignore) {}
    }

    // показ ячеек с номерами
    private void soutNum(Button btn) {
        btn.setGraphic(new ImageView("img/num" + getCountAroundButton(btn) + ".png"));
    }

    // определение кол-ва бомб вокруг нажатой клетки (инициализация цифры)
    private int getCountAroundButton(Button btn) {
        int count = 0;
        int x = parseForInt(btn, 0);
        int y = parseForInt(btn, 1);
        for (int i = -1; i < 2; i++)
            for (int j = -1; j < 2; j++)
                if (checkInsertArray(x + i, y + j))
                    try {
                        String value = buttons[x + i][y + j].getId().split("/")[1];
                        if (value.equals("bomb"))
                            count++;
                    } catch (IndexOutOfBoundsException ignore) {}
        return count;
    }

    // определение X и Y если ставил флаг (лучше не трогать!)
    private int parseForInt(Button btn, int index) {
        int id = 0;
        try {
            id = Integer.parseInt(btn.getId().split("_")[index]);
        } catch (Exception ignore) {
            try {
                id = Integer.parseInt(btn.getId().split("_")[index].split("/")[0]);
            } catch (Exception ignored) {}
        }
        return id;
    }

    // проверка на вхождение в gridPane
    private boolean checkInsertArray(int x, int y) {
        return (x >= 0 && x < paneSize) && (y >= 0 && y < paneSize);
    }

    /*** game started ***/

    // действие при нажатии
    private void onClick(ActionEvent e) {
        Button btn = (Button) e.getSource();
        btn.setOnMouseClicked(this::onMouseClick);
        checkValue(btn);
        btn.setDisable(true);
        btn.setStyle("-fx-background-color: #ffffff");
        btn.setOpacity(1);
    }

    // установка/удаление флажков
    private void onMouseClick(MouseEvent e) {
        Button btn = (Button) e.getSource();
        if (e.getButton() == MouseButton.SECONDARY) // если нажата правая кнопка мыши
            try {
                String value = btn.getId().split("/")[1];
                if (e.getButton() == MouseButton.SECONDARY && value.equals("flag"))
                    removeFlag(btn);
                else if (e.getButton() == MouseButton.SECONDARY)
                    setFlag(btn);
            } catch (Exception ignore){
                setFlag(btn);
            }
    }

    // установка флажка
    private void setFlag(Button bomb) {
        try {
            bomb.setId(bomb.getId().split("/")[0] + "/flag");
        } catch (Exception ignore){
            bomb.setId(bomb.getId() + "/flag");
        }
        bomb.setGraphic(new ImageView("img/flaged.png"));
    }

    // удаление флажка
    private void removeFlag(Button btn) {
        if (checkBomb(btn))
            btn.setId(btn.getId().split("/")[0] + "/bomb");
        else {
            btn.setId(btn.getId().split("/")[0]);
        }
        btn.setGraphic(new ImageView("img/closed.png"));
    }

    // проверка на бомбу
    private boolean checkBomb(Button btn) {
        for (Button bomb : bombs)
            if (btn.equals(bomb))
                    return true;
        return false;
    }

}