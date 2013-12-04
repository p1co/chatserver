import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client implements Runnable
{
    /*
        These are the user set preferences, the userName will be set by the user
        when the Client loads.
    */
    private static String userName = "" + ( int ) ( 0 + ( Math.random() * ( 255 - 0 ) ) ) * 10;  // this field will be changed when user is prompted for a name

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

    boolean connected = true;

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

        try
        {
            toServer = new ObjectOutputStream( socket.getOutputStream() );
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
        try
        {
            toServer.writeObject( userName );
        }
        catch( IOException e )
        {
            e.printStackTrace();
        }

        //while( !Thread.interrupted() )
        while( connected )
        {
            try
            {
                receiveMessage = ( Message ) fromServer.readObject();

                if( receiveMessage != null )
                {
                    if( receiveMessage.getComeOrGo() == 1 )
                        print( "added user " + receiveMessage.getMsgBody() );
                    else if( receiveMessage.getComeOrGo() == 2 )
                        print( "removed user " + receiveMessage.getMsgBody() );
                    else if( receiveMessage.getComeOrGo() == 3 )
                        NinjaController.guiOnlineListUpdate( receiveMessage.getMsgBody() );
                    else
                        NinjaController.sendMessageToFXMLuserOutput( receiveMessage );
                }
                else
                {
                    NinjaController.sendMessageToFXMLuserOutput( ">>>  Message from server == null <<< BUGCODE1001" );
                }
            }
            catch( ClassNotFoundException e1 )
            {
                e1.printStackTrace();
            }
            catch( IOException e )
            {
                print( "NOT CONNECTED ANY MORE" );
                connected = false;
            }
        }

        print( "client completed disconnected" );
    }

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