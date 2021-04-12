package teammates.common.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;

/**
 * Class contains methods to sanitize user provided
 * parameters so that they conform to our data format
 * and possible threats can be removed first
 * as well as methods to revert sanitized text
 * back to its previous unsanitized state.
 */
public final class SanitizationHelper {

    private static PolicyFactory richTextPolicy =
            new HtmlPolicyBuilder()
                .allowStandardUrlProtocols()
                .allowAttributes("title").globally()
                .allowAttributes("href").onElements("a")
                .allowAttributes("src").onElements("img")
                .allowAttributes("align")
                    .matching(true, "center", "left", "right", "justify", "char")
                    .onElements("p")
                .allowAttributes("colspan", "rowspan").onElements("td", "th")
                .allowAttributes("cellspacing").onElements("table")
                .allowElements(
                    "a", "p", "div", "i", "b", "em", "blockquote", "tt", "strong", "hr",
                    "br", "ul", "ol", "li", "h1", "h2", "h3", "h4", "h5", "h6", "img", "span",
                    "table", "tr", "td", "th", "tbody", "tfoot", "thead", "caption", "colgroup",
                    "sup", "sub", "code")
                .allowElements("quote", "ecode")
                .allowStyling()
                .toFactory();
    private static final Logger log = Logger.getLogger();

    private SanitizationHelper() {
        // utility class
    }

    /**
     * Sanitizes a google ID by removing leading/trailing whitespace
     * and the trailing "@gmail.com".
     *
     * @return the sanitized google ID or null (if the parameter was null).
     */
    public static String sanitizeGoogleId(String rawGoogleId) {
        if (rawGoogleId == null) {
            return null;
        }

        String sanitized = rawGoogleId.trim();
        if (sanitized.toLowerCase().endsWith("@gmail.com")) {
            sanitized = sanitized.split("@")[0];
        }
        return sanitized.trim();
    }

    /**
     * Sanitizes an email address by removing leading/trailing whitespace.
     *
     * @return the sanitized email address or null (if the parameter was null).
     */
    public static String sanitizeEmail(String rawEmail) {
        return StringHelper.trimIfNotNull(rawEmail);
    }

    /**
     * Sanitizes name by removing leading, trailing, and duplicate internal whitespace.
     *
     * @return the sanitized name or null (if the parameter was null).
     */
    public static String sanitizeName(String rawName) {
        return StringHelper.removeExtraSpace(rawName);
    }

    /**
     * Sanitizes title by removing leading, trailing, and duplicate internal whitespace.
     *
     * @return the sanitized title or null (if the parameter was null).
     */
    public static String sanitizeTitle(String rawTitle) {
        return StringHelper.removeExtraSpace(rawTitle);
    }

    /**
     * Sanitizes a user input text field by removing leading/trailing whitespace.
     * i.e. comments, instructions, etc.
     *
     * @return the sanitized text or null (if the parameter was null).
     */
    public static String sanitizeTextField(String rawText) {
        return StringHelper.trimIfNotNull(rawText);
    }

    /**
     * Sanitizes the string with rich-text.
     * Removes disallowed elements based on defined policy.
     */
    public static String sanitizeForRichText(String content) {
        if (content == null) {
            return null;
        }
        return richTextPolicy.sanitize(sanitizeTextField(content));
    }

    /**
     * Sanitizes the string for inserting into HTML. Converts special characters
     * into HTML-safe equivalents.
     */
    public static String sanitizeForHtml(String unsanitizedString) {
        if (unsanitizedString == null) {
            return null;
        }
        return unsanitizedString.replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("/", "&#x2f;")
                .replace("'", "&#39;")
                //To ensure when apply sanitizeForHtml for multiple times, the string's still fine
                //Regex meaning: replace '&' with safe encoding, but not the one that is safe already
                .replaceAll("&(?!(amp;)|(lt;)|(gt;)|(quot;)|(#x2f;)|(#39;))", "&amp;");
    }

    /**
     * Recovers a html-sanitized string using {@link #sanitizeForHtml}
     * to original encoding for appropriate display.<br>
     * It restores encoding for < > \ / ' &  <br>
     * The method should only be used once on sanitized html
     *
     * @return recovered string
     */
    static String desanitizeFromHtml(String sanitizedString) {

        if (sanitizedString == null) {
            return null;
        }

        return sanitizedString.replace("&lt;", "<")
                              .replace("&gt;", ">")
                              .replace("&quot;", "\"")
                              .replace("&#x2f;", "/")
                              .replace("&#39;", "'")
                              .replace("&amp;", "&");
    }

    /**
     * Converts a string to be put in URL (replaces some characters).
     */
    public static String sanitizeForUri(String uri) {
        try {
            return URLEncoder.encode(uri, Const.ENCODING).replaceAll("\\+", "%20");
        } catch (UnsupportedEncodingException wontHappen) {
            log.warning("Unexpected UnsupportedEncodingException in "
                        + "SanitizationHelper.sanitizeForUri(" + uri + ", " + Const.ENCODING + ")");
            return uri;
        }
    }

    /**
     * Returns true if the {@code string} has evidence of having been sanitized.
     * A string is considered sanitized if it does not contain any of the chars '<', '>', '/', '\"', '\'',
     * and contains at least one of their sanitized equivalents or the sanitized equivalent of '&'.
     *
     * <p>Eg. "No special characters", "{@code <p>&quot;with quotes&quot;</p>}" are considered to be not sanitized.<br>
     *     "{@code &lt;p&gt; a p tag &lt;&#x2f;p&gt;}" is considered to be sanitized.
     * </p>
     */
    public static boolean isSanitizedHtml(String string) {
        return string != null
                && !StringHelper.isTextContainingAny(string, "<", ">", "\"", "/", "\'")
                && StringHelper.isTextContainingAny(string, "&lt;", "&gt;", "&quot;", "&#x2f;", "&#39;", "&amp;");
    }

    /**
     * Returns the desanitized {@code string} if it is sanitized, otherwise returns the unchanged string.
     */
    public static String desanitizeIfHtmlSanitized(String string) {
        return isSanitizedHtml(string) ? desanitizeFromHtml(string) : string;
    }

}
