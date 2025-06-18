package br.edu.ifsuldeminas.sd.chat;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer implements Receiver {
    private int portNumber;
    private MessageContainer container;

    public TCPServer(int portNumber, MessageContainer container) {
        this.portNumber = portNumber;
        this.container = container;
        new Thread(this).start();
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(this.portNumber)) {
            container.newMessage("Sistema> Servidor pronto na porta " + this.portNumber + ". Aguardando conexão...");

 
            Socket clientSocket = serverSocket.accept();
            container.newMessage("Sistema> Peer conectado! A comunicação já pode ser iniciada.");


            try {

                Sender sender = new TCPSender(clientSocket);

                container.setChatSender(sender);
            } catch (ChatException e) {
                container.newMessage("Sistema> Erro ao criar canal de resposta: " + e.getMessage());
            }
           
            DataInputStream inputFlow = new DataInputStream(clientSocket.getInputStream());
            while (true) {
                String message = inputFlow.readUTF();
                container.newMessage(message);
            }
        } catch (IOException e) {
            container.newMessage("Sistema> Conexão com o peer foi perdida.");
        }
    }
}