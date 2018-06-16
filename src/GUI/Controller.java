package GUI;

import Model.Engine;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

import java.net.MalformedURLException;
import java.net.URL;

public class Controller {
    ObservableList<String> observableList = FXCollections.observableArrayList("Danish", "English", "German", "Italian");

    @FXML
    private AnchorPane root;

    @FXML
    private TextField url;

    @FXML
    private TextField username;

    @FXML
    public Text statusTxtField;

    @FXML
    private PasswordField password;

    @FXML
    private Button goBtn;

    @FXML
    ChoiceBox<String> languageSelector;

    @FXML
    private Button quitBtn;

    @FXML
    void quitBtnWasPressed(ActionEvent event){
        Platform.exit();
    }

    @FXML
    void showQuitBtn(){
        Image image = new Image("quit.png");
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(20);
        imageView.setFitWidth(20);
        quitBtn.setGraphic(imageView);

    }

    @FXML
    void hideQuitBtn(){
        Image image = new Image("quit.png");
        ImageView imageView = new ImageView(image);
        imageView.setImage(null);
        quitBtn.setGraphic(imageView);

    }

    @FXML
    void goBtnWasPressed(ActionEvent event) {
        Engine engine = new Engine();
        int objID = 0;

        String selectedOption = languageSelector.getValue();
        String user = username.getText();
        String pass = password.getText();

        try {
            URL theUrl = new URL(url.getText());

            String baseURL = theUrl.getProtocol() + "://" + theUrl.getHost();

            // Searching input url for repository name and destination object id
            String path = theUrl.getPath();
            String[] strings = path.split("/");
            String repository = strings[2];

            for (int i = 0; i < strings.length; i++) {
                if (strings[i].equals("obj")){
                    objID = Integer.parseInt(strings[i+1]);
                }
            }

            engine.startDownloadAndSendContentToAPI(this, selectedOption, baseURL, repository, objID, user, pass);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void initialize() {
        languageSelector.setItems(observableList);
        languageSelector.getSelectionModel().selectFirst();
        goBtn.setOnAction(event -> goBtnWasPressed(event));
        quitBtn.isCancelButton();
    }
}
