package teammates.ui.webapi;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.storage.entity.FeedbackQuestion;
import teammates.storage.entity.FeedbackSession;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Student;
import teammates.ui.output.FeedbackQuestionData;
import teammates.ui.output.FeedbackQuestionsData;
import teammates.ui.request.Intent;

/**
 * Get a list of feedback questions for a feedback session.
 */
public class GetFeedbackQuestionsAction extends BasicFeedbackSubmissionAction {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.PUBLIC;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        UUID feedbackSessionId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_ID);
        Intent intent = Intent.valueOf(getNonNullRequestParamValue(Const.ParamsNames.INTENT));

        FeedbackSession feedbackSession = logic.getFeedbackSession(feedbackSessionId);
        if (feedbackSession == null) {
            throw new EntityNotFoundException("Feedback session not found");
        }
        String courseId = feedbackSession.getCourseId();

        switch (intent) {
        case STUDENT_SUBMISSION:
            Student student = getStudentOfCourseFromRequest(courseId);
            checkAccessControlForStudentFeedbackSubmission(student, feedbackSession);
            break;
        case FULL_DETAIL:
            gateKeeper.verifyLoggedInUserPrivileges(userInfo);
            gateKeeper.verifyAccessible(logic.getInstructorByGoogleId(courseId, userInfo.getId()),
                    feedbackSession);
            break;
        case INSTRUCTOR_SUBMISSION:
            Instructor instructor = getInstructorOfCourseFromRequest(courseId);
            checkAccessControlForInstructorFeedbackSubmission(instructor, feedbackSession);
            break;
        case INSTRUCTOR_RESULT:
            instructor = getInstructorOfCourseFromRequest(courseId);
            checkAccessControlForInstructorFeedbackResult(instructor, feedbackSession);
            break;
        case STUDENT_RESULT:
            student = getStudentOfCourseFromRequest(courseId);
            checkAccessControlForStudentFeedbackResult(student, feedbackSession);
            break;
        default:
            throw new InvalidHttpParameterException("Unknown intent " + intent);
        }
    }

    @Override
    public JsonResult execute() {
        UUID feedbackSessionId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_ID);
        Intent intent = Intent.valueOf(getNonNullRequestParamValue(Const.ParamsNames.INTENT));

        FeedbackSession feedbackSession = logic.getFeedbackSession(feedbackSessionId);
        if (feedbackSession == null) {
            throw new EntityNotFoundException("Feedback session not found");
        }

        List<FeedbackQuestion> questions;
        Map<UUID, Optional<List<String>>> dynamicallyGeneratedOptions = new HashMap<>();
        switch (intent) {
        case STUDENT_SUBMISSION:
            questions = logic.getFeedbackQuestionsForStudents(feedbackSession);
            Student student = getStudentOfCourseFromRequest(feedbackSession.getCourseId());
            for (FeedbackQuestion question : questions) {
                Optional<List<String>> options = logic.getDynamicallyGeneratedOptions(question, student);
                dynamicallyGeneratedOptions.put(question.getId(), options);
            }
            break;
        case INSTRUCTOR_SUBMISSION:
            Instructor instructor = getInstructorOfCourseFromRequest(feedbackSession.getCourseId());
            questions = logic.getFeedbackQuestionsForInstructors(feedbackSession, instructor.getEmail());
            for (FeedbackQuestion question : questions) {
                Optional<List<String>> options = logic.getDynamicallyGeneratedOptions(question, null);
                dynamicallyGeneratedOptions.put(question.getId(), options);
            }
            break;
        case FULL_DETAIL, INSTRUCTOR_RESULT, STUDENT_RESULT:
            questions = logic.getFeedbackQuestionsForSession(feedbackSession);
            break;
        default:
            throw new InvalidHttpParameterException("Unknown intent " + intent);
        }

        String moderatedPerson = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON);
        if (!StringHelper.isEmpty(moderatedPerson)) {
            // filter out questions that the instructor cannot see
            questions.removeIf(question -> !canInstructorSeeQuestion(question));
        }

        List<FeedbackQuestionData> questionDatas = questions.stream()
                .map(question -> {
                    Optional<List<String>> options =
                            dynamicallyGeneratedOptions.getOrDefault(question.getId(), Optional.empty());
                    return new FeedbackQuestionData(question, options);
                })
                .toList();

        if (intent == Intent.STUDENT_SUBMISSION || intent == Intent.STUDENT_RESULT) {
            for (FeedbackQuestionData questionData : questionDatas) {
                questionData.hideInformationForStudent();
            }
        }

        FeedbackQuestionsData response = new FeedbackQuestionsData(questionDatas);
        response.normalizeQuestionNumber();

        return new JsonResult(response);
    }
}
