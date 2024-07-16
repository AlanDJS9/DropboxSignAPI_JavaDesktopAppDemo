import com.dropbox.sign.ApiException;
import com.dropbox.sign.api.EmbeddedApi;
import com.dropbox.sign.api.SignatureRequestApi;
import com.dropbox.sign.model.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.Toolkit;
import java.net.URI;
import java.util.Arrays;

public class TestSimulation extends JPanel {

    private JCheckBox embeddedCheckBox;
    private JTextField nameField;
    private JTextField emailField;
    private JTextField titleField;
    private JTextField subjectField;
    private JTextField messageField;
    private JLabel fileLabel;
    private JLabel signatureRequestIdLabel;
    private JLabel signatureIdLabel;
    private JLabel signUrlLabel;
    private File selectedFile;
    private SignatureRequestApi signatureRequestApi;
    private EmbeddedApi embeddedApi;
    private String clientId;

    public TestSimulation(SignatureRequestApi signatureRequestApi, EmbeddedApi embeddedApi, String clientId) {
        this.signatureRequestApi = signatureRequestApi;
        this.embeddedApi = embeddedApi;
        this.clientId = clientId;

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Checkbox for embedded signature
        embeddedCheckBox = new JCheckBox("Embedded Signature");
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.NORTHEAST;
        add(embeddedCheckBox, gbc);

        // File chooser button
        JButton fileButton = new JButton("Select PDF");
        fileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("PDF Documents", "pdf"));
                int returnValue = fileChooser.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    selectedFile = fileChooser.getSelectedFile();
                    fileLabel.setText("Selected File: " + selectedFile.getName());
                }
            }
        });
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        add(fileButton, gbc);

        // Label to display selected file
        fileLabel = new JLabel("No file selected");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        add(fileLabel, gbc);

        // Title field
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        add(new JLabel("Title: "), gbc);

        titleField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 2;
        add(titleField, gbc);

        // Subject field
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        add(new JLabel("Subject: "), gbc);

        subjectField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 3;
        add(subjectField, gbc);

        // Message field
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        add(new JLabel("Message: "), gbc);

        messageField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 4;
        add(messageField, gbc);

        // Signer name field
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.WEST;
        add(new JLabel("Signer Name: "), gbc);

        nameField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 5;
        add(nameField, gbc);

        // Signer email field
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.anchor = GridBagConstraints.WEST;
        add(new JLabel("Signer Email: "), gbc);

        emailField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 6;
        add(emailField, gbc);

        // Submit signature button
        JButton submitButton = new JButton("Submit Signature");
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendSignatureRequest();
            }
        });
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(submitButton, gbc);

        // Labels to display signature request ID and signature ID
        signatureRequestIdLabel = new JLabel("Signature Request ID: N/A");
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(signatureRequestIdLabel, gbc);

        signatureIdLabel = new JLabel("Signature ID: N/A");
        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(signatureIdLabel, gbc);

        // Buttons to copy IDs to clipboard
        JButton copySignatureRequestIdButton = new JButton("Copy Signature Request ID");
        copySignatureRequestIdButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                copyToClipboard(signatureRequestIdLabel.getText().replace("Signature Request ID: ", ""));
            }
        });
        gbc.gridx = 0;
        gbc.gridy = 10;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(copySignatureRequestIdButton, gbc);

        JButton copySignatureIdButton = new JButton("Copy Signature ID");
        copySignatureIdButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                copyToClipboard(signatureIdLabel.getText().replace("Signature ID: ", ""));
            }
        });
        gbc.gridx = 0;
        gbc.gridy = 11;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(copySignatureIdButton, gbc);

        // Button to get sign URL
        JButton getSignUrlButton = new JButton("Get Sign URL");
        getSignUrlButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                getSignUrl();
            }
        });
        gbc.gridx = 0;
        gbc.gridy = 12;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(getSignUrlButton, gbc);

        // Label to display sign URL
        signUrlLabel = new JLabel("Sign URL: N/A");
        gbc.gridx = 0;
        gbc.gridy = 13;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(signUrlLabel, gbc);

        // Button to copy sign URL to clipboard
        JButton copySignUrlButton = new JButton("Copy Sign URL");
        copySignUrlButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                copyToClipboard(signUrlLabel.getText().replace("Sign URL: ", ""));
            }
        });
        gbc.gridx = 0;
        gbc.gridy = 14;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(copySignUrlButton, gbc);

        // Button to copy Client ID to clipboard
        JButton copyClientIdButton = new JButton("Copy Client ID");
        copyClientIdButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                copyToClipboard(clientId);
            }
        });
        gbc.gridx = 0;
        gbc.gridy = 15;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(copyClientIdButton, gbc);

        // Button to open embedded testing tool
        JButton openTestingToolButton = new JButton("Open Embedded Testing Tool");
        openTestingToolButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openEmbeddedTestingTool();
            }
        });
        gbc.gridx = 0;
        gbc.gridy = 16;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(openTestingToolButton, gbc);
    }

    private void copyToClipboard(String text) {
        StringSelection stringSelection = new StringSelection(text);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
    }

    private void getSignUrl() {
        String signatureId = signatureIdLabel.getText().replace("Signature ID: ", "");
        try {
            EmbeddedSignUrlResponse result = embeddedApi.embeddedSignUrl(signatureId);

            String signUrl = result.getEmbedded().getSignUrl();
            signUrlLabel.setText("Sign URL: " + signUrl);
            JOptionPane.showMessageDialog(this, "Sign URL retrieved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (ApiException e) {
            JOptionPane.showMessageDialog(this, "Failed to get sign URL: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void openEmbeddedTestingTool() {
        String signUrl = signUrlLabel.getText().replace("Sign URL: ", "");
        copyToClipboard(signUrl);
        try {
            Desktop.getDesktop().browse(new URI("https://developers.hellosign.com/additional-resources/embedded-testing-tool/"));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to open the embedded testing tool: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void sendSignatureRequest() {
        String signerName = getSignerName();
        String signerEmail = getSignerEmail();
        String title = getTitle();
        String subject = getSubject();
        String message = getMessage();

        if (signerName.isEmpty() || signerEmail.isEmpty() || title.isEmpty() || subject.isEmpty() || message.isEmpty() || selectedFile == null) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields and select a file.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        SubSignatureRequestSigner signer1 = new SubSignatureRequestSigner().emailAddress(signerEmail).name(signerName);

        try {
            SignatureRequestGetResponse result;
            if (isEmbedded()) {
                SignatureRequestCreateEmbeddedRequest data = new SignatureRequestCreateEmbeddedRequest()
                        .clientId(clientId)
                        .title(title)
                        .subject(subject)
                        .message(message)
                        .addSignersItem(signer1)
                        .addFilesItem(new File(selectedFile.getPath()))
                        .testMode(true);

                result = signatureRequestApi.signatureRequestCreateEmbedded(data);
            } else {
                SignatureRequestSendRequest data = new SignatureRequestSendRequest()
                        .title(title)
                        .subject(subject)
                        .message(message)
                        .addSignersItem(signer1)
                        .addFilesItem(new File(selectedFile.getPath()));

                result = signatureRequestApi.signatureRequestSend(data);
            }

            String signatureRequestId = result.getSignatureRequest().getSignatureRequestId();
            String signatureId = result.getSignatureRequest().getSignatures().get(0).getSignatureId();

            signatureRequestIdLabel.setText("Signature Request ID: " + signatureRequestId);
            signatureIdLabel.setText("Signature ID: " + signatureId);

            JOptionPane.showMessageDialog(this, "Signature request sent successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            System.out.println(result);
        } catch (ApiException e) {
            JOptionPane.showMessageDialog(this, "Failed to send signature request: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public boolean isEmbedded() {
        return embeddedCheckBox.isSelected();
    }

    public String getSignerName() {
        return nameField.getText();
    }

    public String getSignerEmail() {
        return emailField.getText();
    }

    public String getTitle() {
        return titleField.getText();
    }

    public String getSubject() {
        return subjectField.getText();
    }

    public String getMessage() {
        return messageField.getText();
    }

    public static void main(SignatureRequestApi signatureRequestApi, String clientId, EmbeddedApi embeddedApi ) {
        // Mock the SignatureRequestApi object for demonstration purposes
        JFrame frame = new JFrame("Signature Panel");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);
        frame.add(new TestSimulation(signatureRequestApi, embeddedApi, clientId));
        frame.setVisible(true);
    }
}
