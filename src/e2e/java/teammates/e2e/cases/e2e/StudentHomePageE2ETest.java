package teammates.e2e.cases.e2e;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.e2e.util.TestProperties;

/**
 * SUT: {@link teammates.common.util.Const.WebPageURIs#STUDENT_HOME_PAGE}.
 */
public class StudentHomePageE2ETest extends BaseE2ETestCase {

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/StudentHomePageE2ETest.json");

        String student1GoogleId = TestProperties.TEST_STUDENT1_ACCOUNT;
        String student1Email = student1GoogleId + "@gmail.com";
        testData.accounts.get("alice.tmms").googleId = student1GoogleId;
        testData.accounts.get("alice.tmms").email = student1Email;

        // This student has a registered account but yet to join course
        testData.students.get("alice.tmms@SHomeUiT.CS2104").email = student1Email;

        testData.students.get("alice.tmms@SHomeUiT.CS1101").googleId = student1GoogleId;
        testData.students.get("alice.tmms@SHomeUiT.CS1101").email = student1Email;
        testData.students.get("alice.tmms@SHomeUiT.CS4215").googleId = student1GoogleId;
        testData.students.get("alice.tmms@SHomeUiT.CS4215").email = student1Email;
        testData.students.get("alice.tmms@SHomeUiT.CS4221").googleId = student1GoogleId;
        testData.students.get("alice.tmms@SHomeUiT.CS4221").email = student1Email;

        removeAndRestoreDataBundle(testData);
    }

    @Test
    public void allTests() throws Exception {
        testContentAndLogin();
    }

    private void testContentAndLogin() {

        ______TS("login successfully");

        getHomePage().clickStudentLogin().loginAsStudent(
                TestProperties.TEST_STUDENT1_ACCOUNT, TestProperties.TEST_STUDENT1_PASSWORD);

        browser.waitForPageLoad();

        List<String> courseIds = getAllVisibleCourseIds();

        for (int i = 0; i < courseIds.size(); i++) {
            String courseId = courseIds.get(i);

            assertTrue(verifyVisibleCourseToStudents(courseId, i));

            String feedbackSessionName = testData.feedbackSessions.entrySet().stream()
                    .filter(feedbackSession -> courseId.equals(feedbackSession.getValue().getCourseId()))
                    .map(x -> x.getValue().getFeedbackSessionName())
                    .collect(Collectors.joining());

            assertTrue(verifyVisibleFeedbackSessionToStudents(feedbackSessionName, i));
        }
    }

    private List<WebElement> getStudentHomeCoursePanels() {
        return browser.driver.findElements(By.cssSelector("div.card.bg-light"));
    }

    private List<String> getAllVisibleCourseIds() {
        List<String> courseIds = new ArrayList<>();

        for (StudentAttributes student : testData.students.values()) {
            if (student.googleId.equals(TestProperties.TEST_STUDENT1_ACCOUNT)) {
                courseIds.add(student.getCourse());
            }
        }
        return courseIds;
    }

    private boolean verifyVisibleCourseToStudents(String courseName, int index) {
        return getStudentHomeCoursePanels().get(index).getText().contains(courseName);
    }

    private boolean verifyVisibleFeedbackSessionToStudents(String feedbackSessionName, int index) {
        return getStudentHomeCoursePanels().get(index)
                .findElement(By.cssSelector("div.table-responsive table.table tbody")).getText()
                .contains(feedbackSessionName);
    }
}
