package teammates.test.cases.browsertests;

import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.test.pageobjects.InstructorFeedbackEditPage;

/**
 * Base class for all Feedback*QuestionUiTest.
 */
public abstract class BaseFeedbackQuestionUiTest extends BaseLegacyUiTestCase {

    protected abstract void testNewQuestionFrame();

    protected abstract void testInputValidation();

    protected abstract void testCustomizeOptions();

    protected abstract void testAddQuestionAction() throws Exception;

    protected abstract void testEditQuestionAction() throws Exception;

    protected abstract void testDeleteQuestionAction();

    protected InstructorFeedbackEditPage getFeedbackEditPage(String instructorId, String courseId,
            String feedbackSessionName) {
        AppUrl feedbackPageLink = createUrl(Const.WebPageURIs.INSTRUCTOR_SESSION_EDIT_PAGE).withUserId(instructorId)
                .withCourseId(courseId).withSessionName(feedbackSessionName); // .withEnableSessionEditDetails(true);
        return loginAdminToPageOld(feedbackPageLink, InstructorFeedbackEditPage.class);
    }
}
