package teammates.main;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.time.zone.ZoneRulesProvider;
import java.util.logging.Level;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.component.LifeCycle;
import org.eclipse.jetty.webapp.WebAppContext;

import org.jetbrains.annotations.NotNull;
import teammates.common.util.Config;
import teammates.common.util.Logger;
import teammates.ui.servlets.DevServerLoginServlet;

/**
 * Entrypoint to the system.
 *
 * @see <a href="https://github.com/GoogleCloudPlatform/java-docs-samples/blob/master/appengine-java11/appengine-simple-jetty-main/src/main/java/com/example/appengine/demo/jettymain/Main.java">https://github.com/GoogleCloudPlatform/java-docs-samples/blob/master/appengine-java11/appengine-simple-jetty-main/src/main/java/com/example/appengine/demo/jettymain/Main.java</a>
 */
// CHECKSTYLE.OFF:UncommentedMain this is the entrypoint class
public final class Application {

    private static final Logger log = Logger.getLogger(Application.class);
    private static final String JETTY_LOG_LEVEL_PROPERTY = "org.eclipse.jetty.LEVEL";
    private static final String INFO = "INFO";

    private Application() {
        // prevent initialization
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // ok to ignore as this is a startup method
    public static void main(String[] args) {
        try {
            System.setProperty(JETTY_LOG_LEVEL_PROPERTY, INFO);

            final int port = Config.getPort();
            Server server = new Server(port);

            final WebAppContext webapp = getWebAppContext();

            server.setHandler(webapp);
            server.setStopAtShutdown(true);
            server.addEventListener(createLifeCycleListener());

            server.start();
            server.join();
        } catch (URISyntaxException | IOException e) {
            log.log(Level.SEVERE, "Failed to determine WAR path", e);
        } catch (Exception e) {
            log.log(Level.SEVERE, "Failed to start the server", e);
        }
    }

    private static WebAppContext getWebAppContext() throws URISyntaxException, IOException {
        final WebAppContext webapp = new WebAppContext();
        webapp.setContextPath("/");

        final String warPath = Application.getWarPath();
        webapp.setWar(warPath);

        if (Config.isDevServerLoginEnabled()) {
            // For dev server, we dynamically add servlet to serve the dev server login page.
            ServletHolder devServerLoginServlet = new ServletHolder("DevServerLoginServlet", new DevServerLoginServlet());
            webapp.addServlet(devServerLoginServlet, "/devServerLogin");
        }
        return webapp;
    }
    private static String getWarPath() throws URISyntaxException, IOException {
        return Paths.get(Application.class.getProtectionDomain().getCodeSource().getLocation().toURI())
                .getParent().getParent().toString();
    }

    private static LifeCycle.Listener createLifeCycleListener() {
        return new LifeCycle.Listener() {
            @Override
            public void lifeCycleStarting(LifeCycle event) {
                log.startup();
            }


            @Override
            public void lifeCycleStarted(LifeCycle event) {
                log.info("Using zone rules version " + ZoneRulesProvider.getVersions("UTC").firstKey());
            }

            @Override
            public void lifeCycleFailure(LifeCycle event, Throwable cause) {
                log.log(Level.SEVERE, "Instance failed to start/stop: " + Config.getInstanceId(), new Exception(cause));
            }

            @Override
            public void lifeCycleStopping(LifeCycle event) {
                log.shutdown();
            }

            @Override
            public void lifeCycleStopped(LifeCycle event) {
                // do nothing
            }
        };
    }
}

