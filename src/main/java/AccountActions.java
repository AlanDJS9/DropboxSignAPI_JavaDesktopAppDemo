import com.dropbox.sign.ApiException;
import com.dropbox.sign.api.AccountApi;
import com.dropbox.sign.Configuration;
import com.dropbox.sign.model.*;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.util.Arrays;

public class AccountActions extends Component {
    private JPanel panel;

    public JPanel CreateAccountPanel(AccountApi accountApi) {
        panel = new JPanel(new GridLayout(4, 1));

        JButton getAccountButton = new JButton("Get Account");
        JButton updateAccountButton = new JButton("Update Account");
        JButton createAccountButton = new JButton("Create Account");
        JButton verifyAccountButton = new JButton("Verify Account");

        panel.add(getAccountButton);
        panel.add(updateAccountButton);
        panel.add(createAccountButton);
        panel.add(verifyAccountButton);

        getAccountButton.addActionListener((e) -> {executeGetAccount(accountApi);});
        updateAccountButton.addActionListener((e) -> {executeUpdateAccount(accountApi);});
        createAccountButton.addActionListener((e) -> {executeCreateAccount(accountApi);});
        verifyAccountButton.addActionListener((e) -> {executeVerifyAccount(accountApi);});
        return panel;

    }


    private  void executeGetAccount(AccountApi accountApi) {
        JDialog dialog = new JDialog((Frame)null, "Get Account", true);
        dialog.setSize(400, 200);
        dialog.setLayout(new GridLayout(0, 2));
        JLabel emailLabel = new JLabel("Email Address:");
        JTextField emailField = new JTextField();
        JButton submitButton = new JButton("Get Account");
        dialog.add(emailLabel);
        dialog.add(emailField);
        dialog.add(new JLabel());
        dialog.add(submitButton);
        submitButton.addActionListener((e) -> {
            String email = emailField.getText();

            try {
                AccountGetResponse result = accountApi.accountGet((String)null, email);
                showResult(result);
            } catch (ApiException var6) {
                ApiException exx = var6;
                handleException(exx);
            }

            dialog.dispose();
        });
        dialog.setVisible(true);
    }

    private  void executeUpdateAccount(AccountApi accountApi) {
        JDialog dialog = new JDialog((Frame)null, "Update Account", true);
        dialog.setSize(400, 200);
        dialog.setLayout(new GridLayout(0, 2));
        JLabel callbackUrlLabel = new JLabel("Callback URL:");
        JTextField callbackUrlField = new JTextField();
        JButton submitButton = new JButton("Update Account");
        dialog.add(callbackUrlLabel);
        dialog.add(callbackUrlField);
        dialog.add(new JLabel());
        dialog.add(submitButton);
        submitButton.addActionListener((e) -> {
            String callbackUrl = callbackUrlField.getText();
            AccountUpdateRequest updateData = (new AccountUpdateRequest()).callbackUrl(callbackUrl);

            try {
                AccountGetResponse result = accountApi.accountUpdate(updateData);
                showResult(result);
            } catch (ApiException var7) {
                ApiException ex = var7;
                handleException(ex);
            }

            dialog.dispose();
        });
        dialog.setVisible(true);
    }

    private  void executeCreateAccount(AccountApi accountApi) {
        JDialog dialog = new JDialog((Frame)null, "Create Account", true);
        dialog.setSize(400, 200);
        dialog.setLayout(new GridLayout(0, 2));
        JLabel emailLabel = new JLabel("Email Address:");
        JTextField emailField = new JTextField();
        JButton submitButton = new JButton("Create Account");
        dialog.add(emailLabel);
        dialog.add(emailField);
        dialog.add(new JLabel());
        dialog.add(submitButton);
        submitButton.addActionListener((e) -> {
            String emailAddress = emailField.getText();
            AccountCreateRequest createData = (new AccountCreateRequest()).emailAddress(emailAddress);

            try {
                AccountCreateResponse result = accountApi.accountCreate(createData);
                showResult(result);
            } catch (ApiException var7) {
                ApiException ex = var7;
                handleException(ex);
            }

            dialog.dispose();
        });
        dialog.setVisible(true);
    }

    private  void executeVerifyAccount(AccountApi accountApi) {
        JDialog dialog = new JDialog((Frame)null, "Verify Account", true);
        dialog.setSize(400, 200);
        dialog.setLayout(new GridLayout(0, 2));
        JLabel emailLabel = new JLabel("Email Address:");
        JTextField emailField = new JTextField();
        JButton submitButton = new JButton("Verify Account");
        dialog.add(emailLabel);
        dialog.add(emailField);
        dialog.add(new JLabel());
        dialog.add(submitButton);
        submitButton.addActionListener((e) -> {
            String emailAddress = emailField.getText();
            AccountVerifyRequest verifyData = (new AccountVerifyRequest()).emailAddress(emailAddress);

            try {
                AccountVerifyResponse result = accountApi.accountVerify(verifyData);
                showResult(result);
            } catch (ApiException var7) {
                ApiException ex = var7;
                handleException(ex);
            }

            dialog.dispose();
        });
        dialog.setVisible(true);
    }
    private  void showResult(Object result) {
        ObjectMapper mapper = new ObjectMapper();
        String jsonText = "";

        try {
            jsonText = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(result);
        } catch (Exception var13) {
            Exception ex = var13;
            jsonText = "Error parsing result to JSON: " + ex.getMessage();
        }

        JTextArea textArea = new JTextArea();
        textArea.setEditable(true);
        textArea.setFont(new Font("Courier New", 0, 12));
        StyleContext styleContext = StyleContext.getDefaultStyleContext();
        AttributeSet regular = styleContext.getStyle("default");
        styleContext.addAttribute(regular, StyleConstants.Foreground, Color.BLUE);
        styleContext.addAttribute(regular, StyleConstants.Foreground, Color.GREEN);
        styleContext.addAttribute(regular, StyleConstants.Foreground, Color.ORANGE);

        try {
            Object jsonObject = mapper.readValue(jsonText, Object.class);
            String formattedJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject);
            textArea.setText("");
            textArea.setTabSize(2);
            JsonParser parser = mapper.getFactory().createParser(formattedJson);

            label53:
            while(true) {
                while(true) {
                    if (parser.isClosed()) {
                        break label53;
                    }

                    JsonToken jsonToken = parser.nextToken();
                    if (JsonToken.FIELD_NAME.equals(jsonToken)) {
                        textArea.append(parser.getCurrentName() + ": ");
                        textArea.setCaretPosition(textArea.getDocument().getLength());
                    } else if (JsonToken.VALUE_STRING.equals(jsonToken)) {
                        textArea.append(parser.getText() + "\n");
                        textArea.setCaretPosition(textArea.getDocument().getLength());
                    } else if (!JsonToken.VALUE_NUMBER_INT.equals(jsonToken) && !JsonToken.VALUE_NUMBER_FLOAT.equals(jsonToken)) {
                        if (!JsonToken.START_OBJECT.equals(jsonToken) && !JsonToken.START_ARRAY.equals(jsonToken)) {
                            if (JsonToken.END_OBJECT.equals(jsonToken) || JsonToken.END_ARRAY.equals(jsonToken)) {
                                textArea.append(Arrays.toString(parser.getCurrentToken().asByteArray()) + "\n");
                                textArea.setCaretPosition(textArea.getDocument().getLength());
                            }
                        } else {
                            textArea.append(Arrays.toString(parser.getCurrentToken().asByteArray()) + "\n");
                            textArea.setCaretPosition(textArea.getDocument().getLength());
                        }
                    } else {
                        textArea.append(String.valueOf(parser.getValueAsInt()) + "\n");
                        textArea.setCaretPosition(textArea.getDocument().getLength());
                    }
                }
            }
        } catch (Exception var14) {
            Exception ex = var14;
            textArea.setText("Error displaying JSON: " + ex.getMessage());
        }

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(800, 600));
        JOptionPane.showMessageDialog((Component)null, scrollPane, "JSON Result", -1);
    }

    private  void handleException(ApiException e) {
        JOptionPane.showMessageDialog((Component)null, "Exception when calling API: " + e.getMessage());
    }
}
