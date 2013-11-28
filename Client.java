

/*
import javafx.event.EventHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;


import static java.text.DateFormat.getTimeInstance;
*/

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

import static java.text.DateFormat.MEDIUM;
import static java.text.DateFormat.getTimeInstance;

public class Client extends NinjaController
{
    /*
        These are the user set preferences, the userName will be set by the user
        when the Client loads.
        The font-related settings will be adjustable via the ClientGUI controls.
    */
    private static String userName;  // this field will be changed when user is prompted for a name
    private static int    fontSize   = 12;
    private static String fontType   = "Serif";
    private static Color  fontColour; // = Color.DARK_GRAY;
    private static int    fontStyle;  //  = Font.BOLD;
    private static Font   myFont;

    // Object IO streams
    private ObjectOutputStream toServer;
    private ObjectInputStream  fromServer;

    /*
    Plan to update the port selection to use a showInputDialog that will request a user
    specified port and url of the server to connect to.
    Like the other showInputDialogs, this will have a server address and port preset.
     */
    private static final int port = 4000;

    String  date;
    boolean connected;

    // static Font myFont = new Font( fontType, Font.BOLD, fontSize ); // testing the font functionality // works!

    Message wantToSend;
    Message beingSentToClient;

    public Client() throws IOException
    {
        String hostname = "localhost";

        Socket socket = new Socket( hostname, port );

        // prompt the user for an alias using JOptionPane.showInputDialog
        userName = "bob"; //getName();

        toServer = new ObjectOutputStream( socket.getOutputStream() );
        fromServer = new ObjectInputStream( socket.getInputStream() );

        // send the username to the server for "online list"
        // ** feature is beta right now **

        wantToSend = new Message( userName );
        toServer.writeObject( wantToSend );

        Message receiveMessage;

        connected = true;

        while( connected )
        {
            try
            {
                // make the date look pretty: 8:54:17 PM
                date = getTimeInstance( MEDIUM ).format( new Date() );

                receiveMessage = ( Message ) fromServer.readObject();

                if( receiveMessage != null )
                {
                    userOutput.appendText( date                          /* displays date */
                            + " "                                    /* space */
                            + receiveMessage.getUserName()           /* displays username */
                            + ": "                                   /* colon and space */
                            + receiveMessage.getMsgBody() + "\n" );  /* displays message text */

                    userInput.setText( "" );                              // clears the textfield where user inputs message
                }
                else
                {
                    userOutput.appendText( ">>>  Message from server==null <<<" );
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

    public static Font getMyFont()
    {
        return myFont;
    }

    public static void setMyFont( String fontType, int fontStyle, int fontSize )
    {
        //Client.myFont = new Font( fontType, fontStyle, fontSize );
    }

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

    // this class is replacing our old Listener class
    private class handleMessage
    {
        public Message getMessageFromFXMLViaController()
        {
            return new Message( retrieveText(),
                                //getMyFont(),
                                //getFontColour(),
                                getUserName() );
        }

        public void setMessagetoFXMLViaController()
        {
            System.out.println("--sending message to controller");
            sendMessageToUserOutput( beingSentToClient );
            System.out.println("--message sent to controller");
        }
    }

    /*
    private class Listener implements ActionListener
    {
        @Override
        public void actionPerformed( ActionEvent e )
        {
            setMyFont( fontType, fontStyle, fontSize );

            try
            {

                    //Upon user hitting enter in the chat client window, this grabs the String, username
                   // and font parameters

                    //This object will be modified to use the font class when we get time to implement it.

                wantToSend = new Message( userInput.getText().trim(),
                        Client.getMyFont(),
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
                else
                {
                    ;
                }
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
     */

    // returns a String trimmed of spaces
    public static String retrieveText()
    {
        return userInput.getText().trim();
    }

    // appends the incoming text area (userOut) with an inbound message
    public static void sendMessageToUserOutput( Message incomingMessage )
    {
        userOutput.appendText( incomingMessage.getMsgBody() + "\n" );
    }

    @Override
    public void initialize( URL fxmlFileLocation, ResourceBundle resources )
    {
        this.resources = resources;
        this.location = fxmlFileLocation;

        System.out.println( "about to show the resources var: " + resources );
        System.out.println( "about to show the fxmlFileLocation var: " + fxmlFileLocation );

        userInput.setOnAction( new EventHandler<ActionEvent>()
        {
            @Override
            public void handle( javafx.event.ActionEvent actionEvent )
            {
                if(userInput.getText().trim().equals( "" ))
                    return;


            }
        });

        sendButton.setOnAction( new EventHandler<javafx.event.ActionEvent>()
        {
            @Override
            public void handle( javafx.event.ActionEvent actionEvent )
            {
                System.out.println( "add this when userInput box functionality done" );
            }
        } );
    }

}