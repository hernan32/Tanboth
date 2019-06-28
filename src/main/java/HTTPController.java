import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import java.io.IOException;
import java.net.CookieManager;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class HTTPController {
    private String server = "7";
    private String user;
    private String password;
    private String loginURI = "https://s7-es.tanoth.gameforge.com/user/login/";
    private HttpClient httpClient;
    private String serverPath;
    private String sessionID;
    private HttpRequest loginRequest;

    public HTTPController(String user, String password) {
        this.user = user;
        this.password = password;
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
        setGameBasicData();
    }

    private void setGameBasicData () throws IOException, InterruptedException {
        HttpResponse<String> loginResponse = httpClient.send(loginRequest, HttpResponse.BodyHandlers.ofString());
        Document doc = Jsoup.parse(loginResponse.body());
        Element scriptData = doc.select("body").select("script").get(0);
        serverPath = StringUtils.substringBetween(scriptData.toString(), "serverpath: \"", "\",");
        sessionID = StringUtils.substringBetween(scriptData.toString(), "sessionID: \"", "\",");
    }

    public String getXMLByMethod(String methodName) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(serverPath))
                .header("Content-Type", "text/xml")
                .timeout(Duration.ofMinutes(1))
                .POST(HttpRequest.BodyPublishers.ofString("<methodCall> <methodName>" + methodName + "</methodName> <params> <param> <value> <string>" + sessionID + "</string> </value> </param> </params></methodCall>"))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }


}
