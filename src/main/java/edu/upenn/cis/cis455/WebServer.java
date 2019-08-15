package edu.upenn.cis.cis455;
import java.io.*;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.LogManager;
import edu.upenn.cis.cis455.m2.server.interfaces.Request;
import edu.upenn.cis.cis455.m2.server.interfaces.Response;
import static edu.upenn.cis.cis455.WebServiceController.*;

public class WebServer {
    public static void main(String[] args) {
        org.apache.logging.log4j.core.config.Configurator.setLevel("edu.upenn.cis.cis455", Level.DEBUG);
        
        int port;
       String directory;
//        ServiceFactory service=new ServiceFactory();
//        if (args.length == 2) {
//            port = Integer.parseInt(args[0]);
//		    directory = args[1];
//		    
//		    service.port(port);
//		    service.staticFileLocation(directory);
//		    service.start();
//		    }
//		 else{
//		     System.out.println("invalid args");    
//		     service.start();
//		 }
//		    
//        System.out.println("Waiting to handle requests!");
//        service.get("/", (request, response) -> {
//           
//            
//                
//            response.redirect("/");
//            return null;
//           
//            
//        });
       
        if (args.length == 2) {
          port = Integer.parseInt(args[0]);
	    directory = args[1];
	    port(port);
	    staticFileLocation(directory);
	    
    }
            start();
            
            get("/name", (request, response) -> {
         
           
                
           
           return "<html><body>What's your name?: <form action=\"/entry\" method=\"POST\"><input type=\"text\" name=\"name\"/><input type=\"submit\" value=\"go\"/></form></body></html>";
           
        });
        get("/iiii", (request, response) -> {
            
            response.redirect("/control");
            return null;
        });
        get("/special", (request, response) -> {
            request.session(true);
            response.redirect("/control");
            return null;
        });
            
    }

}
