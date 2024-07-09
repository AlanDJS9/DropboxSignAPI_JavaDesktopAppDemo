import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class ServerActions {
    private  ServerManager serverManager;

    public  JPanel createServerPanel() {
        JPanel serverPanel = new JPanel();
        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);

        JButton startServerButton = new JButton("Start Server");
        JButton stopServerButton = new JButton("Stop Server");
        JTextField portField = new JTextField(5);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(new JLabel("Port:"));
        buttonPanel.add(portField);
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(startServerButton);
        buttonPanel.add(stopServerButton);

        serverPanel.setLayout(new BorderLayout());
        serverPanel.add(buttonPanel, BorderLayout.NORTH);
        serverPanel.add(scrollPane, BorderLayout.CENTER);

        startServerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int port = Integer.parseInt(portField.getText());
                    serverManager = new ServerManager(port, textArea);
                    serverManager.startServer();
                } catch (NumberFormatException ex) {
                    textArea.append("Invalid port number.\n");
                } catch (IOException ioException) {
                    textArea.append("Failed to start server: " + ioException.getMessage() + "\n");
                }
            }
        });

        stopServerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (serverManager != null) {
                    serverManager.stopServer();
                } else {
                    textArea.append("Server is not running.\n");
                }
            }
        });

        return serverPanel;
    }
}
