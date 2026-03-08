package teammates.e2e.cases.sql.axe;

import org.testng.annotations.Test;

import com.deque.html.axecore.results.Results;

import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.InstructorCourseStudentDetailsViewPageSql;

/**
 * SUT: {@link Const.WebPageURIs#INSTRUCTOR_COURSE_STUDENT_DETAILS_PAGE}.
 */
public class InstructorCourseStudentDetailsPageAxeTest extends BaseAxeTestCase {

    @Override
    protected void prepareTestData() {
        testData = loadSqlDataBundle("/InstructorCourseStudentDetailsPageE2ETestSql.json");
        removeAndRestoreDataBundle(testData);
        putDocuments(testData);
    }

    @Test
    @Override
    public void testAll() {
        AppUrl viewPageUrl = createFrontendUrl(Const.WebPageURIs.INSTRUCTOR_COURSE_STUDENT_DETAILS_PAGE)
                .withCourseId(testData.courses.get("ICSDet.CS2104").getId())
                .withStudentEmail(testData.students.get("ICSDet.jose.tmms").getEmail());
        InstructorCourseStudentDetailsViewPageSql viewPage =
                loginToPage(viewPageUrl, InstructorCourseStudentDetailsViewPageSql.class,
                testData.instructors.get("ICSDet.instr").getGoogleId());

        Results results = getAxeBuilder().analyze(viewPage.getBrowser().getDriver());
        assertTrue(formatViolations(results), results.violationFree());
    }

}
