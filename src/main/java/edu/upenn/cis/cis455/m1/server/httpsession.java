package edu.upenn.cis.cis455.m1.server;
import java.io.UnsupportedEncodingException;
import java.util.*;
import edu.upenn.cis.cis455.m2.server.interfaces.Session;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.stream.Collectors;
public class httpsession extends Session{
    private String ID;
    private long creationTime;
    private long lastAccessedTime;
    private boolean valid;
    private int maxInactiveInterval;
    private HashMap<String, Object> attributes;
    
    public httpsession(){
        this.ID=generate_id();
        this.creationTime=generate_time();
        this.lastAccessedTime=generate_time();
        this.valid=true;
        this.maxInactiveInterval = -1;
        this.attributes = new HashMap<String, Object>();
    }
    public boolean valid_state(){
        return this.valid;
    }
    
    public String generate_id(){
        int length = 20;
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                        + "abcdefghijklmnopqrstuvwxyz"
                        + "0123456789";
        String str = new Random().ints(length, 0, chars.length())
                         .mapToObj(i -> "" + chars.charAt(i))
                         .collect(Collectors.joining());
        return str;
                         
    }
    public long generate_time(){
        return System.currentTimeMillis();
    }
    
    
     public  String id(){
         return this.ID;
         
     }
    
    /**
     * Time the session was created
     */
    public  long creationTime(){
        return this.creationTime;
        
    }
    
    /**
     * Time the session was last accessed
     */
    public long lastAccessedTime(){
        return this.lastAccessedTime;
        
    }
    
    /**
     * Invalidate the session
     */
    public  void invalidate(){
        this.valid=false;
        
    }
    
    /**
     * Get the inactivity timeout
     */
    public  int maxInactiveInterval(){
        return this.maxInactiveInterval;
        
    }
    
    /**
     * Set the inactivity timeout
     */
    public  void maxInactiveInterval(int interval){
        this.maxInactiveInterval=interval;
        
    }
    
    /**
     * Notify the session that it was just accessed -- should
     * update the last accessed time
     */
    public  void access(){
        this.lastAccessedTime=generate_time();
        
    }
    
    
    /**
     * Store an object under the name
     */
    public void attribute(String name, Object value){
        this.attributes.put(name,value);
        
    }
    
    /**
     * Get an object associatd with the name
     */
    public  Object attribute(String name){
        return this.attributes.get(name);
        
    }
    
    /**
     * Get all objects bound to the session
     */
    public  Set<String> attributes(){
        return this.attributes.keySet();
        
    }
    
    /**
     * Delete an object from the session
     */
    public  void removeAttribute(String name){
        this.attributes.remove(name);
        
    }
}