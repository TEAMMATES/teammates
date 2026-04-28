package teammates.e2e.cases.axe;

import org.testng.annotations.Test;

import com.deque.html.axecore.results.Results;

import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.InstructorCourseEditPageSql;

/**
 * SUT: {@link Const.WebPageURIs#INSTRUCTOR_COURSE_EDIT_PAGE}.
 */
public class InstructorCourseEditPageAxeTest extends BaseAxeTestCase {

    @Override
    protected void prepareTestData() {
        testData = removeAndRestoreDataBundle(loadDataBundle("/InstructorCourseEditPageE2ETest.json"));
    }

    @Test
    @Override
    public void testAll() {
        AppUrl url = createFrontendUrl(Const.WebPageURIs.INSTRUCTOR_COURSE_EDIT_PAGE)
                .withCourseId(testData.courses.get("ICEdit.CS2104").getId());
        InstructorCourseEditPageSql editPage = loginToPage(url, InstructorCourseEditPageSql.class,
                testData.instructors.get("ICEdit.coowner.CS2104").getGoogleId());

        Results results = getAxeBuilder().analyze(editPage.getBrowser().getDriver());
        assertViolationFree(results);
    }

}
