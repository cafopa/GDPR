package Webscraper;

import API.APIConnector;
import API.APIContentCreator;
import GDPR.Chapter;
import GDPR.GDPR;
import JSON.JsonConverter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Scraper {
    Element body;
    List<String> listOfChapterSiblinIDs = new ArrayList<>();
    GDPR gdpr;

    public Scraper(GDPR gdpr, String url, String chapterInLocalLanguage) {
        this.gdpr = gdpr;

        body = getContentFromWeb(url);
        getAllChapterSiblingIDs(body, listOfChapterSiblinIDs, chapterInLocalLanguage);

        for (int i = 0; i < listOfChapterSiblinIDs.size(); i++) {
            List<Element> listOfElementsInChapter = new ArrayList<>();
            getContentOfChapter(body, listOfElementsInChapter, listOfChapterSiblinIDs, i, i + 1);
            gdpr.getChapters().add(new Chapter(i, listOfChapterSiblinIDs.get(i), listOfElementsInChapter));
        }
    }

    private Element getContentFromWeb(String url) {
        try {
            Document d = Jsoup.connect(url).timeout(10000).get();
            return d.body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<Element> getContentOfChapter(Element body, List<Element> chapterElements, List<String> chapterIDs, int start, int end) {

        if (end == chapterIDs.size()){
            Element something = body.getElementsByClass("final").get(0);
            int firstItem = body.getElementById(chapterIDs.get(start)).siblingIndex()-1;

            body.getAllElements().forEach(element -> {
                int index = element.siblingIndex();
                if (index > firstItem && index < something.siblingIndex()) {
                    chapterElements.add(element);
                }
            });
        } else {
            int firstItem = body.getElementById(chapterIDs.get(start)).siblingIndex()-1;
            int secondItem = body.getElementById(chapterIDs.get(end)).siblingIndex();

            body.getAllElements().forEach(element -> {
                int index = element.siblingIndex();
                if (index > firstItem && index < secondItem) {
                    chapterElements.add(element);
                }
            });
        }

        return chapterElements;
    }

    private void getAllChapterSiblingIDs(Element body, List<String> chapterIDs, String chapterDefiniton) {
        List<String> tempIDList = new ArrayList<>();
        for (Element element : body.getAllElements()){
            if (element.text().contains(chapterDefiniton) && element.text().length() < 20){
                tempIDList.add(element.attributes().get("id"));
            }
        }

        removeBlanksFromIDList(tempIDList, chapterIDs);
    }

    private void removeBlanksFromIDList(List<String> tempIDList, List<String> chapterIDs) {
        tempIDList.forEach(s -> { if (s.length() > 2) { chapterIDs.add(s); }});
    }


}
