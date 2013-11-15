import java.awt.*;
import java.io.Serializable;

/**
 * @author Sergey 
 *         Date: 10/29/13 Time: 12:17 AM
 */

class Message implements Serializable
{
    private String msgBody;
    private int    fontSize;
    private String fontType;
    private Color fontColour;
    private String userName;

    /*                             _                           _
                                  | |                         | |
       ___    ___    _ __    ___  | |_   _ __   _   _    ___  | |_    ___    _ __
      / __|  / _ \  | '_ \  / __| | __| | '__| | | | |  / __| | __|  / _ \  | '__|
     | (__  | (_) | | | | | \__ \ | |_  | |    | |_| | | (__  | |_  | (_) | | |
      \___|  \___/  |_| |_| |___/  \__| |_|     \__,_|  \___|  \__|  \___/  |_|
    */
    Message( String msgBody, int fontSize, String fontType, Color fontColour, String userName )
    {
        this.msgBody = msgBody;
        this.fontSize = fontSize;
        this.fontType = fontType;
        this.fontColour = fontColour;
        this.userName = userName;
    }

    // this constructor is used to send the initial username to the server for the online list
    Message( String userName )
    {
        this.userName = userName;
    }

    Message( String userName, String allOfThisTest )
    {
        this.msgBody = userName + " " + allOfThisTest;
    }

    /*               _     _                                                   _     _
                    | |   | |                           ___                   | |   | |
       __ _    ___  | |_  | |_    ___   _ __   ___     ( _ )      ___    ___  | |_  | |_    ___   _ __   ___
      / _` |  / _ \ | __| | __|  / _ \ | '__| / __|    / _ \/\   / __|  / _ \ | __| | __|  / _ \ | '__| / __|
     | (_| | |  __/ | |_  | |_  |  __/ | |    \__ \   | (_>  <   \__ \ |  __/ | |_  | |_  |  __/ | |    \__ \
      \__, |  \___|  \__|  \__|  \___| |_|    |___/    \___/\/   |___/  \___|  \__|  \__|  \___| |_|    |___/
       __/ |
      |___/
    */

    public String getMsgBody()
    {
        return msgBody;
    }

    String getUserName()
    {
        return userName;
    }

    Color getFontColour()
    {
        return fontColour;
    }

    String getFontType()
    {
        return fontType;
    }

    int getFontSize()
    {
        return fontSize;
    }

    @Override
    public String toString()
    {
        return super.toString() + msgBody + " "
                + fontSize + " "
                + fontType + " "
                + fontColour + " "
                + userName + " ";
    }
}
