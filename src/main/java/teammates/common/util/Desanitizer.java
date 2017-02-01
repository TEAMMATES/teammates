package teammates.common.util;

import java.util.HashSet;
import java.util.Set;

/**
 * Class contains methods to convert sanitized inputs to its previous unsanitized state.
 */
public final class Desanitizer {

    private Desanitizer() {
        // utility class
    }

    /**
     * Recovers the URL from sanitization due to {@link Sanitizer.sanitizeForNextUrl}.
     * In addition, any un-encoded whitespace (they may be there due to Google's
     * behind-the-screen decoding process) will be encoded again to +.
     */
    public static String desanitizeFromNextUrl(String url) {
        return url.replace("${amp}", "&").replace("${plus}", "%2B").replace("${hash}", "%23")
                  .replace(" ", "+");
    }

    /**
     * This recovers a html-sanitized string using {@link Sanitizer.sanitizeForHtml}
     * to original encoding for appropriate display in files such as csv file <br>
     * It restores encoding for < > \ / ' &  <br>
     * The method should only be used once on sanitized html
     * @param sanitizedString
     * @return recovered string
     */
    public static String desanitizeFromHtml(String sanitizedString) {

        if (sanitizedString == null) {
            return null;
        }

        return sanitizedString.replace("&lt;", "<")
                  .replace("&gt;", ">")
                  .replace("&quot;", "\"")
                  .replace("&#x2f;", "/")
                  .replace("&#39;", "'")
                  .replaceAll("&amp;", "&");
    }

    /**
     * This recovers a set of html-sanitized string using {@link Sanitizer.sanitizeForHtml}
     * to original encoding for appropriate display in files such as csv file <br>
     * It restores encoding for < > \ / ' &  <br>
     * The method should only be used once on sanitized html
     * @param sanitizedStringSet
     * @return recovered string set
     */
    public static Set<String> desanitizeFromHtml(Set<String> sanitizedStringSet) {
        Set<String> textSetTemp = new HashSet<String>();
        for (String text : sanitizedStringSet) {
            textSetTemp.add(desanitizeFromHtml(text));
        }
        return textSetTemp;
    }
}
