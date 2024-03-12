package teammates.e2e.cases.axe;

import org.testng.annotations.Test;

import com.deque.html.axecore.results.Results;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.CourseJoinConfirmationPage;

/**
 * SUT: {@link Const.WebPageURIs#JOIN_PAGE}.
 */
public class InstructorCourseJoinConfirmationPageAxeTest extends BaseAxeTestCase {
    InstructorAttributes newInstructor;

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/InstructorCourseJoinConfirmationPageE2ETest.json");
        removeAndRestoreDataBundle(testData);
        sqlTestData = removeAndRestoreSqlDataBundle(
                loadSqlDataBundle("/InstructorCourseJoinConfirmationPageE2ETest_SqlEntities.json"));

        newInstructor = testData.instructors.get("ICJoinConf.instr.CS1101");
        newInstructor.setGoogleId("tm.e2e.ICJoinConf.instr2");
    }

    @Test
    @Override
    public void testAll() {
        AppUrl joinLink = createFrontendUrl(Const.WebPageURIs.JOIN_PAGE)
                .withRegistrationKey(getKeyForInstructor(testData.courses.get("ICJoinConf.CS1101").getId(),
                        newInstructor.getEmail()))
                .withEntityType(Const.EntityType.INSTRUCTOR);
        CourseJoinConfirmationPage confirmationPage = loginToPage(
                joinLink, CourseJoinConfirmationPage.class, newInstructor.getGoogleId());

        Results results = getAxeBuilder().analyze(confirmationPage.getBrowser().getDriver());
        assertTrue(formatViolations(results), results.violationFree());
    }
}
