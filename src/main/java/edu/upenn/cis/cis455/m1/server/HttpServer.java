package edu.upenn.cis.cis455.m1.server;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.io.IOException;
import java.net.SocketException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import edu.upenn.cis.cis455.handlers.Filter;
import edu.upenn.cis.cis455.handlers.Route;
/**
 * Stub for your HTTP server, which
 * listens on a ServerSocket and handles
 * requests
 */
public class HttpServer extends Thread implements ThreadManager  {
    public boolean running=true;
    public ServerSocket server;
    public int queuesize;
    public int threadsize;
    private Queue<HttpTask> sharedQueue;
    private HttpTaskQueue httpsharedqueue;
    public ArrayList<HttpWorker>threads=new ArrayList<HttpWorker>();
    protected int[] threadstatus;
    private String [] threadcontent;
    private Socket tmpSocket;
    private String directory;
    private HashMap <String,HashMap <String, Route >> routes;
    private ArrayList<Filterobject> before_filters;
    private ArrayList<Filterobject> after_filters;
    private HashMap <String, httpsession> sessions;
    static final Logger logger = LogManager.getLogger(HttpServer.class);
   
    public HttpServer(int port, int queuesize, int threadsize, String directory){
        this.threadstatus=new int[threadsize];
        this.threadcontent=new String[threadsize];
        try {
			this.server = new ServerSocket(port,1000);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
        this.queuesize=queuesize;
        this.sharedQueue=new LinkedList<>();
        this.httpsharedqueue=new HttpTaskQueue(sharedQueue);
        this.threadsize=threadsize;
        this.directory=directory;
        this.routes=new HashMap <String,HashMap <String, Route >>();
        this.routes.put("GET", new HashMap<String,Route>());
        this.routes.put("PUT", new HashMap<String,Route>());
        this.routes.put("DELETE",new HashMap<String,Route>());
        this.routes.put("OPTIONS",new HashMap<String,Route>());
        this.routes.put("POST",new HashMap<String,Route>());
        this.routes.put("HEAD",new HashMap<String,Route>());
        this.before_filters=new ArrayList <Filterobject>();
        this.after_filters=new ArrayList <Filterobject>();
        this.sessions=new HashMap <String,httpsession>();
        
        
    }
    public httpsession get_session(String sessionid){
        return this.sessions.getOrDefault(sessionid,null);
    }
    public  void add_session(String sessionid, httpsession session){
        this.sessions.put(sessionid, session);
    }
    public void remove_session(String sessionid){
        this.sessions.remove(sessionid);
    }
    public ArrayList <Filterobject> get_before_filters(){
        return this.before_filters;
    }
    public ArrayList <Filterobject> get_after_filters(){
        return this.after_filters;
    }
    public void add_before_filter(String path, String type, Filter filter){
        this.before_filters.add(new Filterobject(path,type,filter));
    }
     public void add_after_filter(String path, String type, Filter filter){
        this.after_filters.add(new Filterobject(path,type,filter));
    }
    
    public HashMap<String, Route> getRoutes(String type_key){
        return this.routes.get(type_key);
    }
    public void addRoute(String type_key, String path, Route route){
        this.routes.get(type_key).put(path,route);
    }
    
    public void shutdown(){
        this.running=false;
        try{
        this.server.close();
        System.out.println("shutdown");}
        catch(IOException e){
            return;
        }
        for (int i=0;i<threadstatus.length;i++){
            if (threadstatus[i]==0){
                threadstatus[i]=-1;
            }
        }
    }
    public void setrunningstatus(boolean set){
        this.running=set;
    }
    public int getthreadsize(){
		return threadsize;
	}
    public String getdirectory(){
        return directory;
    }
    public String getworkerstatus(int i){
		if( threadstatus[i]==1){
			return threadcontent[i];
		}
		return "not working";

	}
	public void setworkerstatus(int i, int status){
		threadstatus[i]=status;

	}
	public void setworkercontent(int i, String uri){
	    threadcontent[i]=uri;
	}
	@Override
	public HttpTaskQueue getRequestQueue() {
	    return this.httpsharedqueue;
    }

    
    public void addQueue() {
        while (this.httpsharedqueue.size() == queuesize) {
            logger.info("queue is full");
			
				return;
			
		}		
		
    try{
			tmpSocket = server.accept();
			logger.info("accept a socket");
			}
		catch (IOException e) {
			return ;
		}
		
		    HttpTask tmptask=new HttpTask(tmpSocket);
		    try{
			this.httpsharedqueue.addToQueue(tmptask);
			
			}
			catch (InterruptedException e) {
					e.printStackTrace();
				}
			
		
        
        
       
        return ;
    }
    public void run(){
        
        
    for (int i=0; i<threadsize;i++){
        
        HttpWorker worker = new HttpWorker(i,this.httpsharedqueue,this);
		threads.add(worker);	
		logger.info("a worker created");
    }
    for (int i=0;i<threads.size();i++){
        try {
				threads.get(i).start();
				
			} catch (Exception e) {
				e.printStackTrace();
			}
        
    }
    while (running){
        
        addQueue(); 				

		logger.info(this.httpsharedqueue.size());
    }
    if (this.running!=true){
        for (int i=0;i<threads.size();i++){
        if (threadstatus[i]!=-1){
        try {   
				threads.get(i).interrupt();
				System.out.println("interrupt a worker");
			} catch (Exception e) {
				e.printStackTrace();
			

    }}
}

 

    }
    }
    @Override
    public boolean isActive() {
        
        return running;
    }

    @Override
    public void start(HttpWorker worker) {
        this.setworkerstatus(worker.getworkerid(),1);
        String numberAsString = Integer.toString(worker.getworkerid());
        System.out.println("worker "+numberAsString+" is started");
    }

    @Override
    public void done(HttpWorker worker) {
        this.setworkerstatus(worker.getworkerid(),0);
        String numberAsString = Integer.toString(worker.getworkerid());
        System.out.println("worker "+numberAsString+" is done");
        
    }

    @Override
    public void error(HttpWorker worker) {
       this.setworkerstatus(worker.getworkerid(),-1);
       String numberAsString = Integer.toString(worker.getworkerid());
       System.out.println("worker "+numberAsString+" has an error");
        
    } 
    class Filterobject{
        private Filter filter;
        private String path;
        private String type;
        public Filterobject(String path, String type, Filter filter ){
            this.filter=filter;
            this.path=path;
            this.type=type;
        }
        public Filter get_filter(){
            return this.filter;
        }
        public String get_path(){
            return this.path;
        }
        public String get_type(){
            return this.type;
        }
    }
    
    
   }

