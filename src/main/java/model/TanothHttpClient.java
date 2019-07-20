package model;

import java.io.IOException;
import java.net.CookieManager;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class TanothHttpClient {
    private String server;
    private String serverPath;
    private String user;
    private String password;
    private String loginURI;
    private HttpClient httpClient;
    private HttpRequest loginRequest;
    private String loginResponse;

    public TanothHttpClient(String user, String password, String loginURI, String serverNumber) {
        this.loginURI = loginURI;
        this.user = user;
        this.password = password;
        this.server = serverNumber;
        //HTTP Protocol - Specifications
        httpClient = HttpClient.newBuilder()
                .cookieHandler(new CookieManager())
                .version(HttpClient.Version.HTTP_1_1)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
    }

    public void login() throws IOException, InterruptedException {
        loginRequest = HttpRequest.newBuilder()
                .uri(URI.create(loginURI))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .timeout(Duration.ofMinutes(1))
                .POST(HttpRequest.BodyPublishers.ofString("server=" + server + "&username=" + user + "&userpass=" + password))
                .build();
        loginResponse = httpClient.send(loginRequest, HttpResponse.BodyHandlers.ofString()).body();
    }

    public String getLoginResponse() {
        return loginResponse;
    }

    public void setServerPath(String serverPath) {
        this.serverPath = serverPath;
    }

    public String getXMLByAction(GameActionRequest GAR) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(serverPath))
                .header("Content-Type", "text/xml")
                .timeout(Duration.ofMinutes(1))
                .POST(HttpRequest.BodyPublishers.ofString(GAR.getRequest()))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }


}
