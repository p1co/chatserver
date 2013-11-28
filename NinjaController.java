import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class NinjaController implements Initializable
{
    @FXML //  fx:id="userOutput"
    static protected TextArea  userOutput = null;

    @FXML //  fx:id="userInput"
    static protected TextField userInput  = null;

    @FXML //  fx:id="sendButton"
    static protected Button    sendButton = null;

    @FXML
    ResourceBundle resources = null;

    @FXML
    URL location = null;

    @Override
    public void initialize( URL fxmlFileLocation, ResourceBundle resources )
    {
        this.resources = resources;
        this.location  = fxmlFileLocation;

        System.out.println( "about to show the resources var: "        + resources );
        System.out.println( "about to show the fxmlFileLocation var: " + fxmlFileLocation );

        userInput.setOnAction( new EventHandler<ActionEvent>()
        {
            @Override
            public void handle( javafx.event.ActionEvent actionEvent )
            {
                Client.sendMessageToServer( constructMessageFromFXMLuserInput() );
                userInput.setText( "" );

                System.out.println( "send via enter key" );

            }
        });

        sendButton.setOnAction( new EventHandler<javafx.event.ActionEvent>()
        {
            @Override
            public void handle( javafx.event.ActionEvent actionEvent )
            {
                Client.sendMessageToServer( constructMessageFromFXMLuserInput() );
                userInput.setText( "" );

                System.out.println( "sent via send button" );
            }
        } );
    }

    /* dont think i need this anymore
    public void onEnter()
    {
        System.out.println( "user hit enter while mouse was focused in the textfield with this message: " + userInput.getText() );
    }
    */

    // returns a String trimmed of spaces
    public static String retrieveTextFromFXMLuserInput()
    {
        return userInput.getText().trim();
    }

    // appends the incoming text area (userOut) with an inbound message
    public static void sendMessageToFXMLuserOutput( Message incomingMessage )
    {
        userOutput.appendText( incomingMessage.getMsgBody() + "\n" );
    }

    public static Message constructMessageFromFXMLuserInput()
    {
        // if the userInput field is empty, don't even bother sending the message
        if( retrieveTextFromFXMLuserInput().equals( "" ))
        {
            System.out.println( "message is empty, didnt send anything" );
            return null;
        }

        return new Message( retrieveTextFromFXMLuserInput(),
                            null,    // font entry
                            null,    // font entry
                            "bob" /*getUserName()*/ );
    }
}