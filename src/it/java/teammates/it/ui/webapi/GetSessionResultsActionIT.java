package teammates.it.ui.webapi;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackResultFetchType;
import teammates.common.util.Const;
import teammates.common.util.HibernateUtil;
import teammates.common.util.JsonUtils;
import teammates.logic.entity.Course;
import teammates.logic.entity.FeedbackQuestion;
import teammates.logic.entity.FeedbackSession;
import teammates.logic.entity.Instructor;
import teammates.logic.entity.Section;
import teammates.logic.entity.Student;
import teammates.ui.output.SessionResultsData;
import teammates.ui.request.Intent;
import teammates.ui.webapi.GetSessionResultsAction;
import teammates.ui.webapi.JsonResult;

/**
 * SUT: {@link GetSessionResultsAction}.
 */
public class GetSessionResultsActionIT extends BaseActionIT<GetSessionResultsAction> {
    private DataBundle typicalBundle;

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.RESULT;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Override
    @BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
        logoutUser();
        typicalBundle = persistDataBundle(getTypicalDataBundle());
        HibernateUtil.flushSession();
        HibernateUtil.clearSession();
    }

    @Override
    @Test
    protected void testExecute() {
        Instructor instructor = typicalBundle.instructors.get("instructor1OfCourse1");
        loginAsInstructor(instructor.getGoogleId());

        ______TS("Typical: Instructor accesses results of their course");

        FeedbackSession accessibleFeedbackSession = typicalBundle.feedbackSessions.get("session1InCourse1");
        String[] submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_ID, accessibleFeedbackSession.getId().toString(),
                Const.ParamsNames.INTENT, Intent.FULL_DETAIL.name(),
        };

        GetSessionResultsAction a = getAction(submissionParams);
        JsonResult r = getJsonResult(a);

        SessionResultsData output = (SessionResultsData) r.getOutput();

        SessionResultsData expectedResults = SessionResultsData.initForInstructor(
                logic.getSessionResults(accessibleFeedbackSession,
                        instructor.getEmail(),
                        null, null, FeedbackResultFetchType.BOTH));

        assertTrue(isSessionResultsDataEqual(expectedResults, output));

        ______TS("Typical: Instructor accesses results of their course with breakdown");

        Set<Section> sections = new HashSet<>();
        typicalBundle.feedbackResponses.values().forEach(resp -> {
            sections.add(resp.getGiverSection());
            sections.add(resp.getRecipientSection());
        });

        for (FeedbackResultFetchType fetchType : FeedbackResultFetchType.values()) {
            for (Section section : sections) {
                submissionParams = new String[] {
                        Const.ParamsNames.FEEDBACK_SESSION_ID, accessibleFeedbackSession.getId().toString(),
                        Const.ParamsNames.INTENT, Intent.FULL_DETAIL.name(),
                        Const.ParamsNames.FEEDBACK_RESULTS_GROUPBYSECTION, section.getName(),
                        Const.ParamsNames.FEEDBACK_RESULTS_SECTION_BY_GIVER_RECEIVER, fetchType.name(),
                };

                a = getAction(submissionParams);
                r = getJsonResult(a);

                output = (SessionResultsData) r.getOutput();

                expectedResults = SessionResultsData.initForInstructor(
                        logic.getSessionResults(accessibleFeedbackSession,
                                instructor.getEmail(),
                                null, section.getName(), fetchType));

                assertTrue(isSessionResultsDataEqual(expectedResults, output));
            }
        }

        ______TS("Typical: Instructor previews session results as student");

        Student student = typicalBundle.students.get("student1InCourse1");

        submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_ID, accessibleFeedbackSession.getId().toString(),
                Const.ParamsNames.INTENT, Intent.STUDENT_RESULT.name(),
                Const.ParamsNames.PREVIEWAS, student.getEmail(),
        };

        a = getAction(submissionParams);
        r = getJsonResult(a);

        output = (SessionResultsData) r.getOutput();
        expectedResults = SessionResultsData.initForStudent(
                logic.getSessionResultsForUser(accessibleFeedbackSession,
                        student.getEmail(),
                        false, null, true),
                student);

        assertTrue(isSessionResultsDataEqual(expectedResults, output));

        ______TS("Typical: Student accesses results of their course");

        loginAsStudent(student.getGoogleId());

        submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_ID, accessibleFeedbackSession.getId().toString(),
                Const.ParamsNames.INTENT, Intent.STUDENT_RESULT.name(),
        };

        a = getAction(submissionParams);
        r = getJsonResult(a);

        output = (SessionResultsData) r.getOutput();
        expectedResults = SessionResultsData.initForStudent(
                logic.getSessionResultsForUser(accessibleFeedbackSession,
                        student.getEmail(),
                        false, null, false),
                student);

        assertTrue(isSessionResultsDataEqual(expectedResults, output));

        ______TS("Typical: Student accesses results of their course by questionId");

        loginAsStudent(student.getGoogleId());

        FeedbackQuestion question = typicalBundle.feedbackQuestions.get("qn1InSession1InCourse1");

        submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_ID, accessibleFeedbackSession.getId().toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_ID, question.getId().toString(),
                Const.ParamsNames.INTENT, Intent.STUDENT_RESULT.name(),
        };

        a = getAction(submissionParams);
        r = getJsonResult(a);

        output = (SessionResultsData) r.getOutput();
        expectedResults = SessionResultsData.initForStudent(
                logic.getSessionResultsForUser(accessibleFeedbackSession,
                        student.getEmail(),
                        false, question.getId(), false),
                student);

        assertTrue(isSessionResultsDataEqual(expectedResults, output));
    }

    @Override
    protected void testAccessControl() throws Exception {
        String[] submissionParams;
        FeedbackSession publishedFeedbackSession = typicalBundle.feedbackSessions.get("session1InCourse1");
        Course course = typicalBundle.courses.get("course1");
        FeedbackSession inaccessibleFeedbackSession = typicalBundle.feedbackSessions.get(
                "unpublishedSession1InTypicalCourse");

        ______TS("Inaccessible for authenticated instructor when unpublished");
        submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_ID, inaccessibleFeedbackSession.getId().toString(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.name(),
        };
        verifyCannotAccess(submissionParams);

        ______TS("Inaccessible for authenticated student when unpublished");
        submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_ID, inaccessibleFeedbackSession.getId().toString(),
                Const.ParamsNames.INTENT, Intent.STUDENT_RESULT.name(),
        };
        Student student1InCourse1 = typicalBundle.students.get("student1InCourse1");
        loginAsStudent(student1InCourse1.getGoogleId());
        verifyCannotAccess(submissionParams);

        ______TS("Accessible for authenticated instructor when published");
        submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_ID, publishedFeedbackSession.getId().toString(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.name(),
        };
        verifyAccessibleForInstructorsOfTheSameCourse(course, submissionParams);
        verifyInaccessibleForInstructorsOfOtherCourses(course, submissionParams);

        ______TS("Accessible for authenticated student when published");
        submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_ID, publishedFeedbackSession.getId().toString(),
                Const.ParamsNames.INTENT, Intent.STUDENT_RESULT.name(),
        };
        verifyAccessibleForStudentsOfTheSameCourse(course, submissionParams);
        verifyInaccessibleForStudentsOfOtherCourse(course, submissionParams);

        ______TS("Invalid intent");
        submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_ID, publishedFeedbackSession.getId().toString(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.name(),
        };
        verifyHttpParameterFailure(submissionParams);
        submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_ID, publishedFeedbackSession.getId().toString(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.name(),
        };
        verifyHttpParameterFailure(submissionParams);
    }

    private boolean isSessionResultsDataEqual(SessionResultsData self, SessionResultsData other) {
        List<SessionResultsData.QuestionOutput> thisQuestions = self.getQuestions();
        List<SessionResultsData.QuestionOutput> otherQuestions = other.getQuestions();
        if (thisQuestions.size() != otherQuestions.size()) {
            return false;
        }
        for (int i = 0; i < thisQuestions.size(); i++) {
            SessionResultsData.QuestionOutput thisQuestion = thisQuestions.get(i);
            SessionResultsData.QuestionOutput otherQuestion = otherQuestions.get(i);
            if (!isQuestionOutputEqual(thisQuestion, otherQuestion)) {
                return false;
            }
        }
        return true;
    }

    private boolean isQuestionOutputEqual(SessionResultsData.QuestionOutput self,
            SessionResultsData.QuestionOutput other) {
        if (!JsonUtils.toJson(self.getFeedbackQuestion()).equals(JsonUtils.toJson(other.getFeedbackQuestion()))
                || !self.getQuestionStatistics().equals(other.getQuestionStatistics())
                || self.getHasResponseButNotVisibleForPreview() != other.getHasResponseButNotVisibleForPreview()
                || self.getHasCommentNotVisibleForPreview() != other.getHasCommentNotVisibleForPreview()) {
            return false;
        }
        List<SessionResultsData.ResponseOutput> thisResponses;
        List<SessionResultsData.ResponseOutput> otherResponses;
        thisResponses = self.getAllResponses();
        otherResponses = other.getAllResponses();
        if (thisResponses.size() != otherResponses.size()) {
            return false;
        }
        for (int j = 0; j < thisResponses.size(); j++) {
            if (!isResponseOutputEqual(thisResponses.get(j), otherResponses.get(j))) {
                return false;
            }
        }
        return true;
    }

    private boolean isResponseOutputEqual(SessionResultsData.ResponseOutput self,
            SessionResultsData.ResponseOutput other) {
        return self.getGiver().equals(other.getGiver())
                && self.getGiverTeam().equals(other.getGiverTeam())
                && self.getGiverSection().equals(other.getGiverSection())
                && self.getRecipient().equals(other.getRecipient())
                && self.getRecipientTeam().equals(other.getRecipientTeam())
                && self.getRecipientSection().equals(other.getRecipientSection())
                && self.getResponseDetails().getJsonString().equals(other.getResponseDetails().getJsonString());
    }

    @Test
    public void testAccessControl_withoutCorrectAuthInfoAccessStudentResult_shouldFail() {
        FeedbackSession feedbackSession = typicalBundle.feedbackSessions.get("session1InCourse1");

        String[] submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_ID, feedbackSession.getId().toString(),
                Const.ParamsNames.INTENT, Intent.STUDENT_RESULT.toString(),
        };

        verifyInaccessibleForUnregisteredUsers(submissionParams);
    }

    @Test
    public void testAccessControl_studentAccessOwnCourseSessionResult_shouldPass() {
        Student student1InCourse1 = typicalBundle.students.get("student1InCourse1");
        FeedbackSession feedbackSession = typicalBundle.feedbackSessions.get("session1InCourse1");

        String[] submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_ID, feedbackSession.getId().toString(),
                Const.ParamsNames.INTENT, Intent.STUDENT_RESULT.toString(),
        };

        loginAsStudent(student1InCourse1.getGoogleId());
        verifyCanAccess(submissionParams);
    }

    @Test
    public void testAccessControl_studentAccessUnpublishedSessionStudentResult_shouldFail() {
        Student student1InCourse1 = typicalBundle.students.get("student1InCourse1");
        FeedbackSession unpublishedFeedbackSession = typicalBundle.feedbackSessions.get("session2InTypicalCourse");

        String[] submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_ID, unpublishedFeedbackSession.getId().toString(),
                Const.ParamsNames.INTENT, Intent.STUDENT_RESULT.toString(),
        };
        loginAsStudent(student1InCourse1.getGoogleId());
        verifyCannotAccess(submissionParams);
    }

    @Test
    public void testAccessControl_accessStudentSessionResultWithMasqueradeMode_shouldPass() {
        Student student1InCourse1 = typicalBundle.students.get("student1InCourse1");
        FeedbackSession feedbackSession = typicalBundle.feedbackSessions.get("session1InCourse1");
        String[] submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_ID, feedbackSession.getId().toString(),
                Const.ParamsNames.INTENT, Intent.STUDENT_RESULT.toString(),
        };

        loginAsAdmin();
        verifyCanMasquerade(student1InCourse1.getGoogleId(), submissionParams);
    }

    @Test
    public void testAccessControl_instructorAccessHisCourseFullDetail_shouldPass() throws Exception {
        Course typicalCourse1 = typicalBundle.courses.get("course1");
        FeedbackSession feedbackSession = typicalBundle.feedbackSessions.get("session1InCourse1");

        String[] submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_ID, feedbackSession.getId().toString(),
                Const.ParamsNames.INTENT, Intent.FULL_DETAIL.toString(),
        };
        verifyOnlyInstructorsOfTheSameCourseCanAccess(typicalCourse1, submissionParams);
    }

}
