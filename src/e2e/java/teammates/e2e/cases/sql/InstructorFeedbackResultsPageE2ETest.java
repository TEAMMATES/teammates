package teammates.e2e.cases.sql;

import java.util.ArrayList;
import java.util.Collection;

import org.testng.annotations.Test;

import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.InstructorFeedbackResultsPageSql;
import teammates.storage.sqlentity.FeedbackQuestion;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;

/**
 * SUT: {@link Const.WebPageURIs#INSTRUCTOR_SESSION_REPORT_PAGE}.
 */
public class InstructorFeedbackResultsPageE2ETest extends BaseE2ETestCase {
    private Instructor instructor;
    private FeedbackSession session;
    private FeedbackQuestion question;
    private Collection<Instructor> instructors;
    private Collection<Student> students;

    @Override
    protected void prepareTestData() {
        testData = loadSqlDataBundle("/InstructorFeedbackResultsPageE2ETestSql.json");
        testData = removeAndRestoreDataBundle(testData);

        instructor = testData.instructors.get("IFR.instr");
        session = testData.feedbackSessions.get("openSession");
        question = testData.feedbackQuestions.get("qn1");
        instructors = testData.instructors.values();
        students = testData.students.values();
    }

    @Test
    @Override
    public void testAll() {
        AppUrl resultsUrl = createFrontendUrl(Const.WebPageURIs.INSTRUCTOR_SESSION_REPORT_PAGE)
                .withCourseId(session.getCourseId())
                .withSessionName(session.getName());
        InstructorFeedbackResultsPageSql resultsPage =
                loginToPage(resultsUrl, InstructorFeedbackResultsPageSql.class, instructor.getGoogleId());

        ______TS("verify session details and no responses");
        resultsPage.verifySessionDetails(session);
        resultsPage.verifyQnViewResponses(question, new ArrayList<>(), instructors, students);
    }
}