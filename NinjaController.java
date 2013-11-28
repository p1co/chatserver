import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

import java.net.URL;
import java.util.ResourceBundle;

public abstract class NinjaController implements Initializable
{
    @FXML //  fx:id="userOutput"
    protected static TextArea userOutput = new TextArea();

    @FXML //  fx:id="userInput"
    protected static TextField userInput = new TextField();

    @FXML
    protected BorderPane ninjaborderpane;
    protected AnchorPane ninjaanchor;

    @FXML //  fx:id="sendButton"
    protected Button sendButton;

    @FXML
    protected ResourceBundle resources;

    @FXML
    protected URL location;

    public void onEnter()
    {
        System.out.println( "user hit enter while mouse was focused in the textfield with this message: " + userInput.getText() );
    }
}