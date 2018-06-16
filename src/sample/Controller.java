package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class Controller {

    ObservableList<String> options = FXCollections.observableArrayList("Danish", "English", "German", "Italian");

    @FXML
    private void initialize(){
        languaugeSelector.setItems(options);
        languaugeSelector.getSelectionModel().selectFirst();
    }


    @FXML
    private TextField url;

    @FXML
    private TextField repository;

    @FXML
    private TextField objectID;

    @FXML
    private TextField username;

    @FXML
    private PasswordField password;

    @FXML
    private Button goBtn;

    @FXML
    private ChoiceBox<String> languaugeSelector;

    @FXML
    void goBtnWasPressed(ActionEvent event) {

    }



}
