import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.HashSet;

import static java.text.DateFormat.MEDIUM;
import static java.text.DateFormat.getTimeInstance;
import static javax.swing.JOptionPane.PLAIN_MESSAGE;
import static javax.swing.JOptionPane.showInputDialog;

/**
 * @author Sergey Volkov
 *         Date: 11/30/13 Time: 2:30 AM
 */

public class ChatServer extends JFrame
{
    private static String port;

    // this hash will keep a list of users, the .LIST command will
    // access the list
    private static HashSet<String> onlineList = new HashSet<String>();

    // this hash will keep a list of objectoutputstream objects for each
    // user, purpose for this is to send a message to every connected client (echo)
    private static HashSet<ObjectOutputStream> writers = new HashSet<ObjectOutputStream>();

    private static JTextArea log;

    // start the user count at 1
    static int clientNo = 1;

    // this variable will be used to make the date look pretty: 8:54:17 PM
    private static String date;

    public ChatServer()
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
        new ChatServer();

        System.out.println( "The chat server is running." );

        ServerSocket listener = new ServerSocket( Integer.valueOf( port ) );

        // make the date look pretty: 8:54:17 PM
        date = getTimeInstance( MEDIUM ).format( new Date() );

        pushThis( "MultiThreadServer started on port " + port );

        InetAddress userAddress;

        try
        {
            while( true )
            {
                // make the date look pretty: 8:54:17 PM
                date = getTimeInstance( MEDIUM ).format( new Date() );

                ClientHandle task = new ClientHandle( listener.accept() );

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

    private static class ClientHandle implements Runnable
    {
        private String name;
        private Socket socket;

        private ObjectInputStream  inputFromClient;
        private ObjectOutputStream outputToClient;

        private Message echoedMessage = null;
        private String userName;

        public ClientHandle( Socket socket )
        {
            this.socket = socket;
        }

        public void run()
        {
            // make the date look pretty: 8:54:17 PM
            date = getTimeInstance( MEDIUM ).format( new Date() );
            try
            {
                // Create character streams for the socket.
                inputFromClient = new ObjectInputStream( socket.getInputStream() );
                outputToClient = new ObjectOutputStream( socket.getOutputStream() );

                writers.add( outputToClient );

                /*
                this section gets the username from the client, and adds the username
                to the online list hash of users connected

                sure, could have just sent a string instead of an object, this
                seemed more fun for now
                 */
                Message userNameToSave = ( Message ) inputFromClient.readObject();
                userName = userNameToSave.getUserName();
                onlineList.add( userName );

                pushThis( userName + " has connected to the server." );

                // Accept messages from this client and broadcast them.
                while( true )
                {
                    echoedMessage = ( Message ) inputFromClient.readObject();
                    System.out.println( "messages were sent" );

                    // query the server for a user list by sending .LIST to the server
                    if( echoedMessage.getMsgBody().matches( ".LIST" ) )
                    {
                        pushThis( "Userlist request made by " + echoedMessage.getUserName() );
                        log.append( "List of users online: " );
                        for( String person : onlineList )
                        {

                            log.append( person + " " );

                        }
                        log.append( "\n" );
                        log.setCaretPosition( log.getDocument().getLength() );

                    }

                    if( echoedMessage.getMsgBody().matches( ".QUIT" ) )
                    {
                        pushThis( "Quit request made by " + echoedMessage.getUserName() );

                        // this removes the username from the online list
                        sendMessageToAll( echoedMessage.getUserName(), " has left chat." );
                        onlineList.remove( echoedMessage.getUserName() );
                        break;
                    }

                    // this is the code that echos the Message object to all the clients
                    for( ObjectOutputStream writer : writers )
                    {
                        writer.writeObject( echoedMessage );
                        writer.flush();
                    }
                }
            }
            catch( IOException e )
            {
                System.out.println( e );
            }
            catch( ClassNotFoundException e )
            {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            finally
            {
                // Thi♦s client is going down!  Remove its name and its print
                // writer from the sets, and close its socket.
                if( name != null )
                {
                    System.out.println( "Removing " + name + "from online list." );
                    onlineList.remove( name );
                }
                if( outputToClient != null )
                {
                    writers.remove( outputToClient );
                }
                try
                {
                    socket.close();
                }
                catch( IOException e )
                {
                }
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

        // automagically scrolls to the button
        log.setCaretPosition( log.getDocument().getLength() );
    }

    public static void sendMessageToAll( String user, String displayThis ) throws IOException
    {
        Message sendThisMessageToAllConnected = new Message( user, displayThis );

        for( ObjectOutputStream writer : writers )
        {
            writer.writeObject( sendThisMessageToAllConnected );
            writer.flush();
        }
    }
}