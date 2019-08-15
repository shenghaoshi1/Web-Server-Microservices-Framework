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
import edu.upenn.cis.cis455.ServiceFactory;
import org.apache.logging.log4j.Level;
public class TestServer{
     @Before
    public void setUp() {
        org.apache.logging.log4j.core.config.Configurator.setLevel("edu.upenn.cis.cis455", Level.DEBUG);
    }
    
    @Test
    public void testserver()throws IOException{
         int port=8080;
        String directory="/.www";
        ServiceFactory service=new ServiceFactory();
        service.port(port);
		service.staticFileLocation(directory);
		service.start();
		System.out.println("test dir");
		assertEquals("directory?",service.getdir(),directory);
		System.out.println("test port");
		assertEquals("port?",service.getport(),port);

        
    }
    @After
    public void tearDown() {}
    
}