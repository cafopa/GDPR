package GDPR;

import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.List;

public class Chapter {
    String sectionClassIdentifier1 = "ti-section-1";
    String sectionClassIdentifier2 = "expanded";
    String articleClassIdentifier = "ti-art";
    int index;
    int siblingIndex;
    String htmlID;
    String header;
    List<Element> inputContent;
    List<Article> articleList = new ArrayList<>();
    List<Section> sectionList = new ArrayList<>();
    List<Integer> indexList = new ArrayList<>();

    public Chapter(int index, String id, List<Element> chapterContent) {
        this.htmlID = id;
        this.inputContent = chapterContent;
        this.index = index;
        siblingIndex = inputContent.get(0).siblingIndex();
        header = inputContent.get(0).text() + " " + inputContent.get(1).text();

        findSectionsAndArticles(inputContent);
        fillArticlesWithContent(articleList, inputContent);
    }

    private void fillArticlesWithContent(List<Article> articleList, List<Element> inputContent) {
        articleList.forEach(article -> article.fillContent(articleList, inputContent));
    }

    private void findSectionsAndArticles(List<Element> inputContent) {
        for (int i = 0; i < inputContent.size()-1; i++) {
            Element element = inputContent.get(i);
            Element nextElement = inputContent.get(i+1);

            String htmlclass = element.attributes().get("class");

            if(sectionClassIdentifier1.contentEquals(htmlclass)){
                if (sectionClassIdentifier2.contentEquals(element.childNode(1).attributes().get("class"))){
                    Section section = new Section(i, element, nextElement);
                    sectionList.add(section);
                    indexList.add(i);
                }
            } else if(articleClassIdentifier.contentEquals(htmlclass)){
                Article article = new Article(i ,element, nextElement);
                articleList.add(article);
                indexList.add(i);
            }
        }
    }

    public String getHeader() {
        return header;
    }

    public List<Article> getArticleList() {
        return articleList;
    }

}
