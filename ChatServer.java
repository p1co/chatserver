import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.HashSet;

import static java.awt.BorderLayout.*;
import static java.text.DateFormat.MEDIUM;
import static java.text.DateFormat.getTimeInstance;

public class ChatServer extends JFrame
{
    private static final int PORT = 9004;

    // this hash will keep a list of users, the .LIST command will
    // access the list
    private static HashSet<String> onlineList = new HashSet<String>();

    // this hash will keep a list of objectoutputstream objects for each
    // user, purpose for this is to send a message to every connected client (echo)
    private static HashSet<ObjectOutputStream> writers = new HashSet<ObjectOutputStream>();

    private static JTextArea                   log;

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

        setTitle( "ChatNinjas server log" );
        setSize( 394, 200 );
        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        setVisible( true );
    }

    public static void main( String[] args ) throws Exception
    {
        new ChatServer();

        System.out.println( "The chat server is running." );

        ServerSocket listener = new ServerSocket( PORT );

        // make the date look pretty: 8:54:17 PM
        date = getTimeInstance( MEDIUM ).format( new Date() );

        pushThis( "MultiThreadServer started." );

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

                new Thread(task).start();

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

                // receive the username from the client for online userlist
                userName = (String) inputFromClient.readObject();

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
                        log.setCaretPosition(log.getDocument().getLength());

                    }

                    if( echoedMessage.getMsgBody().matches( ".QUIT" ) )
                    {
                        pushThis( "Quit request made by " + echoedMessage.getUserName() );

                        // quit code will go here, such as remove user from online list
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
        log.setCaretPosition(log.getDocument().getLength());
    }
}