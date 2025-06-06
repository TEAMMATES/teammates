package teammates.e2e.cases.sql;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.testng.annotations.Test;

import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.InstructorFeedbackSessionsPageSql;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;

/**
 * SUT: {@link Const.WebPageURIs#INSTRUCTOR_SESSIONS_PAGE}.
 */
public class InstructorFeedbackSessionsPageE2ETest extends BaseE2ETestCase{
    FeedbackSession openSession;
    FeedbackSession openSession2;
    Instructor instructor;
    Course course;

    @Override
    protected void prepareTestData() {
        testData = removeAndRestoreDataBundle(
                loadSqlDataBundle("/InstructorFeedbackSessionsPageE2ETestSql.json"));
        instructor = testData.instructors.get("IFSP.instr");
        course = testData.courses.get("IFSP.CS2104");

        openSession = testData.feedbackSessions.get("openSession");
        openSession2 = testData.feedbackSessions.get("openSession2");




    }

    @Test
    @Override
    protected void testAll() {
        AppUrl url = createFrontendUrl(Const.WebPageURIs.INSTRUCTOR_SESSIONS_PAGE);
        InstructorFeedbackSessionsPageSql feedbackSessionsPage =
                loginToPage(url, InstructorFeedbackSessionsPageSql.class, instructor.getGoogleId());

        ______TS("verify loaded data");
        FeedbackSession[] loadedSessions = {openSession2, openSession};
        feedbackSessionsPage.verifySessionsTable(loadedSessions);

        ______TS("verify response rate");
        feedbackSessionsPage.verifyResponseRate(openSession2, getExpectedResponseRate(openSession2));
        feedbackSessionsPage.verifyResponseRate(openSession, getExpectedResponseRate(openSession));

//        ______TS("add new session");
//        FeedbackSession[] sessionsForAdded = { openSession2, openSession, newSession };
//
//        newSession = getTypicalFeedbackSessionForCourse(course);
//         newSession.setCreatedAt(Instant.now());
//        feedbackSessionsPage.addFeedbackSession(getTypicalFeedbackSessionForCourse(course), true);
//        feedbackSessionsPage.verifyStatusMessage("The feedback session has been added."
//                + "Click the \"Add New Question\" button below to begin adding questions for the feedback session.");
//        feedbackSessionsPage = getNewPageInstance(url,
//                InstructorFeedbackSessionsPageSql.class);
//        feedbackSessionsPage.sortBySessionsName();
//        feedbackSessionsPage.verifySessionsTable(sessionsForAdded);
//
//        verifyPresentInDatabase(newSession);



    }

    private String getExpectedResponseRate(FeedbackSession session) {
        String sessionName = session.getName();
        boolean hasQuestion = testData.feedbackQuestions.values()
                .stream()
                .anyMatch(q -> q.getFeedbackSessionName().equals(sessionName));

        if (!hasQuestion) {
            return "0 / 0";
        }

        Collection<Student> students = testData.students.values();
        long numStudents = testData.students.values()
                .stream()
                .filter(s -> s.getCourseId().equals(session.getCourseId()))
                .count();

        Set<String> uniqueGivers = new HashSet<>();
        testData.feedbackResponses.values()
                .stream()
                .filter(r -> r.getFeedbackQuestion().getFeedbackSessionName().equals(sessionName))
                .forEach(r -> uniqueGivers.add(r.getGiver()));
        int numResponses = uniqueGivers.size();

        return numResponses + " / " + numStudents;
    }
}
