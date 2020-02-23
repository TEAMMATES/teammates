package teammates.ui.webapi.action;

import teammates.common.datatransfer.SectionDetail;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.EntityNotFoundException;
import teammates.common.exception.ExceedingRangeException;
import teammates.common.exception.RequestExceedingRangeException;
import teammates.common.util.Assumption;
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
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);

        String questionId = getRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_ID);
        String section = getRequestParamValue(Const.ParamsNames.SECTION_NAME);
        String sectionDetailValue = getRequestParamValue(Const.ParamsNames.SECTION_NAME_DETAIL);
        SectionDetail sectionDetail = SectionDetail.NOT_APPLICABLE;
        boolean isMissingResponsesShown = getBooleanRequestParamValue(
                Const.ParamsNames.FEEDBACK_RESULTS_INDICATE_MISSING_RESPONSES);
        boolean isStatsShown = getBooleanRequestParamValue(Const.ParamsNames.FEEDBACK_RESULTS_SHOWSTATS);

        String fileContent = "";
        if (section != null && sectionDetailValue != null && !sectionDetailValue.isEmpty()) {
            Assumption.assertNotNull(SectionDetail.containsSectionDetail(sectionDetailValue));
            sectionDetail = SectionDetail.valueOf(sectionDetailValue);
        }

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
