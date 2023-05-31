import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Timer;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509KeyManager;
import javax.net.ssl.X509TrustManager;

public class TimeServer {
    private static final String SSL_PROTOCOL = "TLSv1.2";
    private static final String KEY_STORE_PATH = "scripts/server/serverkeystore.jks";
    private static final String KEY_STORE_PASSWORD = "server";
    private static final String TRUST_STORE_PATH = "scripts/server/servertruststore.jts";
    private static final String TRUST_STORE_PASSWORD = "client";

    public static void main(String[] args) throws UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, CertificateException, IOException, KeyManagementException {
        if (args.length < 1)
            return;

        // The Port number through wich server will accept client connections
        int port = Integer.parseInt(args[0]);

        // Generate a unique and secure key pair for the server
        KeyManager[] keyManagers = createKeyManager();

        // Create a trust manager to verify client certificates
        TrustManager[] trustManagers = createTrustManager();

        // Configure the SSL context with the server key pair and trust manager
        SSLContext sslContext = SSLContext.getInstance(SSL_PROTOCOL);
        sslContext.init(keyManagers, trustManagers, new SecureRandom());

        // Create a secure server socket that accepts client connections
        SSLServerSocketFactory socketFactory = sslContext.getServerSocketFactory();
        SSLServerSocket serverSocket = (SSLServerSocket) socketFactory.createServerSocket(port);
        System.out.println("Server listenig to port " + port);

        // Require client authentication for each connection
        serverSocket.setNeedClientAuth(true);

        // Set protocol
        serverSocket.setEnabledProtocols(new String[] { SSL_PROTOCOL });

        // Create data for server
        Data data = new Data();
        Timer timer = new Timer(true);
        timer.schedule(new DataTimer(data), 0, 60000);

        while (true) {  
            try {
                // Wait for a client connection
                SSLSocket clientSocket = (SSLSocket) serverSocket.accept();
                System.out.println("Client connected: " + clientSocket);

                // Verify the client's certificate
                X509Certificate[] clientCertificates = (X509Certificate[]) clientSocket.getSession().getPeerCertificates();
                String username = verifyClientCertificate(clientCertificates);

                // Handle Client
                ClientHandler clientHandler = new ClientHandler(clientSocket, data, username);
                new Thread(clientHandler, "Client Handler").start();
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    private static KeyManager[] createKeyManager()
            throws NoSuchAlgorithmException, UnrecoverableKeyException, KeyStoreException, CertificateException, IOException {
        // Load the key store
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(getKeyStore(), KEY_STORE_PASSWORD.toCharArray());

        // Create a key manager
        X509KeyManager keyManager = (X509KeyManager) keyManagerFactory.getKeyManagers()[0];
        return new KeyManager[] { keyManager };
    }

    private static KeyStore getKeyStore()
            throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
        // Load the key store from a file
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        FileInputStream fileInputStream = new FileInputStream(KEY_STORE_PATH);
        keyStore.load(fileInputStream, KEY_STORE_PASSWORD.toCharArray());
        fileInputStream.close();
        return keyStore;
    }

    private static TrustManager[] createTrustManager() throws NoSuchAlgorithmException, KeyStoreException, CertificateException, IOException {
        // Load the trust store with trusted client certificates
        TrustManagerFactory trustManagerFactory = TrustManagerFactory
                .getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(getTrustStore());

        // Create a trust manager that trusts the loaded certificates
        X509TrustManager trustManager = (X509TrustManager) trustManagerFactory.getTrustManagers()[0];
        return new TrustManager[] { trustManager };
    }

    private static KeyStore getTrustStore()
            throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
        // Load the trust store from a file
        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        FileInputStream fileInputStream = new FileInputStream(TRUST_STORE_PATH);
        trustStore.load(fileInputStream, TRUST_STORE_PASSWORD.toCharArray());
        fileInputStream.close();
        return trustStore;
    }

    private static String verifyClientCertificate(X509Certificate[] clientCertificates) {
        // Verify the client's certificate against the trusted certificate authority
        // This can involve checking the certificate chain, expiry date, revocation
        // status, etc.
        // In this example, we simply print out the client's certificate subject name
        // for debugging purposes
        String username = clientCertificates[0].getSubjectX500Principal().getName().split(",")[0].substring(3);
        System.out.println("Client certificate subject name: " + username);
        return username;
    }
}

