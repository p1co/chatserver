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
            FXMLLoader loader = new FXMLLoader( getClass().getResource( "ninjamenu.fxml" ) );
            rootLayout = ( BorderPane ) loader.load();
            NinjaController controller = (NinjaController)loader.getController();

            //controller.setStageAndSetupListeners(stage); // or what you want to do

            Scene scene = new Scene( rootLayout );
            primaryStage.setScene( scene );

            primaryStage.setOnCloseRequest( new EventHandler<WindowEvent>() {
                @Override public void handle(WindowEvent t) {
                    System.out.println("CLOSING");

                    // this check is to see if there exists a connection
                    // if no connection, then don't send the .LEAVE command
                    // if a connection exists, the .LEAVE command
                    // is intended to update the onlineList
                    if( !(Client.toServer == null) )
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

    static NinjaController controller;
    /**
     * Shows the chat room area that will be nested into the centre of the borderpane above
     */
    public void showChatRoom()
    {
        try
        {
            // Load the fxml file and set into the center of the main layout
            FXMLLoader loader = new FXMLLoader( getClass().getResource( "ninja.fxml" ) );
            AnchorPane overviewPage = ( AnchorPane ) loader.load();

            NinjaController controller = loader.getController();

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