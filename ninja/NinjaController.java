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
import java.util.prefs.Preferences;


/**
 * @author Sergey
 *         Date: 11/22/13 Time: 2:52 AM
 */

public class NinjaController implements Initializable
{
    @FXML //  fx:id="userOutput"
    public TextArea  userOutput = new TextArea();

    @FXML //  fx:id="userInput"
    public TextField userInput = new TextField();
    //userInput.addActionListener( new Listener() );

    @FXML //  fx:id="sendButton"
    public Button    sendButton;

    @FXML
    public ResourceBundle resources;

    @FXML
    private URL location;

    @Override
    public void initialize( URL fxmlFileLocation, ResourceBundle resources )
    {
        userInput.setText( "This is a text" );
        this.resources = resources;
        this.location = fxmlFileLocation;

        System.out.println( resources );

        Preferences prefs = Preferences.userRoot().node(this.getClass().getName());
        userInput.setText(prefs.get("userInput", userInput.getText()));

        //String tryMe = userInput.getText().trim();
        //System.out.println( tryMe );

        sendButton.setOnAction( new EventHandler<ActionEvent>()
        {
            @Override
            public void handle( ActionEvent actionEvent )
            {
                userOutput.append( String.valueOf( actionEvent ) );

                System.out.println( "user pressed the send button: " + actionEvent );
            }
        } );

        // this isnt executed when i do anything in the textfield, why?
        userInput.addActionListener( new ActionListener()
        {
            @Override
            public void actionPerformed( java.awt.event.ActionEvent actionEvent )
            {
                // String cmd = actionEvent.getActionCommand();
                System.out.println("CMD IN ECHO LISTENER = " + actionEvent);
            }
        });
    }

    public void onEnter()
    {
        System.out.println( "user hit enter while mouse was focused in the textfield" );

        // this line is to try to append the textarea with the text from the textfield *does work*
        userOutput.append( userInput.getText().trim() );
    }
}