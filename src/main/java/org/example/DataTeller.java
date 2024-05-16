package org.example;

import com.esotericsoftware.yamlbeans.YamlReader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;
import java.util.Map;

public class DataTeller {
    private YamlReader reader;
    private Object cachedData;
    private List<String> unwanted_title_keywords = null;
    private Map<String, String> questions = null;
    private String cookie = null;
    private Map<String, String> credentials = null;
    public DataTeller(){
        try {
            reader = new YamlReader(new FileReader("src/main/resources/data.yml"));
            cachedData = reader.read();
            unwanted_title_keywords = getUnwantedKeywords();
            questions = getCachedQuestions();
            cookie = getCookie();
            credentials = getCredentials();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("Could not find data.yml file");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Could not read data.yml file");
        }
    }

    private Map<String, String> getCredentials() {
        try {
            if (credentials != null) {
                return credentials;
            }
            Map map = (Map) cachedData;
            credentials = (Map) map.get("credentials");
            return credentials;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Could not find credentials");
        }
        return null;
    }

    public String getCookie() {
        try {
            Map map = (Map) cachedData;
            return (String) map.get("cookie");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Could not find cookie");
        }
        return cookie;
    }

    public boolean setCookie(String cookie){
        try {
            Map map = (Map) cachedData;
            map.put("cookie", cookie);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Could not set cookie");
        }
        return false;
    }

    public Map<String, String> getCachedQuestions(){
        try {
            if (questions != null) {
                return questions;
            }
            Map map = (Map) cachedData;
            questions = (Map) map.get("questions");
            return questions;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Could not find answers");
        }
        return null;
    }


    public String getAnswer(String question){
        questions = getCachedQuestions();
        String answer = questions.get(question);
        if (answer != null){
            return answer;
        }
        System.out.println("Could not find cached answer to question:\n" + question);
        // Fetch answer from generative model
        answer = CVAI.ask(question);
        System.out.println("Fetched answer from generative model:\n" + answer);
        return answer;
    }

    public List<String> getUnwantedKeywords(){
        if (unwanted_title_keywords != null){
            return unwanted_title_keywords;
        }
        try {
            Map map = (Map) cachedData;
            return (List<String>) map.get("unwanted_title_keywords");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Could not find unwanted_title_keywords");
        }
        return null;
    }

    public List<String> getUnwantedCompanies(){
        try {
            Map map = (Map) cachedData;
            return (List<String>) map.get("unwanted_companies");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Could not find unwanted_companies");
        }
        return null;
    }

    public String getEmail() {
        return credentials.get("email");
    }

    public String getPassword() {
        return credentials.get("password");
    }
}
