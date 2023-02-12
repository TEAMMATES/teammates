package teammates.e2e.cases.axe;

import org.testng.annotations.Test;

import com.deque.html.axecore.results.Results;

import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.cases.BaseE2ETestCase;
import teammates.e2e.pageobjects.InstructorStudentRecordsPage;
import teammates.e2e.util.AxeUtil;

/**
 * SUT: {@link Const.WebPageURIs#INSTRUCTOR_STUDENT_RECORDS_PAGE}.
 */
public class InstructorStudentRecordsPageAxeTest extends BaseE2ETestCase {

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/InstructorStudentRecordsPageE2ETest.json");
        removeAndRestoreDataBundle(testData);
    }

    @Test
    @Override
    public void testAll() {
        AppUrl recordsPageUrl = createFrontendUrl(Const.WebPageURIs.INSTRUCTOR_STUDENT_RECORDS_PAGE)
                .withCourseId(testData.courses.get("CS2104").getId())
                .withStudentEmail(testData.students.get("benny.c.tmms@ISR.CS2104").getEmail());

        InstructorStudentRecordsPage recordsPage =
                loginToPage(recordsPageUrl, InstructorStudentRecordsPage.class,
                testData.instructors.get("teammates.test.CS2104").getGoogleId());

        Results results = AxeUtil.AXE_BUILDER.analyze(recordsPage.getBrowser().getDriver());
        assertTrue(AxeUtil.formatViolations(results), results.violationFree());
    }

}
