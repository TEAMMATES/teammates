package teammates.e2e.cases.sql.axe;

import org.testng.annotations.Test;

import com.deque.html.axecore.results.Results;

import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.CourseJoinConfirmationPage;
import teammates.storage.sqlentity.Instructor;

/**
 * SUT: {@link Const.WebPageURIs#JOIN_PAGE}.
 */
public class InstructorCourseJoinConfirmationPageAxeTest extends BaseAxeTestCase {

    private static final String NEW_INSTRUCTOR_GOOGLE_ID = "tm.e2e.ICJoinConf.instr2";

    private Instructor newInstructor;

    @Override
    protected void prepareTestData() {
        testData = removeAndRestoreDataBundle(
                loadSqlDataBundle("/InstructorCourseJoinConfirmationPageE2ETestSql.json"));

        newInstructor = testData.instructors.get("ICJoinConf.instr.CS1101");
    }

    @Test
    @Override
    public void testAll() {
        AppUrl joinLink = createFrontendUrl(Const.WebPageURIs.JOIN_PAGE)
                .withRegistrationKey(getKeyForInstructor(
                        testData.courses.get("ICJoinConf.CS1101").getId(), newInstructor.getEmail()))
                .withEntityType(Const.EntityType.INSTRUCTOR);
        CourseJoinConfirmationPage confirmationPage = loginToPage(
                joinLink, CourseJoinConfirmationPage.class, NEW_INSTRUCTOR_GOOGLE_ID);

        Results results = getAxeBuilder().analyze(confirmationPage.getBrowser().getDriver());
        assertViolationFree(results);
    }

}
