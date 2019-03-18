package teammates.e2e.cases.e2e;

import org.openqa.selenium.By;
import org.testng.annotations.Test;

import teammates.e2e.pageobjects.StudentHomePage;

/**
 * SUT: {@link teammates.common.util.Const.WebPageURIs#STUDENT_COURSE_DETAILS_PAGE}.
 */
public class StudentCourseDetailsPageE2ETest extends BaseE2ETestCase {

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/StudentCourseDetailsPageE2ETest.json");
        removeAndRestoreDataBundle(testData);
    }

    @Test
    public void testAll() {

        ______TS("Typical case: Log in with all available student course details");

        StudentHomePage shp = getHomePage().clickStudentLogin().loginAsStudent(
                testData.students.get("SCDetailsUiT.alice").googleId, "");
        shp.loadStudentCourseDetails();

        browser.waitForPageLoad();
        verifyContent();
    }

    private void verifyContent() {
        String instructorDetails = browser.driver.findElements(By.tagName("tbody")).get(0).getText();
        String teammatesDetails = browser.driver.findElements(By.tagName("tbody")).get(1).getText();
        String courseDetails = browser.driver.findElements(By.className("form")).get(0).getText();
        String studentDetails = browser.driver.findElements(By.className("form")).get(1).getText();

        assertTrue(courseDetails.contains(testData.courses.get("SCDetailsUiT.CS2104").getId()));
        assertTrue(instructorDetails.contains(testData.instructors.get("SCDetailsUiT.instr").email));
        assertTrue(studentDetails.contains(testData.students.get("SCDetailsUiT.alice").email));
        assertTrue(teammatesDetails.contains(testData.profiles.get("SCDetailsUiT.student1InTSCourse").email));
    }
}
