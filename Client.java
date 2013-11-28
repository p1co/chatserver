import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Date;

import static java.text.DateFormat.MEDIUM;
import static java.text.DateFormat.getTimeInstance;

public class Client
{
    /*
        These are the user set preferences, the userName will be set by the user
        when the Client loads.
    */
    private static String userName = "default";  // this field will be changed when user is prompted for a name
    private static int fontSize = 12;


    // Object IO streams
    private static ObjectOutputStream toServer;
    private        ObjectInputStream  fromServer;

    /*
    Plan to update the port selection to use a showInputDialog that will request a user
    specified port and url of the server to connect to.
    Like the other showInputDialogs, this will have a server address and port preset.
     */
    private static final int port = 4000;

    static String date;
    boolean connected;

    // static Font myFont = new Font( fontType, Font.BOLD, fontSize ); // testing the font functionality // works!

    Message wantToSend;

    public Client() throws IOException
    {
        String hostname = "localhost";

        Socket socket = new Socket( hostname, port );

        // prompt the user for an alias using JOptionPane.showInputDialog
        userName = "bob"; //getName(); // getName needs to be rewritten, it used the old JFrames approach, needs to use the new css approach

        toServer   = new ObjectOutputStream( socket.getOutputStream() );
        fromServer = new ObjectInputStream(  socket.getInputStream() );

        // send the username to the server for "online list"
        // ** feature is beta right now **
        wantToSend = new Message( getName() );
        toServer.writeObject( wantToSend );

        Message receiveMessage;

        connected = true;

        while( connected )
        {
            try
            {
                receiveMessage = ( Message ) fromServer.readObject();

                if( receiveMessage != null )
                {
                    sendMessageToController( receiveMessage );
                }
                else
                {
                    sendMessageToController( new Message( 1, ">>>  Message from server == null <<< BUGCODE1001" ) );
                }
            }
            catch( ClassNotFoundException e1 )
            {
                e1.printStackTrace();
            }
        }
    }

    /*                   _     _                                                   _     _
                        | |   | |                           ___                   | |   | |
           __ _    ___  | |_  | |_    ___   _ __   ___     ( _ )      ___    ___  | |_  | |_    ___   _ __   ___
          / _` |  / _ \ | __| | __|  / _ \ | '__| / __|    / _ \/\   / __|  / _ \ | __| | __|  / _ \ | '__| / __|
         | (_| | |  __/ | |_  | |_  |  __/ | |    \__ \   | (_>  <   \__ \ |  __/ | |_  | |_  |  __/ | |    \__ \
          \__, |  \___|  \__|  \__|  \___| |_|    |___/    \___/\/   |___/  \___|  \__|  \__|  \___| |_|    |___/
           __/ |
          |___/
    */

    /**
     * Prompt for and return the desired screen name.
     */


    // need to update this code with nicole's login screen functionality
    /*
    public String getName()
    {
        // the random generator is for testing purposes, i didn't want to keep having
        // to type in usernames to test features
        return ( String ) JOptionPane.showInputDialog(
                null,
                "Choose a name:",
                "Screen name selection",
                JOptionPane.PLAIN_MESSAGE,
                null,
                null,
                "moose" + ( int ) ( Math.random() * ( 9999 - 1111 ) ) );
    }
    */
    public static String getName()
    {
        return userName;
    }

    public static void sendMessageToController( Message sendThisToController )
    {
        // make the date look pretty: 8:54:17 PM
        date = getTimeInstance( MEDIUM ).format( new Date() );

        print( "--sending message to controller" );
        NinjaController.sendMessageToFXMLuserOutput( sendThisToController, date );
        print( "--message sent to controller" );
    }

    static void sendMessageToServer( Message messageToSend )
    {
        sendMessageToController( new Message( 1, "test1") ); // works
        try
        {
            if( ! ( messageToSend.getMsgBody().isEmpty() ) )
            {
                // sends wantToSend object to the  server via ObjectOutputStream->OutputStream->Socket
                toServer.writeObject( messageToSend );
                print( "message sent to server" );
                toServer.flush();
            }
            else if( messageToSend == null ) // this shouldn't ever happen  BUGCODE1000
            {
                print( " >>> wantToSend is null .. this message is a bug  BUGCODE1000 <<< " );
            }
            else;
        }
        catch( IOException ex )
        {
            System.err.println( ex );
        }

    }

    public static Message constructMessageFromFXMLuserInput()
    {
        // if the userInput field is empty, don't even bother sending the message
        if( NinjaController.retrieveTextFromFXMLuserInput().equals( "" ))
        {
            System.out.println( "message is empty, didnt send anything" );
            return null;
        }

        return new Message( NinjaController.retrieveTextFromFXMLuserInput(),
                null,    // font entry
                null,    // font entry
                "bob" /*getUserName()*/ );
    }

    // basic stdout print because System.out.println( "" )
    public static void print( String printThisMessageToSystemOut )
    {
        System.out.println( printThisMessageToSystemOut );
    }
}