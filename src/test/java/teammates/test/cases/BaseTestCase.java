package teammates.test.cases;

import static org.testng.AssertJUnit.assertTrue;

import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import teammates.common.datatransfer.DataBundle;
import teammates.common.util.Config;
import teammates.common.util.FileHelper;
import teammates.common.util.Utils;
import teammates.logic.backdoor.BackDoorLogic;
import teammates.test.driver.TestProperties;

/** Base class for all test cases */
public class BaseTestCase {


    /**
     * Test Segment divider. Used to divide a test case into logical sections.
     * The weird name is for easy spotting.
     * 
     * @param description
     *            of the logical section. This will be printed.
     */
    public static void ______TS(String description) {
        print(" * " + description);
    }

    public static void printTestCaseHeader() {
        print("[TestCase]---:" + 
                Thread.currentThread().getStackTrace()[2].getMethodName());
    }

    public static void printTestClassHeader() {
        print("[============================="
                + Thread.currentThread().getStackTrace()[2].getClassName()
                + "=============================]");
    }

    public static void printTestClassFooter() {
        print(Thread.currentThread().getStackTrace()[2].getClassName() + " completed");
    }

    protected static void print(String message) {
        System.out.println(message);
    }

    protected static void setLogLevelOfClass(Class<?> testedClass, Level level)
            throws NoSuchFieldException, IllegalAccessException {
        Field logField = testedClass.getDeclaredField("log");
        logField.setAccessible(true);
        Logger log = (Logger) logField.get(null);
        log.setLevel(level);
    }

    protected static void setConsoleLoggingLevel(Level level) {
        Logger topLogger = java.util.logging.Logger.getLogger("");
        Handler consoleHandler = null;
        for (Handler handler : topLogger.getHandlers()) {
            if (handler instanceof ConsoleHandler) {
                consoleHandler = handler;
                break;
            }
        }
        consoleHandler.setLevel(level);
    }

    protected static void setGeneralLoggingLevel(Level level) {
        java.util.logging.Logger.getLogger("").setLevel(level);
    }

    /**
     * Sets the general logging level to WARNING <br>
     * Sets the logging level of the given class to FINE <br>
     * Sets the logging level of the console to FINE <br>
     */
    protected static void turnLoggingUp(Class<?> classBeingTested)
            throws NoSuchFieldException, IllegalAccessException {
        setGeneralLoggingLevel(Level.WARNING);
        setLogLevelOfClass(classBeingTested, Level.FINE);
        setConsoleLoggingLevel(Level.FINE);
    }

    /**
     * Sets the logging level of the given class to WARNING <br>
     * Sets the logging level of the console to WARNING <br>
     */
    protected static void turnLoggingDown(Class<?> classBeingTested)
            throws NoSuchFieldException, IllegalAccessException {
        setLogLevelOfClass(classBeingTested, Level.WARNING);
        setConsoleLoggingLevel(Level.WARNING);
    }

    /**
     * Creates a DataBundle as specified in typicalDataBundle.json
     */
    protected static DataBundle getTypicalDataBundle() {
        return loadDataBundle("/typicalDataBundle.json");
    }
    
    protected static DataBundle loadDataBundle(String pathToJsonFile){
        if(pathToJsonFile.startsWith("/")){
            pathToJsonFile = TestProperties.TEST_DATA_FOLDER + pathToJsonFile;
        }
        String jsonString;
        try {
            jsonString = FileHelper.readFile(pathToJsonFile);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        jsonString = injectRealAccounts(jsonString);
        return Utils.getTeammatesGson().fromJson(jsonString, DataBundle.class);
    }

    /**
     * Creates in the datastore a fresh copy of data in typicalDataBundle.json
     */
    protected static void restoreTypicalDataInDatastore() throws Exception {
        BackDoorLogic backDoorLogic = new BackDoorLogic();
        DataBundle dataBundle = getTypicalDataBundle();
        backDoorLogic.persistDataBundle(dataBundle);
    }

    protected static void removeAndRestoreTypicalDataInDatastore() throws Exception {
        BackDoorLogic backDoorLogic = new BackDoorLogic();
        DataBundle dataBundle = getTypicalDataBundle();
        backDoorLogic.deleteExistingData(dataBundle);
        backDoorLogic.persistDataBundle(dataBundle);
    }
    
    protected static void removeTypicalDataInDatastore() throws Exception {
        BackDoorLogic backDoorLogic = new BackDoorLogic();
        DataBundle dataBundle = getTypicalDataBundle();
        backDoorLogic.deleteExistingData(dataBundle);
    }
    
    /**
     * Creates in the datastore a fresh copy of data in the given json file
     */
    protected static  void restoreDatastoreFromJson(String pathToJsonFile) throws Exception {
        BackDoorLogic backDoorLogic = new BackDoorLogic();
        DataBundle dataBundle = loadDataBundle(pathToJsonFile);
        backDoorLogic.persistDataBundle(dataBundle);
    }

    protected static void removeAndRestoreDatastoreFromJson(String pathToJsonFile) throws Exception {
        BackDoorLogic backDoorLogic = new BackDoorLogic();
        DataBundle dataBundle = loadDataBundle(pathToJsonFile);
        backDoorLogic.deleteExistingData(dataBundle);
        backDoorLogic.persistDataBundle(dataBundle);
    }

    protected void signalFailureToDetectException(String... messages) {
        throw new RuntimeException("Expected exception not detected."+ Arrays.toString(messages));
    }

    protected void ignoreExpectedException() {
        assertTrue(true);
    }

    private static String injectRealAccounts(String jsonString) {
        
        return jsonString
                .replace("${test.student1}", TestProperties.inst().TEST_STUDENT1_ACCOUNT)
                .replace("${test.student2}", TestProperties.inst().TEST_STUDENT2_ACCOUNT)
                .replace("${test.instructor}", TestProperties.inst().TEST_INSTRUCTOR_ACCOUNT)
                .replace("${support.email}", Config.SUPPORT_EMAIL);
    }

}
