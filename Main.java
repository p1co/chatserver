import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * @author Sergey
 *         Date: 11/22/13 Time: 9:10 PM
 */
public class Main extends Application
{
    private Stage      primaryStage;
    private BorderPane rootLayout;


    /**
     * this code creates a borderpane with a menubar at the top, the purpose is of
     * this is to nest the chat client into the borderpane's centre location
     */
    @Override
    public void start( Stage primaryStage )
    {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle( "chatninjas client" );

        try
        {
            // load the menubar layout from the fxml file
            FXMLLoader loader = new FXMLLoader( Client.class.getResource( "ninjamenu.fxml" ) );
            rootLayout = ( BorderPane ) loader.load();
            Scene scene = new Scene( rootLayout );
            primaryStage.setScene( scene );
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
            FXMLLoader loader = new FXMLLoader( Client.class.getResource( "ninja.fxml" ) );
            AnchorPane overviewPage = ( AnchorPane ) loader.load();
            rootLayout.setCenter( overviewPage );
        }
        catch( IOException e )
        {
            // Exception gets thrown if the fxml file could not be loaded
            e.printStackTrace();
        }
    }

    /**
     * Returns the main stage.
     *
     * @return
     */
    public Stage getPrimaryStage()
    {
        return primaryStage;
    }

    public static void main( String[] args )
    {
        launch( args );
    }
}
