package teammates.e2e.cases;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.testng.annotations.Test;

import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.StudentHomePage;
import teammates.logic.entity.Student;

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
        StudentHomePage homePage = loginToPage(url, StudentHomePage.class, "tm.e2e.SHome.student");

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

        for (Student student : testData.students.values()) {
            if ("tm.e2e.SHome.student".equals(student.getGoogleId())) {
                courseIds.add(student.getCourseId());
            }
        }
        return courseIds;
    }

}
