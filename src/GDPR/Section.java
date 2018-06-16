package GDPR;

import org.jsoup.nodes.Element;

public class Section {
    int index;
    String header;

    public Section(int i, Element element, Element nextElement) {
        this.index = i;
        header = element.text() + " " + nextElement.text();
    }

    public int getIndex() {
        return index;
    }

    public String getHeader() {
        return header;
    }
}
