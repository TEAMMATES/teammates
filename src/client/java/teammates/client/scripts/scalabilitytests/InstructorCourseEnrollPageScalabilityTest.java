package teammates.client.scripts.scalabilitytests;

import java.io.IOException;

import org.testng.annotations.Test;

import teammates.client.scripts.util.Stopwatch;
import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.common.util.Logger;
import teammates.test.cases.browsertests.BaseUiTestCase;
import teammates.test.pageobjects.InstructorCourseEnrollPage;

/**
 * SUT: {@link Const.ActionURIs#INSTRUCTOR_COURSE_ENROLL_PAGE}.
 *
 * <p>Run InstructorCourseEnrollPageScaleTestDataGenerator.java before running the tests.
 */
public class InstructorCourseEnrollPageScalabilityTest extends BaseUiTestCase {

    private static final Logger log = Logger.getLogger();

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/InstructorCourseEnrollPageScTest.json");
        removeAndRestoreDataBundle(testData);
    }

    @Test
    public void testInstructorCourseEnrollPage() throws Exception {
        //Number of students for each case.
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
                        .withUserId(testData.instructors.get("CCEnrollScT.teammates.test").googleId)
                        .withCourseId(testData.courses.get("CCEnrollScT.CS2104").getId());

        InstructorCourseEnrollPage enrollPage = loginAdminToPage(enrollUrl, InstructorCourseEnrollPage.class);

        String enrollString = InstructorCourseEnrollPageDataGenerator.generateStudents(numStudents);

        log.info("Testing with " + numStudents + " students...");
        Stopwatch stopwatch = new Stopwatch();
        stopwatch.start();
        enrollPage.enroll(enrollString);
        log.info("Time taken: " + stopwatch.getTimeElapsedInSeconds());
    }
}
