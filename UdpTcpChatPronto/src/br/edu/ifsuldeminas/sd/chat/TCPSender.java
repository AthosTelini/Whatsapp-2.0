package br.edu.ifsuldeminas.sd.chat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class TCPSender implements Sender {
    private DataOutputStream outputFlow;

    // Construtor para o "CLIENTE": inicia uma nova conexão E COMEÇA A OUVIR
    public TCPSender(String receiverIp, int receiverPort, final MessageContainer container) throws ChatException {
        try {
            Socket socket = new Socket(receiverIp, receiverPort);
            this.outputFlow = new DataOutputStream(socket.getOutputStream());

            // --- INÍCIO DA LÓGICA DE ESCUTA (A PARTE QUE FALTA NO SEU) ---
            new Thread(() -> {
                try {
                    DataInputStream inputFlow = new DataInputStream(socket.getInputStream());
                    while (true) {
                        String message = inputFlow.readUTF();
                        // Entrega a mensagem recebida para a GUI
                        container.newMessage(message);
                    }
                } catch (IOException e) {
                    container.newMessage("Sistema> Conexão com o peer foi perdida.");
                }
            }).start();
            // --- FIM DA LÓGICA DE ESCUTA ---

        } catch (UnknownHostException e) {
            throw new ChatException("Host desconhecido: " + receiverIp, e);
        } catch (IOException e) {
            throw new ChatException("Não foi possível conectar ao peer em " + receiverIp + ":" + receiverPort, e);
        }
    }

    // Construtor para o "SERVIDOR": usa uma conexão que já foi aceita
    public TCPSender(Socket socket) throws ChatException {
        try {
            this.outputFlow = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            throw new ChatException("Não foi possível obter o fluxo de saída do socket.", e);
        }
    }

    @Override
    public void send(String message) throws ChatException {
        try {
            outputFlow.writeUTF(message);
        } catch (IOException e) {
            throw new ChatException("Houve um erro ao enviar sua mensagem.", e);
        }
    }
}