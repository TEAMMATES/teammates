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

    static {
        Properties prop = new Properties();
        try {
            try (InputStream testPropStream = Files.newInputStream(Paths.get("src/client/resources/client.properties"))) {
                prop.load(testPropStream);
            }

            TARGET_URL = prop.getProperty("client.target.url");

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
