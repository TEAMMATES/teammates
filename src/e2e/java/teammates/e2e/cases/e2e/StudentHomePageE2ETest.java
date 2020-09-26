package teammates.e2e.cases.e2e;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.StudentHomePage;

/**
 * SUT: {@link Const.WebPageURIs#STUDENT_HOME_PAGE}.
 */
public class StudentHomePageE2ETest extends BaseE2ETestCase {


	@Override
    protected void prepareTestData() {
        testData = loadDataBundle(Const.TestCase.STUDENT_HOME_PAGE_E2E_TEST_JSON);
        removeAndRestoreDataBundle(testData);
    }

    @Test
    public void testAll() {

        AppUrl url = createUrl(Const.WebPageURIs.STUDENT_HOME_PAGE).withUserId(Const.TestCase.S_HOME_UI_T_STUDENT);
        loginAdminToPage(url, StudentHomePage.class);

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

        logout();
    }

    private List<WebElement> getStudentHomeCoursePanels() {
        return browser.driver.findElements(By.cssSelector(Const.TestCase.DIV_CARD_BG_LIGHT));
    }

    private List<String> getAllVisibleCourseIds() {
        List<String> courseIds = new ArrayList<>();

        for (StudentAttributes student : testData.students.values()) {
            if (Const.TestCase.S_HOME_UI_T_STUDENT.equals(student.googleId)) {
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
                .findElement(By.cssSelector(Const.TestCase.DIV_TABLE_RESPONSIVE_TABLE_TABLE_TBODY)).getText()
                .contains(feedbackSessionName);
    }
}
