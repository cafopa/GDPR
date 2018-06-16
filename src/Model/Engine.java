package Model;

import API.APIConnector;
import API.APIContentCreator;
import GDPR.GDPR;
import GUI.Controller;
import Webscraper.Scraper;
import okhttp3.Request;
import okhttp3.Response;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class Engine extends Thread {

    public void startDownloadAndSendContentToAPI(Controller controller, String option, String url, String repository, int objectID, String username, String password) {
        GDPR gdpr = new GDPR();
        File file = new File("output.json");

        switch (option) {
            case "Danish":
                new Scraper(gdpr, "https://eur-lex.europa.eu/legal-content/DA/TXT/HTML/?uri=CELEX:32016R0679&from=EN", "KAPITEL");
                break;
            case "English":
                new Scraper(gdpr, "https://eur-lex.europa.eu/legal-content/EN/TXT/HTML/?uri=CELEX:32016R0679&from=EN", "CHAPTER");
                break;
            case "German":
                new Scraper(gdpr, "https://eur-lex.europa.eu/legal-content/DE/TXT/HTML/?uri=CELEX:32016R0679&from=EN", "KAPITEL");
                break;
            case "Italian":
                new Scraper(gdpr, "https://eur-lex.europa.eu/legal-content/IT/TXT/HTML/?uri=CELEX:32016R0679&from=EN", "CAPO");
                break;
        }

        Thread t1 = new Thread(new Runnable() {
            public void run()
            {
                // JsonConverter jsonConverter = new JsonConverter(gdpr);
                // jsonConverter.writeJsonTo(file);

                APIConnector apiConnector = new APIConnector(controller, url, repository, objectID);
                apiConnector.encodeCredentialsToBase64(username, password);
                APIContentCreator apiContentCreator = new APIContentCreator(controller, apiConnector, gdpr, "GDPR (" + option +")");
                HashMap<Request, Response> responses = apiConnector.getRequestResponseHashMap();

                for (Response response : responses.values()) {
                    if (response.code() != 201){
                        System.out.println(response);
                    }
                }

                System.out.println(responses.entrySet());
            }});
        t1.start();

    }



    }
