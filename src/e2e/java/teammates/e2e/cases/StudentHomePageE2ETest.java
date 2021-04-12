package teammates.e2e.cases;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
        StudentHomePage homePage = loginAdminToPage(url, StudentHomePage.class);

        List<String> courseIds = getAllVisibleCourseIds();

        for (int i = 0; i < courseIds.size(); i++) {
            String courseId = courseIds.get(i);

            homePage.verifyVisibleCourseToStudents(courseId, i);

            String feedbackSessionName = testData.feedbackSessions.entrySet().stream()
                    .filter(feedbackSession -> courseId.equals(feedbackSession.getValue().getCourseId()))
                    .map(x -> x.getValue().getFeedbackSessionName())
                    .collect(Collectors.joining());

            homePage.verifyVisibleFeedbackSessionToStudents(feedbackSessionName, i);
        }
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

}
