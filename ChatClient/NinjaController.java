package ChatClient;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBoxBuilder;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

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

    static String thisUserName;

    @Override
    public void initialize( URL fxmlFileLocation, ResourceBundle resources )
    {
        this.resources = resources;
        this.location = fxmlFileLocation;

        System.out.println( "about to show the resources var: " + resources );
        System.out.println( "about to show the fxmlFileLocation var: " + fxmlFileLocation );

        Client.setUserName( requestUserName() );

        userInput.setOnAction( new EventHandler<ActionEvent>()
        {
            @Override
            public void handle( ActionEvent actionEvent )
            {
                consolidatedSendMessagePrompt();
            }
        } );

        sendButton.setOnAction( new EventHandler<ActionEvent>()
        {
            @Override
            public void handle( ActionEvent actionEvent )
            {
                consolidatedSendMessagePrompt();
            }
        } );

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

    public static void guiOnlineListUpdate( String onlineListUpdate )
    {
        onlineList.appendText( onlineListUpdate );
    }

    private void consolidatedSendMessagePrompt()
    {
        Client.sendMessageToServer( Client.constructMessageFromFXMLuserInput() );
        userInput.setText( "" );

        System.out.println( "send via enter key" );
    }

    static Stage dialogStage;

    // incase we ever need to display a popup dialog
    static void popupText( String popupMessage )
    {
        Button but = new Button( "Close" );
        but.setOnAction( new EventHandler<ActionEvent>()
        {
            @Override
            public void handle( ActionEvent e )
            {
                dialogStage.hide();
            }
        } );

        dialogStage = new Stage();
        dialogStage.initModality( Modality.WINDOW_MODAL );
        dialogStage.setScene( new Scene( VBoxBuilder.create()
                .children( new Text( popupMessage ),
                           but
                         ).alignment( Pos.CENTER ).padding( new Insets( 5 ) ).build() ) );
        dialogStage.show();

    }

    static String requestUserName()
    {
        final TextField userNameField = new TextField();

        userNameField.setOnAction( new EventHandler<ActionEvent>() {
            @Override
            public void handle( ActionEvent actionEvent )
            {
                Client.setUserName( userNameField.getText().trim() );
                userNameField.setText( "" );
                dialogStage.close();
            }
        } );

        // ignore this comment

        dialogStage = new Stage();
        dialogStage.initModality( Modality.WINDOW_MODAL );
        dialogStage.setScene( new Scene( VBoxBuilder.create().children(
                                        new Text( "Enter your desired username." ),
                                        userNameField
                                        ).alignment( Pos.CENTER ).padding( new Insets( 5 ) ).build() ) );
        dialogStage.show();

        return thisUserName;
    }
}