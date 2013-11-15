import java.awt.*;
import java.io.Serializable;

/**
 * @author Sergey 
 *         Date: 10/29/13 Time: 12:17 AM
 */

class Message implements Serializable
{
    private String msgBody;
    private Font   myFont;
    private Color  fontColour;
    private String fromUserName;
    private String toUserName;

    /*                             _                           _
                                  | |                         | |
       ___    ___    _ __    ___  | |_   _ __   _   _    ___  | |_    ___    _ __
      / __|  / _ \  | '_ \  / __| | __| | '__| | | | |  / __| | __|  / _ \  | '__|
     | (__  | (_) | | | | | \__ \ | |_  | |    | |_| | | (__  | |_  | (_) | | |
      \___|  \___/  |_| |_| |___/  \__| |_|     \__,_|  \___|  \__|  \___/  |_|
    */

    /*
    this constructor is used to send the initial
    username to the server for the online list
     */
    Message( String userName )
    {
        this.fromUserName = userName;
    }

    /*
    this constructor is used by the server to let the
    whole room know a client has disconnected
     */
    Message( String userName, String allOfThisTest )
    {
        this.msgBody = userName + " " + allOfThisTest;
    }

    /*
    this constructor is used for all chat messages a user
     sends that are not private
     */
    Message( String msgBody, Font myFont, Color fontColour, String userName )
    {
        this.msgBody = msgBody;
        this.myFont = myFont;
        this.fontColour = fontColour;
        this.fromUserName = userName;
    }

    /*
    this constructor is used when a private message is
     requested (feature needs to be implemented)
     */
    Message( String msgBody, Font myFont, Color fontColour, String fromUserName, String toUserName )
    {
        this.msgBody = msgBody;
        this.myFont = myFont;
        this.fontColour = fontColour;
        this.fromUserName = fromUserName;
        this.toUserName = toUserName;
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
        return fromUserName;
    }

    Color getFontColour()
    {
        return fontColour;
    }

    Font getMyFont()
    {
        return myFont;
    }

    @Override
    public String toString()
    {
        return super.toString() + msgBody + " "
                + fontColour + " "
                + fromUserName + " ";
    }
}
