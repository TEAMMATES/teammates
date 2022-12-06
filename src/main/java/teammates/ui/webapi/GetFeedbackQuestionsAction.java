package teammates.ui.webapi;

import java.util.List;

import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.ui.output.FeedbackQuestionData;
import teammates.ui.output.FeedbackQuestionsData;
import teammates.ui.request.Intent;

/**
 * Get a list of feedback questions for a feedback session.
 */
class GetFeedbackQuestionsAction extends BasicFeedbackSubmissionAction {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.PUBLIC;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        FeedbackSessionAttributes feedbackSession = getNonNullFeedbackSession(feedbackSessionName, courseId);
        Intent intent = Intent.valueOf(getNonNullRequestParamValue(Const.ParamsNames.INTENT));

        switch (intent) {
        case STUDENT_SUBMISSION:
            StudentAttributes studentAttributes = getStudentOfCourseFromRequest(courseId);
            checkAccessControlForStudentFeedbackSubmission(studentAttributes, feedbackSession);
            break;
        case FULL_DETAIL:
            gateKeeper.verifyLoggedInUserPrivileges(userInfo);
            gateKeeper.verifyAccessible(logic.getInstructorForGoogleId(courseId, userInfo.getId()), feedbackSession);
            break;
        case INSTRUCTOR_SUBMISSION:
            InstructorAttributes instructorAttributes = getInstructorOfCourseFromRequest(courseId);
            checkAccessControlForInstructorFeedbackSubmission(instructorAttributes, feedbackSession);
            break;
        case INSTRUCTOR_RESULT:
            instructorAttributes = getInstructorOfCourseFromRequest(courseId);
            checkAccessControlForInstructorFeedbackResult(instructorAttributes, feedbackSession);
            break;
        case STUDENT_RESULT:
            studentAttributes = getStudentOfCourseFromRequest(courseId);
            checkAccessControlForStudentFeedbackResult(studentAttributes, feedbackSession);
            break;
        default:
            throw new InvalidHttpParameterException("Unknown intent " + intent);
        }
    }

    @Override
    public JsonResult execute() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        Intent intent = Intent.valueOf(getNonNullRequestParamValue(Const.ParamsNames.INTENT));

        List<FeedbackQuestionAttributes> questions;
        switch (intent) {
        case STUDENT_SUBMISSION:
            questions = logic.getFeedbackQuestionsForStudents(feedbackSessionName, courseId);
            StudentAttributes studentAttributes = getStudentOfCourseFromRequest(courseId);
            questions.forEach(question ->
                    logic.populateFieldsToGenerateInQuestion(question,
                            studentAttributes.getEmail(), studentAttributes.getTeam()));
            break;
        case INSTRUCTOR_SUBMISSION:
            InstructorAttributes instructor = getInstructorOfCourseFromRequest(courseId);
            questions = logic.getFeedbackQuestionsForInstructors(feedbackSessionName, courseId, instructor.getEmail());
            questions.forEach(question ->
                    logic.populateFieldsToGenerateInQuestion(question,
                            instructor.getEmail(), null));
            break;
        case FULL_DETAIL:
        case INSTRUCTOR_RESULT:
        case STUDENT_RESULT:
            questions = logic.getFeedbackQuestionsForSession(feedbackSessionName, courseId);
            break;
        default:
            throw new InvalidHttpParameterException("Unknown intent " + intent);
        }

        String moderatedPerson = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON);
        if (!StringHelper.isEmpty(moderatedPerson)) {
            // filter out unmodifiable questions
            questions.removeIf(question -> !canInstructorSeeQuestion(question));
        }

        FeedbackQuestionsData response = new FeedbackQuestionsData(questions);
        response.normalizeQuestionNumber();
        if (intent.equals(Intent.STUDENT_SUBMISSION) || intent.equals(Intent.STUDENT_RESULT)) {
            for (FeedbackQuestionData questionData : response.getQuestions()) {
                questionData.hideInformationForStudent();
            }
        }
        return new JsonResult(response);
    }

}
