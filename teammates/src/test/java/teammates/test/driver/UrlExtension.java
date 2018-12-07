package teammates.test.driver;

import java.net.MalformedURLException;
import java.net.URL;

import teammates.common.util.Url;

/**
 * Holds additional methods for {@link Url} used only in tests.
 */
public final class UrlExtension {

    private UrlExtension() {
        // utility class
    }

    /**
     * Gets the relative path of a full URL. Useful for http/https-based URLs.
     * @throws MalformedURLException if the given {@code url} is malformed
     */
    public static String getRelativePath(String url) throws MalformedURLException {
        new URL(url); // ensure that the given URL is not malformed
        return new Url(url).toString();
    }

}
