package br.edu.ifsuldeminas.sd.chat;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class ChatFactory {

    private static int DEFAULT_RECEIVER_BUFFER_SIZE = 1000;

    public static Sender build(Protocol protocol, String serverName, int serverPort, int localPort, MessageContainer container)
            throws ChatException {
        
	    	switch (protocol) {
	        case TCP:
	            // No novo modelo, a fábrica também inicia um receptor...
	            new TCPServer(localPort, container);
	            // ...e retorna um emissor.
	            return new TCPSender(serverName, serverPort, container);
	
	        case UDP:
	        default:
	            try {
	                new UDPReceiver(localPort, DEFAULT_RECEIVER_BUFFER_SIZE, container);
	                return new UDPSender(InetAddress.getByName(serverName), serverPort);
	            } catch (UnknownHostException unknownHostException) {
	                throw new ChatException("Servidor não conhecido.", unknownHostException);
	            }
	    }
    }
}