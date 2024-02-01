package teammates.ui.webapi;

import teammates.common.datatransfer.FeedbackResultFetchType;
import teammates.common.datatransfer.SessionResultsBundle;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;
import teammates.ui.output.SessionResultsData;
import teammates.ui.request.Intent;

/**
 * Gets feedback session results including statistics where necessary.
 */
public class GetSessionResultsAction extends Action {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.PUBLIC;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        Intent intent = Intent.valueOf(getNonNullRequestParamValue(Const.ParamsNames.INTENT));
        
        if (isCourseMigrated(courseId)) {
            FeedbackSession feedbackSession = getNonNullSqlFeedbackSession(feedbackSessionName, courseId);
            
            switch (intent) {
                case FULL_DETAIL:
                    gateKeeper.verifyLoggedInUserPrivileges(userInfo);
                    Instructor instructor = sqlLogic.getInstructorByGoogleId(courseId, userInfo.getId());
                    gateKeeper.verifyAccessible(instructor, feedbackSession);
                    break;
                case INSTRUCTOR_RESULT:
                    instructor = getPossiblyUnregisteredSqlInstructor(courseId);
                    gateKeeper.verifyAccessible(instructor, feedbackSession);
                    if (!feedbackSession.isPublished()) {
                        throw new UnauthorizedAccessException("This feedback session is not yet published.", true);
                    }
                    break;
                case STUDENT_RESULT:
                    Student student = getPossiblyUnregisteredSqlStudent(courseId);
                    gateKeeper.verifyAccessible(student, feedbackSession);
                    if (!feedbackSession.isPublished()) {
                        throw new UnauthorizedAccessException("This feedback session is not yet published.", true);
                    }
                    break;
                case INSTRUCTOR_SUBMISSION:
                case STUDENT_SUBMISSION:
                    throw new InvalidHttpParameterException("Invalid intent for this action");
                default:
                    throw new InvalidHttpParameterException("Unknown intent " + intent);
                }
        } else {
            FeedbackSessionAttributes feedbackSession = getNonNullFeedbackSession(feedbackSessionName, courseId);

            switch (intent) {
            case FULL_DETAIL:
                gateKeeper.verifyLoggedInUserPrivileges(userInfo);
                InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, userInfo.getId());
                gateKeeper.verifyAccessible(instructor, feedbackSession);
                break;
            case INSTRUCTOR_RESULT:
                instructor = getPossiblyUnregisteredInstructor(courseId);
                gateKeeper.verifyAccessible(instructor, feedbackSession);
                if (!feedbackSession.isPublished()) {
                    throw new UnauthorizedAccessException("This feedback session is not yet published.", true);
                }
                break;
            case STUDENT_RESULT:
                StudentAttributes student = getPossiblyUnregisteredStudent(courseId);
                gateKeeper.verifyAccessible(student, feedbackSession);
                if (!feedbackSession.isPublished()) {
                    throw new UnauthorizedAccessException("This feedback session is not yet published.", true);
                }
                break;
            case INSTRUCTOR_SUBMISSION:
            case STUDENT_SUBMISSION:
                throw new InvalidHttpParameterException("Invalid intent for this action");
            default:
                throw new InvalidHttpParameterException("Unknown intent " + intent);
            }
        }
    }

    @Override
    public JsonResult execute() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);

        // Allow additional filter by question ID (equivalent to question number) and section name
        String questionId = getRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_ID);
        String selectedSection = getRequestParamValue(Const.ParamsNames.FEEDBACK_RESULTS_GROUPBYSECTION);
        FeedbackResultFetchType fetchType = FeedbackResultFetchType.parseFetchType(
                getRequestParamValue(Const.ParamsNames.FEEDBACK_RESULTS_SECTION_BY_GIVER_RECEIVER));

        SessionResultsBundle bundle;
        InstructorAttributes instructor;
        StudentAttributes student;
        Intent intent = Intent.valueOf(getNonNullRequestParamValue(Const.ParamsNames.INTENT));
        switch (intent) {
        case FULL_DETAIL:
            instructor = logic.getInstructorForGoogleId(courseId, userInfo.id);

            bundle = logic.getSessionResultsForCourse(feedbackSessionName, courseId, instructor.getEmail(),
                    questionId, selectedSection, fetchType);
            return new JsonResult(SessionResultsData.initForInstructor(bundle));
        case INSTRUCTOR_RESULT:
            // Section name filter is not applicable here
            instructor = getPossiblyUnregisteredInstructor(courseId);

            bundle = logic.getSessionResultsForUser(feedbackSessionName, courseId, instructor.getEmail(),
                    true, questionId);

            // Build a fake student object, as the results will be displayed as if they are displayed to a student
            student = StudentAttributes.builder(instructor.getCourseId(), instructor.getEmail())
                    .withTeamName(Const.USER_TEAM_FOR_INSTRUCTOR)
                    .build();

            return new JsonResult(SessionResultsData.initForStudent(bundle, student));
        case STUDENT_RESULT:
            // Section name filter is not applicable here
            student = getPossiblyUnregisteredStudent(courseId);

            bundle = logic.getSessionResultsForUser(feedbackSessionName, courseId, student.getEmail(),
                    false, questionId);

            return new JsonResult(SessionResultsData.initForStudent(bundle, student));
        case INSTRUCTOR_SUBMISSION:
        case STUDENT_SUBMISSION:
            throw new InvalidHttpParameterException("Invalid intent for this action");
        default:
            throw new InvalidHttpParameterException("Unknown intent " + intent);
        }
    }

}
