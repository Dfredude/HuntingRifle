package org.example;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class CVAI {
    private final static String CVAAnswererAIURI = "https://cv-answerer-bpwjfipn5a-uc.a.run.app";
    private final static String CVAAnswererAIEndpoint = "/about";
    public static String ask(String question) {
        String paramName = "question";
        URI uri;
        URI uriWithParams;
        try {
            uri = new URI(CVAAnswererAIURI + CVAAnswererAIEndpoint);
            uriWithParams = new URI(uri.getScheme(), uri.getUserInfo(), uri.getHost(), uri.getPort(), uri.getPath(), paramName + "=" + question, uri.getFragment());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uriWithParams)
                .GET()
                .build();

        HttpClient client = HttpClient.newHttpClient();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String full_response = response.body();
            return full_response.substring(0, full_response.length()-1) ; // Trim trailing . from response
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            System.out.println("Could not fetch answer from generative model");
        }
        return null;
    }
}
