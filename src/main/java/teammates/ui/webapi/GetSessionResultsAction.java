package teammates.ui.webapi;

import java.util.UUID;

import teammates.common.datatransfer.FeedbackResultFetchType;
import teammates.common.datatransfer.SqlSessionResultsBundle;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;
import teammates.storage.sqlentity.Team;
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
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        Intent intent = Intent.valueOf(getNonNullRequestParamValue(Const.ParamsNames.INTENT));

        String previewAsPerson = getRequestParamValue(Const.ParamsNames.PREVIEWAS);
        boolean isPreviewResults = !StringHelper.isEmpty(previewAsPerson);

        checkSpecificAccessControl(courseId, feedbackSessionName, intent, isPreviewResults);
    }

    private void checkSpecificAccessControl(
            String courseId, String feedbackSessionName, Intent intent, boolean isPreviewResults)
            throws UnauthorizedAccessException {
        FeedbackSession feedbackSession = getNonNullFeedbackSession(feedbackSessionName, courseId);

        switch (intent) {
        case FULL_DETAIL:
            gateKeeper.verifyLoggedInUserPrivileges(userInfo);
            Instructor instructor = sqlLogic.getInstructorByGoogleId(courseId, userInfo.getId());
            gateKeeper.verifyAccessible(instructor, feedbackSession);
            break;
        case INSTRUCTOR_RESULT:
            if (!isPreviewResults && !feedbackSession.isPublished()) {
                throw new UnauthorizedAccessException("This feedback session is not yet published.", true);
            }
            instructor = getSqlInstructorOfCourseFromRequest(courseId);
            checkAccessControlForInstructorFeedbackResult(instructor, feedbackSession);
            break;
        case STUDENT_RESULT:
            if (!isPreviewResults && !feedbackSession.isPublished()) {
                throw new UnauthorizedAccessException("This feedback session is not yet published.", true);
            }
            Student student = getSqlStudentOfCourseFromRequest(courseId);
            checkAccessControlForStudentFeedbackResult(student, feedbackSession);
            break;
        case INSTRUCTOR_SUBMISSION:
        case STUDENT_SUBMISSION:
            throw new InvalidHttpParameterException("Invalid intent for this action");
        default:
            throw new InvalidHttpParameterException("Unknown intent " + intent);
        }
    }

    @Override
    public JsonResult execute() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);

        // Allow additional filter by question ID and section name
        String questionId = getRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_ID);
        String selectedSection = getRequestParamValue(Const.ParamsNames.FEEDBACK_RESULTS_GROUPBYSECTION);
        FeedbackResultFetchType fetchType = FeedbackResultFetchType.parseFetchType(
                getRequestParamValue(Const.ParamsNames.FEEDBACK_RESULTS_SECTION_BY_GIVER_RECEIVER));

        String previewAsPerson = getRequestParamValue(Const.ParamsNames.PREVIEWAS);
        boolean isPreviewResults = !StringHelper.isEmpty(previewAsPerson);

        Intent intent = Intent.valueOf(getNonNullRequestParamValue(Const.ParamsNames.INTENT));

        if (questionId != null) {
            UUID questionUuid = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_ID);
            return execute(courseId, feedbackSessionName, questionUuid,
                    selectedSection, fetchType, intent, isPreviewResults);
        }
        return execute(courseId, feedbackSessionName, null, selectedSection,
                fetchType, intent, isPreviewResults);
    }

    private JsonResult execute(
            String courseId, String feedbackSessionName, UUID questionUuid, String selectedSection,
            FeedbackResultFetchType fetchType, Intent intent, boolean isPreviewResults) {
        Instructor instructor;
        Student student;
        FeedbackSession feedbackSession = getNonNullFeedbackSession(feedbackSessionName, courseId);
        SqlSessionResultsBundle bundle;
        switch (intent) {
        case FULL_DETAIL:
            instructor = getSqlInstructorOfCourseFromRequest(courseId);

            bundle = sqlLogic.getSessionResultsForCourse(feedbackSession, courseId, instructor.getEmail(),
                    questionUuid, selectedSection, fetchType);
            return new JsonResult(SessionResultsData.initForInstructor(bundle));
        case INSTRUCTOR_RESULT:
            // Section name filter is not applicable here
            instructor = getSqlInstructorOfCourseFromRequest(courseId);

            bundle = sqlLogic.getSessionResultsForUser(feedbackSession, courseId, instructor.getEmail(),
                    true, questionUuid, isPreviewResults);

            // Build a fake student object, as the results will be displayed as if they are displayed to a student
            student = new Student(instructor.getCourse(), instructor.getName(), instructor.getEmail(), "");
            student.setTeam(new Team(null, Const.USER_TEAM_FOR_INSTRUCTOR));

            return new JsonResult(SessionResultsData.initForStudent(bundle, student));
        case STUDENT_RESULT:
            // Section name filter is not applicable here
            student = getSqlStudentOfCourseFromRequest(courseId);

            bundle = sqlLogic.getSessionResultsForUser(feedbackSession, courseId, student.getEmail(),
                    false, questionUuid, isPreviewResults);

            return new JsonResult(SessionResultsData.initForStudent(bundle, student));
        case INSTRUCTOR_SUBMISSION:
        case STUDENT_SUBMISSION:
            throw new InvalidHttpParameterException("Invalid intent for this action");
        default:
            throw new InvalidHttpParameterException("Unknown intent " + intent);
        }
    }

}
