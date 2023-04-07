package teammates.main;

import java.io.File;
import java.time.zone.ZoneRulesProvider;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.component.LifeCycle;
import org.eclipse.jetty.webapp.WebAppContext;

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

    private static final Logger log = Logger.getLogger();

    private Application() {
        // prevent initialization
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // ok to ignore as this is a startup method
    public static void main(String[] args) throws Exception {
        System.setProperty("org.eclipse.jetty.LEVEL", "INFO");

        Server server = new Server(Config.getPort());

        WebAppContext webapp = new WebAppContext();
        webapp.setContextPath("/");
        String classPath = Application.class.getProtectionDomain().getCodeSource().getLocation().getFile();
        String warPath = new File(classPath).getParentFile().getParentFile().getAbsolutePath();
        webapp.setWar(warPath);

        if (Config.isDevServerLoginEnabled()) {
            // For dev server, we dynamically add servlet to serve the dev server login page.

            ServletHolder devServerLoginServlet =
                    new ServletHolder("DevServerLoginServlet", new DevServerLoginServlet());
            webapp.addServlet(devServerLoginServlet, "/devServerLogin");
        }

        LifeCycle.Listener customLifeCycleListener = new LifeCycle.Listener() {
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
                log.severe("Instance failed to start/stop: " + Config.getInstanceId());
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

        server.setHandler(webapp);
        server.setStopAtShutdown(true);
        server.addEventListener(customLifeCycleListener);

        server.start();

        // By using the server.join() the server thread will join with the current thread.
        // See https://docs.oracle.com/javase/8/docs/api/java/lang/Thread.html#join-- for more details.
        server.join();
    }

}
