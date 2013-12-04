import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import static java.text.DateFormat.MEDIUM;
import static java.text.DateFormat.getTimeInstance;
import static javax.swing.JOptionPane.PLAIN_MESSAGE;
import static javax.swing.JOptionPane.showInputDialog;

/**
 * @author Sergey Volkov
 *         Date: 11/30/13 Time: 2:30 AM
 */

public class NinjaServer extends JFrame
{
    private static String port;

    // this hash will keep a list of users, the .LIST command will
    // access the list
    private static HashSet<String> onlineList = new HashSet<String>();

    private static HashMap<String, ObjectOutputStream> connectedMap = new HashMap<String, ObjectOutputStream>();

    // this hash will keep a list of objectoutputstream objects for each
    // user, purpose for this is to send a message to every connected client (echo)
    private static HashSet<ObjectOutputStream> writers = new HashSet<ObjectOutputStream>();

    private static JTextArea log;

    // start the user count at 1
    static int clientNo = 1;

    // this variable will be used to make the date look pretty: 8:54:17 PM
    private static String date;

    public NinjaServer()
    {
        log = new JTextArea( 5, 32 );
        add( new JScrollPane( log ) );
        log.setBackground( Color.DARK_GRAY );
        log.setForeground( Color.LIGHT_GRAY );

        /*
        when the server loads, it requests a port
        a random port outside the reserved ports range is preselected
         */
        port = ( String ) showInputDialog(
                this,
                "Choose a port:",
                "Port selection",
                PLAIN_MESSAGE,
                null,
                null,
                4000 );

        setTitle( "ChatNinjas server log" );
        setSize( 394, 200 );
        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        setVisible( true );
    }

    public static void main( String[] args ) throws Exception
    {
        new NinjaServer();

        System.out.println( "The chat server is running." );

        ServerSocket listener = new ServerSocket( Integer.valueOf( port ) );

        pushThis( "MultiThreadServer started on port " + port );

        InetAddress userAddress;

        try
        {
            while( true )
            {
                ClientHandle task = new ClientHandle( listener.accept(), clientNo );

                userAddress = listener.getInetAddress();

                // this section appends the log with the initial client connection information

                // Find the client's host name, and IP address
                pushThis( "Client "
                        + clientNo
                        + "'s host name is "
                        + userAddress.getHostName() );

                pushThis( "Client "
                        + clientNo
                        + "'s IP Address is "
                        + userAddress.getHostAddress() );

                pushThis( "Starting thread for client " + clientNo );

                new java.lang.Thread( task ).start();

                clientNo++;
            }
        }
        finally
        {
            listener.close();
        }
    }

    private static class ClientHandle implements java.lang.Runnable
    {
        private Socket socket;

        private ObjectInputStream  inputFromClient;
        private ObjectOutputStream outputToClient;

        private        Message echoedMessage = null;
        private static String  userName      = "server's default username";

        private static boolean isRunning = true;
        private static int clientNo;

        public ClientHandle( Socket socket, int clientNo )
        {
            this.socket = socket;
            this.clientNo = clientNo;
        }

        public void run()
        {
            try
            {
                // Create character streams for the socket.
                inputFromClient = new ObjectInputStream( socket.getInputStream() );
                outputToClient = new ObjectOutputStream( socket.getOutputStream() );

                // this section gets the username from the client, and adds the username
                // to the online list hash of users connected
                userName = ( String ) inputFromClient.readObject();

                pushThis( "Adding outputToClient to the writers hashset for client " + clientNo + "." );

                connectedMap.put( userName, outputToClient );

                pushThis( userName + " has connected to the server." );

                // 1 means the user is entering
                updateClientsOnlineList( 1, userName );


                pushThis( "begin while loop" );
                while( true )
                {
                    // waits for a message from connected client
                    echoedMessage = ( Message ) inputFromClient.readObject();
                    pushThis( "Received a message from: " + echoedMessage.getUserName() );

                    // display onlineList
                    if( echoedMessage.getMsgBody().matches( ".LIST" ) )
                        pushOnlineListToClients( echoedMessage );

                        // request to quit chat
                    else if( echoedMessage.getMsgBody().matches( ".LEAVE" ) )
                        kickUserOff( echoedMessage );

                    else
                        sendToAllConnected( echoedMessage );
                }
            }
            catch( ClassNotFoundException e )
            {
                e.printStackTrace();
            }
            catch( IOException e )
            {
                e.printStackTrace();
            }
            finally
            {
                System.out.println( "This client's thread is done =)" );
            }
        }

        private static void updateClientsOnlineList( int comeOrGo, String userName ) throws IOException
        {
            sendToAllConnected( new Message( comeOrGo, userName ) );
        }

        private static void kickUserOff( Message kickUserOffMessage ) throws IOException
        {
            pushThis( "Quit request made by " + kickUserOffMessage.getUserName() );

            pushThis( userName + " being removed from client onlineLists." );

            // 2 indicates user is leaving
            updateClientsOnlineList( 2, userName );

            if( connectedMap.isEmpty() != true )
            {
                pushThis( "Removing " + userName + "from server's online list." );
                connectedMap.remove( userName );
                // inputFromClient.close();
            }

            // this removes the username from the online list
            isRunning = false;
        }

        // so far this command shows online list to server log
        private static void pushOnlineListToClients( Message pushOnlineListToClients )
        {
            pushThis( "Userlist request made by " + pushOnlineListToClients.getUserName() );
            log.append( "List of users online: " );

            Iterator<String> keySetIterator = connectedMap.keySet().iterator();

            pushThis( "Online list: " );
            while( keySetIterator.hasNext() )
            {
                String key = keySetIterator.next();
                pushThis( key );
            }

            log.setCaretPosition( log.getDocument().getLength() );
        }

        private static void sendToAllConnected( Message messageObject ) throws IOException
        {
            Iterator<String> keySetIterator = connectedMap.keySet().iterator();

            while( keySetIterator.hasNext() )
            {
                String key = keySetIterator.next();
                connectedMap.get( key ).writeObject( messageObject );
            }
        }
    }

    /*
    just wanted to make log appending easier and standard. this will display the date in
    a nice format with the message(s), and then a newline
     */
    public static void pushThis( String displayThis )
    {
        date = getTimeInstance( MEDIUM ).format( new Date() );

        log.append( date + ": " + displayThis + "\n" );
        System.out.println( date + ": " + displayThis );

        // automagically scrolls to the button
        log.setCaretPosition( log.getDocument().getLength() );
    }
}
