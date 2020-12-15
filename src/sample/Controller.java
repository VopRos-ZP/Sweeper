package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import sample.game.Cell;
import java.util.*;

public class Controller {

    @FXML
    private int paneSize;
    public Pane pane;
    public ProgressBar progress;
    public Label time;
    public Label txt;
    public GridPane gridPane;
    private Button[][] buttons;
    private final Map<Button, Cell> bombs = new HashMap<>();
    private final Map<Button, Cell> cells = new HashMap<>();
    private final Map<Button, Cell> flags = new HashMap<>();
    private int countBombs;
    private Cell currentCell;
    private int x;
    private int y;
    private int curBomb = 0;
    private int hour = 0;
    private int minute = 0;
    private int second = 0;
    private final ContextMenu contextMenu = new ContextMenu(new MenuItem(curBomb +  "/" + countBombs));

    /*** Initialisations ***/
    // инициализация игры
    public void initGame(int size) {
        this.paneSize = size;
        buttons = new Button[paneSize][paneSize];
        initGridPane();
        countBombs = countBomb();
        initBombs();
        initValues();
        initProgressBar();
    }

    // инициализация панели для кнопок
    public void initGridPane() {
        GridPane gp = new GridPane();
        gridPane = gp;
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
        Button btn = new Button("");
        btn.setId(i + "_" + j);
        btn.setGraphic(new ImageView("img/closed.png"));
        buttons[i][j] = btn;
        initCell(btn);
        return btn;
    }

    // инициализация клеток (Cells)
    private void initCell(Button btn) {
        Cell cell = new Cell(btn);
        cell.setButton(btn);
        cell.setSize(50, 50);
        cell.setImage("closed");
        cell.setValue(Cell.NUMBER);
        cell.setCell_id(btn.getId());
        cell.setOnAction(this::onClick);
        cell.setOnMousePressed(this::onPressed);
        cells.put(btn, cell);
    }

    // инициализация бомб
    public void initBombs() {
        Random r = new Random();
        for (int x = 0; x < countBombs; x++) {
            int rx = r.nextInt(paneSize);
            int ry = r.nextInt(paneSize);
            if (!checkRepeatBombs(rx, ry))
                bombs.put(buttons[rx][ry], cells.get(buttons[rx][ry]));
            else x--;
        }
    }

    // инициализация прогресс бара
    private void initProgressBar() {
        progress.setProgress(0);
        progress.setContextMenu(contextMenu);
        progress.setPrefSize(paneSize * 50, 20);
    }

    // инициализация времени
    private void initTime() {
        time.setText(minute + ":" + second + "");
    }

    // получение кол-ва бомб
    private int countBomb() {
        int answer = 15;
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
                if (checkBombInMap(buttons[i][j])) {
                    cells.get(buttons[i][j]).setValue(Cell.BOMBED);
                    cells.get(buttons[i][j]).setType("bomb");
                }
                else {
                    cells.get(buttons[i][j]).setValue(Cell.NUMBER);
                    cells.get(buttons[i][j]).setType("no_bomb");
                }
    }
    
    /*** Backend ***/
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
                    if (cells.get(buttons[x + i][y + j]).getType().equals("bomb"))
                        count++;
        return count;
    }

    // проверка на вхождение в gridPane
    private boolean checkInsertArray(int x, int y) {
        return (x >= 0 && x < paneSize) && (y >= 0 && y < paneSize);
    }

    // определение открытия клетки
    private void checkOpenCell() {

    }

    // инициализация x и y
    private void parseXAndY(Cell cell) {
        int x = Integer.parseInt(cell.getCell_id().split("_")[0]);
        int y = Integer.parseInt(cell.getCell_id().split("_")[1]);
    }

    // удаление флага
    private void removeFlag() {
        currentCell.setValue(Cell.NUMBER);
        currentCell.setImage("closed");
        flags.remove(currentCell.button(), currentCell);
        curBomb--;
        if (progress.getProgress() <= 0.0)
            progress.setProgress(0.0);
        else
            progress.setProgress(progress.getProgress() - ((double) 1 / countBombs));
    }

    // установка флага
    private void setFlag() {
        currentCell.setValue(Cell.FLAGGED);
        currentCell.setImage("flagged");
        flags.put(currentCell.button(), currentCell);
        curBomb++;
        progress.setProgress(progress.getProgress() + ((double) 1 / countBombs));
        if (progress.getProgress() >= 1)
            if (checkTypesForWin())
                gameWin();
    }

    // проверка на правильность расположения флагов
    private boolean checkTypesForWin() {
        boolean answer = false;
        boolean[] arrAnswer = new boolean[flags.size()];
        int i = 0;
        for (Cell cell : flags.values()) {
            arrAnswer[i] = cell.getType().equals("bomb");
            i++;
        }
        for (boolean ans : arrAnswer)
            if (ans)
                answer = true;
            else return false;
        return answer;
    }

    // поздравления победителя конец игры
    private void gameWin() {
        flags.forEach((Button btn, Cell cell) -> cell.setImage("flagged"));
        cells.forEach((Button btn, Cell cell) -> cell.disableCell());
    }

    // конец игры
    private void gameOver() {
        bombs.forEach((Button btn, Cell cell) -> cell.setImage("bomb"));
        cells.forEach((Button btn, Cell cell) -> cell.disableCell());
    }

    /*** Actions ***/
    // нажатие на кнопку
    public void onClick(ActionEvent e) {
        currentCell = cells.get((Button) e.getSource());
        if (!currentCell.getValue().equals(Cell.FLAGGED))
            checkOpenCell();
    }

    // нажатие мышки на кнопку //
    public void onPressed(MouseEvent e) {
        currentCell = cells.get(e.getSource());
        if (e.getButton() == MouseButton.SECONDARY) // если нажата правая кнопка мыши
            if (!currentCell.getValue().equals(Cell.FLAGGED))
                setFlag();
            else removeFlag();
    }
}