package teammates.main;

import java.io.File;

import org.eclipse.jetty.annotations.AnnotationConfiguration;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.Configuration.ClassList;
import org.eclipse.jetty.webapp.JettyWebXmlConfiguration;
import org.eclipse.jetty.webapp.WebAppContext;

import teammates.common.util.Config;
import teammates.ui.webapi.DevServerLoginServlet;
import teammates.ui.webapi.WebPageServlet;

/**
 * Entrypoint to the system.
 *
 * @see <a href="https://github.com/GoogleCloudPlatform/java-docs-samples/blob/master/appengine-java11/appengine-simple-jetty-main/src/main/java/com/example/appengine/demo/jettymain/Main.java">https://github.com/GoogleCloudPlatform/java-docs-samples/blob/master/appengine-java11/appengine-simple-jetty-main/src/main/java/com/example/appengine/demo/jettymain/Main.java</a>
 */
// CHECKSTYLE.OFF:UncommentedMain this is the entrypoint class
public final class Application {

    private Application() {
        // prevent initialization
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // ok to ignore as this is a startup method
    public static void main(String[] args) throws Exception {
        System.setProperty("org.eclipse.jetty.util.log.class", org.eclipse.jetty.util.log.StdErrLog.class.getName());
        System.setProperty("org.eclipse.jetty.LEVEL", "INFO");

        Server server = new Server(8080);

        WebAppContext webapp = new WebAppContext();
        webapp.setContextPath("/");
        String classPath = Application.class.getProtectionDomain().getCodeSource().getLocation().getFile();
        String warPath = new File(classPath).getParentFile().getParentFile().getAbsolutePath();
        webapp.setWar(warPath);
        ClassList classlist = ClassList.setServerDefault(server);

        if (Config.isDevServer()) {
            // For dev server, we dynamically add servlets to serve the dev server login page and the bundled front-end.
            // The front-end needs not be added in production server as it is configured separately in app.yaml.
            webapp.setWelcomeFiles(new String[] { "index.html" });

            ServletHolder webPageServlet = new ServletHolder("WebPageServlet", new WebPageServlet());
            webapp.addServlet(webPageServlet, "/web/*");

            ServletHolder devServerLoginServlet =
                    new ServletHolder("DevServerLoginServlet", new DevServerLoginServlet());
            webapp.addServlet(devServerLoginServlet, "/devServerLogin");
        }

        // Enable Jetty annotation scanning
        classlist.addBefore(
                JettyWebXmlConfiguration.class.getName(),
                AnnotationConfiguration.class.getName());

        server.setHandler(webapp);

        server.start();

        // By using the server.join() the server thread will join with the current thread.
        // See https://docs.oracle.com/javase/8/docs/api/java/lang/Thread.html#join-- for more details.
        server.join();
    }

}
