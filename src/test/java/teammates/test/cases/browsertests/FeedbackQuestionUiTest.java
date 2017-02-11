package teammates.test.cases.browsertests;

import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.test.pageobjects.InstructorFeedbackEditPage;

public abstract class FeedbackQuestionUiTest extends BaseUiTestCase {

    public abstract void testNewQuestionFrame();

    public abstract void testInputValidation();

    public abstract void testCustomizeOptions();

    public abstract void testAddQuestionAction() throws Exception;

    public abstract void testEditQuestionAction() throws Exception;

    public abstract void testDeleteQuestionAction();
    
    protected InstructorFeedbackEditPage getFeedbackEditPage(String instructorId, String courseId,
            String feedbackSessionName) {
        AppUrl feedbackPageLink = createUrl(Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE)
                .withUserId(instructorId).withCourseId(courseId).withSessionName(feedbackSessionName);
        return loginAdminToPage(feedbackPageLink, InstructorFeedbackEditPage.class);
    }
    
}
