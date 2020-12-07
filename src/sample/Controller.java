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
import sample.game.*;

import java.util.*;

public class Controller {

    @FXML
    private int paneSize;
    public Pane pane;
    public ProgressBar progress;
    public Label time;
    public Font nowTime;
    public Label txt;
    private Button[][] buttons;
    private final Map<Button, Cell> bombs = new HashMap<>();
    private final Map<Button, Cell> cells = new HashMap<>();
    private final Map<Button, Cell> flags = new HashMap<>();
    private final List<Cell> nulls = new ArrayList<>();

    /*** Initialisations ***/
    // инициализация игры
    public void initGame(int size) {
        this.paneSize = size;
        buttons = new Button[paneSize][paneSize];
        initGridPane();
        initBombs();
        initValues();
    }

    // инициализация панели для кнопок
    public void initGridPane() {
        GridPane gp = new GridPane();
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
        Cell cell = new Cell(new Button(""));
        cell.setSize(50, 50);
        cell.setImage("closed");
        cell.setValue(Cell.UNDETECTED);
        cell.setBtn_id(i + "_" + j);
        cell.setCell_id(i + "_" + j);
        cell.setOnAction(this::onClick);
        cell.setOnMousePressed(this::onPressed);
        buttons[i][j] = cell.button();
        cells.put(cell.button(), cell);
        return cell.button();
    }
    
    private void

    // инициализация бомб
    public void initBombs() {
        Random r = new Random();
        for (int x = 0; x < countBomb(); x++) {
            int rx = r.nextInt(paneSize);
            int ry = r.nextInt(paneSize);
            if (!checkRepeatBombs(rx, ry))
                bombs.put(buttons[rx][ry], cells.get(buttons[rx][ry]));
            else x--;
        }
        System.out.println();
        System.out.println(buttons[0][0]);
    }

    // получение кол-ва бомб
    private int countBomb() {
        int answer = 25;
        if (paneSize == 15)
            answer = 40;
        return answer;
    }

    // проверка на повтор бомб
    private boolean checkRepeatBombs(int randX, int randY) {
        final boolean[] answer = {false};
        bombs.forEach((Button bomb, Cell cell) -> {
            if (buttons[randX][randY].equals(bomb))
                answer[0] = true;
        });
        return answer[0];
    }

    // установка "значений" кнопкам
    private void initValues() {
        for (int i = 0; i < paneSize; i++)
            for (int j = 0; j < paneSize; j++)
                if (checkBombInMap(buttons[i][j]))
                    cells.get(buttons[i][j]).setValue(Cell.BOMBED);
                else cells.get(buttons[i][j]).setValue(Cell.NUMBER);
    }
    
    /*** Backend game ***/

    // проверка на бомбу в карте для установки значений
    private boolean checkBombInMap(Button btn) {
        final boolean[] answer = {false};
        bombs.forEach((Button bomb, Cell cell) -> {
            if (btn.equals(bomb))
                answer[0] = true;
        });
        return answer[0];
    }

    // определение кол-ва бомб вокруг нажатой клетки (инициализация цифры)
    private int getCountAroundButton(Cell cell) {
        int count = 0;
        int x = Integer.parseInt(cell.getCell_id().split("_")[0]);
        int y = Integer.parseInt(cell.getCell_id().split("_")[1]);
        for (int i = -1; i < 2; i++)
            for (int j = -1; j < 2; j++)
                if (checkInsertArray(x + i, y + j))
                    if (cells.get(buttons[x + i][y + j]).getValue() == Cell.BOMBED)
                        count++;
        return count;
    }

    // проверка на вхождение в gridPane
    private boolean checkInsertArray(int x, int y) {
        return (x >= 0 && x < paneSize) && (y >= 0 && y < paneSize);
    }

    // открытие клетки
    private void openCell(Cell cell) {
        if (cell.value == Cell.BOMBED) gameOver(cell);
        else openNumber(cell);
    }

    // открытие клетки с номером
    private void openNumber(Cell cell) {
        if (getCountAroundButton(cell) == 0) openNullCells(cell);
        else
            new Cell(parseCell(cell)).setImage("num" + getCountAroundButton(cell));
    }

    private void openAround(Cell cell) {
        Cell c = new Cell(parseCell(cell));
        c.setImage("num" + getCountAroundButton(cell));
        c.setCell_id(parseCell(cell).getId());
        if (getCountAroundButton(c) == 0)
            openNullCells(c);
    }

    private void openNullCells(Cell cell) {
        int x = Integer.parseInt(cell.getCell_id().split("_")[0]);
        int y = Integer.parseInt(cell.getCell_id().split("_")[1]);
        if (getCountAroundButton(cell) == 0) {
            for (int i = -1; i < 2; i++)
                for (int j = -1; j < 2; j++)
                    if (checkInsertArray(x + i, y + j))
                        openAround(cells.get(buttons[x + i][y + j]));
        }
    }

    private Button parseCell(Cell cell) {
        int x = Integer.parseInt(cell.getCell_id().split("_")[0]);
        int y = Integer.parseInt(cell.getCell_id().split("_")[1]);
        return buttons[x][y];
    }

    // конец игры
    private void gameOver(Cell bomb) {
        new Cell(parseCell(bomb)).setImage("bomb");
    }

    /*** Actions ***/
    // нажатие на кнопку
    public void onClick(ActionEvent e) {
        Button btn = ((Button) e.getSource());
        fhsdjf();
        new Cell(parseCell(cells.get(btn))).disableCell();
        openCell(cells.get(btn));
    }

    private void fhsdjf() {
        cells.forEach((Button btn, Cell cell) -> System.out.println("btn = " + btn + "\nCell button = " + cell.button()));
    }

    // нажатие мышки на кнопку
    public void onPressed(MouseEvent e) {
        Button btn = (Button) e.getSource();
//        if (e.getButton() == MouseButton.SECONDARY) // если нажата правая кнопка мыши
//            System.out.println("");
    }
    
}