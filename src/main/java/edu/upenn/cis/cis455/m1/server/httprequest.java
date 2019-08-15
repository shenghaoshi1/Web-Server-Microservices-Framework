package edu.upenn.cis.cis455.m1.server;
import java.util.*;
import edu.upenn.cis.cis455.m2.server.interfaces.Request;
import java.net.Socket;
import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import edu.upenn.cis.cis455.util.HttpParsing;
import edu.upenn.cis.cis455.m2.server.interfaces.Session;
import edu.upenn.cis.cis455.WebServiceController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

class httprequest extends Request{
    
    final static Logger logger = LogManager.getLogger(httprequest.class);
    
    
    public String directory;
    private String host;
    private int port;
    private String path_info;
    private String url;
    private String uri;
    private String content_type;
    private String body;
    private int contentlength;
    public Map<String, String> headers;
    private Map<String,List<String>> parms;
    private Map<String, String> pre;
    
    private Map<String, Object> attributes;
    public String session_id=null;
    
    
     /**
     * The request method (GET, POST, ...)
     */
    public  String requestMethod(){
        return this.headers.get("Method");
    }

    /**
     * @return The host
     */
    public String host(){
        return this.host;
        
    }
    
    /**
     * @return The user-agent
     */
    public String userAgent(){
        return this.headers.get("user-agent");
        
    }
    
    /**
     * @return The server port
     */
    public  int port(){
        return this.port;
        
    }
    
    /**
     * @return The path
     */
    public  String pathInfo(){
        //return this.pre.get("uri");
        return this.uri;
    }
    
    /**
     * @return The URL
     */
    public  String url(){
        String url="http://localhost:"+host()+this.uri;
        return url;
    }
    
    /**
     * @return The URI up to the query string
     */
    public  String uri(){
        return this.uri;
        //return this.pre.get("uri");
        
    }
    
    /**
     * @return The protocol name and version from the request
     */
    public String protocol(){
        return this.headers.get("protocolVersion");
        
    }

    /**
     * @return The MIME type of the body
     */
    public  String contentType(){
        return this.content_type;
        
    }
    
    /**
     * @return The client's IP address
     */
    public  String ip(){
        
        return this. headers.get("http-client-ip");
}
    
    /**
     * @return The request body sent by the client
     */
    public  String body(){
        return this.body;
        
    }
    
    /**
     * @return The length of the body
     */
    public  int contentLength(){
        return this.contentlength;
        
    }
    
    /**
     * @return Get the item from the header
     */
    public  String headers(String name){
        return this.headers.get(name);
        
    }
    
    public  Set<String> headers(){
        return this.headers.keySet();
        
    }
    
    /**
     * @return Gets the session associated with this request
     */
    public  Session session(){
        String request_cookie_id=this.session_id;
        if (request_cookie_id==null){
            if (this.session_id==null)
            return null;
        }
        
        httpsession session=WebServiceController.getserver().getserver().get_session(request_cookie_id);
        if (session==null){
            return null;
        }
        if (session.valid_state()) {
                session.access();
                return session;}
        WebServiceController.getserver().getserver().remove_session(request_cookie_id);
        return null;
        
    }
    
    /**
     * @return Gets or creates a session for this request
     */
    public  Session session(boolean create){
        if (create){
             httpsession session=new httpsession();
            if (this.session_id==null){
               this.session_id=session.id();
                
            }
            WebServiceController.getserver().getserver().add_session(this.session_id,session);
            return session;
        }
       else if (this.session_id!=null){
           httpsession session=WebServiceController.getserver().getserver().get_session(this.session_id);
           return session;
           
       }
       return null;
        
    }
    
    /**
     * @return  a map containing the route parameters
     */
    public  Map<String, String> params(){
        HashMap <String,String> map=new HashMap <String,String>();
        for (String key:parms.keySet()){
            map.put(key,parms.get(key).get(0));
        }
        return map;
        // todo
    }
    /**
     * @return Query parameter from the URL
     */
    public  String queryParams(String param){
        return this.parms.get(param).get(0);
        
    }
     /**
     * @return Get the list of values for the query parameter
     */
    public  List<String> queryParamsValues(String param){
        return this.parms.get(param);
        
    }
    
    public  Set<String> queryParams(){
        return this.parms.keySet();
        
    }
    
    /**
     * @return The raw query string
     */
    public  String queryString(){
        if (this.uri.contains("?")){
            String [] split=this.uri.split("\\?");
            String querystring="?"+split[1];
            return querystring;
        }
        return "";
        
    }
    
    /**
     * Add an attribute to the request (eg in a filter)
     */
    public  void attribute(String attrib, Object val){
        this.attributes.put(attrib,val);
        
    }
    
    /**
     * @return Gets an attribute attached to the request
     */
    public  Object attribute(String attrib){
        return this.attributes.get(attrib);
        
    }
    
    /**
     * @return All attributes attached to the request
     */
    public  Set<String> attributes(){
        return this.attributes.keySet();
        
    }
    
    public  Map<String, String> cookies(){
        HashMap<String,String> cookie_map=new HashMap<String,String>();
        //System.out.println(this.headers);
        String cookiestring=this.headers.get("cookie");
        if (cookiestring==null){
            return cookie_map;
        }
        String[] parts = cookiestring.split(";");
                for (String part : parts) {
                    String[] cookie_part = part.split("=");
                    cookie_map.put(cookie_part[0], cookie_part[1]);
                }
                return cookie_map;

        
    }
   
    
    public httprequest (Socket socket){
        try {
            InputStream inputstream = socket.getInputStream();
            String ip = socket.getRemoteSocketAddress().toString();
            InputStreamReader inputstreamreader = new InputStreamReader(socket.getInputStream());
            BufferedReader bufferReader = new BufferedReader(inputstreamreader);
            this.port = socket.getLocalPort();
            this.host = socket.getLocalAddress().toString();
            this.persistentConnection(false);
            this.headers = new HashMap<String, String>();
            this.parms = new HashMap<String, List<String>>();
            this.pre=new HashMap<String, String>();
            this.attributes=new HashMap <String, Object>();
            //HttpParsing.decodeHeader(bufferReader,this.pre,this.parms,this.headers);
            this.uri = HttpParsing.parseRequest(ip, inputstream, this.headers, this.parms);
            if (cookie("JSESSIONID")!=null)
            {this.session_id=cookie("JSESSIONID");}
        } catch (IOException e) {
            logger.error(e);
        }           
        
        
        
    }
    
    
}