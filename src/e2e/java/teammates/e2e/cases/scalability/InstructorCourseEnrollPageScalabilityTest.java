package teammates.e2e.cases.scalability;

import java.io.IOException;

import org.testng.annotations.Test;

import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.common.util.Logger;
import teammates.e2e.cases.e2e.BaseE2ETestCase;
import teammates.e2e.util.Stopwatch;
import teammates.e2e.util.StudentEnrollmentGenerator;
import teammates.test.pageobjects.InstructorCourseEnrollPage;

/**
 * SUT: {@link Const.ActionURIs#INSTRUCTOR_COURSE_ENROLL_PAGE}.
 *
 * <p>Run {@link InstructorCourseEnrollPageScaleTestDataGenerator} before running the tests.
 */
public class InstructorCourseEnrollPageScalabilityTest extends BaseE2ETestCase {

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
                createUrl(Const.WebPageURIs.INSTRUCTOR_COURSE_ENROLL_PAGE)
                        .withUserId(testData.instructors.get("CCEnrollScT.teammates.test").googleId)
                        .withCourseId(testData.courses.get("CCEnrollScT.CS2104").getId());

        InstructorCourseEnrollPage enrollPage = loginAdminToPageOld(enrollUrl, InstructorCourseEnrollPage.class);

        String enrollString = StudentEnrollmentGenerator.generateStudents(numStudents);

        log.info("Testing with " + numStudents + " students...");
        Stopwatch stopwatch = new Stopwatch();
        stopwatch.start();
        enrollPage.enroll(enrollString);
        log.info("Time taken: " + stopwatch.getTimeElapsedInSeconds());
    }
}
