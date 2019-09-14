package model;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class FlashVarsParser {

    private String serverPath;
    private String sessionID;

    public FlashVarsParser(TanothHttpClientSingleton httpClient) {
        serverPath = parseServerPath(httpClient);
        sessionID = parseSessionID(httpClient);
    }

    private String parseServerPath(TanothHttpClientSingleton httpClient) {
        Document doc = Jsoup.parse(httpClient.getLoginResponse());
        Element scriptData = doc.select("body").select("script").get(0);
        return StringUtils.substringBetween(scriptData.toString(), "serverpath: \"", "\",");
    }

    private String parseSessionID(TanothHttpClientSingleton httpClient) {
        Document doc = Jsoup.parse(httpClient.getLoginResponse());
        Element scriptData = doc.select("body").select("script").get(0);
        return StringUtils.substringBetween(scriptData.toString(), "sessionID: \"", "\",");
    }

    public String getServerPath() {
        return serverPath;
    }

    public String getSessionID() {
        return sessionID;
    }

}
