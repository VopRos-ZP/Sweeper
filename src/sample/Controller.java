package sample;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import sample.game.Cell;
import java.util.*;

public class Controller {
	
    private int paneSize;
    private Stage currentStage;
    @FXML
    public Pane pane;
    @FXML
    public ProgressBar progress;
    @FXML
    public Label time;
    public GridPane gridPane;
    private Button[][] buttons;
    private final Map<Button, Cell> bombs = new HashMap<>();
    private final Map<Button, Cell> cells = new HashMap<>();
    private final Map<Button, Cell> flags = new HashMap<>();
    private final Timer timer = new Timer();
    private int countBombs;
    private Cell currentCell;
    private int minute = 0;
    private int second = 0;
    private boolean isFirst = true;

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
        initTime();
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
        progress.setPrefSize(paneSize * 50, 20);
    }

    // инициализация времени
    private void initTime() {
        if (minute == 0 && second == 0)
            time.setText("00:00");
        else time.setText(minute + ":" + second);
    }

    // получение кол-ва бомб
    private int countBomb() {
        int answer = 18;
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
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                if (checkInsertArray(x + i, y + j)) {
                    if (cells.get(buttons[x + i][y + j]).getType().equals("bomb")) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    // проверка на вхождение в gridPane
    private boolean checkInsertArray(int x, int y) {
        return (x >= 0 && x < paneSize) && (y >= 0 && y < paneSize);
    }

    // определение открытия клетки
    private void checkOpenCell(Cell cell) {
        if (cell.getValue().equals(Cell.BOMBED)) endGame(false);
        else {
            if (getCountAroundButton(cell) == 0)
                recNull(cell);
            cell.setImage("num" + getCountAroundButton(cell));
        }
    }

    // открытие пустых клеток
    private void recNull(Cell cell) {
    	cell.setClick();
        int x = Integer.parseInt(cell.getCell_id().split("_")[0]);
        int y = Integer.parseInt(cell.getCell_id().split("_")[1]);
        for (int i = -1; i < 2; i++)
            for (int j = -1; j < 2; j++)
                if (checkInsertArray(x + i, y + j)) {
                    cells.get(buttons[x + i][y + j]).setImage("num" + getCountAroundButton(cells.get(buttons[x + i][y + j])));
                    cells.get(buttons[x + i][y + j]).disableCell();
                }
		for (int i = -1; i < 2; i++)
			for (int j = -1; j < 2; j++)
				if (checkInsertArray(x + i, y + j))
					if (!cells.get(buttons[x + i][y + j]).click())
						if (getCountAroundButton(cells.get(buttons[x + i][y + j])) == 0)
							recNull(cells.get(buttons[x + i][y + j]));
    }

    // показ времени
    private void time() {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                second++;
                if (second == 60) {
                    second = 0;
                    minute++;
                }
                Platform.runLater(() -> {
                    if (minute < 10) {
                        if (second < 10)
                            time.setText("0" + minute + ":0" + second);
                        else time.setText("0" + minute + ":" + second);
                    }
                    else {
                        if (second < 10)
                            time.setText(minute + ":0" + second);
                        else time.setText(minute + ":" + second);
                    }
                });
            }
        }, new Date(), 1000);
    }

    // удаление флага
    private void removeFlag() {
        currentCell.setValue(Cell.NUMBER);
        currentCell.setImage("closed");
        flags.remove(currentCell.button(), currentCell);
        if (progress.getProgress() <= 0)
            progress.setProgress(0.0);
        else
            progress.setProgress(progress.getProgress() - ((double) 1 / countBombs));
    }

    // установка флага
    private void setFlag() {
        currentCell.setValue(Cell.FLAGGED);
        currentCell.setImage("flagged");
        flags.put(currentCell.button(), currentCell);
        progress.setProgress(progress.getProgress() + ((double) 1 / countBombs));
        if (progress.getProgress() >= 1)
            if (checkTypesForWin())
                endGame(true);
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

    // определение победы или поражение + disable кнопок
    private void endGame(boolean isWon) {
        timer.cancel();
        bombs.forEach((Button btn, Cell bomb) -> bomb.setImage("bomb"));
        flags.forEach((Button b, Cell flag) -> {
            if (flag.getType().equals("bomb")) flag.setImage("flagged");
            else flag.setImage("nobomb");
        });
        cells.forEach((Button btn, Cell cell) -> cell.disableCell());
        if (isWon)
            game("Победа!");
        else game("Поражение!");
    }

    // поздравления победителя конец игры
    private void game(String status) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Конец игры");
        alert.setHeaderText(status);
        alert.setContentText("Ваше время игры: " + time.getText());
        ButtonType newGame = new ButtonType("новая игра");
        ButtonType exit = new ButtonType("выход");
        alert.getButtonTypes().setAll(newGame, exit);
        Optional<ButtonType> result = alert.showAndWait();
        alert.show();
        if (result.get() == newGame)
            returnToWelcomWindow(alert);
        else if (result.get() == exit)
            System.exit(0);
        currentStage.close();
    }

    // возвращение к главному экрану
    private void returnToWelcomWindow(Alert alert) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("../Views/HelloWindow.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();
        } catch (Exception ignored) {}
        alert.close();
    }

    /*** Actions ***/
    // нажатие на кнопку
    public void onClick(ActionEvent e) {
        final Node source = (Node) e.getSource();
        currentStage = (Stage) source.getScene().getWindow();
        currentCell = cells.get((Button) e.getSource());
        currentCell.setClick();
        if (isFirst) { isFirst = false; time(); } // проверка на первое нажатие
        if (!currentCell.getValue().equals(Cell.FLAGGED)) {
            currentCell.disableCell();
            checkOpenCell(currentCell);
        }
    }

    // нажатие мышки на кнопку
    public void onPressed(MouseEvent e) {
        currentCell = cells.get((Button) e.getSource());
        currentCell.setClick();
        if (e.getButton() == MouseButton.SECONDARY) // если нажата правая кнопка мыши
            if (!currentCell.getValue().equals(Cell.FLAGGED))
                setFlag();
            else removeFlag();
    }
}