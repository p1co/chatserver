package ninja;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

import java.awt.*;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.ResourceBundle;


/**
 * @author Sergey Volkov
 *         Date: 11/22/13 Time: 2:52 AM
 */

public class NinjaController implements Initializable
{
    @FXML //  fx:id="userOutput"
    private TextArea userOutput = new TextArea();

    @FXML //  fx:id="userInput"
    private TextField userInput = new TextField();

    @FXML //  fx:id="sendButton"
    private Button sendButton;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @Override
    public void initialize( URL fxmlFileLocation, ResourceBundle resources )
    {
        this.resources = resources;
        this.location = fxmlFileLocation;

        sendButton.setOnAction( new EventHandler<ActionEvent>()
        {
            @Override
            public void handle( ActionEvent actionEvent )
            {
                //userOutput.append( "\ntest1" );
                String tempCopy = userInput.getText().trim();

                System.out.println( tempCopy );
            }
        } );

        userInput.addActionListener( new ActionListener()
        {
            @Override
            public void actionPerformed( java.awt.event.ActionEvent actionEvent )
            {
                String cmd = actionEvent.toString();
                System.out.println("CMD IN ECHO LISTENER = " + cmd);
            }
        });
    }
}