package teammates.ui.webapi;

import java.util.UUID;

import teammates.common.datatransfer.FeedbackResultFetchType;
import teammates.common.datatransfer.SessionResultsBundle;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.storage.entity.FeedbackSession;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Student;
import teammates.storage.entity.Team;
import teammates.ui.exception.EntityNotFoundException;
import teammates.ui.exception.InvalidHttpParameterException;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.output.SessionResultsData;
import teammates.ui.request.Intent;

/**
 * Gets feedback session results including statistics where necessary.
 */
public class GetSessionResultsAction extends BasicFeedbackSubmissionAction {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.PUBLIC;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        UUID feedbackSessionId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_ID);
        Intent intent = Intent.valueOf(getNonNullRequestParamValue(Const.ParamsNames.INTENT));

        String previewAsPerson = getRequestParamValue(Const.ParamsNames.PREVIEWAS);
        boolean isPreviewResults = !StringHelper.isEmpty(previewAsPerson);

        FeedbackSession feedbackSession = logic.getFeedbackSession(feedbackSessionId);
        if (feedbackSession == null) {
            throw new EntityNotFoundException("Feedback session not found");
        }

        String courseId = feedbackSession.getCourseId();

        switch (intent) {
        case FULL_DETAIL:
            gateKeeper.verifyLoggedInUserPrivileges(userInfo);
            Instructor instructor = logic.getInstructorByGoogleId(courseId, userInfo.getId());
            gateKeeper.verifyAccessible(instructor, feedbackSession);
            break;
        case INSTRUCTOR_RESULT:
            if (!isPreviewResults && !feedbackSession.isPublished()) {
                throw new UnauthorizedAccessException("This feedback session is not yet published.", true);
            }
            instructor = getInstructorOfCourseFromRequest(courseId);
            checkAccessControlForInstructorFeedbackResult(instructor, feedbackSession);
            break;
        case STUDENT_RESULT:
            if (!isPreviewResults && !feedbackSession.isPublished()) {
                throw new UnauthorizedAccessException("This feedback session is not yet published.", true);
            }
            Student student = getStudentOfCourseFromRequest(courseId);
            checkAccessControlForStudentFeedbackResult(student, feedbackSession);
            break;
        case INSTRUCTOR_SUBMISSION, STUDENT_SUBMISSION:
            throw new InvalidHttpParameterException("Invalid intent for this action");
        default:
            throw new InvalidHttpParameterException("Unknown intent " + intent);
        }
    }

    @Override
    public JsonResult execute() {
        UUID feedbackSessionId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_ID);

        // Allow additional filter by question ID and section name
        UUID questionId = getNullableUuidRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_ID);
        String selectedSection = getRequestParamValue(Const.ParamsNames.FEEDBACK_RESULTS_GROUPBYSECTION);
        FeedbackResultFetchType fetchType = FeedbackResultFetchType.parseFetchType(
                getRequestParamValue(Const.ParamsNames.FEEDBACK_RESULTS_SECTION_BY_GIVER_RECEIVER));

        String previewAsPerson = getRequestParamValue(Const.ParamsNames.PREVIEWAS);
        boolean isPreviewResults = !StringHelper.isEmpty(previewAsPerson);

        Intent intent = Intent.valueOf(getNonNullRequestParamValue(Const.ParamsNames.INTENT));

        return execute(feedbackSessionId, questionId, selectedSection,
                fetchType, intent, isPreviewResults);
    }

    private JsonResult execute(
            UUID feedbackSessionId, UUID questionUuid, String selectedSection,
            FeedbackResultFetchType fetchType, Intent intent, boolean isPreviewResults) {
        FeedbackSession feedbackSession = logic.getFeedbackSession(feedbackSessionId);
        if (feedbackSession == null) {
            throw new EntityNotFoundException("Feedback session not found");
        }

        String courseId = feedbackSession.getCourseId();
        Instructor instructor;
        Student student;
        SessionResultsBundle bundle;

        switch (intent) {
        case FULL_DETAIL:
            instructor = getInstructorOfCourseFromRequest(courseId);

            bundle = logic.getSessionResults(feedbackSession, instructor.getEmail(),
                    questionUuid, selectedSection, fetchType);
            return new JsonResult(SessionResultsData.initForInstructor(bundle));
        case INSTRUCTOR_RESULT:
            // Section name filter is not applicable here
            instructor = getInstructorOfCourseFromRequest(courseId);

            bundle = logic.getSessionResultsForUser(feedbackSession, instructor.getEmail(),
                    true, questionUuid, isPreviewResults);

            // Build a fake student object, as the results will be displayed as if they are displayed to a student
            student = new Student(instructor.getCourse(), instructor.getName(), instructor.getEmail(), "");
            Team team = new Team(Const.USER_TEAM_FOR_INSTRUCTOR);
            student.setTeam(team);

            return new JsonResult(SessionResultsData.initForStudent(bundle, student));
        case STUDENT_RESULT:
            // Section name filter is not applicable here
            student = getStudentOfCourseFromRequest(courseId);

            bundle = logic.getSessionResultsForUser(feedbackSession, student.getEmail(),
                    false, questionUuid, isPreviewResults);

            return new JsonResult(SessionResultsData.initForStudent(bundle, student));
        case INSTRUCTOR_SUBMISSION, STUDENT_SUBMISSION:
            throw new InvalidHttpParameterException("Invalid intent for this action");
        default:
            throw new InvalidHttpParameterException("Unknown intent " + intent);
        }
    }

}
