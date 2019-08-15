package edu.upenn.cis.cis455.m1.server;
import java.net.Socket;
import java.util.*;
import java.io.InputStream;
import edu.upenn.cis.cis455.util.HttpParsing;
import java.io.IOException;
import edu.upenn.cis.cis455.exceptions.HaltException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
/**
 * Stub class for a thread worker for
 * handling Web requests
 */
 public class HttpWorker extends Thread{
        private Map<String, String> headers;
        private Map<String, List<String>> parms;
         private final int workerid;
         private boolean isrunning;
         private HttpTaskQueue httpsharedqueue;
         private HttpServer hpserver;
         final static Logger  logger = LogManager.getLogger(HttpWorker.class);
         public HttpWorker(int id, HttpTaskQueue httpsharedqueue, HttpServer hpserver){
             
             isrunning=true;
             this.httpsharedqueue=httpsharedqueue;
             this.workerid=id;
             this.hpserver=hpserver;
         }
         public  void run(){
             HttpTask task;
             while(isrunning){
                 synchronized (this.httpsharedqueue){
                 
        
                 try{
               
				task=httpsharedqueue.PollFromQueue();
				this.hpserver.start(this); 
			  logger.info("have poll a task");
				}

				catch (InterruptedException e){
				    e.printStackTrace();
				    return;
				}
				}
				
				
				if(task == null){
				    this.hpserver.error(this); 
					return;
				}
				
				Socket socket=task.getSocket();
				httprequest request=null;
				logger.info("get a socket");
				try{
				    request=new httprequest(socket);
				    hpserver.setworkercontent(this.workerid,request.uri());
				    logger.info("create a request");
				    
				}
				catch(HaltException except){
				   logger.debug("cannot create a httprequest");
				    HttpIoHandler.sendException(socket, request, except);
				    hpserver.error(this);
				    return;
				}
				httpresponse response=new httpresponse();
				requesthandler handler=new requesthandler(request, response, hpserver);
				//System.out.println(response.getHeaders());
				
				try{
				    HttpIoHandler.sendResponse(socket,request,response);
				    logger.info("Response sent");
				}
				catch(HaltException except){
				    logger.debug("cannot send response");
				    HttpIoHandler.sendException(socket, request, except);
				    hpserver.error(this);
				    return;
				 
				}
				 
				 hpserver.done(this);
				 
				
			
            
}
			}
			
		public void shutdown(){
		    isrunning=false;
		}
		public int getworkerid(){
		    return this.workerid;
		}
		public boolean status()
		{
		    return isrunning;
		}

             
         }

         
    
	

