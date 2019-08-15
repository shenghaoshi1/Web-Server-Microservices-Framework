package edu.upenn.cis.cis455;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import edu.upenn.cis.cis455.m2.server.interfaces.WebService;
import edu.upenn.cis.cis455.m1.server.interfaces.HttpRequestHandler;
import edu.upenn.cis.cis455.m2.server.interfaces.Request;
import edu.upenn.cis.cis455.m2.server.interfaces.Response;
import edu.upenn.cis.cis455.m2.server.interfaces.Session;
import edu.upenn.cis.cis455.exceptions.HaltException;
import edu.upenn.cis.cis455.handlers.Filter;
import edu.upenn.cis.cis455.handlers.Route;
import edu.upenn.cis.cis455.m1.server.HttpServer;
import edu.upenn.cis.cis455.m1.server.httpsession;
import edu.upenn.cis.cis455.m1.server.httpresponse;


public class ServiceFactory extends WebService{
        private int port=8080;
        private String directory="/projects/555-hw1/www";
        private int numberofthread=10;
        private String ip="0.0.0.0";
        private HashMap <String, httpsession> sessions=new HashMap <String, httpsession>();
        private HttpServer basicServer;
        
        /**
     * Launches the Web server thread pool and the listener
     */
    public  void start(){
        this.basicServer=new HttpServer(this.port,10,this.numberofthread,this.directory);
        this.basicServer.start();
        
    }
    public HttpServer getserver(){
        return this.basicServer;
    }
    public String getdir(){
        return this.basicServer.getdirectory();
    }
    public int getport(){
        return this.port;
    }
    /**
     * Gracefully shut down the server
     */
    public void stop(){
        this.basicServer.shutdown();
        
    }
    
    /**
     * Set the root directory of the "static web" files
     */
    public  void staticFileLocation(String directory){
        this.directory=directory;
        
    }
    
    ///////////////////////////////////////////////////
    // For more advanced capabilities
    
    /**
     * Handle an HTTP GET request to the path
     */
    public  void get(String path, Route route){
        this.basicServer.addRoute("GET",path,route);
        
 }

    ////////////////////////////////////////////
    // Server configuration
    ////////////////////////////////////////////
    
    /**
     * Set the IP address to listen on (default 0.0.0.0)
     */
    public void ipAddress(String ipAddress){
        this.ip=ipAddress;

    }
   
    
    /**
     * Set the TCP port to listen on (default 80)
     */
    public  void port(int port){
        this.port=port;
        
    }
    
    /**
     * Set the size of the thread pool
     */
    public  void threadPool(int threads){
        this.numberofthread=threads;
        
    }
    



    
     /**
     * Get the HTTP server associated with port 8080
     */
    
    public static WebService getServerInstance() {
        return null;
    }
    
    /**
     * Create an HTTP request given an incoming socket
     */
    public static Request createRequest(Socket socket,
                         String uri,
                         boolean keepAlive,
                         Map<String, String> headers,
                         Map<String, List<String>> parms) {
        return null;
    }
    
    /**
     * Gets a request handler for files (i.e., static content) or dynamic content
     */
    public static HttpRequestHandler createRequestHandlerInstance(Path serverRoot) {
        return null;
    }

    /**
     * Gets a new HTTP Response object
     */
    public static Response createResponse() {
        httpresponse response=new httpresponse();
        return response;
    }

    /**
     * Creates a blank session ID and registers a Session object for the request
     */
    public static String createSession() {
        
        return null;
    }
    
    /**
     * Looks up a session by ID and updates / returns it
     */
    public static Session getSession(String id) {
        
        return null;
    }
     /**
     * Handle an HTTP POST request to the path
     */
    public  void post(String path, Route route){
        this.basicServer.addRoute("POST",path,route);
        
    }

    /**
     * Handle an HTTP PUT request to the path
     */
    public  void put(String path, Route route){
        this.basicServer.addRoute("PUT",path,route);
    }

    /**
     * Handle an HTTP DELETE request to the path
     */
    public  void delete(String path, Route route){
        this.basicServer.addRoute("DELETE",path,route);
        
    }

    /**
     * Handle an HTTP HEAD request to the path
     */
    public  void head(String path, Route route){
        this.basicServer.addRoute("HEAD",path,route);
        
    }

    

    /**
     * Handle an HTTP OPTIONS request to the path
     */
    public void options(String path, Route route){
        this.basicServer.addRoute("OPTIONS",path,route);
        
    }
    
    ///////////////////////////////////////////////////
    // HTTP request filtering
    ///////////////////////////////////////////////////
    
    /**
     * Add filters that get called before a request
     */
    public  void before(Filter filter){
        this.basicServer.add_before_filter("","",filter);
        
    }

    /**
     * Add filters that get called after a request
     */
    public void after(Filter filter){
        this.basicServer.add_after_filter("","",filter);
        
    }
    /**
     * Add filters that get called before a request
     */
    public void before(String path, String acceptType, Filter filter){
         this.basicServer.add_before_filter(path,acceptType,filter);
        
    }
    /**
     * Add filters that get called after a request
     */
    public  void after(String path, String acceptType, Filter filter){
        this.basicServer.add_after_filter(path,acceptType,filter);
        
    }
    
}