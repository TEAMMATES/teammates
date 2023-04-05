package teammates.e2e.cases.axe;

import org.testng.annotations.Test;

import com.deque.html.axecore.results.Results;

import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.cases.BaseE2ETestCase;
import teammates.e2e.pageobjects.InstructorCourseEnrollPage;
import teammates.e2e.util.AxeUtil;

/**
 * SUT: {@link Const.WebPageURIs#INSTRUCTOR_COURSE_ENROLL_PAGE}.
 */
public class InstructorCourseEnrollPageAxeTest extends BaseE2ETestCase {

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/InstructorCourseEnrollPageE2ETest.json");
        removeAndRestoreDataBundle(testData);
    }

    @Test
    @Override
    public void testAll() {
        AppUrl url = createFrontendUrl(Const.WebPageURIs.INSTRUCTOR_COURSE_ENROLL_PAGE)
                .withCourseId(testData.courses.get("ICEnroll.CS2104").getId());
        InstructorCourseEnrollPage enrollPage = loginToPage(url, InstructorCourseEnrollPage.class,
                testData.instructors.get("ICEnroll.teammates.test").getGoogleId());

        Results results = AxeUtil.AXE_BUILDER.analyze(enrollPage.getBrowser().getDriver());
        assertTrue(AxeUtil.formatViolations(results), results.violationFree());
    }

}
