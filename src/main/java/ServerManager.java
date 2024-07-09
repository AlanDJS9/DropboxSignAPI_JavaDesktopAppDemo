import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import javax.swing.JTextArea;
import org.apache.commons.io.IOUtils;
import java.util.Arrays;
import java.util.Map;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class ServerManager {
    private HttpServer server;
    private int port;
    private JTextArea textArea;

    public ServerManager(int port, JTextArea textArea) {
        this.port = port;
        this.textArea = textArea;
    }

    public void startServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);

        // Endpoint 1: Root endpoint
        server.createContext("/", new RootHandler());

        // Endpoint 2: Hello endpoint
        server.createContext("/hello", new HelloHandler());

        // Endpoint 3: Bye endpoint
        server.createContext("/test", new TestHandler());

        // Endpoint 4: sigReqEvents endpoint
        server.createContext("/sigReqEvents", new SigReqEventsHandler());

        server.setExecutor(null); // Use the default executor
        server.start();
        textArea.append("Server is running on port " + port + "\n");
    }

    public void stopServer() {
        if (server != null) {
            server.stop(0);
            textArea.append("Server stopped.\n");
        }
    }

    private class RootHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = "Root endpoint!";
            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
            textArea.append("Handled request for Root endpoint\n");
        }
    }

    private class HelloHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = "Hello, World!";
            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
            textArea.append("Handled request for Hello endpoint\n");
        }
    }

    private class TestHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = "Hello API Event Received";
            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
            textArea.append("Handled request for Test endpoint\n");
        }
    }

    private class SigReqEventsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = "Hello API Event Received";
            exchange.sendResponseHeaders(200, response.length());
            InputStream inputStream = exchange.getRequestBody();
            String body = IOUtils.toString(inputStream, String.valueOf(StandardCharsets.UTF_8));
            textArea.append("Received POST request at /sigReqEvents\n");
            textArea.append(body + "\n");
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();

            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> jsonMap = objectMapper.readValue(body, Map.class);
            textArea.append(Arrays.toString("Hello API Event Received".getBytes()) + "\n");

            // Process the event
            Map<String, Object> event = (Map<String, Object>) jsonMap.get("event");
            String eventTime = (String) event.get("event_time");
            String eventType = (String) event.get("event_type");
            String eventHash = (String) event.get("event_hash");

            String apiKey = System.getenv("API_KEY");
            String hash;
            try {
                Mac mac = Mac.getInstance("HmacSHA256");
                SecretKeySpec secretKeySpec = new SecretKeySpec(apiKey.getBytes(), "HmacSHA256");
                mac.init(secretKeySpec);
                byte[] hmacBytes = mac.doFinal((eventTime + eventType).getBytes());
                hash = Base64.getEncoder().encodeToString(hmacBytes);
            } catch (NoSuchAlgorithmException | InvalidKeyException e) {
                textArea.append("Error generating hash: " + e.getMessage() + "\n");
                return;
            }

            textArea.append("Hash Key Check: " + hash + "\n");
            textArea.append("Hash from Event: " + eventHash + "\n");
            textArea.append("Event Type: " + eventType + "\n");
        }
    }
}

