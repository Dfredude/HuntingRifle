package org.example;

import com.esotericsoftware.yamlbeans.YamlReader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;
import java.util.Map;

public class DataTeller {
    YamlReader reader;
    Object data;
    private List<String> unwanted_title_keywords = null;
    private Map<String, String> questions = null;
    private String cookie = null;
    private Map<String, String> credentials = null;
    public DataTeller(){
        try {
            reader = new YamlReader(new FileReader("src/main/resources/data.yml"));
            data = reader.read();
            unwanted_title_keywords = getUnwantedKeywords();
            questions = getQuestions();
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
            Map map = (Map) data;
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
            Map map = (Map) data;
            return (String) map.get("cookie");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Could not find cookie");
        }
        return cookie;
    }

    public Map<String, String> getQuestions(){
        try {
            if (questions != null) {
                return questions;
            }
            Map map = (Map) data;
            questions = (Map) map.get("questions");
            return questions;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Could not find answers");
        }
        return null;
    }


    public String getAnswer(String question){
        try {
            questions = getQuestions();
            return questions.get(question);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Could not find answer to question:\n" + question);
        }
        return null;
    }

    public List<String> getUnwantedKeywords(){
        if (unwanted_title_keywords != null){
            return unwanted_title_keywords;
        }
        try {
            Map map = (Map) data;
            return (List<String>) map.get("unwanted_title_keywords");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Could not find unwanted_title_keywords");
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
