import com.dropbox.sign.ApiException;
import com.dropbox.sign.api.AccountApi;
import com.dropbox.sign.model.*;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.util.Arrays;

public class AccountActions extends Component {
    private JPanel panel;

    public JPanel createAccountPanel(AccountApi accountApi) {
        panel = new JPanel(new GridLayout(4, 1));

        JButton getAccountButton = new JButton("Get Account");
        JButton updateAccountButton = new JButton("Update Account");
        JButton createAccountButton = new JButton("Create Account");
        JButton verifyAccountButton = new JButton("Verify Account");

        panel.add(getAccountButton);
        panel.add(updateAccountButton);
        panel.add(createAccountButton);
        panel.add(verifyAccountButton);

        getAccountButton.addActionListener(e -> executeGetAccount(accountApi));
        updateAccountButton.addActionListener(e -> executeUpdateAccount(accountApi));
        createAccountButton.addActionListener(e -> executeCreateAccount(accountApi));
        verifyAccountButton.addActionListener(e -> executeVerifyAccount(accountApi));
        return panel;
    }

    private void executeGetAccount(AccountApi accountApi) {
        JDialog dialog = createInputDialog("Get Account", "Email Address:");
        JButton submitButton = (JButton) dialog.getContentPane().getComponent(3);
        JTextField emailField = (JTextField) dialog.getContentPane().getComponent(1);

        submitButton.addActionListener(e -> {
            String email = emailField.getText();
            showLoadingIndicator(dialog);
            try {
                AccountGetResponse result = accountApi.accountGet(null, email);
                showResult(result);
            } catch (ApiException ex) {
                handleException(ex);
            } finally {
                dialog.dispose();
            }
        });
        dialog.setVisible(true);
    }

    private void executeUpdateAccount(AccountApi accountApi) {
        JDialog dialog = createInputDialog("Update Account", "Callback URL:");
        JButton submitButton = (JButton) dialog.getContentPane().getComponent(3);
        JTextField callbackUrlField = (JTextField) dialog.getContentPane().getComponent(1);

        submitButton.addActionListener(e -> {
            String callbackUrl = callbackUrlField.getText();
            AccountUpdateRequest updateData = new AccountUpdateRequest().callbackUrl(callbackUrl);
            showLoadingIndicator(dialog);
            try {
                AccountGetResponse result = accountApi.accountUpdate(updateData);
                showResult(result);
            } catch (ApiException ex) {
                handleException(ex);
            } finally {
                dialog.dispose();
            }
        });
        dialog.setVisible(true);
    }

    private void executeCreateAccount(AccountApi accountApi) {
        JDialog dialog = createInputDialog("Create Account", "Email Address:");
        JButton submitButton = (JButton) dialog.getContentPane().getComponent(3);
        JTextField emailField = (JTextField) dialog.getContentPane().getComponent(1);

        submitButton.addActionListener(e -> {
            String emailAddress = emailField.getText();
            AccountCreateRequest createData = new AccountCreateRequest().emailAddress(emailAddress);
            showLoadingIndicator(dialog);
            try {
                AccountCreateResponse result = accountApi.accountCreate(createData);
                showResult(result);
            } catch (ApiException ex) {
                handleException(ex);
            } finally {
                dialog.dispose();
            }
        });
        dialog.setVisible(true);
    }

    private void executeVerifyAccount(AccountApi accountApi) {
        JDialog dialog = createInputDialog("Verify Account", "Email Address:");
        JButton submitButton = (JButton) dialog.getContentPane().getComponent(3);
        JTextField emailField = (JTextField) dialog.getContentPane().getComponent(1);

        submitButton.addActionListener(e -> {
            String emailAddress = emailField.getText();
            AccountVerifyRequest verifyData = new AccountVerifyRequest().emailAddress(emailAddress);
            showLoadingIndicator(dialog);
            try {
                AccountVerifyResponse result = accountApi.accountVerify(verifyData);
                showResult(result);
            } catch (ApiException ex) {
                handleException(ex);
            } finally {
                dialog.dispose();
            }
        });
        dialog.setVisible(true);
    }

    private static void showResult(Object result) {
        ObjectMapper mapper = new ObjectMapper();
        String jsonText;

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
        StyleConstants.setForeground(valueStyle, Color.DARK_GRAY);
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

    private void handleException(ApiException e) {
        JOptionPane.showMessageDialog(null, "Exception when calling API:\nStatus code: " + e.getCode() + "\nMessage: " + e.getMessage() + "\nResponse body: " + e.getResponseBody());
    }

    private JDialog createInputDialog(String title, String label) {
        JDialog dialog = new JDialog((Frame) null, title, true);
        dialog.setSize(400, 200);
        dialog.setLayout(new GridLayout(0, 2));
        JLabel inputLabel = new JLabel(label);
        JTextField inputField = new JTextField();
        JButton submitButton = new JButton(title);
        dialog.add(inputLabel);
        dialog.add(inputField);
        dialog.add(new JLabel());
        dialog.add(submitButton);
        return dialog;
    }

    private void showLoadingIndicator(JDialog dialog) {
        dialog.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    }
}
