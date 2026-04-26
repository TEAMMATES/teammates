package teammates.e2e.cases.axe;

import org.testng.annotations.Test;

import com.deque.html.axecore.results.Results;

import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.InstructorStudentListPageSql;

/**
 * SUT: {@link Const.WebPageURIs#INSTRUCTOR_STUDENT_LIST_PAGE}.
 */
public class InstructorStudentListPageAxeTest extends BaseAxeTestCase {

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/InstructorStudentListPageE2ETest.json");
        removeAndRestoreDataBundle(testData);
    }

    @Test
    @Override
    public void testAll() {
        AppUrl listPageUrl = createFrontendUrl(Const.WebPageURIs.INSTRUCTOR_STUDENT_LIST_PAGE);
        InstructorStudentListPageSql listPage = loginToPage(listPageUrl, InstructorStudentListPageSql.class,
                testData.instructors.get("instructorOfCourse1").getGoogleId());

        Results results = getAxeBuilder().analyze(listPage.getBrowser().getDriver());
        assertTrue(formatViolations(results), results.violationFree());
    }

}
