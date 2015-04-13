package teammates.test.cases.ui.browsertests;

import teammates.common.util.Const;
import teammates.common.util.Url;
import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.InstructorFeedbackEditPage;

public abstract class FeedbackQuestionUiTest extends BaseUiTestCase {

    public abstract void testNewQuestionFrame();

    public abstract void testInputValidation();

    public abstract void testCustomizeOptions();

    public abstract void testAddQuestionAction();

    public abstract void testEditQuestionAction();

    public abstract void testDeleteQuestionAction();
    
    protected InstructorFeedbackEditPage getFeedbackEditPage(String instructorId, String courseId, 
            String feedbackSessionName, Browser browser) {
        Url feedbackPageLink = createUrl(Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE).
                withUserId(instructorId).withCourseId(courseId).withSessionName(feedbackSessionName);
        return loginAdminToPage(browser, feedbackPageLink, InstructorFeedbackEditPage.class);
    }
    
}
