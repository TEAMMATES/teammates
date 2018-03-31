package teammates.test.cases.automated;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.Test;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.StudentEnrollDetails;
import teammates.common.datatransfer.StudentUpdateStatus;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;
import teammates.common.util.Const.ParamsNames;
import teammates.common.util.JsonUtils;
import teammates.logic.core.FeedbackQuestionsLogic;
import teammates.logic.core.FeedbackResponsesLogic;
import teammates.logic.core.StudentsLogic;
import teammates.ui.automated.FeedbackResponseAdjustmentWorkerAction;

/**
 * SUT: {@link FeedbackResponseAdjustmentWorkerAction}.
 */
public class FeedbackResponseAdjustmentWorkerActionTest extends BaseAutomatedActionTest {

    private static final FeedbackQuestionsLogic fqLogic = FeedbackQuestionsLogic.inst();
    private static final FeedbackResponsesLogic frLogic = FeedbackResponsesLogic.inst();
    private static final StudentsLogic studentsLogic = StudentsLogic.inst();

    @Override
    protected String getActionUri() {
        return Const.TaskQueue.FEEDBACK_RESPONSE_ADJUSTMENT_WORKER_URL;
    }

    @Test
    public void allTests() throws Exception {

        ______TS("typical case : existing student changes team");

        FeedbackSessionAttributes session = dataBundle.feedbackSessions.get("session2InCourse1");
        StudentAttributes student = dataBundle.students.get("student1InCourse1");

        // Verify pre-existing submissions and responses

        List<FeedbackResponseAttributes> oldResponsesForSession =
                getAllResponsesForStudentForSession(student, session.getFeedbackSessionName());
        assertFalse(oldResponsesForSession.isEmpty());

        String oldTeam = student.team;
        String oldSection = student.section;
        String newTeam = "Team 1.2";
        String newSection = "Section 2";
        student.team = newTeam;
        student.section = newSection;

        StudentEnrollDetails enrollDetails =
                new StudentEnrollDetails(StudentUpdateStatus.MODIFIED, student.course, student.email,
                                         oldTeam, newTeam, oldSection, newSection);
        List<StudentEnrollDetails> enrollList = new ArrayList<>();
        enrollList.add(enrollDetails);

        studentsLogic.updateStudentCascadeWithSubmissionAdjustmentScheduled(student.email, student, false);

        String[] submissionParams = new String[] {
                ParamsNames.COURSE_ID, student.course,
                ParamsNames.FEEDBACK_SESSION_NAME, session.getFeedbackSessionName(),
                ParamsNames.ENROLLMENT_DETAILS, JsonUtils.toJson(enrollList)
        };

        FeedbackResponseAdjustmentWorkerAction action = getAction(submissionParams);
        action.execute();

        List<FeedbackResponseAttributes> newResponsesForSession =
                getAllResponsesForStudentForSession(student, session.getFeedbackSessionName());
        assertTrue(newResponsesForSession.isEmpty());

    }

    @Override
    protected FeedbackResponseAdjustmentWorkerAction getAction(String... params) {
        return (FeedbackResponseAdjustmentWorkerAction) gaeSimulation.getAutomatedActionObject(getActionUri(), params);
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

    private List<FeedbackResponseAttributes> getAllTeamResponsesForStudent(StudentAttributes student) {
        List<FeedbackResponseAttributes> returnList = new ArrayList<>();
        List<FeedbackResponseAttributes> studentReceiverResponses =
                frLogic.getFeedbackResponsesForReceiverForCourse(student.course, student.email);

        for (FeedbackResponseAttributes response : studentReceiverResponses) {
            FeedbackQuestionAttributes question = fqLogic.getFeedbackQuestion(response.feedbackQuestionId);
            if (question.recipientType == FeedbackParticipantType.OWN_TEAM_MEMBERS) {
                returnList.add(response);
            }
        }

        List<FeedbackResponseAttributes> studentGiverResponses =
                frLogic.getFeedbackResponsesFromGiverForCourse(student.course, student.email);

        for (FeedbackResponseAttributes response : studentGiverResponses) {
            FeedbackQuestionAttributes question = fqLogic.getFeedbackQuestion(response.feedbackQuestionId);
            if (question.giverType == FeedbackParticipantType.TEAMS
                    || question.recipientType == FeedbackParticipantType.OWN_TEAM_MEMBERS) {
                returnList.add(response);
            }
        }

        return returnList;
    }

}
