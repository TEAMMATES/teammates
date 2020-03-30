package teammates.ui.webapi.action;

import teammates.common.datatransfer.SectionDetail;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.EntityNotFoundException;
import teammates.common.exception.ExceedingRangeException;
import teammates.common.exception.InvalidHttpParameterException;
import teammates.common.exception.RequestExceedingRangeException;
import teammates.common.util.Const;

/**
 * Gets feedback session results in csv.
 */
public class GetSessionResultsAsCsvAction extends Action {

    @Override
    protected AuthType getMinAuthLevel() {
        return AuthType.PUBLIC;
    }

    @Override
    public void checkSpecificAccessControl() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);

        FeedbackSessionAttributes fs = logic.getFeedbackSession(feedbackSessionName, courseId);

        if (fs == null) {
            throw new EntityNotFoundException(new EntityDoesNotExistException("Feedback session is not found"));
        }

        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, userInfo.id);
        gateKeeper.verifyAccessible(instructor, fs);
    }

    @Override
    public ActionResult execute() {
        // read in session details
        String section = getRequestParamValue(Const.ParamsNames.SECTION_NAME);
        String sectionDetailValue = getRequestParamValue(Const.ParamsNames.SECTION_NAME_DETAIL);
        SectionDetail sectionDetail = SectionDetail.NOT_APPLICABLE;
        if (section != null && sectionDetailValue != null && !sectionDetailValue.isEmpty()) {
            if (!SectionDetail.containsSectionDetail(sectionDetailValue)) {
                throw new InvalidHttpParameterException("Section detail is invalid.");
            }
            sectionDetail = SectionDetail.valueOf(sectionDetailValue);
        }

        // read in other session-related info
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        String questionId = getRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_ID);

        // read in optional params for csv generation
        boolean isMissingResponsesShown = getBooleanRequestParamValue(
                Const.ParamsNames.FEEDBACK_RESULTS_INDICATE_MISSING_RESPONSES);
        boolean isStatsShown = getBooleanRequestParamValue(Const.ParamsNames.FEEDBACK_RESULTS_SHOWSTATS);

        String fileContent = "";

        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, userInfo.id);

        try {
            if (section == null) {
                fileContent = logic.getFeedbackSessionResultSummaryAsCsv(
                        courseId, feedbackSessionName, instructor.email,
                        isMissingResponsesShown, isStatsShown, questionId);
            } else {
                fileContent = logic.getFeedbackSessionResultSummaryInSectionAsCsv(
                        courseId, feedbackSessionName, instructor.email, section, sectionDetail,
                        questionId, isMissingResponsesShown, isStatsShown);
            }
        } catch (EntityDoesNotExistException e) {
            throw new EntityNotFoundException(e);
        } catch (ExceedingRangeException e) {
            throw new RequestExceedingRangeException(e);
        }

        return new CsvResult(fileContent);
    }
}
