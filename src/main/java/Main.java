import com.dropbox.sign.Configuration;
import com.dropbox.sign.api.AccountApi;
import com.dropbox.sign.api.EmbeddedApi;
import com.dropbox.sign.api.SignatureRequestApi;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Dropbox Sign API Client");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        JTabbedPane tabbedPane = new JTabbedPane();
        var props = new Properties();
        var envFile = Paths.get("src/main/resources/dev.env");
        try (var inputStream = Files.newInputStream(envFile)) {
            props.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String apiKey = (String) props.get("API_KEY");
        String clientId = (String) props.get("CLIENT_ID");

        Configuration.getDefaultApiClient().setApiKey(apiKey);
        AccountApi accountApi = new AccountApi(Configuration.getDefaultApiClient());
        SignatureRequestApi signatureRequestApi = new SignatureRequestApi(Configuration.getDefaultApiClient());
        EmbeddedApi embeddedApi = new EmbeddedApi(Configuration.getDefaultApiClient());

        tabbedPane.addTab("Account Management", new AccountActions().CreateAccountPanel(accountApi));
        tabbedPane.addTab("Signature Requests", new SignatureRequestActions().createSignaturePanel(signatureRequestApi));
        tabbedPane.addTab("Embedded Signatures", new EmbeddedActions().createEmbeddedPanel(signatureRequestApi, embeddedApi, clientId));
        tabbedPane.addTab("Callback Server Testing", new ServerActions().createServerPanel());

        frame.add(tabbedPane);
        frame.setVisible(true);
    }
}
