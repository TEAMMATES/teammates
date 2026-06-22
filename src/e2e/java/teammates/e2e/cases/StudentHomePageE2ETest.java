package teammates.e2e.cases;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.testng.annotations.Test;

import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.StudentHomePage;
import teammates.storage.entity.Student;

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

        AppUrl url = createFrontendUrl(Const.WebPageURIs.STUDENT_HOME_PAGE);
        String studentEmail = testData.accounts.get("SHome.student").getEmail();
        StudentHomePage homePage = loginToPage(url, StudentHomePage.class, studentEmail);

        ______TS("courses visible to student are shown");
        List<String> courseIds = getAllVisibleCourseIds();

        courseIds.forEach(courseId -> {
            int panelIndex = homePage.getStudentHomeCoursePanelIndex(courseId);

            String feedbackSessionName = testData.feedbackSessions.entrySet().stream()
                    .filter(feedbackSession -> courseId.equals(feedbackSession.getValue().getCourseId()))
                    .map(x -> x.getValue().getName())
                    .collect(Collectors.joining());

            homePage.verifyVisibleFeedbackSessionToStudents(feedbackSessionName, panelIndex);
        });
    }

    private List<String> getAllVisibleCourseIds() {
        List<String> courseIds = new ArrayList<>();

        UUID studentAccountIdWithVisibleCourses = testData.accounts.get("SHome.student").getId();
        for (Student student : testData.students.values()) {
            if (studentAccountIdWithVisibleCourses.equals(student.getAccountId())) {
                courseIds.add(student.getCourseId());
            }
        }
        return courseIds;
    }

}
