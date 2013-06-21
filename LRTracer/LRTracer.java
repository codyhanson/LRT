/**
* This class provides static methods that
*
*
*/

//TODO: concurrency protection?
//TODO: Message buffering if we temporarily lost a connection?
//TODO: nonblocking sending to the websocket? maybe its fast enough that it doesnt matter.

public class LRTracer
{
    //init must be called before other methods are used
    private static boolean initialized;

    /**
    * This method establishes the connection with the LRT Server
    * Via a Websocket connection
    *
    */
    public static void init()
    {

        initialized = true;
    }

    /**
    * Exports a trace point as a JSON message 
    * to the LRT Server over the
    * previously established websocket connection.
    */
    public static void trace(int lineNumber, string methodName, string message)
    {
        if (initialized == false) return;

        //build json msg

        //send it out the websocket

    }

    /**
    * clean up and 
    * tear down the websocket connection
    */
    public static void close()
    {
        //close down the websocket


    }

}
