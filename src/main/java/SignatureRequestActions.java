import com.dropbox.sign.ApiClient;
import com.dropbox.sign.ApiException;
import com.dropbox.sign.api.SignatureRequestApi;
import com.dropbox.sign.Configuration;
import com.dropbox.sign.model.*;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

public class SignatureRequestActions {

    BufferedImage[] pdfImages;
    JLabel coordinatesLabel;
    JTextField fieldType;
    JTextField fieldWidth;
    JTextField fieldHeight;
    JTextField fieldPage;
    JTextField fieldPlaceholder;
    JButton addFieldButton;
    JButton sendRequestButton;
    JButton editRequestButton;
    JPanel fieldsPanel;
    int selectedX;
    int selectedY;
    static List<SubFormFieldsPerDocumentBase> fields;
    String requestTitle = "NDA with Acme Co.";
    String requestSubject = "The NDA we talked about";
    String requestMessage = "Please sign this NDA and then we can discuss more. Let me know if you have any questions.";
    SubSignatureRequestSigner signer1;
    String accountId = "accountId";
    int page = 1;
    int pageSize = 20;
    String query = null;

    public JPanel createSignaturePanel(SignatureRequestApi signatureRequestApi) {
        JPanel signaturePanel = new JPanel(new GridLayout(10, 1));
        JButton getSignatureRequestButton = new JButton("Get Signature Request");
        JButton listSignatureRequestsButton = new JButton("List Signature Requests");
        JButton downloadFilesButton = new JButton("Download Files");
        JButton downloadFilesAsDataUriButton = new JButton("Download Files as Data URI");
        JButton downloadFilesAsFileUrlButton = new JButton("Download Files as File URL");
        JButton sendSignatureRequestFormFieldsButton = new JButton("Send Signature Request using Form Fields");
        JButton sendSignatureRequestTextTagsButton = new JButton("Send Signature Request using Text Tags");
        JButton sendSignatureRequestButton = new JButton("Send Signature Request");
        JButton updateSignatureRequestButton = new JButton("Update Signature Request");
        JButton cancelSignatureRequestButton = new JButton("Cancel Incomplete Signature Request");
        JButton removeSignatureRequestButton = new JButton("Remove Signature Request Access");
        JButton bulkSendWithTemplateButton = new JButton("Embedded Bulk Send with Template");


        signaturePanel.add(sendSignatureRequestFormFieldsButton);
        signaturePanel.add(sendSignatureRequestTextTagsButton);
        signaturePanel.add(sendSignatureRequestButton);
        signaturePanel.add(updateSignatureRequestButton);
        signaturePanel.add(cancelSignatureRequestButton);
        signaturePanel.add(removeSignatureRequestButton);
        signaturePanel.add(bulkSendWithTemplateButton);
        signaturePanel.add(getSignatureRequestButton);
        signaturePanel.add(listSignatureRequestsButton);
        signaturePanel.add(downloadFilesButton);
        signaturePanel.add(downloadFilesAsDataUriButton);
        signaturePanel.add(downloadFilesAsFileUrlButton);


        getSignatureRequestButton.addActionListener(e -> executeGetSignatureRequest(signatureRequestApi));
        listSignatureRequestsButton.addActionListener((e) -> {executeListSignatureRequests(signatureRequestApi, accountId, page, pageSize, query);});
        downloadFilesButton.addActionListener(e -> executeDownloadFiles(signatureRequestApi));
        downloadFilesAsDataUriButton.addActionListener(e -> executeDownloadFilesAsDataUri(signatureRequestApi));
        downloadFilesAsFileUrlButton.addActionListener(e -> executeDownloadFilesAsFileUrl(signatureRequestApi));
        sendSignatureRequestFormFieldsButton.addActionListener((e) -> {executeSendSignatureRequestFormsFields(signatureRequestApi);});
        sendSignatureRequestTextTagsButton.addActionListener((e) -> {executeSendSignatureRequestTextTags(signatureRequestApi);});
        sendSignatureRequestButton.addActionListener(e -> executeSendSignatureRequest(signatureRequestApi));
        updateSignatureRequestButton.addActionListener(e -> executeUpdateSignatureRequest(signatureRequestApi));
        cancelSignatureRequestButton.addActionListener(e -> executeCancelSignatureRequest(signatureRequestApi));
        removeSignatureRequestButton.addActionListener(e -> executeRemoveSignatureRequest(signatureRequestApi));
        bulkSendWithTemplateButton.addActionListener(e -> executeBulkSendWithTemplate(signatureRequestApi));

        return signaturePanel;
    }




    private static void executeGetSignatureRequest(SignatureRequestApi signatureRequestApi) {
        JDialog dialog = new JDialog((Frame)null, "Get Signature Request", true);
        dialog.setSize(400, 200);
        dialog.setLayout(new GridLayout(0, 2));
        JLabel idLabel = new JLabel("Signature Request ID:");
        JTextField idField = new JTextField();
        JButton submitButton = new JButton("Get Signature Request");
        dialog.add(idLabel);
        dialog.add(idField);
        dialog.add(new JLabel());
        dialog.add(submitButton);
        submitButton.addActionListener((e) -> {
            String signatureRequestId = idField.getText();

            try {
                SignatureRequestGetResponse result = signatureRequestApi.signatureRequestGet(signatureRequestId);
                showResult(result);
            } catch (ApiException var6) {
                ApiException exx = var6;
                handleException(exx);
            }

            dialog.dispose();
        });
        dialog.setVisible(true);
    }

    private static void executeListSignatureRequests(SignatureRequestApi signatureRequestApi, String accountId, int page, int pageSize, String query) {
        try {
            SignatureRequestListResponse result = signatureRequestApi.signatureRequestList(accountId, page, pageSize, query);
            showResult(result);
        } catch (ApiException var6) {
            ApiException e = var6;
            handleException(e);
        }

    }

    private static void executeDownloadFiles(SignatureRequestApi signatureRequestApi) {
        JDialog dialog = new JDialog((Frame)null, "Download Files", true);
        dialog.setSize(400, 200);
        dialog.setLayout(new GridLayout(0, 2));
        JLabel idLabel = new JLabel("Signature Request ID:");
        JTextField idField = new JTextField();
        JLabel fileTypeLabel = new JLabel("File Type:");
        String[] fileTypes = new String[]{"pdf", "zip"};
        JComboBox<String> fileTypeDropdown = new JComboBox(fileTypes);
        JButton submitButton = new JButton("Download File");
        dialog.add(idLabel);
        dialog.add(idField);
        dialog.add(fileTypeLabel);
        dialog.add(fileTypeDropdown);
        dialog.add(new JLabel());
        dialog.add(submitButton);
        submitButton.addActionListener((e) -> {
            String signatureRequestId = idField.getText();
            String fileType = (String)fileTypeDropdown.getSelectedItem();

            try {
                File result = signatureRequestApi.signatureRequestFiles(signatureRequestId, fileType);
                String fileName = fileType.equals("pdf") ? "file_response.pdf" : "file_response.zip";
                result.renameTo(new File(fileName));
                JOptionPane.showMessageDialog((Component)null, "File downloaded as " + fileName);
            } catch (ApiException var9) {
                ApiException ex = var9;
                handleException(ex);
            }

            dialog.dispose();
        });
        dialog.setVisible(true);
    }

    private static void executeDownloadFilesAsDataUri(SignatureRequestApi signatureRequestApi) {
        JFrame frame = new JFrame("Download Files as Data URI");
        frame.setLayout(new GridLayout(0, 2));
        JLabel idLabel = new JLabel("Signature Request ID:");
        JTextField idField = new JTextField();
        JButton submitButton = new JButton("Download Files as Data URI");
        frame.add(idLabel);
        frame.add(idField);
        frame.add(new JLabel());
        frame.add(submitButton);
        submitButton.addActionListener((e) -> {
            String signatureRequestId = idField.getText();

            try {
                FileResponseDataUri result = signatureRequestApi.signatureRequestFilesAsDataUri(signatureRequestId);
                showResult(result);
            } catch (ApiException var6) {
                ApiException exx = var6;
                handleException(exx);
            }

            frame.dispose();
        });
        frame.pack();
        frame.setVisible(true);
    }

    private static void executeDownloadFilesAsFileUrl(SignatureRequestApi signatureRequestApi) {
        JFrame frame = new JFrame("Download Files as File URL");
        frame.setLayout(new GridLayout(0, 2));
        JLabel idLabel = new JLabel("Signature Request ID:");
        JTextField idField = new JTextField();
        JButton submitButton = new JButton("Download Files as File URL");
        frame.add(idLabel);
        frame.add(idField);
        frame.add(new JLabel());
        frame.add(submitButton);
        submitButton.addActionListener((e) -> {
            String signatureRequestId = idField.getText();

            try {
                FileResponse result = signatureRequestApi.signatureRequestFilesAsFileUrl(signatureRequestId);
                showResult(result);
            } catch (ApiException var6) {
                ApiException exx = var6;
                handleException(exx);
            }

            frame.dispose();
        });
        frame.pack();
        frame.setVisible(true);
    }

    private void executeSendSignatureRequestFormsFields(SignatureRequestApi signatureRequestApi) {
        fields = new ArrayList();
        JFrame frame = new JFrame("Form Fields Tests");
        frame.setTitle("PDF Viewer");
        frame.setSize(1200, 800);
        frame.setDefaultCloseOperation(3);
        frame.setLayout(new BorderLayout());
        JPanel pdfPanel = new JPanel();
        pdfPanel.setLayout(new BoxLayout(pdfPanel, 1));
        JScrollPane scrollPane = new JScrollPane(pdfPanel);
        scrollPane.setPreferredSize(new Dimension(800, 800));
        frame.add(scrollPane, "Center");
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, 1));
        frame.add(controlPanel, "West");
        coordinatesLabel = new JLabel("Click coordinates: X=, Y=");
        controlPanel.add(coordinatesLabel);
        fieldType = new JTextField("Text");
        controlPanel.add(new JLabel("Field Type:"));
        controlPanel.add(fieldType);
        fieldWidth = new JTextField("100");
        controlPanel.add(new JLabel("Field Width:"));
        controlPanel.add(fieldWidth);
        fieldHeight = new JTextField("16");
        controlPanel.add(new JLabel("Field Height:"));
        controlPanel.add(fieldHeight);
        fieldPage = new JTextField("1");
        controlPanel.add(new JLabel("Page Number:"));
        controlPanel.add(fieldPage);
        fieldPlaceholder = new JTextField("Placeholder");
        controlPanel.add(new JLabel("Placeholder:"));
        controlPanel.add(fieldPlaceholder);
        addFieldButton = new JButton("Add Field");
        controlPanel.add(addFieldButton);
        sendRequestButton = new JButton("Send Request");
        controlPanel.add(sendRequestButton);
        editRequestButton = new JButton("Edit Request Details");
        controlPanel.add(editRequestButton);
        fieldsPanel = new JPanel();
        fieldsPanel.setLayout(new BoxLayout(fieldsPanel, 1));
        controlPanel.add(fieldsPanel);
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(frame);
        if (result == 0) {
            File file = fileChooser.getSelectedFile();

            try {
                loadPDF(file, pdfPanel);
            } catch (IOException var9) {
                IOException e = var9;
                e.printStackTrace();
            }
        }

        addFieldButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addField();
            }
        });
        sendRequestButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendSignatureRequest(signatureRequestApi);
                frame.dispose();

            }
        });
        editRequestButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openEditRequestWindow();
            }
        });
        frame.pack();

        frame.setVisible(true);
    }

    private void executeSendSignatureRequestTextTags(SignatureRequestApi signatureRequestApi) {
        JDialog dialog = new JDialog((Frame) null, "Send Signature Request using Text Tags", true);
        dialog.setSize(800, 600);
        dialog.setLayout(new GridLayout(0, 2));

        JLabel pdfLabel = new JLabel("Select PDF:");
        JTextField pdfField = new JTextField();
        JButton pdfBrowseButton = new JButton("Browse");

        JLabel subjectLabel = new JLabel("Subject:");
        JTextField subjectField = new JTextField();

        JLabel messageLabel = new JLabel("Message:");
        JTextField messageField = new JTextField();

        JCheckBox testModeCheckBox = new JCheckBox("Test Mode", false);

        JLabel signer1EmailLabel = new JLabel("Signer 1 Email:");
        JTextField signer1EmailField = new JTextField();
        JLabel signer1NameLabel = new JLabel("Signer 1 Name:");
        JTextField signer1NameField = new JTextField();
        JLabel signer1PhoneLabel = new JLabel("Signer 1 Phone:");
        JTextField signer1PhoneField = new JTextField();

        JLabel signer2EmailLabel = new JLabel("Signer 2 Email:");
        JTextField signer2EmailField = new JTextField();
        JLabel signer2NameLabel = new JLabel("Signer 2 Name:");
        JTextField signer2NameField = new JTextField();
        JLabel signer2PhoneLabel = new JLabel("Signer 2 Phone:");
        JTextField signer2PhoneField = new JTextField();

        JButton sendButton = new JButton("Send");

        pdfBrowseButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int option = fileChooser.showOpenDialog(dialog);
            if (option == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                pdfField.setText(file.getAbsolutePath());
            }
        });

        sendButton.addActionListener(e -> {
            SignatureRequestSendRequest sendRequest = new SignatureRequestSendRequest()
                    .subject(subjectField.getText())
                    .message(messageField.getText())
                    .addFilesItem(new File(pdfField.getText()))
                    .addSignersItem(new SubSignatureRequestSigner()
                            .emailAddress(signer1EmailField.getText())
                            .name(signer1NameField.getText())
                            .smsPhoneNumber(signer1PhoneField.getText())
                            .order(0))
                    .addSignersItem(new SubSignatureRequestSigner()
                            .emailAddress(signer2EmailField.getText())
                            .name(signer2NameField.getText())
                            .smsPhoneNumber(signer2PhoneField.getText())
                            .order(1))
                    .testMode(testModeCheckBox.isSelected())
                    .useTextTags(true);

            try {
                SignatureRequestGetResponse result = signatureRequestApi.signatureRequestSend(sendRequest);

                showResult(result);
                dialog.dispose();
            } catch (ApiException ex) {
                handleException(ex);
            }
        });

        dialog.add(pdfLabel);
        dialog.add(pdfField);
        dialog.add(pdfBrowseButton);

        dialog.add(subjectLabel);
        dialog.add(subjectField);

        dialog.add(messageLabel);
        dialog.add(messageField);

        dialog.add(testModeCheckBox);

        dialog.add(signer1EmailLabel);
        dialog.add(signer1EmailField);
        dialog.add(signer1NameLabel);
        dialog.add(signer1NameField);
        dialog.add(signer1PhoneLabel);
        dialog.add(signer1PhoneField);

        dialog.add(signer2EmailLabel);
        dialog.add(signer2EmailField);
        dialog.add(signer2NameLabel);
        dialog.add(signer2NameField);
        dialog.add(signer2PhoneLabel);
        dialog.add(signer2PhoneField);

        dialog.add(sendButton);
        dialog.setVisible(true);
    }

    private static void executeSendSignatureRequest(SignatureRequestApi signatureRequestApi) {
        JDialog dialog = new JDialog((Frame)null, "Send Signature Request", true);
        dialog.setSize(800, 600);
        dialog.setLayout(new GridLayout(0, 2));
        JLabel pdfLabel = new JLabel("Select PDF:");
        JTextField pdfField = new JTextField();
        JButton pdfBrowseButton = new JButton("Browse");
        JLabel subjectLabel = new JLabel("Subject:");
        JTextField subjectField = new JTextField();
        JLabel messageLabel = new JLabel("Message:");
        JTextField messageField = new JTextField();
        JCheckBox testModeCheckBox = new JCheckBox("SMS Notification", false);
        JButton addSignerButton = new JButton("Add Signer");
        JPanel signersPanel = new JPanel(new GridLayout(0, 3));
        JButton sendButton = new JButton("Send");
        pdfBrowseButton.addActionListener((e) -> {
            JFileChooser fileChooser = new JFileChooser();
            int option = fileChooser.showOpenDialog(dialog);
            if (option == 0) {
                File file = fileChooser.getSelectedFile();
                pdfField.setText(file.getAbsolutePath());
            }

        });
        addSignerButton.addActionListener((e) -> {
            if (signersPanel.getComponentCount() / 2 < 50) {
                int var10002 = signersPanel.getComponentCount() / 3;
                JLabel emailLabel = new JLabel("Signer " + (var10002 + 1) + " Email:");
                JTextField emailField = new JTextField();
                var10002 = signersPanel.getComponentCount() / 3;
                JLabel nameLabel = new JLabel("Signer " + (var10002 + 1) + " Name:");
                JTextField nameField = new JTextField();
                var10002 = signersPanel.getComponentCount() / 3;
                JLabel phoneLabel = new JLabel("Signer " + (var10002 + 1) + " Phone:");
                JTextField phoneField = new JTextField();
                signersPanel.add(emailLabel);
                signersPanel.add(emailField);
                signersPanel.add(nameLabel);
                signersPanel.add(nameField);
                signersPanel.add(phoneLabel);
                signersPanel.add(phoneField);
                dialog.pack();
            } else {
                JOptionPane.showMessageDialog(dialog, "Maximum 50 signers allowed.");
            }

        });
        sendButton.addActionListener((e) -> {
            SignatureRequestSendRequest sendRequest = (new SignatureRequestSendRequest()).subject(subjectField.getText()).message(messageField.getText()).addFilesItem(new File(pdfField.getText()));

            for(int i = 0; i < signersPanel.getComponentCount(); i += 6) {
                JTextField emailField = (JTextField)signersPanel.getComponent(i + 1);
                JTextField nameField = (JTextField)signersPanel.getComponent(i + 3);
                JTextField phoneField = (JTextField)signersPanel.getComponent(i + 5);
                sendRequest.addSignersItem((new SubSignatureRequestSigner()).emailAddress(emailField.getText()).name(nameField.getText()).smsPhoneNumber(phoneField.getText()).order(i / 6));
            }

            sendRequest.testMode(testModeCheckBox.isSelected());

            try {
                SignatureRequestGetResponse result = signatureRequestApi.signatureRequestSend(sendRequest);
                showResult(result);
                dialog.dispose();
            } catch (ApiException var13) {
                ApiException ex = var13;
                handleException(ex);
            }

        });
        dialog.add(pdfLabel);
        dialog.add(pdfField);
        dialog.add(new JLabel(""));
        dialog.add(pdfBrowseButton);
        dialog.add(subjectLabel);
        dialog.add(subjectField);
        dialog.add(messageLabel);
        dialog.add(messageField);
        dialog.add(new JLabel(""));
        dialog.add(testModeCheckBox);
        dialog.add(new JLabel(""));
        dialog.add(addSignerButton);
        dialog.add(new JLabel("Signers:"));
        dialog.add(signersPanel);
        dialog.add(new JLabel(""));
        dialog.add(sendButton);
        dialog.setVisible(true);
    }

    private static void executeUpdateSignatureRequest(SignatureRequestApi signatureRequestApi) {
        JDialog dialog = new JDialog((Frame)null, "Update Signature Request", true);
        dialog.setSize(400, 300);
        dialog.setLayout(new GridLayout(0, 2));
        JLabel requestIdLabel = new JLabel("Request ID:");
        JTextField requestIdField = new JTextField();
        JLabel emailLabel = new JLabel("Email Address:");
        JTextField emailField = new JTextField();
        JLabel signatureIdLabel = new JLabel("Signature ID:");
        JTextField signatureIdField = new JTextField();
        JButton submitButton = new JButton("Update Request");
        dialog.add(requestIdLabel);
        dialog.add(requestIdField);
        dialog.add(emailLabel);
        dialog.add(emailField);
        dialog.add(signatureIdLabel);
        dialog.add(signatureIdField);
        dialog.add(new JLabel());
        dialog.add(submitButton);
        submitButton.addActionListener((e) -> {
            String requestId = requestIdField.getText();
            String email = emailField.getText();
            String signatureId = signatureIdField.getText();
            SignatureRequestUpdateRequest data = (new SignatureRequestUpdateRequest()).emailAddress(email).signatureId(signatureId);

            try {
                SignatureRequestGetResponse result = signatureRequestApi.signatureRequestUpdate(requestId, data);
                showResult(result);
            } catch (ApiException var11) {
                ApiException exx = var11;
                handleException(exx);
            }

            dialog.dispose();
        });
        dialog.setVisible(true);
    }

    private static void executeCancelSignatureRequest(SignatureRequestApi signatureRequestApi) {
        JDialog dialog = new JDialog((Frame)null, "Cancel Incomplete Signature Request", true);
        dialog.setSize(400, 200);
        dialog.setLayout(new GridLayout(0, 2));
        JLabel requestIdLabel = new JLabel("Request ID:");
        JTextField requestIdField = new JTextField();
        JButton submitButton = new JButton("Cancel Request");
        dialog.add(requestIdLabel);
        dialog.add(requestIdField);
        dialog.add(new JLabel());
        dialog.add(submitButton);
        submitButton.addActionListener((e) -> {
            String requestId = requestIdField.getText();

            try {
                signatureRequestApi.signatureRequestCancel(requestId);
                JOptionPane.showMessageDialog(dialog, "Signature request canceled successfully.");
            } catch (ApiException var6) {
                ApiException exx = var6;
                handleException(exx);
            }

            dialog.dispose();
        });
        dialog.setVisible(true);
    }

    private static void executeRemoveSignatureRequest(SignatureRequestApi signatureRequestApi) {
        JDialog dialog = new JDialog((Frame)null, "Remove Signature Request Access", true);
        dialog.setSize(400, 200);
        dialog.setLayout(new GridLayout(0, 2));
        JLabel requestIdLabel = new JLabel("Request ID:");
        JTextField requestIdField = new JTextField();
        JButton submitButton = new JButton("Remove Request");
        dialog.add(requestIdLabel);
        dialog.add(requestIdField);
        dialog.add(new JLabel());
        dialog.add(submitButton);
        submitButton.addActionListener((e) -> {
            String requestId = requestIdField.getText();

            try {
                signatureRequestApi.signatureRequestRemove(requestId);
                JOptionPane.showMessageDialog(dialog, "Signature request access removed successfully.");
            } catch (ApiException var6) {
                ApiException exx = var6;
                handleException(exx);
            }

            dialog.dispose();
        });
        dialog.setVisible(true);
    }

    private static void executeBulkSendWithTemplate(SignatureRequestApi signatureRequestApi) {
        // Create the frame
        JFrame frame = new JFrame("Signature Request Input");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(400, 650);

        // Create a panel with a grid layout
        JPanel panel = new JPanel(new GridLayout(14, 2));

        // Add fields for template ID
        panel.add(new JLabel("Template ID:"));
        JTextField templateIdField = new JTextField();
        panel.add(templateIdField);

        // Add fields for signer 1
        panel.add(new JLabel("Signer 1 Name:"));
        JTextField signer1NameField = new JTextField();
        panel.add(signer1NameField);

        panel.add(new JLabel("Signer 1 Email:"));
        JTextField signer1EmailField = new JTextField();
        panel.add(signer1EmailField);

        panel.add(new JLabel("Signer 1 Role:"));
        JTextField signer1RoleField = new JTextField();
        panel.add(signer1RoleField);

        panel.add(new JLabel("Signer 1 Pin:"));
        JTextField signer1PinField = new JTextField();
        panel.add(signer1PinField);



        // Add fields for subject and message
        panel.add(new JLabel("Subject:"));
        JTextField subjectField = new JTextField();
        panel.add(subjectField);

        panel.add(new JLabel("Message:"));
        JTextArea messageArea = new JTextArea();
        panel.add(messageArea);

        // Add send button
        JButton sendButton = new JButton("Send");
        panel.add(sendButton);

        // Add an empty label to align the button correctly
        panel.add(new JLabel(""));

        // Add action listener to the send button
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Get user input
                String templateId = templateIdField.getText();
                String signer1Name = signer1NameField.getText();
                String signer1Email = signer1EmailField.getText();
                String signer1Role = signer1RoleField.getText();
                String signer1Pin = signer1PinField.getText();
                String subject = subjectField.getText();
                String message = messageArea.getText();

                // Create signers using user input
                var signerList1Signer = new SubSignatureRequestTemplateSigner()
                        .role(signer1Role)
                        .name(signer1Name)
                        .emailAddress(signer1Email)
                        .pin(signer1Pin);

                var signerList1 = new SubBulkSignerList()
                        .signers(List.of(signerList1Signer))
                        .customFields(List.of());


                var data = new SignatureRequestBulkSendWithTemplateRequest()
                        .templateIds(List.of(templateId))
                        .subject(subject)
                        .message(message)
                        .signerList(List.of(signerList1))
                        .testMode(true);

                try {
                    BulkSendJobSendResponse result = signatureRequestApi.signatureRequestBulkSendWithTemplate(data);
                    JOptionPane.showMessageDialog(frame, "Signature Request Sent! Job ID: " + result.toString());
                } catch (ApiException ex) {
                    JOptionPane.showMessageDialog(frame, "An error occurred: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Add the panel to the frame and display the frame
        frame.getContentPane().add(panel);
        frame.setVisible(true);
    }


    private static void showResult(Object result) {
        ObjectMapper mapper = new ObjectMapper();
        String jsonText = "";

        try {
            jsonText = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(result);
        } catch (Exception ex) {
            jsonText = "Error parsing result to JSON: " + ex.getMessage();
        }

        JTextPane textPane = new JTextPane();
        textPane.setEditable(false);
        textPane.setFont(new Font("Courier New", Font.PLAIN, 12));

        StyledDocument doc = textPane.getStyledDocument();
        Style defaultStyle = doc.addStyle("default", null);
        Style nameStyle = doc.addStyle("name", null);
        StyleConstants.setForeground(nameStyle, Color.BLUE);
        Style valueStyle = doc.addStyle("value", null);
        StyleConstants.setForeground(valueStyle, Color.darkGray);
        Style otherStyle = doc.addStyle("other", null);
        StyleConstants.setForeground(otherStyle, Color.RED);

        try {
            JsonFactory factory = new JsonFactory();
            JsonParser parser = factory.createParser(jsonText);

            while (!parser.isClosed()) {
                JsonToken token = parser.nextToken();
                if (token == null) break;

                switch (token) {
                    case FIELD_NAME:
                        doc.insertString(doc.getLength(), parser.getCurrentName() + ": ", nameStyle);
                        break;
                    case VALUE_STRING:
                        doc.insertString(doc.getLength(), "\"" + parser.getText() + "\"\n", valueStyle);
                        break;
                    case VALUE_NUMBER_INT:
                    case VALUE_NUMBER_FLOAT:
                        doc.insertString(doc.getLength(), parser.getNumberValue().toString() + "\n", valueStyle);
                        break;
                    case START_OBJECT:
                    case END_OBJECT:
                    case START_ARRAY:
                    case END_ARRAY:
                        doc.insertString(doc.getLength(), token.toString() + "\n", otherStyle);
                        break;
                    default:
                        doc.insertString(doc.getLength(), token.toString() + "\n", defaultStyle);
                        break;
                }
            }
        } catch (Exception ex) {
            try {
                doc.insertString(doc.getLength(), "Error displaying JSON: " + ex.getMessage(), defaultStyle);
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        }

        JScrollPane scrollPane = new JScrollPane(textPane);
        scrollPane.setPreferredSize(new Dimension(800, 600));
        JOptionPane.showMessageDialog(null, scrollPane, "JSON Result", JOptionPane.PLAIN_MESSAGE);
    }

    private static void handleException(ApiException e) {
        JOptionPane.showMessageDialog((Component)null, "Exception when calling API: " + e.getMessage());
    }

    private void loadPDF(File file, JPanel panel) throws IOException {
        PDDocument document = Loader.loadPDF(file);
        PDFRenderer pdfRenderer = new PDFRenderer(document);
        int pageCount = document.getNumberOfPages();
        pdfImages = new BufferedImage[pageCount];

        for(int i = 0; i < pageCount; ++i) {
            BufferedImage originalImage = pdfRenderer.renderImageWithDPI(i, 72.0F);
            pdfImages[i] = getScaledImage(originalImage, 612, 792);
            JLabel pageLabel = new JLabel(new ImageIcon(pdfImages[i]));
            final int finalI = i + 1;
            pageLabel.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    selectedX = e.getX();
                    selectedY = e.getY();
                    coordinatesLabel.setText("Click coordinates: X=" + selectedX + ", Y=" + selectedY + ", Page=" + finalI);
                    fieldPage.setText(String.valueOf(finalI));
                }
            });
            panel.add(pageLabel);
        }

        document.close();
        panel.revalidate();
        panel.repaint();
    }

    private  BufferedImage getScaledImage(BufferedImage originalImage, int width, int height) {
        BufferedImage scaledImage = new BufferedImage(width, height, 2);
        Graphics2D g2d = scaledImage.createGraphics();
        g2d.drawImage(originalImage, 0, 0, width, height, (ImageObserver)null);
        g2d.dispose();
        return scaledImage;
    }

    private  void addField() {
        String type = fieldType.getText();
        int width = Integer.parseInt(fieldWidth.getText());
        int height = Integer.parseInt(fieldHeight.getText());
        int page = Integer.parseInt(fieldPage.getText());
        String placeholder = fieldPlaceholder.getText();
        if ("Text".equalsIgnoreCase(type)) {
            SubFormFieldsPerDocumentText formField = new SubFormFieldsPerDocumentText();
            formField.documentIndex(0);
            formField.required(true);
            formField.signer(0);
            formField.width(width);
            formField.height(height);
            formField.x(selectedX);
            formField.y(selectedY);
            formField.page(page);
            formField.placeholder(placeholder);
            formField.validationType(SubFormFieldsPerDocumentText.ValidationTypeEnum.LETTERS_ONLY);
            fields.add(formField);
        } else if ("Signature".equalsIgnoreCase(type)) {
            SubFormFieldsPerDocumentSignature formField = new SubFormFieldsPerDocumentSignature();
            formField.documentIndex(0);
            formField.required(true);
            formField.signer(0);
            formField.width(width);
            formField.height(height);
            formField.x(selectedX);
            formField.y(selectedY);
            formField.page(page);
            fields.add(formField);
        }

        JLabel fieldLabel = new JLabel("Field added: " + type + " at (" + selectedX + ", " + selectedY + ") on page " + page);
        fieldsPanel.add(fieldLabel);
        fieldsPanel.revalidate();
        fieldsPanel.repaint();
    }

    private  void sendSignatureRequest(SignatureRequestApi signatureRequestApi) {
        if (signer1 != null) {
            SignatureRequestSendRequest data = (new SignatureRequestSendRequest()).title(requestTitle).subject(requestSubject).message(requestMessage).addFileUrlsItem("https://www.dropbox.com/s/ad9qnhbrjjn64tu/mutual-NDA-example.pdf?dl=1").addSignersItem(signer1);
            Iterator var4 = fields.iterator();

            while(var4.hasNext()) {
                SubFormFieldsPerDocumentBase field = (SubFormFieldsPerDocumentBase)var4.next();
                data.addFormFieldsPerDocumentItem(field);
            }

            try {
                SignatureRequestGetResponse result = signatureRequestApi.signatureRequestSend(data);
                showResult(result);
                System.out.println(result);
            } catch (ApiException var6) {
                ApiException e = var6;
                System.err.println("Exception when calling AccountApi#accountCreate");
                System.err.println("Status code: " + e.getCode());
                System.err.println("Reason: " + e.getResponseBody());
                System.err.println("Response headers: " + String.valueOf(e.getResponseHeaders()));
                e.printStackTrace();
            }

        }
    }

    private  void openEditRequestWindow() {
        final JFrame editWindow = new JFrame("Edit Request Details");
        editWindow.setSize(400, 300);
        editWindow.setLayout(new GridLayout(5, 2));
        final JTextField titleField = new JTextField(requestTitle);
        final JTextField subjectField = new JTextField(requestSubject);
        final JTextField messageField = new JTextField(requestMessage);
        final JTextField signerNameField = new JTextField();
        final JTextField signerEmailField = new JTextField();
        editWindow.add(new JLabel("Title:"));
        editWindow.add(titleField);
        editWindow.add(new JLabel("Subject:"));
        editWindow.add(subjectField);
        editWindow.add(new JLabel("Message:"));
        editWindow.add(messageField);
        editWindow.add(new JLabel("Signer Name:"));
        editWindow.add(signerNameField);
        editWindow.add(new JLabel("Signer Email:"));
        editWindow.add(signerEmailField);
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                requestTitle = titleField.getText();
                requestSubject = subjectField.getText();
                requestMessage = messageField.getText();
                signer1 = (new SubSignatureRequestSigner()).name(signerNameField.getText()).emailAddress(signerEmailField.getText()).order(0);
                editWindow.dispose();
            }
        });
        editWindow.add(saveButton);
        editWindow.setVisible(true);
    }
}
