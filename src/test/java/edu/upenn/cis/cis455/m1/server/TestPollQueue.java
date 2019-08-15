package edu.upenn.cis.cis455.m1.server;

import edu.upenn.cis.cis455.m1.server.HttpTaskQueue;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import java.util.*;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;
import edu.upenn.cis.cis455.TestHelper;
import edu.upenn.cis.cis455.exceptions.HaltException;
import edu.upenn.cis.cis455.m1.server.HttpIoHandler;

import org.apache.logging.log4j.Level;
public class TestPollQueue{
    private Queue<HttpTask> sharedQueue;
    private HttpTaskQueue httpsharedqueue;
    @Before
    public void setUp() {
        org.apache.logging.log4j.core.config.Configurator.setLevel("edu.upenn.cis.cis455", Level.DEBUG);
    }
    
    String sampleGetRequest = 
        "GET /a/b/hello.htm?q=x&v=12%200 HTTP/1.1\r\n" +
        "User-Agent: Mozilla/4.0 (compatible; MSIE5.01; Windows NT)\r\n" +
        "Host: www.cis.upenn.edu\r\n" +
        "Accept-Language: en-us\r\n" +
        "Accept-Encoding: gzip, deflate\r\n" +
        "Cookie: name1=value1;name2=value2;name3=value3\r\n" +
        "Connection: Keep-Alive\r\n\r\n";
        @Test
    public void testqueue() throws IOException{
        sharedQueue=new LinkedList<>();
        httpsharedqueue=new HttpTaskQueue(sharedQueue);
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Socket s = TestHelper.getMockSocket(
            sampleGetRequest, 
            byteArrayOutputStream);
        
        HttpTask task=new HttpTask(s);
            try{httpsharedqueue.addToQueue(task);}
            catch(InterruptedException e){
                return;}
        
        try{httpsharedqueue. PollFromQueue();}
        catch(InterruptedException e2){
            return;
        }
        System.out.println(httpsharedqueue.size());
        assertEquals("queuesize",httpsharedqueue.size(),0);
        
        
    }
    @After
    public void tearDown() {}
} 