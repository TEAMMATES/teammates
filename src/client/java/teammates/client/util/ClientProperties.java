package teammates.client.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Represents properties in client.properties file.
 */
public final class ClientProperties {

    /** The value of "client.target.url" in client.properties file. */
    public static final String TARGET_URL;

    /** The value of "client.api.url" in client.properties file. */
    public static final String API_URL;

    /** The value of "client.backdoor.key" in client.properties file. */
    public static final String BACKDOOR_KEY;

    /** The value of "client.csrf.key" in client.properties file. */
    public static final String CSRF_KEY;

    /** The value of "client.script.api.url" in client.properties file. */
    public static final String SCRIPT_API_URL;

    /** The value of "client.script.api.name" in client.properties file. */
    public static final String SCRIPT_API_NAME;

    /** The value of "client.script.api.password" in client.properties file. */
    public static final String SCRIPT_API_PASSWORD;

    static {
        Properties prop = new Properties();
        try {
            try (InputStream testPropStream = Files
                    .newInputStream(Paths.get("src/client/resources/client.properties"))) {
                prop.load(testPropStream);
            }

            TARGET_URL = prop.getProperty("client.target.url");
            API_URL = prop.getProperty("client.api.url");
            BACKDOOR_KEY = prop.getProperty("client.backdoor.key");
            CSRF_KEY = prop.getProperty("client.csrf.key");

            SCRIPT_API_URL = prop.getProperty("client.script.api.url");
            SCRIPT_API_NAME = prop.getProperty("client.script.api.name");
            SCRIPT_API_PASSWORD = prop.getProperty("client.script.api.password");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private ClientProperties() {
        // access static fields directly
    }

    public static boolean isTargetUrlDevServer() {
        return TARGET_URL.matches("^https?://localhost:[0-9]+(/.*)?");
    }
}
