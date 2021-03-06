package ChatClient;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;


// TODO test 3


/**
 * @author Sergey
 *         Date: 11/22/13 Time: 9:10 PM
 */
public class Main extends Application
{
    private BorderPane rootLayout;

    /**
     * this code creates a borderpane with a menubar at the top, the purpose is of
     * this is to nest the chat client into the borderpane's centre location
     */
    @Override
    public void start( Stage primaryStage )
    {
        try
        {
            // load the menubar layout from the fxml file
            FXMLLoader loader = new FXMLLoader( getClass().getResource( "ChatClient/ninjamenu.fxml" ) );
            rootLayout = ( BorderPane ) loader.load();
            Scene scene = new Scene( rootLayout );
            primaryStage.setScene( scene );

            primaryStage.setOnCloseRequest( new EventHandler<WindowEvent>() {
                @Override public void handle(WindowEvent t) {
                    System.out.println("CLOSING");
                    Client.sendMessageToServer( new Message( ".LEAVE" ) );
                }
            });

            primaryStage.setTitle( "chatninjas client" );
            primaryStage.show();
        }
        catch( Exception ex )
        {
            // Exception gets thrown if the fxml file could not be loaded
            ex.printStackTrace();
        }

        showChatRoom();
    }


    /**
     * Shows the chat room area that will be nested into the centre of the borderpane above
     */
    public void showChatRoom()
    {
        try
        {
            // Load the fxml file and set into the center of the main layout
            FXMLLoader loader = new FXMLLoader( getClass().getResource( "ChatClient/ninja.fxml" ) );
            AnchorPane overviewPage = ( AnchorPane ) loader.load();
            rootLayout.setCenter( overviewPage );
        }
        catch( IOException e )
        {
            // Exception gets thrown if the fxml file could not be loaded
            e.printStackTrace();
        }
    }

    public static void main( String[] args )
    {
        launch( args );
    }
}