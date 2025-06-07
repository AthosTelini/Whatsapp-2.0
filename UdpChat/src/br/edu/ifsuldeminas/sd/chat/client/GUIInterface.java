package br.edu.ifsuldeminas.sd.chat.client;

import br.edu.ifsuldeminas.sd.chat.ChatException;
import br.edu.ifsuldeminas.sd.chat.ChatFactory;
import br.edu.ifsuldeminas.sd.chat.MessageContainer;
import br.edu.ifsuldeminas.sd.chat.Sender;

import javax.swing.*;
import java.awt.*;

public class GUIInterface extends JFrame implements MessageContainer {

    private static final long serialVersionUID = 1L;

    private JTextField txtLocalPort, txtRemotePort, txtUser, txtMessage;
    private JTextArea textAreaChat;
    private JButton btnConnect, btnSend;
    private Sender chatSender;
    private String username;

    private final Color GREEN_BG = new Color(0xA5D6A7);

    public GUIInterface() {
        setTitle("Whatsapp 2.0 (Beta)");
        setSize(800, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        initializeLayout();
        initializeEvents();

        getContentPane().setBackground(GREEN_BG);
        setVisible(true);
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
        JLabel lblRemote = new JLabel("Porta Remota:");

        txtUser = new JTextField();
        txtLocalPort = new JTextField();
        txtRemotePort = new JTextField();

        for (JTextField field : new JTextField[]{txtUser, txtLocalPort, txtRemotePort}) {
            field.setMaximumSize(fieldSize);
            field.setPreferredSize(fieldSize);
            field.setFont(font);
        }

        btnConnect = new JButton("Iniciar");
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

    private void attemptConnection() {
        try {
            username = txtUser.getText().trim();
            int local = Integer.parseInt(txtLocalPort.getText().trim());
            int remote = Integer.parseInt(txtRemotePort.getText().trim());

            if (username.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Informe um nome de usuário.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            chatSender = ChatFactory.build("localhost", remote, local, this);

            txtUser.setEditable(false);
            txtLocalPort.setEditable(false);
            txtRemotePort.setEditable(false);
            btnConnect.setEnabled(false);
            txtMessage.setEnabled(true);
            btnSend.setEnabled(true);

            appendToChat("Sistema", "Conexão estabelecida com sucesso!");

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Use apenas números válidos nas portas.", "Erro de Entrada", JOptionPane.ERROR_MESSAGE);
        } catch (ChatException e) {
            JOptionPane.showMessageDialog(this, "Falha ao conectar: " + e.getMessage(), "Erro de Conexão", JOptionPane.ERROR_MESSAGE);
        }
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

        String[] parts = fullMessage.split(MessageContainer.FROM);
        String msg = parts[0];
        String from = (parts.length > 1) ? parts[1].trim() : "Anônimo";

        appendToChat(from, msg);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(GUIInterface::new);
    }
}
