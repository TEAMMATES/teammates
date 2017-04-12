package teammates.client.scripts.scalabilitytests;

import org.testng.annotations.Test;
import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.common.util.Logger;
import teammates.client.scripts.util.Stopwatch;
import teammates.test.cases.browsertests.BaseUiTestCase;
import teammates.test.pageobjects.InstructorCourseEnrollPage;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Covers 'enroll' view for instructors. Run 'instructorCourseEnrollPageScaleTestDataGenerator.py' before running the tests.
 * SUT: {@link InstructorCourseEnrollPage}.
 */
public class InstructorCourseEnrollPageScaleTest extends BaseUiTestCase {

    private static final String DATA_FOLDER_PATH = "src/client/java/teammates/client/scripts/scalabilitytests/";
    private InstructorCourseEnrollPage enrollPage;
    private Logger logger = Logger.getLogger();

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/InstructorCourseEnrollPageUiTest.json");
        removeAndRestoreDataBundle(testData);
    }

    @Test
    public void testInstructorCourseEnrollPage() throws Exception {
        int[] loads = {10, 20, 50, 75, 100, 150};
        testEnrollActionWithIncreasingLoad(loads);
    }

    private void testEnrollActionWithIncreasingLoad(int[] loads) throws Exception {
        for (int load : loads) {
            ______TS("enroll action: " + load + " students");
            testEnrollAction(load);
            removeAndRestoreDataBundle(testData);
        }
    }

    private void testEnrollAction(int numStudents) throws IOException {
        AppUrl enrollUrl =
                createUrl(Const.ActionURIs.INSTRUCTOR_COURSE_ENROLL_PAGE)
                        .withUserId(testData.instructors.get("CCEnrollUiT.teammates.test").googleId)
                        .withCourseId(testData.courses.get("CCEnrollUiT.CS2104").getId());

        enrollPage = loginAdminToPage(enrollUrl, InstructorCourseEnrollPage.class);

        String enrollString =
                readData("InstructorCourseEnrollPageScaleTestData" + numStudents, Charset.defaultCharset());

        logger.info("Testing with " + numStudents + " students...");
        Stopwatch stopwatch = new Stopwatch();
        stopwatch.start();
        enrollPage.enroll(enrollString);
        stopwatch.logTimeElapsedInSeconds(logger);
    }

    String readData(String filename, Charset encoding) throws IOException {
        byte[] encoded =
                Files.readAllBytes(Paths.get(DATA_FOLDER_PATH + filename));
        return new String(encoded, encoding);
    }
}
