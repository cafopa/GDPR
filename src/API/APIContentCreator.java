package API;

import GDPR.Article;
import GDPR.Chapter;
import GDPR.GDPR;
import GUI.Controller;
import okhttp3.Request;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class APIContentCreator {
    private final Controller controller;
    private HashMap<Integer, List<Integer>> parentChildObjectMapping = new HashMap<>();
    APIConnector apiConnector;
    GDPR gdpr;

    public APIContentCreator(Controller controller, APIConnector apiConnector, GDPR gdpr, String input) {
        this.apiConnector = apiConnector;
        this.gdpr = gdpr;
        this.controller = controller;
        apiConnector.createRootObject(this, "Folder", input, "", "");
        createGDPRContent();

        //TODO skal flyttes over i controlleren
        controller.statusTxtField.setText("Status: Operation completed");
    }

    public void addObjectToObjectMap(int objectID, int parentObjectID) {
        if (parentChildObjectMapping.containsKey(parentObjectID)){
            parentChildObjectMapping.get(parentObjectID).add(objectID);
        } else {
            List<Integer> list = new ArrayList<>();
            list.add(objectID);
            parentChildObjectMapping.put(parentObjectID, list);
        }
    }

    public void createGDPRContent() {
        gdpr.getChapters().forEach(chapter -> createChapter(chapter));
    }

    private void createChapter(Chapter chapter) {
        int chapterObjectID = createChapterObject(chapter);
        createArticles(chapterObjectID, chapter);
    }

    private void createArticles(int chapterObjectID, Chapter chapter) {
        for (Article article : chapter.getArticleList()) {

            StringBuilder stringBuilder = new StringBuilder();

            article.getText().forEach(s -> {
                if (Character.isDigit(s.substring(0, 1).charAt(0))) {
                    if (Integer.parseInt(s.substring(0, 1)) != 1) {
                        stringBuilder.append("\\n");
                        stringBuilder.append(s);
                        stringBuilder.append("\\n");
                    } else {
                        stringBuilder.append(s);
                        stringBuilder.append("\\n");
                    }
                } else {
                    stringBuilder.append(s);
                    stringBuilder.append("\\n");
                }
            });

            apiConnector.createObject(this, chapterObjectID, "Requirement", article.getHeader(), stringBuilder.toString(), "");
        }
    }

    private int createChapterObject(Chapter chapter) {
        return apiConnector.createNodeObject(this,"Requirement", chapter.getHeader(), "", "");
    }

}
