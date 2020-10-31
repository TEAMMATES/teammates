package teammates.e2e.cases.e2e;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.AppPage;
import teammates.e2e.pageobjects.FeedbackSubmitPage;
import teammates.e2e.pageobjects.InstructorFeedbackEditPage;

/**
 * Base class for all feedback question related browser tests.
 *
 * <p>SUT: {@link Const.WebPageURIs#INSTRUCTOR_SESSION_EDIT_PAGE}, {@link Const.WebPageURIs#SESSION_SUBMISSION_PAGE}.
 */
public abstract class BaseFeedbackQuestionE2ETest extends BaseE2ETestCase {
    protected InstructorAttributes instructor;
    protected CourseAttributes course;
    protected FeedbackSessionAttributes feedbackSession;
    protected StudentAttributes student;

    protected abstract void testEditPage();

    protected abstract void testSubmitPage();

    protected InstructorFeedbackEditPage loginToFeedbackEditPage() {
        AppUrl url = createUrl(Const.WebPageURIs.INSTRUCTOR_SESSION_EDIT_PAGE)
                .withUserId(instructor.googleId)
                .withCourseId(course.getId())
                .withSessionName(feedbackSession.getFeedbackSessionName());

        return loginAdminToPage(url, InstructorFeedbackEditPage.class);
    }

    protected FeedbackSubmitPage loginToFeedbackSubmitPage() {
        AppUrl url = createUrl(Const.WebPageURIs.SESSION_SUBMISSION_PAGE)
                .withUserId(student.googleId)
                .withCourseId(student.course)
                .withSessionName(feedbackSession.getFeedbackSessionName());

        FeedbackSubmitPage submitPage = loginAdminToPage(url, FeedbackSubmitPage.class);
        submitPage.reloadPageIfStuckLoading();

        return submitPage;
    }

    protected FeedbackSubmitPage getFeedbackSubmitPage() {
        AppUrl url = createUrl(Const.WebPageURIs.SESSION_SUBMISSION_PAGE)
                .withUserId(student.googleId)
                .withCourseId(student.course)
                .withSessionName(feedbackSession.getFeedbackSessionName());

        FeedbackSubmitPage submitPage = AppPage.getNewPageInstance(browser, url, FeedbackSubmitPage.class);
        submitPage.reloadPageIfStuckLoading();

        return submitPage;
    }
}
