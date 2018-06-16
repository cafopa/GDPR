package GDPR;

import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.List;

public class Article {
    int index;
    int siblingIndex;
    String header;
    List<String> text = new ArrayList<String>();

    public Article(int i, Element element, Element nextElement) {
        this.index = i;
        this.siblingIndex = element.siblingIndex();
        header = element.text() + " " + nextElement.text();
    }

    public void fillContent(List<Article> articleList, List<Element> inputContent) {
        int index = 0;

        for (Element element : inputContent) {
            if (header.contains(element.text())){
                index = inputContent.indexOf(element);
            }
        }

        index++;

        while (index < inputContent.size() &&
                (inputContent.get(index).attributes().get("class").contains("normal") ||
               inputContent.get(index).tag().getName() == "table")){
            text.add(inputContent.get(index).text());
            index++;
        }
    }

    public int getIndex() {
        return index;
    }

    public String getHeader() {
        return header;
    }

    public List<String> getText() {
        return text;
    }
}
