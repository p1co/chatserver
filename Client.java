import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Date;

import static java.text.DateFormat.MEDIUM;
import static java.text.DateFormat.getTimeInstance;

public class Client implements Runnable
{
    /*
        These are the user set preferences, the userName will be set by the user
        when the Client loads.
    */
    private static String userName = "default";  // this field will be changed when user is prompted for a name

    // Object IO streams
    private static ObjectOutputStream toServer;
    private        ObjectInputStream  fromServer;

    Message receiveMessage;

    /*
    Plan to update the port selection to use a showInputDialog that will request a user
    specified port and url of the server to connect to.
    Like the other showInputDialogs, this will have a server address and port preset.
     */
    String hostname = "localhost";
    private static final int port = 4000;
    Socket socket;

    static String date;
    boolean connected;

    // static Font myFont = new Font( fontType, Font.BOLD, fontSize ); // testing the font functionality // works!

    Message wantToSend;

    @Override
    public void run()
    {
        try
        {
            socket = new Socket( hostname, port );
        }
        catch( IOException e )
        {
            e.printStackTrace();
        }

        // prompt the user for an alias using JOptionPane.showInputDialog
        userName = "" + (int)(0 + (Math.random() * (255 - 0)))*10; //getName(); // getName needs to be rewritten, it used the old JFrames approach, needs to use the new css approach

        try
        {
            toServer   = new ObjectOutputStream( socket.getOutputStream() );
        }
        catch( IOException e )
        {
            e.printStackTrace();
        }
        try
        {
            fromServer = new ObjectInputStream(  socket.getInputStream() );
        }
        catch( IOException e )
        {
            e.printStackTrace();
        }

        // send the username to the server for "online list"
        // ** feature is beta right now **
        wantToSend = new Message( getName() );
        try
        {
            toServer.writeObject( wantToSend );
        }
        catch( IOException e )
        {
            e.printStackTrace();
        }

        connected = true;

        while( connected )
        {
            date = getTimeInstance( MEDIUM ).format( new Date() );
            try
            {
                receiveMessage = ( Message ) fromServer.readObject();

                if( receiveMessage != null )
                {
                    NinjaController.sendMessageToFXMLuserOutput( receiveMessage, date );
                }
                else
                {
                    NinjaController.sendMessageToFXMLuserOutput( ">>>  Message from server == null <<< BUGCODE1001", date );
                }
            }
            catch( ClassNotFoundException e1 )
            {
                e1.printStackTrace();
            }
            catch( IOException e )
            {
                e.printStackTrace();
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

    static void sendMessageToServer( Message messageToSend )
    {
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
                getName() );
    }

    // basic stdout print because System.out.println( "" )
    public static void print( String printThisMessageToSystemOut )
    {
        System.out.println( printThisMessageToSystemOut );
    }
}