import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Date;

import static java.awt.BorderLayout.*;
import static java.text.DateFormat.MEDIUM;
import static java.text.DateFormat.getTimeInstance;

public class Client extends JFrame
{
    /*
        These are the user set preferences, the userName will be set by the user
        when the Client loads.
        The font-related settings will be adjustable via the ClientGUI controls.
    */
    private static String userName;  // this field will be changed when user is prompted for a name
    private static int    fontSize   = 12;
    private static String fontType   = "Serif";
    private static Color fontColour = Color.DARK_GRAY;

    private JTextField userInput  = new JTextField(); // Text field for receiving message
    private JTextArea  userOutput = new JTextArea();  // Text area to display messages

    // Object IO streams
    private ObjectOutputStream toServer;
    private ObjectInputStream  fromServer;

    String  date;
    boolean connected;
    JPanel p = null;

    Font myFont = new Font( fontType, Font.BOLD, fontSize); // testing the font functionality // works!

    public static void main( String[] args ) throws IOException
    {
        new Client();
    }

    //ignore this comment
    public Client() throws IOException
    {
        // Panel p to hold the label and text field
        p = new JPanel();
        p.setLayout( new BorderLayout() );
        p.add( new JLabel( "Type your message" ), WEST );
        p.add( userInput, CENTER );
        userInput.setHorizontalAlignment( JTextField.RIGHT );

        /*
        this line allows for text wrapping in the message display area, otherwise
        a long  horizontal scroll bar will be automagically used
        */
        userOutput.setLineWrap( true );
        userOutput.setFont(myFont);

        // the following code will be replaced when our JavaFX ClientGUI is finished
        setLayout( new BorderLayout() );
        add( p, NORTH );
        add( new JScrollPane( userOutput ), CENTER );

        userInput.addActionListener( new Listener() );

        setTitle( "__chatclient__" ); // woo! thats us!!
        setSize( 500, 300 );
        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        setLocationRelativeTo( null );
        setVisible( true );

        String hostname = "localhost";
        int port = 9004;

        Socket socket = new Socket( hostname, port );

        // prompt the user for an alias using JOptionPane.showInputDialog
        userName = getName();

        toServer   = new ObjectOutputStream( socket.getOutputStream() );
        fromServer = new ObjectInputStream( socket.getInputStream()  );

        // send the username to the server for "online list"
        // ** feature is beta right now **
        toServer.writeObject( userName );

        Message receiveMessage;

        connected = true;
        while( connected )
        {
            try
            {
                // make the date look pretty: 8:54:17 PM
                date = getTimeInstance( MEDIUM ).format( new Date() );
                userOutput.setForeground( fontColour );

                receiveMessage = ( Message ) fromServer.readObject();

                if( receiveMessage != null )
                {
                    userOutput.append( date                          /* displays date */
                            + " "                                    /* space */
                            + receiveMessage.getUserName()           /* displays username */
                            + ": "                                   /* colon and space */
                            + receiveMessage.getMsgBody() + "\n" );  /* displays message text */

                    userInput.setText( "" );                              // clears the textfield where user inputs message
                }
                else
                {
                    userOutput.append( ">>>  Message from server==null <<<" );
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
    public static String getUserName()
    {
        return userName;
    }

    public void setUserName( String userName )
    {
        Client.userName = userName;
    }

    public static int getFontSize()
    {
        return fontSize;
    }

    public void setFontSize( int fontSize )
    {
        this.fontSize = fontSize;
    }

    public static String getFontType()
    {
        return fontType;
    }

    public void setFontType( String fontType )
    {
        this.fontType = fontType;
    }

    public static Color getFontColour()
    {
        return fontColour;
    }

    public void setFontColour( Color fontColour )
    {
        this.fontColour = fontColour;
    }

    /**
     * Prompt for and return the desired screen name.
     */
    public String getName()
    {
        return JOptionPane.showInputDialog(
                p,
                "Choose a name:",
                "Screen name selection",
                JOptionPane.PLAIN_MESSAGE );
    }

    private class Listener implements ActionListener
    {
        Message wantToSend;

        @Override
        public void actionPerformed( ActionEvent e )
        {
            try
            {
                /*
                    Upon user hitting enter in the chat client window, this grabs the String, username
                    and font parameters

                    This object will be modified to use the font class when we get time to implement it.
                */
                wantToSend = new Message( userInput.getText().trim(),
                        Client.getFontSize(),
                        Client.getFontType(),
                        Client.getFontColour(),
                        Client.getUserName() );

                // if the message has length (user hit enter after typing something)
                if( wantToSend.getMsgBody().matches( ".BLUE" ) )
                {
                    setFontColour( Color.BLUE );
                    userInput.setText( "" );
                }

                else if( wantToSend.getMsgBody().matches( ".DARK" ) )
                {
                    setFontColour( Color.DARK_GRAY );
                    userInput.setText( "" );
                }
                else if( ! ( wantToSend.getMsgBody().isEmpty() ) )
                {
                    // sends wantToSend object to the  server via ObjectOutputStream->OutputStream->Socket
                    toServer.writeObject( wantToSend );
                    print( "message sent to server" );
                    toServer.flush();
                }
                else if( wantToSend == null ) // this shouldn't ever happen
                {
                    userInput.setText( " >>> wantToSend is null .. this message is a bug <<< " );
                }
                else;
            }
            catch( IOException ex )
            {
                System.err.println( ex );
            }
        }

        public void print( String printThisMessageToSystemOutPrintln )
        {
            System.out.println( printThisMessageToSystemOutPrintln );
        }
    }

}