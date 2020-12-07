package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloController {
    @FXML
    public Button btn_size_10;
    public Button btn_size_15;
    public Label lbl1;
    public Label lbl2;
    public Pane Stage;

    private void openStage(int paneSize) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../Views/sample.fxml"));
            Parent root = loader.load();
            Controller controller = loader.getController();
            controller.initGame(paneSize);
            Stage stage = new Stage();
            stage.setTitle("Сапёр");
            stage.setResizable(false);
            int COLS = 50;
            stage.setScene(new Scene(root, (paneSize * COLS + 40), ((paneSize * COLS + 40) + 130)));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onClick_15(ActionEvent event) {
        blockedLabelsAndButtons(event, 15);
    }

    public void onClick_10(ActionEvent event) {
        blockedLabelsAndButtons(event, 10);
    }

    private void blockedLabelsAndButtons(ActionEvent event, int size) {
        openStage(size);
        final Node source = (Node) event.getSource();
        final Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

}
