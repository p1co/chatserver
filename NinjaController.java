import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

import static java.text.DateFormat.MEDIUM;
import static java.text.DateFormat.getTimeInstance;

public class NinjaController implements Initializable
{
    @FXML //  fx:id="userOutput"
    private static TextArea userOutput;

    @FXML //  fx:id="userInput"
    private static TextField userInput;

    @FXML //  fx:id="onlineList"
    protected static TextArea onlineList;

    @FXML //  fx:id="sendButton"
    private Button sendButton;

    @FXML
    ResourceBundle resources;

    @FXML
    URL location;

    // date stuff
    static String date;

    @Override
    public void initialize( URL fxmlFileLocation, ResourceBundle resources )
    {
        this.resources = resources;
        this.location = fxmlFileLocation;

        System.out.println( "about to show the resources var: " + resources );
        System.out.println( "about to show the fxmlFileLocation var: " + fxmlFileLocation );

        userInput.setOnAction( new EventHandler<ActionEvent>()
        {
            @Override
            public void handle( ActionEvent actionEvent )
            {
                consolidatedSendMessagePrompt();
            }
        });

        sendButton.setOnAction( new EventHandler<ActionEvent>()
        {
            @Override
            public void handle( ActionEvent actionEvent )
            {
                consolidatedSendMessagePrompt();
            }
        } );

        // this little bit right here allows our client to
        // talk to the chat server
        Client task = new Client();
        new Thread(task).start();
    }

    // returns a String trimmed of spaces
    public static String retrieveTextFromFXMLuserInput()
    {
        return userInput.getText().trim();
    }

    // appends the incoming text area (userOut) with an inbound message
    public static void sendMessageToFXMLuserOutput( Message incomingMessage )
    {
        date = getTimeInstance( MEDIUM ).format( new Date() );
        userOutput.appendText( date + " ["
                + incomingMessage.getUserName() + "]: "
                + incomingMessage.getMsgBody() + "\n" );
    }

    public static void sendMessageToFXMLuserOutput( String message )
    {
        date = getTimeInstance( MEDIUM ).format( new Date() );
        userOutput.appendText( date + " [special message]: " + message + "\n");
    }

    public static void guiOnlineListUpdate( String userName )
    {
        onlineList.appendText( userName + "\n" );
    }

    private void consolidatedSendMessagePrompt()
    {
        Client.sendMessageToServer( Client.constructMessageFromFXMLuserInput() );
        userInput.setText( "" );

        System.out.println( "send via enter key" );
    }


}