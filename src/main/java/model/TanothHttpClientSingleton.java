package model;

import configuration.ConfigSingleton;
import model.game.parser.GameAction;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.net.CookieManager;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;

public class TanothHttpClientSingleton {
    private final String serverNumber;
    private final String user;
    private final String password;
    private final String loginURI;
    private HttpClient httpClient;
    private String loginResponse;
    private static TanothHttpClientSingleton INSTANCE;
    private static String SERVER_PATH;
    private static String SESSION_ID;

    private TanothHttpClientSingleton() throws IOException, InterruptedException {
        loginURI = ConfigSingleton.getInstance().getProperty(ConfigSingleton.Property.serverURL);
        user = ConfigSingleton.getInstance().getProperty(ConfigSingleton.Property.user);
        password = ConfigSingleton.getInstance().getProperty(ConfigSingleton.Property.password);
        serverNumber = ConfigSingleton.getInstance().getProperty(ConfigSingleton.Property.serverNumber);

        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    public void checkClientTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }

                    public void checkServerTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }
                }
        };

        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            httpClient = HttpClient.newBuilder()
                    .cookieHandler(new CookieManager())
                    .version(HttpClient.Version.HTTP_1_1)
                    .followRedirects(HttpClient.Redirect.NORMAL)
                    .sslContext(sc)
                    .build();
            connect();
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }
    }

    public static TanothHttpClientSingleton getInstance() throws IOException, InterruptedException {
        if (INSTANCE == null) {
            INSTANCE = new TanothHttpClientSingleton();
            setSessionData();
        }
        return INSTANCE;
    }

    private static void setSessionData() {
        FlashVarsParser flashVars = new FlashVarsParser(INSTANCE);
        SERVER_PATH = flashVars.getServerPath();
        SESSION_ID = flashVars.getSessionID();
    }


    public void login() throws IOException, InterruptedException {
        connect();
        setSessionData();
    }

    public void connect() throws IOException, InterruptedException {
        HttpRequest loginRequest = HttpRequest.newBuilder()
                .uri(URI.create(loginURI))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .timeout(Duration.ofMinutes(1))
                .POST(HttpRequest.BodyPublishers.ofString("server=" + serverNumber + "&username=" + user + "&userpass=" + password))
                .build();
        loginResponse = httpClient.send(loginRequest, HttpResponse.BodyHandlers.ofString()).body();
    }

    public String getLoginResponse() {
        return loginResponse;
    }

    public String getSessionID() {
        return SESSION_ID;
    }

    public String getXMLByAction(GameAction GAR) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SERVER_PATH))
                .header("Content-Type", "text/xml")
                .timeout(Duration.ofMinutes(1))
                .POST(HttpRequest.BodyPublishers.ofString(GAR.getRequest()))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }


    public String getServerNumber() {
        return serverNumber;
    }

    public String getUser() {
        return user;
    }
}
