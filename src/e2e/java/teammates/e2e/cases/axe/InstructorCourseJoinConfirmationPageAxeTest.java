package teammates.e2e.cases.axe;

import org.testng.annotations.Test;

import com.deque.html.axecore.results.Results;

import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.common.util.KeyUtil;
import teammates.e2e.pageobjects.CourseJoinConfirmationPage;
import teammates.storage.entity.Instructor;

/**
 * SUT: {@link Const.WebPageURIs#JOIN_PAGE}.
 */
public class InstructorCourseJoinConfirmationPageAxeTest extends BaseAxeTestCase {
    private Instructor newInstructor;

    @Override
    protected void prepareTestData() {
        testData = removeAndRestoreDataBundle(
                loadDataBundle("/InstructorCourseJoinConfirmationPageE2ETest.json"));

        newInstructor = testData.instructors.get("ICJoinConf.instr.CS1101");
    }

    @Test
    @Override
    public void testAll() {
        AppUrl joinLink = createFrontendUrl(Const.WebPageURIs.JOIN_PAGE)
                .withKey(KeyUtil.encryptCourseJoinKey(newInstructor.getId(), newInstructor.getRegKey()))
                .withEntityType(Const.EntityType.INSTRUCTOR);
        CourseJoinConfirmationPage confirmationPage = loginToPage(
                joinLink, CourseJoinConfirmationPage.class, newInstructor.getEmail());

        Results results = getAxeBuilder().analyze(confirmationPage.getBrowser().getDriver());
        assertViolationFree(results);
    }

}
