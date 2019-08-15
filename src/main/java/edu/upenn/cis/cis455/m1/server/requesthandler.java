package edu.upenn.cis.cis455.m1.server;
import edu.upenn.cis.cis455.m1.server.interfaces.HttpRequestHandler;
import edu.upenn.cis.cis455.exceptions.HaltException;
import edu.upenn.cis.cis455.m2.server.interfaces.Request;
import edu.upenn.cis.cis455.m2.server.interfaces.Response;
import edu.upenn.cis.cis455.m1.server.HttpServer.Filterobject;
import java.lang.StringBuilder;
import java.util.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import edu.upenn.cis.cis455.util.HttpParsing;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import edu.upenn.cis.cis455.handlers.Filter;
import edu.upenn.cis.cis455.handlers.Route;

public class requesthandler implements HttpRequestHandler {
    final static Logger logger = LogManager.getLogger(requesthandler.class);
    private HttpServer server;
     
     
     public String controlpanel(HttpServer httpserver){
         StringBuilder builder=new StringBuilder();
         builder.append("<!DOCTYPE html><html><head><title>Control Panel Page</title></head><body><table><tr><th>Thread</th><th>Status</th></tr>");
         for( int i=0; i <httpserver.getthreadsize(); i++ ){
			builder.append("<tr><th>	Thread_"+i+"</th><th>"+httpserver.getworkerstatus(i)+"</th></tr>");}
		builder.append("</table>");
		builder.append("<ul><li><a href=\"/shutdown\">Shut down</a></li></ul>");
		builder.append("</body></html>\r\n");
		return builder.toString();
		}
	public String shutdown(){
	    StringBuilder builder= new StringBuilder();
		builder.append("<!DOCTYPE html><html><head><title>Shutdown</title></head><body>");
		builder.append("<ul><li><a>This is a shut down page</a></li></ul>");
		builder.append("</body></html>\r\n");
		return builder.toString();
	    
	}
	private boolean accessfile(String pathInfo) throws HaltException {
        for (String name : pathInfo.split("/")) {
            if (name.equals("..")) {
                return false;
            }
        }
        return true;
    }
    private String processdirectory(File file, httprequest request){
        
        StringBuilder builder = new StringBuilder();
	            	builder.append("<!DOCTYPE html>\r\n");
	            	builder.append("<html><head><meta charset='utf-8' /><title>");
	                builder.append("File Listing in " + request.uri());
	            	builder.append("</title></head><body>\r\n");
	            	builder.append("<ul><li><a href=\"../\">..</a></li>\r\n");
	            	for ( File f: file.listFiles()){
	            		if (f.isHidden()|| !f.canRead()){
	            			continue;
	            		}
	            		String name = f.getName();
	            		builder.append("<li><a href=\"" + name + "\">" + name + "</a></li>\r\n");
	            	}
	            	builder.append("</ul></body></html>\r\n");
	          return builder.toString();
    }
     @Override
    public void handle(Request request, Response response) throws HaltException{}
    public boolean equal(String path, String request_path){
         path = path.replaceAll("\\*", "[^/]*");
        path = path.replaceAll(":name", "[^/]*");
        return request_path.matches(path);
        
        
    }
    private void applyFilters(ArrayList<Filterobject> filters, Request request, Response response) throws Exception {
        for (Filterobject filterobject : filters) {
            String path = filterobject.get_path();
            String type = filterobject.get_type();
            Filter filter = filterobject.get_filter();
            if ((type.equals("") || type.equals(request.requestMethod())) && 
                (path.equals("") || equal(path, request.pathInfo()))) {
                filter.handle(request, response);
            }
        }
    }
    public Route match_route(String path,  HashMap <String, Route > map){
         
        for (String key: map.keySet()){
            if (equal(key,path)){
                return map.get(key);
            }
        }
        return null;
    }
	
	public requesthandler(httprequest request, httpresponse response, HttpServer httpserver){

    

	      String directory=httpserver.getdirectory()+"/"+request.pathInfo();
	      String dir=directory.replaceAll("//","/");
	      logger.info(request.pathInfo());
	      logger.info(directory);
	       logger.info(dir);
	    
            File file = new File(dir);
           
            if(request.uri().equalsIgnoreCase("/control")) {
  
            	System.out.println("It is a special control request");
            	response.type("text/html");   
            	response.status(200);
            	if(request.requestMethod().equalsIgnoreCase("GET")) {
            	response.body(controlpanel(httpserver));
        	}

            	
            }
            else if(request.uri().equalsIgnoreCase("/shutdown")){
                System.out.println("It is a special shutdown request");
                response.status(200);
            	response.type("text/html");
            	if(request.requestMethod().equalsIgnoreCase("GET")) {
                response.body(shutdown());
                httpserver.setrunningstatus(false);
                }
                
            }
            else{
                
            try {applyFilters(httpserver.get_before_filters(),request,response);
                 }
            catch (Exception e){
                logger.info("beforefilter failed");
            }
            Route route=match_route(request.pathInfo(),httpserver.getRoutes(request.requestMethod()));
            if (route!=null){
                try{
                Object route_return=route.handle(request,response);
                if (route_return!=null){
                    response.body(route_return.toString());
                }
                response.type("text/html");
                logger.info("route handled");}
                
                catch (Exception e){
                    logger.info("route handle failed");
                }
                
            }
            
            else if(file.isDirectory()){
                logger.info("check if is a directroy");
                if(accessfile(request.pathInfo())){
                
                    System.out.println("This is a directory");
                    response.type("text/html");
                    if(request.requestMethod().equalsIgnoreCase("GET")) {
                   response.status(404);
                    response.body("404 not found");
                    }
                }

                else{
                    response.status(404);
                    response.body("404 not found");
                }
            }
            
            else{
                
                if (accessfile(request.pathInfo())){
                logger.info("check if is a file");
                logger.info(file.isFile());
                    if (file.isFile()){
            			byte[] body = new byte[(int) file.length()];
            			InputStream stream = null;
            			
            			try {
							stream = new FileInputStream(file);
						} catch (FileNotFoundException e1) {
							
							e1.printStackTrace();
						}
	            		try {
	            			stream.read(body);
	            			stream.close();
	            			if(request.requestMethod().equalsIgnoreCase("GET")) {
	            			response.bodyRaw(body);}
							response.type(HttpParsing.getMimeType(request.pathInfo()));
					
							response.status(200);
						} catch (IOException e) {
							
							e.printStackTrace();
						}
            		}
            		else {
                        System.out.println("404");
                        response.status(404);
                         response.body("404 not found");
                    
                }
             
                  
              }
              else{
                  System.out.println("404");
                        response.status(404);
                         response.body("404 not found");
              }
                
            }
    try {applyFilters(httpserver.get_after_filters(),request,response);
                 }
            catch (Exception e){
                logger.info("afterfilter failed");
            }
	    
	    
	    
	}
	
}

         
     }
     
 
 

     
