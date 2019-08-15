package edu.upenn.cis.cis455.m1.server;

import java.net.Socket;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import static org.mockito.Mockito.*;


public class HttpTask {
    Socket requestSocket;
    
    public HttpTask(Socket socket) {
        requestSocket = socket;
    }
    
    public Socket getSocket() {
        
        

       
        
        return requestSocket;
    }
}
