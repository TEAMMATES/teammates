package teammates.e2e.cases.sql;

import org.testng.annotations.Test;

import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.FeedbackSubmitPage;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.FeedbackQuestion;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;

/**
 * SUT: {@link Const.WebPageURIs#SESSION_SUBMISSION_PAGE}.
 */
public class FeedbackSubmitPageE2ETest extends BaseE2ETestCase{
    Student student;
    Instructor instructor;
    Course course;
    FeedbackSession session;

    @Override
    protected void prepareTestData() {
        testData = removeAndRestoreDataBundle(loadSqlDataBundle("/FeedbackSubmitPageE2ESqlTest.json"));
        student = testData.students.get("alice.tmms@FSPage.CS2104");
        course = testData.courses.get("course");
        instructor = testData.instructors.get("instructor");
        session = testData.feedbackSessions.get("openSession");
    }

    @Test
    @Override
    protected void testAll() {
        AppUrl url = createFrontendUrl(Const.WebPageURIs.INSTRUCTOR_SESSION_SUBMISSION_PAGE)
                .withCourseId(student.getCourse().getId())
                .withSessionName(session.getName());
        FeedbackSubmitPage submitPage = loginToPage(url, FeedbackSubmitPage.class, instructor.getGoogleId());

        ______TS("verify loaded session data");
        submitPage.verifyFeedbackSessionDetails(session, course);

        ______TS("questions with giver type instructor");
        FeedbackQuestion question = testData.feedbackQuestions.get("qn1ForFirstSession");
        submitPage.verifyNumQuestions(1);
        submitPage.verifyQuestionDetails(1, question);

        ______TS("questions with giver type students");
        logout();
        url = createFrontendUrl(Const.WebPageURIs.STUDENT_SESSION_SUBMISSION_PAGE)
                .withCourseId(student.getCourse().getId())
                .withSessionName(session.getName());
        submitPage = loginToPage(url, FeedbackSubmitPage.class, student.getGoogleId());
        submitPage.verifyNumQuestions(2);
        submitPage.verifyQuestionDetails(1, testData.feedbackQuestions.get("qn2ForFirstSession"));
        submitPage.verifyQuestionDetails(2, testData.feedbackQuestions.get("qn3ForFirstSession"));


    }

}
