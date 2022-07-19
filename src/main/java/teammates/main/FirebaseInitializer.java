package teammates.main;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import teammates.common.util.Logger;

/**
 * Setup in web.xml to initialize FirebaseApp instance at application startup.
 **/
public class FirebaseInitializer implements ServletContextListener {

    private static final Logger log = Logger.getLogger();

    @Override
    public void contextInitialized(ServletContextEvent event) {
        try {
            FileInputStream serviceAccount = new FileInputStream("service-account.json");
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();
//            FirebaseOptions options = FirebaseOptions.builder()
//                    .setCredentials(GoogleCredentials.getApplicationDefault())
//                    .build();
            FirebaseApp firebaseApp = FirebaseApp.initializeApp(options);
            log.info("Initialized FirebaseApp instance of name " + firebaseApp.getName());
        } catch (FileNotFoundException | SecurityException e) {
            log.severe("File cannot be read.");
        } catch (IOException e) {
            log.severe("Google credentials cannot be created.");
        } catch (IllegalStateException e) {
            log.severe("The default FirebaseApp has already been initialized.");
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        FirebaseApp.getInstance().delete();
    }
}
