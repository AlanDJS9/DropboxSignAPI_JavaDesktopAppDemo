import com.dropbox.sign.ApiException;
import com.dropbox.sign.api.EmbeddedApi;
import com.dropbox.sign.api.SignatureRequestApi;
import com.dropbox.sign.model.*;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.io.File;
import java.util.Arrays;
import java.util.List;

public class EmbeddedActions {

    public JPanel createEmbeddedPanel(SignatureRequestApi signatureRequestApi, EmbeddedApi embeddedApi, String clientId) {

        JPanel embeddedPanel = new JPanel(new GridLayout(4, 1));
        JButton createEmbeddedSignatureRequestButton = new JButton("Create Embedded Signature Request");
        JButton createEmbeddedSignatureRequestWithTemplateButton = new JButton("Create Embedded Signature Request with Template");
        JButton getEmbeddedSignUrlButton = new JButton("Get Embedded Sign URL");
        JButton getEmbeddedTemplateEditUrlButton = new JButton("Get Embedded Template Edit URL");

        embeddedPanel.add(createEmbeddedSignatureRequestButton);
        embeddedPanel.add(createEmbeddedSignatureRequestWithTemplateButton);
        embeddedPanel.add(getEmbeddedSignUrlButton);
        embeddedPanel.add(getEmbeddedTemplateEditUrlButton);



        createEmbeddedSignatureRequestButton.addActionListener(e -> executeCreateEmbeddedSignatureRequest(signatureRequestApi, clientId));
        createEmbeddedSignatureRequestWithTemplateButton.addActionListener(e -> executeCreateEmbeddedSignatureRequestWithTemplate(signatureRequestApi, clientId));
        getEmbeddedSignUrlButton.addActionListener(e -> executeGetEmbeddedSignUrl(embeddedApi));
        getEmbeddedTemplateEditUrlButton.addActionListener(e -> executeGetEmbeddedTemplateEditUrl(embeddedApi));

        return embeddedPanel;
    }

    private void executeCreateEmbeddedSignatureRequest(SignatureRequestApi signatureRequestApi, String clientId) {
        JFrame frame = new JFrame("Create Embedded Signature Request");
        frame.setLayout(new GridLayout(0, 2));


        JLabel titleLabel = new JLabel("Title:");
        JTextField titleField = new JTextField();

        JLabel subjectLabel = new JLabel("Subject:");
        JTextField subjectField = new JTextField();

        JLabel messageLabel = new JLabel("Message:");
        JTextField messageField = new JTextField();

        JLabel signer1EmailLabel = new JLabel("Signer 1 Email:");
        JTextField signer1EmailField = new JTextField();

        JLabel signer1NameLabel = new JLabel("Signer 1 Name:");
        JTextField signer1NameField = new JTextField();

        JLabel signer2EmailLabel = new JLabel("Signer 2 Email:");
        JTextField signer2EmailField = new JTextField();

        JLabel signer2NameLabel = new JLabel("Signer 2 Name:");
        JTextField signer2NameField = new JTextField();

        JLabel ccEmailsLabel = new JLabel("CC Emails (comma-separated):");
        JTextField ccEmailsField = new JTextField();

        JButton selectFileButton = new JButton("Select PDF File");
        JLabel selectedFileLabel = new JLabel("No file selected");

        JButton submitButton = new JButton("Create Signature Request");


        frame.add(titleLabel);
        frame.add(titleField);
        frame.add(subjectLabel);
        frame.add(subjectField);
        frame.add(messageLabel);
        frame.add(messageField);
        frame.add(signer1EmailLabel);
        frame.add(signer1EmailField);
        frame.add(signer1NameLabel);
        frame.add(signer1NameField);
        frame.add(signer2EmailLabel);
        frame.add(signer2EmailField);
        frame.add(signer2NameLabel);
        frame.add(signer2NameField);
        frame.add(ccEmailsLabel);
        frame.add(ccEmailsField);
        frame.add(selectFileButton);
        frame.add(selectedFileLabel);
        frame.add(submitButton);

        final File[] selectedFile = {null};

        selectFileButton.addActionListener((e) -> {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(frame);
            if (result == JFileChooser.APPROVE_OPTION) {
                selectedFile[0] = fileChooser.getSelectedFile();
                selectedFileLabel.setText(selectedFile[0].getName());
            }
        });

        submitButton.addActionListener((e) -> {
            String title = titleField.getText();
            String subject = subjectField.getText();
            String message = messageField.getText();
            String signer1Email = signer1EmailField.getText();
            String signer1Name = signer1NameField.getText();
            String signer2Email = signer2EmailField.getText();
            String signer2Name = signer2NameField.getText();
            java.util.List<String> ccEmails = Arrays.asList(ccEmailsField.getText().split(","));
            SubSignatureRequestSigner signer1 = new SubSignatureRequestSigner().emailAddress(signer1Email).name(signer1Name).order(0);
            SubSignatureRequestSigner signer2 = new SubSignatureRequestSigner().emailAddress(signer2Email).name(signer2Name).order(1);
            SubSigningOptions signingOptions = new SubSigningOptions().draw(true).type(true).upload(true).phone(true).defaultType(SubSigningOptions.DefaultTypeEnum.DRAW);

            if (selectedFile[0] == null) {
                JOptionPane.showMessageDialog(frame, "Please select a PDF file.");
                return;
            }

            SignatureRequestCreateEmbeddedRequest data = new SignatureRequestCreateEmbeddedRequest()
                    .clientId(clientId)
                    .title(title)
                    .subject(subject)
                    .message(message)
                    .signers(Arrays.asList(signer1, signer2))
                    .ccEmailAddresses(ccEmails)
                    .addFilesItem(selectedFile[0])
                    .signingOptions(signingOptions)
                    .testMode(true);

            try {
                SignatureRequestGetResponse result = signatureRequestApi.signatureRequestCreateEmbedded(data);
                showResult(result);
            } catch (ApiException var26) {
                handleException(var26);
            }

            frame.dispose();
        });

        frame.pack();
        frame.setVisible(true);
    }

    private  void executeCreateEmbeddedSignatureRequestWithTemplate(SignatureRequestApi signatureRequestApi, String clientId) {
        JFrame frame = new JFrame("Create Embedded Signature Request with Template");
        frame.setLayout(new GridLayout(0, 2));
        JLabel templateIdsLabel = new JLabel("Template IDs (comma-separated):");
        JTextField templateIdsField = new JTextField();
        JLabel subjectLabel = new JLabel("Subject:");
        JTextField subjectField = new JTextField();
        JLabel messageLabel = new JLabel("Message:");
        JTextField messageField = new JTextField();
        JLabel signerRoleLabel = new JLabel("Signer Role:");
        JTextField signerRoleField = new JTextField();
        JLabel signerNameLabel = new JLabel("Signer Name:");
        JTextField signerNameField = new JTextField();
        JButton submitButton = new JButton("Create Signature Request");
        frame.add(templateIdsLabel);
        frame.add(templateIdsField);
        frame.add(subjectLabel);
        frame.add(subjectField);
        frame.add(messageLabel);
        frame.add(messageField);
        frame.add(signerRoleLabel);
        frame.add(signerRoleField);
        frame.add(signerNameLabel);
        frame.add(signerNameField);
        frame.add(new JLabel());
        frame.add(submitButton);
        submitButton.addActionListener((e) -> {
            java.util.List<String> templateIds = Arrays.asList(templateIdsField.getText().split(","));
            String subject = subjectField.getText();
            String message = messageField.getText();
            String signerRole = signerRoleField.getText();
            String signerName = signerNameField.getText();
            SubSignatureRequestTemplateSigner signer1 = (new SubSignatureRequestTemplateSigner()).role(signerRole).name(signerName);
            SubSigningOptions signingOptions = (new SubSigningOptions()).draw(true).type(true).upload(true).phone(false).defaultType(SubSigningOptions.DefaultTypeEnum.DRAW);
            SignatureRequestCreateEmbeddedWithTemplateRequest data = (new SignatureRequestCreateEmbeddedWithTemplateRequest()).clientId(clientId).templateIds(templateIds).subject(subject).message(message).signers(java.util.List.of(signer1)).signingOptions(signingOptions).testMode(true);

            try {
                SignatureRequestGetResponse result = signatureRequestApi.signatureRequestCreateEmbeddedWithTemplate(data);
                showResult(result);
            } catch (ApiException var19) {
                ApiException ex = var19;
                handleException(ex);
            }

            frame.dispose();
        });
        frame.pack();
        frame.setVisible(true);
    }

    private  void executeGetEmbeddedSignUrl(EmbeddedApi embeddedApi) {
        JDialog dialog = new JDialog((Frame)null, "Get Embedded Sign URL", true);
        dialog.setSize(400, 200);
        dialog.setLayout(new GridLayout(0, 2));
        JLabel signatureIdLabel = new JLabel("Signature ID:");
        JTextField signatureIdField = new JTextField();
        JButton submitButton = new JButton("Get Sign URL");
        dialog.add(signatureIdLabel);
        dialog.add(signatureIdField);
        dialog.add(new JLabel());
        dialog.add(submitButton);
        submitButton.addActionListener((e) -> {
            String signatureId = signatureIdField.getText();

            try {
                EmbeddedSignUrlResponse result = embeddedApi.embeddedSignUrl(signatureId);
                showResult(result);
            } catch (ApiException var6) {
                ApiException exx = var6;
                handleException(exx);
            }

            dialog.dispose();
        });
        dialog.setVisible(true);
    }

    private void executeGetEmbeddedTemplateEditUrl(EmbeddedApi embeddedApi) {
        JDialog dialog = new JDialog((Frame)null, "Get Embedded Template Edit URL", true);
        dialog.setSize(400, 300);
        dialog.setLayout(new GridLayout(0, 2));
        JLabel templateIdLabel = new JLabel("Template ID:");
        JTextField templateIdField = new JTextField();
        JLabel ccRolesLabel = new JLabel("CC Roles (comma separated):");
        JTextField ccRolesField = new JTextField();
        JLabel mergeFieldsLabel = new JLabel("Merge Fields (comma separated):");
        JTextField mergeFieldsField = new JTextField();
        JButton submitButton = new JButton("Get Edit URL");
        dialog.add(templateIdLabel);
        dialog.add(templateIdField);
        dialog.add(ccRolesLabel);
        dialog.add(ccRolesField);
        dialog.add(mergeFieldsLabel);
        dialog.add(mergeFieldsField);
        dialog.add(new JLabel());
        dialog.add(submitButton);
        submitButton.addActionListener((e) -> {
            String templateId = templateIdField.getText();
            java.util.List<String> ccRoles = java.util.List.of(ccRolesField.getText().split(","));
            java.util.List mergeFields = List.of(mergeFieldsField.getText().split(","));
            EmbeddedEditUrlRequest data = (new EmbeddedEditUrlRequest()).ccRoles(ccRoles).mergeFields(mergeFields);

            try {
                EmbeddedEditUrlResponse result = embeddedApi.embeddedEditUrl(templateId, data);
                showResult(result);
            } catch (ApiException var11) {
                ApiException exx = var11;
                handleException(exx);
            }

            dialog.dispose();
        });
        dialog.setVisible(true);
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

    private  void handleException(ApiException e) {
        JOptionPane.showMessageDialog((Component)null, "Exception when calling API: " + e.getMessage());
    }
}
