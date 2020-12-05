package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;
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
    private Button[] flags;

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

    private Button initButton(int i, int j) {
        Button btn = new Button();
        btn.setText("");
        btn.setId(i + "_" + j);
        btn.setGraphic(new ImageView("img/closed.png"));
        btn.setMinSize(COLS, COLS);
        btn.setPrefSize(COLS, COLS);
        btn.setMaxSize(COLS, COLS);
        btn.setOnAction(this::onClick);
        buttons[i][j] = btn;
        return btn;
    }

    private void initBombs() {
        Random r = new Random();
        bombs = new Button[countBomb()];
        for (int x = 0; x < countBomb(); x++)
            bombs[x] = buttons[r.nextInt(paneSize)][r.nextInt(paneSize)];
        setValueBombs();
    }

    private void setValueBombs() {
        for (Button btn : bombs)
            btn.setId(btn.getId().split("_")[0] + "_" +
                      btn.getId().split("_")[1] + "/bomb");
    }

    private int countBomb() {
        int answer = 25;
        if (paneSize == 15)
            answer = 40;
        return answer;
    }

    private boolean checkInArray(int rx, int ry) {
        for (Button btn : bombs)
            if (buttons[rx][ry].equals(btn))
                return false;
        return true;
    }

    private void viewImagesForClick(Button btn) {
        //
    }

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
            case "flag":
                //
                break;
        }
    }

    private void soutBombs() {
        for (Button bomb : bombs) {
            bomb.setGraphic(new ImageView("img/bomb.png"));
        }
    }

    private void soutNum(Button btn) {
        btn.setGraphic(new ImageView("img/num" + getCountAroundButton(btn) + ".png"));
    }

    private int getCountAroundButton(Button btn) {
        int count = 0;
        int x = Integer.parseInt(btn.getId().split("_")[0]);
        int y = Integer.parseInt(btn.getId().split("_")[1]);
        for (int i = -1; i < 2; i++)
            for (int j = -1; j < 2; j++)
                if (checkInsertArray(x + i, y + j)) {
                    System.out.println(buttons[x + i][y + j].getId());
                    try {
                        String value = buttons[x + i][y + j].getId().split("/")[1];
                        if (value.equals("bomb"))
                            count++;
                    } catch (IndexOutOfBoundsException ignore) {}
                }
        System.out.println();
        return count;
    }

    private boolean checkInsertArray(int x, int y) {
        return (x >= 0 && x < paneSize) && (y >= 0 && y < paneSize);
    }

    /*** game started ***/

    private void onClick(ActionEvent e) {
        Button btn = (Button) e.getSource();
//        System.out.println(btn.getId());
        checkValue(btn);

    }
}