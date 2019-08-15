package edu.upenn.cis.cis455.m1.server;

import edu.upenn.cis.cis455.m1.server.httprequest;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;
import edu.upenn.cis.cis455.TestHelper;
import edu.upenn.cis.cis455.exceptions.HaltException;
import edu.upenn.cis.cis455.m1.server.HttpIoHandler;

import org.apache.logging.log4j.Level;
public class TestRequest_2 {
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
        "Cookie: JSESSIONID=230004200;Weather=snowy;Animal=Dog\r\n" +
        "Connection: Keep-Alive\r\n\r\n";
     @Test
    public void testrequest()throws IOException{
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Socket s = TestHelper.getMockSocket(
            sampleGetRequest, 
            byteArrayOutputStream);
            
        httprequest request=new httprequest(s);
        System.out.println("test JSESSIONID");
        System.out.println(request.cookies());
        assertEquals("method?","230004200",request.session_id);
        
        System.out.println("test cookie-weather");
        assertEquals("weather?","snowy",request.cookie("Weather"));
        
        assertEquals("animal?","Dog",request.cookie("Animal"));
        
        
    }
    @After
    public void tearDown() {}
}