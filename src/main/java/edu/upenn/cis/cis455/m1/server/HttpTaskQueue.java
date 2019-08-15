package edu.upenn.cis.cis455.m1.server;
import java.util.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Stub class for implementing the queue of HttpTasks
 */
public class HttpTaskQueue extends Thread{
   static final Logger logger = LogManager.getLogger(HttpTaskQueue.class);
   private  Queue<HttpTask> sharedQueue;
   
   public HttpTaskQueue(Queue<HttpTask> sharedQueue) {
		this.sharedQueue = sharedQueue;
		
	}
	public synchronized void addToQueue(HttpTask task) throws InterruptedException {
		logger.info("[Output from log4j] Adding element to queue");//This would be logged in the log file created and to the console.
		//wait if the queue is full
		

			

			    //Adding element to queue and notifying all 
			    
			    sharedQueue.add(task);
			    
			    logger.info("[Output from log4j] an element added to queue");
			    this.notifyAll();
			    
		    
		
	}
	public int size(){
	    return sharedQueue.size();
	}
	public synchronized HttpTask PollFromQueue() throws InterruptedException
	
	{HttpTask task=null;
	    while (true){
	    if (sharedQueue.size()==0){
	        System.out.println("Queue is currently empty ");
    		logger.info("Queue is currently empty");
    		 try{
    		     wait();
    		 }
    		 catch(InterruptedException error){
    		     logger.info("wait error");
    		 }
	    }
	    else {
	        logger.debug("Notifying everyone we are removing an item");
    		 
	         task=sharedQueue.remove();
	         this.notifyAll();
	         //notifyAll();
	        return task;
	    
	    
	}
}}
}



