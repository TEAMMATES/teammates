package teammates.common.util;

import java.util.Locale;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

/**
 * HTML-related helpers.
 */
public final class HtmlHelper {

    private HtmlHelper() {
        // utility class
    }

    /**
     * Converts HTML email content to a plain-text representation.
     * Hyperlinks are preserved as {@code visible (url)} when the visible text differs from the URL.
     */
    public static String htmlToPlainText(String html) {
        if (html == null) {
            return "";
        }
        if (html.isEmpty()) {
            return html;
        }
        Document doc = Jsoup.parse(html);
        while (true) {
            Elements anchors = doc.select("a[href]");
            if (anchors.isEmpty()) {
                break;
            }
            Element anchor = anchors.last();
            String href = anchor.attr("href");
            String visible = anchor.text();
            anchor.replaceWith(new TextNode(plainTextForAnchor(href, visible)));
        }
        return doc.text();
    }

    private static String plainTextForAnchor(String href, String visibleText) {
        if (href == null || href.isEmpty()) {
            return visibleText;
        }
        String trimmedHref = href.trim();
        if (trimmedHref.toLowerCase(Locale.ROOT).startsWith("javascript:")) {
            return visibleText;
        }
        String trimmedVisible = visibleText == null ? "" : visibleText.trim();
        if (trimmedVisible.isEmpty()) {
            return trimmedHref;
        }
        if (trimmedVisible.equals(trimmedHref)) {
            return trimmedHref;
        }
        return trimmedVisible + " (" + trimmedHref + ")";
    }

}
