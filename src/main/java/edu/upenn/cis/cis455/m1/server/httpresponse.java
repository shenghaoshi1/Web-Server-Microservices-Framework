package edu.upenn.cis.cis455.m1.server;


import java.io.UnsupportedEncodingException;
import java.util.*;
import edu.upenn.cis.cis455.m2.server.interfaces.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class httpresponse extends Response{
    static final Logger logger = LogManager.getLogger(httpresponse.class);
    private HashMap <String,String> headers= new HashMap <String,String>();
    private ArrayList <Cookie> cookies=new ArrayList <Cookie>();
    public  void header(String header, String value){
        this.headers.put(header,value);
    }
    
    /**
     * Trigger an HTTP redirect to a new location
     */
    public  void redirect(String location){
         StringBuilder builder = new StringBuilder();
	            	builder.append("<!DOCTYPE html><html><head><title>Shutdown</title></head><body>");
	            	
	            	builder.append("This is a redirect page to"+location+"</body></html>");
	            	header("Location",location);
	            	body(builder.toString());
	            	type("text/html");
                    status(301);
        
    }
    
    /**
     * Trigger a redirect with a specific HTTP 3xx status code
     */
    public  void redirect(String location, int httpStatusCode){
         StringBuilder builder = new StringBuilder();
	            	builder.append("<!DOCTYPE html><html><head><title>Shutdown</title></head><body>");
	            	
	            	builder.append("This is a redirect page to"+location+"</body></html>");
	            	header("Location",location);
	            	body(builder.toString());
	            	type("text/html");
                    status(httpStatusCode);
    }
    
    public  void cookie(String name, String value){
        cookies.add(new Cookie(name,value));
    }
    
    public  void cookie(String name, String value, int maxAge){
        cookies.add(new Cookie(name,value,maxAge));
    }

    public  void cookie(String name, String value, int maxAge, boolean secured){
        cookies.add(new Cookie(name,value,maxAge,secured));
        
    }

    public  void cookie(String name, String value, int maxAge, boolean secured, boolean httpOnly){
        cookies.add(new Cookie(name,value,maxAge,secured,httpOnly));
        
    }

    public  void cookie(String path, String name, String value){
        cookies.add(new Cookie(path,name,value));
    }
    
    public  void cookie(String path, String name, String value, int maxAge){
        cookies.add(new Cookie(path,name,value,maxAge));
    }

    public  void cookie(String path, String name, String value, int maxAge, boolean secured){
        cookies.add(new Cookie(path,name,value,maxAge,secured));
    }

    public  void cookie(String path, String name, String value, int maxAge, boolean secured, boolean httpOnly){
        cookies.add(new Cookie(path,name,value,maxAge,secured,httpOnly));
    }
    
    public  void removeCookie(String name){
        cookies.add(new Cookie(name,"",0));
        
    }
    
    public  void removeCookie(String path, String name){
        cookies.add(new Cookie(path,name,"",0));
    }
    public String getstatus(){
        if (this.status()==200){
            return "OK";

        }
        if (this.status()==400){
            return "Bad Request";
        }
        if (this.status()==301 || this.status()==302){
            return "redirect page";
        }
        if (this.status()==500){
            return "server error";
        }
        return "Not Found";
    }
    public  String getHeaders(){
        //logger.info("header is");
        int statuscode=this.status();
        logger.info(this.status());
        String res=Integer.toString(statuscode)+" "+this.getstatus()+" \r\n";
        
        for (String key:headers.keySet()){
            if (key!=null & headers.get(key)!=null){
                res=res+key+" : "+headers.get(key)+"\r\n";
            }
            
        }
        if (cookies.size()!=0){
        for (Cookie cookie : cookies) {
            res += cookie.CookietoString();
        }
    }


        
        ZonedDateTime dateTime = ZonedDateTime.now();
        String date = dateTime.format(DateTimeFormatter.RFC_1123_DATE_TIME);
        
        res=res + "Date: " + date + "\r\n";
        if (this.type()!=null){
            res=res+"Content-Type:"+this.type()+"\r\n";
            
        }
        
        res=res+"\r\n";
        logger.info(res);
        System.out.println("res");
        System.out.println(res.length());
        return res;
    }
    class Cookie{
        private String path;
        private String name;
        private String value;
        private int maxAge;
        private boolean secured;
        private boolean httpOnly;
        public Cookie(String name, String value){
            this.name=name;
            this.value=value;
        }
        public Cookie(String name, String value, int maxAge){
            this.name=name;
            this.value=value;
            this.maxAge=maxAge;
        }
        public Cookie(String name, String value, int maxAge, boolean secured){
            this.name=name;
            this.value=value;
            this.maxAge=maxAge;
            this.secured=secured;
            
        }
        public Cookie(String name, String value, int maxAge, boolean secured, boolean httpOnly){
            this.name=name;
            this.value=value;
            this.maxAge=maxAge;
            this.secured=secured;
            this.httpOnly=httpOnly;
            
        }
        public Cookie(String path, String name, String value){
            this.path=path;
            this.name=name;
            this.value=value;
        }
        public Cookie(String path, String name, String value, int maxAge){
            this.path=path;
            this.name=name;
            this.value=value;
            this.maxAge=maxAge;
        }
        public Cookie(String path, String name, String value, int maxAge, boolean secured){
            this.path=path;
            this.name=name;
            this.value=value;
            this.maxAge=maxAge;
            this.secured=secured;
            
        }
        public Cookie(String path, String name, String value, int maxAge, boolean secured, boolean httpOnly){
            this.path=path;
            this.name=name;
            this.value=value;
            this.maxAge=maxAge;
            this.secured=secured;
            this.httpOnly=httpOnly;
        }
        
    public String CookietoString() {
        ZonedDateTime dateTime = ZonedDateTime.now();
        dateTime = dateTime.plusSeconds(maxAge);
        String expireDate = dateTime.format(DateTimeFormatter.RFC_1123_DATE_TIME);
        
        return "Set-Cookie: " + name + "=" + value + 
                ((path == null) ? "" : "; Path=" + path) + 
                ((maxAge == -2) ? "" : "; Expires=" + expireDate) + 
                (!secured ? "" : "; Secure") + 
                (!httpOnly ? "" : "; HttpOnly") + "\r\n";
    }
        
        
    }
    
}