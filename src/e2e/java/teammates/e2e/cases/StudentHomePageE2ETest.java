package teammates.e2e.cases;

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
        testData = loadDataBundle("/StudentHomePageE2ETest.json");
        removeAndRestoreDataBundle(testData);
    }

    @Test
    @Override
    public void testAll() {

        AppUrl url = createUrl(Const.WebPageURIs.STUDENT_HOME_PAGE).withUserId("tm.e2e.SHome.student");
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
        return browser.driver.findElements(By.cssSelector("div.card.bg-light"));
    }

    private List<String> getAllVisibleCourseIds() {
        List<String> courseIds = new ArrayList<>();

        for (StudentAttributes student : testData.students.values()) {
            if ("tm.e2e.SHome.student".equals(student.googleId)) {
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
