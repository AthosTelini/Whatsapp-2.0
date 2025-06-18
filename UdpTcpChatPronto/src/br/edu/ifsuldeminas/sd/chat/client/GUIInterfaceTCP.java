package br.edu.ifsuldeminas.sd.chat.client;

import br.edu.ifsuldeminas.sd.chat.ChatException;
import br.edu.ifsuldeminas.sd.chat.MessageContainer;
import br.edu.ifsuldeminas.sd.chat.Sender;
import br.edu.ifsuldeminas.sd.chat.TCPServer;
import br.edu.ifsuldeminas.sd.chat.TCPSender;

import javax.swing.*;
import java.awt.*;

public class GUIInterfaceTCP extends JFrame implements MessageContainer {

    private static final long serialVersionUID = 1L;
    private JTextField txtLocalPort, txtRemotePort, txtUser, txtMessage, txtRemoteIp;
    private JTextArea textAreaChat;
    private JButton btnConnect, btnSend;
    private Sender chatSender;
    private String username;
    private final Color GREEN_BG = new Color(0xA5D6A7);

    public GUIInterfaceTCP() {
        setTitle("Whatsapp 2.0 (TCP - P2P Final)");
        setSize(800, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        initializeLayout();
        initializeEvents();
        getContentPane().setBackground(GREEN_BG);
        setVisible(true);
    }
    
    @Override
    public void setChatSender(Sender sender) {
        this.chatSender = sender;
        // Habilita a UI de envio de mensagens na thread correta
        SwingUtilities.invokeLater(() -> {
            txtMessage.setEnabled(true);
            btnSend.setEnabled(true);
            appendToChat("Sistema", "Canal de envio pronto!");
        });
    }

    private void attemptConnection() {
        try {
            username = txtUser.getText().trim();
            final int local = Integer.parseInt(txtLocalPort.getText().trim());
            final String remoteIp = txtRemoteIp.getText().trim();
            final int remote = Integer.parseInt(txtRemotePort.getText().trim());

            if (username.isEmpty() || remoteIp.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nome, IP e Portas são obrigatórios.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Desabilita campos para evitar novas conexões
            txtUser.setEditable(false);
            txtLocalPort.setEditable(false);
            txtRemoteIp.setEditable(false);
            txtRemotePort.setEditable(false);
            btnConnect.setEnabled(false);

            // A lógica de conexão agora roda em uma thread separada para não travar a GUI
            new Thread(() -> {
                try {
                    // 1. Inicia o servidor local para receber conexões
                    new TCPServer(local, this);
                    
                    // 2. Tenta se conectar ao outro peer (atuando como "cliente")
                    Sender sender = new TCPSender(remoteIp, remote, this);
                    
                    // 3. Se a conexão teve sucesso, configura o sender e avisa a GUI
                    setChatSender(sender);
                    appendToChat("Sistema", "Conexão de envio estabelecida com " + remoteIp);

                } catch (ChatException e) {
                    // Se falhou, não é um erro crítico. Apenas significa que somos o "servidor"
                    // e agora estamos esperando o outro lado se conectar ao nosso TCPServer.
                    appendToChat("Sistema", "Falha ao conectar. Aguardando conexão do outro peer...");
                }
            }).start();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Use apenas números válidos nas portas.", "Erro de Entrada", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void initializeLayout() {
        Font font = new Font("SansSerif", Font.PLAIN, 14);
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(GREEN_BG);
        JPanel panelLeft = new JPanel();
        panelLeft.setLayout(new BoxLayout(panelLeft, BoxLayout.Y_AXIS));
        panelLeft.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panelLeft.setBackground(GREEN_BG);
        Dimension fieldSize = new Dimension(150, 20);
        JLabel lblUser = new JLabel("Nome:");
        JLabel lblLocal = new JLabel("Porta Local:");
        JLabel lblRemoteIp = new JLabel("IP Remoto:");
        JLabel lblRemote = new JLabel("Porta Remota:");
        txtUser = new JTextField();
        txtLocalPort = new JTextField();
        txtRemoteIp = new JTextField("localhost");
        txtRemotePort = new JTextField();
        for (JTextField field : new JTextField[]{txtUser, txtLocalPort, txtRemoteIp, txtRemotePort}) {
            field.setMaximumSize(fieldSize);
            field.setPreferredSize(fieldSize);
            field.setFont(font);
        }
        btnConnect = new JButton("Conectar");
        btnConnect.setFont(font);
        btnConnect.setBackground(new Color(0x1B5E20));
        btnConnect.setForeground(Color.WHITE);
        btnConnect.setFocusPainted(false);
        panelLeft.add(lblUser);
        panelLeft.add(txtUser);
        panelLeft.add(Box.createVerticalStrut(10));
        panelLeft.add(lblLocal);
        panelLeft.add(txtLocalPort);
        panelLeft.add(Box.createVerticalStrut(10));
        panelLeft.add(lblRemoteIp);
        panelLeft.add(txtRemoteIp);
        panelLeft.add(Box.createVerticalStrut(10));
        panelLeft.add(lblRemote);
        panelLeft.add(txtRemotePort);
        panelLeft.add(Box.createVerticalStrut(20));
        panelLeft.add(btnConnect);
        JPanel panelRight = new JPanel(new BorderLayout(10, 10));
        panelRight.setBackground(GREEN_BG);
        panelRight.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        textAreaChat = new JTextArea();
        textAreaChat.setEditable(false);
        textAreaChat.setFont(font);
        textAreaChat.setBackground(GREEN_BG);
        JScrollPane scroll = new JScrollPane(textAreaChat);
        scroll.getViewport().setBackground(GREEN_BG);
        scroll.setBorder(null);
        JPanel panelBottom = new JPanel(new BorderLayout(5, 5));
        panelBottom.setBackground(GREEN_BG);
        txtMessage = new JTextField();
        txtMessage.setFont(font);
        txtMessage.setPreferredSize(new Dimension(0, 22));
        txtMessage.setBackground(Color.WHITE);
        txtMessage.setEnabled(false);
        btnSend = new JButton("Enviar");
        btnSend.setFont(font);
        btnSend.setBackground(new Color(0x0D47A1));
        btnSend.setForeground(Color.WHITE);
        btnSend.setEnabled(false);
        btnSend.setFocusPainted(false);
        panelBottom.add(txtMessage, BorderLayout.CENTER);
        panelBottom.add(btnSend, BorderLayout.EAST);
        panelRight.add(scroll, BorderLayout.CENTER);
        panelRight.add(panelBottom, BorderLayout.SOUTH);
        mainPanel.add(panelLeft, BorderLayout.WEST);
        mainPanel.add(panelRight, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);
    }

    private void initializeEvents() {
        btnConnect.addActionListener(e -> attemptConnection());
        btnSend.addActionListener(e -> sendChatMessage());
        txtMessage.addActionListener(e -> sendChatMessage());
    }

    private void sendChatMessage() {
        String text = txtMessage.getText().trim();
        if (!text.isEmpty() && chatSender != null) {
            try {
                String formatted = text + MessageContainer.FROM + username;
                chatSender.send(formatted);
                appendToChat("Você", text);
                txtMessage.setText("");
            } catch (ChatException e) {
                JOptionPane.showMessageDialog(this, "Erro ao enviar mensagem: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void appendToChat(String sender, String message) {
        SwingUtilities.invokeLater(() -> {
            textAreaChat.append(sender + " > " + message + "\n");
            textAreaChat.setCaretPosition(textAreaChat.getDocument().getLength());
        });
    }

    @Override
    public void newMessage(String fullMessage) {
        if (fullMessage == null || fullMessage.trim().isEmpty()) return;
        if (fullMessage.contains(MessageContainer.FROM)) {
            String[] parts = fullMessage.split(MessageContainer.FROM);
            String msg = parts[0];
            String from = (parts.length > 1) ? parts[1].trim() : "Anônimo";
            appendToChat(from, msg);
        } else {
            appendToChat("Sistema", fullMessage);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(GUIInterfaceTCP::new);
    }
}