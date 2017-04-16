package teammates.client.scripts.scalabilitytests;

import org.testng.annotations.Test;
import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.common.util.Logger;
import teammates.client.scripts.util.Stopwatch;
import teammates.test.cases.browsertests.BaseUiTestCase;
import teammates.test.driver.FileHelper;
import teammates.test.pageobjects.InstructorCourseEnrollPage;

import java.io.IOException;

/**
 * SUT: {@link Const.ActionURIs#INSTRUCTOR_COURSE_ENROLL_PAGE}.
 *
 * <p>Run InstructorCourseEnrollPageScaleTestDataGenerator.java before running the tests.
 */
public class InstructorCourseEnrollPageScaleTest extends BaseUiTestCase {

    private static final String DATA_FOLDER_PATH = "src/client/java/teammates/client/scripts/scalabilitytests/data/";
    private static final Logger log = Logger.getLogger();

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/InstructorCourseEnrollPageUiTest.json");
        removeAndRestoreDataBundle(testData);
    }

    @Test
    public void testInstructorCourseEnrollPage() throws Exception {
        int[] loads = {10, 20, 50, 75, 100, 150};
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

        InstructorCourseEnrollPage enrollPage = loginAdminToPage(enrollUrl, InstructorCourseEnrollPage.class);

        String enrollString =
                FileHelper.readFile(DATA_FOLDER_PATH + "InstructorCourseEnrollPageScaleTestData" + numStudents);

        log.info("Testing with " + numStudents + " students...");
        Stopwatch stopwatch = new Stopwatch();
        stopwatch.start();
        enrollPage.enroll(enrollString);
        stopwatch.logTimeElapsedInSeconds(log);
    }
}
