import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.Scanner;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509KeyManager;
import javax.net.ssl.X509TrustManager;

public class TimeClient {
    private static final String SSL_PROTOCOL = "TLSv1.2";

    public static void main(String[] args) throws Exception {
        if (args.length < 2)
            return;

        // The server adress
        String serverAdress = args[0];

        // The Port number through wich server will accept client connections
        int port = Integer.parseInt(args[1]);

        System.out.println("Welcome to Hangman Game!");

        // Get credentials
        String username, password;
        Scanner sc = new Scanner(System.in);
        
        System.out.print("Insert your username: ");
        username = sc.nextLine();
        System.out.print("Insert your password: ");
        password = sc.nextLine();

        String KEY_STORE_PATH = "scripts/" + username + "/clientkeystore.jks";
        String KEY_STORE_PASSWORD = password;
        String TRUST_STORE_PATH = "scripts/" + username + "/clienttruststore.jts";
        String TRUST_STORE_PASSWORD = "server";

        // Generate a unique and secure key pair for the client
        KeyManager[] keyManagers = createKeyManager(KEY_STORE_PATH, KEY_STORE_PASSWORD);

        // Create a trust manager to verify server certificates
        TrustManager[] trustManagers = createTrustManager(TRUST_STORE_PATH, TRUST_STORE_PASSWORD);

        // Configure the SSL context with the client key pair and trust manager
        SSLContext sslContext = SSLContext.getInstance(SSL_PROTOCOL);
        sslContext.init(keyManagers, trustManagers, new SecureRandom());

        // Create a socket to connect to the server
        SSLSocketFactory socketFactory = sslContext.getSocketFactory();
        SSLSocket socket = (SSLSocket) socketFactory.createSocket(serverAdress, port);

        // Set protocol
        socket.setEnabledProtocols(new String[]{SSL_PROTOCOL});

        // Initiate client app
        OutputStream output = socket.getOutputStream();
        PrintWriter writer = new PrintWriter(output, true);
        InputStream input = socket.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));

        Thread read = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        String message = reader.readLine();
                        System.out.println(message);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        read.start();

        Thread write = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    String msg = sc.nextLine();
                    writer.println(msg);
                    writer.flush();
                }
            }
        });
        write.start();
    }

    private static KeyManager[] createKeyManager(String KEY_STORE_PATH, String KEY_STORE_PASSWORD) throws Exception {
        // Load the key store
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(getKeyStore(KEY_STORE_PATH, KEY_STORE_PASSWORD), KEY_STORE_PASSWORD.toCharArray());

        // Create a key manager
        X509KeyManager keyManager = (X509KeyManager) keyManagerFactory.getKeyManagers()[0];
        return new KeyManager[] { keyManager };
    }

    private static KeyStore getKeyStore(String KEY_STORE_PATH, String KEY_STORE_PASSWORD) throws Exception {
        // Load the key store from a file
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        FileInputStream fileInputStream = new FileInputStream(KEY_STORE_PATH);
        keyStore.load(fileInputStream, KEY_STORE_PASSWORD.toCharArray());
        fileInputStream.close();
        return keyStore;
    }

    private static TrustManager[] createTrustManager(String TRUST_STORE_PATH, String TRUST_STORE_PASSWORD) throws Exception {
        // Load the trust store with trusted server certificates
        TrustManagerFactory trustManagerFactory = TrustManagerFactory
                .getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(getTrustStore(TRUST_STORE_PATH, TRUST_STORE_PASSWORD));

        // Create a trust manager that trusts the loaded certificates
        X509TrustManager trustManager = (X509TrustManager) trustManagerFactory.getTrustManagers()[0];
        return new TrustManager[] { trustManager };
    }

    private static KeyStore getTrustStore(String TRUST_STORE_PATH, String TRUST_STORE_PASSWORD) throws Exception {
        // Load the trust store from a file
        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        FileInputStream fileInputStream = new FileInputStream(TRUST_STORE_PATH);
        trustStore.load(fileInputStream, TRUST_STORE_PASSWORD.toCharArray());
        fileInputStream.close();
        return trustStore;
    }
}
