package br.edu.ifsuldeminas.sd.chat.client;

import java.util.Scanner;
import br.edu.ifsuldeminas.sd.chat.ChatException;
import br.edu.ifsuldeminas.sd.chat.ChatFactory;
import br.edu.ifsuldeminas.sd.chat.MessageContainer;
import br.edu.ifsuldeminas.sd.chat.Protocol; // Importando o nosso enum
import br.edu.ifsuldeminas.sd.chat.Sender;

public class Chat {
	public static String KEY_TO_EXIT = "q";
	public static int RECEIVER_BUFFER_SIZE = 1000;

	public static void main(String[] args) {
		Scanner reader = new Scanner(System.in);

		// Pergunta ao usuário qual protocolo usar
		System.out.print("Escolha o protocolo (1 para UDP, 2 para TCP): ");
		int choice = reader.nextInt();
		Protocol protocol = (choice == 2) ? Protocol.TCP : Protocol.UDP;
		
		System.out.print("Porta local: ");
		int localPort = reader.nextInt();
		System.out.print("Porta remota: ");
		int serverPort = reader.nextInt();
		// Para limpar o buffer
		reader.nextLine();
		System.out.print("Nome: ");
		String from = reader.nextLine();
		try {
			// A chamada foi corrigida para enviar o protocolo escolhido
			Sender sender = ChatFactory.build(protocol, "localhost", serverPort, localPort, new SysOutContainer());
			
			System.out.println("Chat iniciado no protocolo " + protocol + ". Digite 'q' para sair.");

			String message = "";
			while (!message.equalsIgnoreCase(KEY_TO_EXIT)) {
				message = reader.nextLine();
				if (message.equalsIgnoreCase(KEY_TO_EXIT)) {
					break;
				}

				String formattedMessage = String.format("%s%s%s", message, MessageContainer.FROM, from);
				sender.send(formattedMessage);
			}
		} catch (ChatException chatException) {
			System.err.printf("Houve algum erro no chat. Mensagem do erro: %s",
					chatException.getMessage());
			if (chatException.getCause() != null) {
				System.err.printf("\nCausa: %s", chatException.getCause().getMessage());
			}
		} finally {
			System.out.println("Encerrando o chat.");
			reader.close();
			System.exit(0);
		}
	}
}