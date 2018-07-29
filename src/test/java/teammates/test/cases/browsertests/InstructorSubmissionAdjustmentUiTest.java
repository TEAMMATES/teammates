package teammates.test.cases.browsertests;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.Test;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.common.util.ThreadHelper;
import teammates.common.util.retry.MaximumRetriesExceededException;
import teammates.common.util.retry.RetryableTaskReturns;
import teammates.test.driver.BackDoor;
import teammates.test.driver.Priority;
import teammates.test.driver.TestProperties;
import teammates.test.pageobjects.InstructorCourseEnrollPage;

/**
 * Covers Ui aspect of submission adjustment for evaluations and feedbacks.
 */
@Priority(1)
public class InstructorSubmissionAdjustmentUiTest extends BaseUiTestCase {
    private InstructorCourseEnrollPage enrollPage;

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/InstructorSubmissionAdjustmentUiTest.json");

        // use the instructor account injected for this test

        testData.accounts.get("instructor1OfCourse1").googleId = TestProperties.TEST_INSTRUCTOR_ACCOUNT;
        testData.accounts.get("instructor1OfCourse1").email = TestProperties.TEST_INSTRUCTOR_ACCOUNT + "@gmail.com";

        removeAndRestoreDataBundle(testData);
    }

    @Test
    public void testAdjustmentOfSubmission() throws MaximumRetriesExceededException {

        //load the enrollPage
        loadEnrollmentPage();

        ______TS("typical case: enroll new student to existing course");

        String enrollString = "Section 1\tsomeName\tTeam1.1\trandom@g.tmt\tcomments\t";

        enrollPage.enroll(enrollString);

        //Wait briefly to allow task queue to successfully execute tasks
        ThreadHelper.waitFor(2000);

        ______TS("typical case : existing student changes team");
        loadEnrollmentPage();

        final FeedbackSessionAttributes session = testData.feedbackSessions.get("session2InCourse1");
        final StudentAttributes student = testData.students.get("student1InCourse1");

        //Verify pre-existing submissions and responses
        List<FeedbackResponseAttributes> oldResponsesForSession =
                getAllResponsesForStudentForSession(student, session.getFeedbackSessionName());
        assertFalse(oldResponsesForSession.isEmpty());

        String newTeam = "Team 1.2";
        student.team = newTeam;

        enrollString = "None\t" + student.getTeam() + "\t"
                + student.getName() + "\t" + student.getEmail() + "\t";
        enrollPage.enroll(enrollString);

        // It might take a while for the submission adjustment to persist (especially on the live server),
        // during which the pre-existing submissions and responses would be counted.
        // Hence, this needs to be retried several times until the count becomes zero.
        persistenceRetryManager.runUntilSuccessful(new RetryableTaskReturns<Integer>("Assert outdated responses removed") {
            @Override
            public Integer run() {
                return getAllResponsesForStudentForSession(student, session.getFeedbackSessionName()).size();
            }

            @Override
            public boolean isSuccessful(Integer numberOfResponses) {
                return numberOfResponses == 0;
            }
        });

    }

    private void loadEnrollmentPage() {
        AppUrl enrollUrl = createUrl(Const.ActionURIs.INSTRUCTOR_COURSE_ENROLL_PAGE)
                            .withUserId(testData.instructors.get("instructor1OfCourse1").googleId)
                            .withCourseId(testData.courses.get("typicalCourse1").getId());

        enrollPage = loginAdminToPage(enrollUrl, InstructorCourseEnrollPage.class);
    }

    private List<FeedbackResponseAttributes> getAllTeamResponsesForStudent(StudentAttributes student) {
        List<FeedbackResponseAttributes> returnList = new ArrayList<>();

        List<FeedbackResponseAttributes> studentReceiverResponses = BackDoor
                .getFeedbackResponsesForReceiverForCourse(student.course, student.email);

        for (FeedbackResponseAttributes response : studentReceiverResponses) {
            FeedbackQuestionAttributes question = BackDoor
                    .getFeedbackQuestion(response.feedbackQuestionId);
            if (question.recipientType == FeedbackParticipantType.OWN_TEAM_MEMBERS) {
                returnList.add(response);
            }
        }

        List<FeedbackResponseAttributes> studentGiverResponses = BackDoor
                .getFeedbackResponsesFromGiverForCourse(student.course, student.email);

        for (FeedbackResponseAttributes response : studentGiverResponses) {
            FeedbackQuestionAttributes question = BackDoor
                    .getFeedbackQuestion(response.feedbackQuestionId);
            if (question.giverType == FeedbackParticipantType.TEAMS
                    || question.recipientType == FeedbackParticipantType.OWN_TEAM_MEMBERS) {
                returnList.add(response);
            }
        }

        return returnList;
    }

    private List<FeedbackResponseAttributes> getAllResponsesForStudentForSession(StudentAttributes student,
            String feedbackSessionName) {
        List<FeedbackResponseAttributes> returnList = new ArrayList<>();

        List<FeedbackResponseAttributes> allResponseOfStudent = getAllTeamResponsesForStudent(student);

        for (FeedbackResponseAttributes responseAttributes : allResponseOfStudent) {
            if (responseAttributes.feedbackSessionName.equals(feedbackSessionName)) {
                returnList.add(responseAttributes);
            }
        }

        return returnList;
    }
}
