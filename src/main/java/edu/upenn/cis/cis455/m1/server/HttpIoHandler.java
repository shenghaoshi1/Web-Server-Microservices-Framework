package edu.upenn.cis.cis455.m1.server;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
    import org.apache.logging.log4j.core.LoggerContext;
    
import static edu.upenn.cis.cis455.ServiceFactory.*;

import edu.upenn.cis.cis455.ServiceFactory;
import edu.upenn.cis.cis455.exceptions.HaltException;
import edu.upenn.cis.cis455.m2.server.interfaces.Request;
import edu.upenn.cis.cis455.m2.server.interfaces.Response;
import edu.upenn.cis.cis455.util.HttpParsing;

/**
 * Handles marshalling between HTTP Requests and Responses
 */
public class HttpIoHandler {
    final static Logger logger = LogManager.getLogger(HttpIoHandler.class);
    
    /**
     * Sends an exception back, in the form of an HTTP response code and message.  Returns true
     * if we are supposed to keep the connection open (for persistent connections).
     */
    public static boolean sendException(Socket socket, httprequest request, HaltException except) {
        String exception = request.protocol()+ " " + except.statusCode() + " " + except.body() + "\r\n";
        try{
             PrintWriter out = new PrintWriter(socket.getOutputStream());
             out.println(exception);
             out.flush();
             out.close();
             
        }
        catch (IOException e) {
            logger.error(e);
        }
        if (request==null){
            return false;
        }
        
        return  request.persistentConnection();
        
    }

    /**
     * Sends data back.   Returns true if we are supposed to keep the connection open (for 
     * persistent connections).
     */
    public static boolean sendResponse(Socket socket, httprequest request, httpresponse response) {
        try{
            
            if(request.session_id!=null){
                response.cookie("JSESSIONID",request.session_id);
            }
            PrintWriter out = new PrintWriter(socket.getOutputStream());
            String header=request.protocol()+" "+response.getHeaders();
            logger.info(header);
           out.print(header);
            //System.out.println(response.getHeaders());
            if (response.bodyRaw()!=null){
                if (response.type()==null){
                    out.println(response.body());
                  out.flush();
                    
                }
            else if (response.type().startsWith("text")){
                out.println(response.body());
                out.flush();
                
            }
            else{
                out.flush();
                socket.getOutputStream().write(response.bodyRaw());
                
            }
        }

            else{
                out.println(response.body());
                out.flush();
                
            }
    

            out.flush();
            out.close();
            
        }
        catch (IOException e) {
            logger.error(e);
        }
        
        return request.persistentConnection() ;
    }
}
