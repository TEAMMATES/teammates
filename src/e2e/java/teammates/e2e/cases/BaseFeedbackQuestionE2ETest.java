package teammates.e2e.cases;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.FeedbackSubmitPage;
import teammates.e2e.pageobjects.InstructorFeedbackEditPage;

/**
 * Base class for all feedback question related browser tests.
 *
 * <p>SUT: {@link Const.WebPageURIs#INSTRUCTOR_SESSION_EDIT_PAGE}, {@link Const.WebPageURIs#SESSION_SUBMISSION_PAGE}.
 *
 * <p>Only UI-intensive operations, e.g. question creation and response submission, are tested separately.
 * This is so that if any part of the testing fails (due to regression or inherent instability), only the
 * specific test for the specific feedback question needs to be re-run.
 *
 * <p>For the above reason, viewing feedback responses/results is not considered to be under this test case.
 * This is because viewing results is a fast action and combining all question types together under one test case
 * will save some testing time.
 */
public abstract class BaseFeedbackQuestionE2ETest extends BaseE2ETestCase {
    InstructorAttributes instructor;
    CourseAttributes course;
    FeedbackSessionAttributes feedbackSession;
    StudentAttributes student;

    abstract void testEditPage();

    abstract void testSubmitPage();

    InstructorFeedbackEditPage loginToFeedbackEditPage() {
        AppUrl url = createFrontendUrl(Const.WebPageURIs.INSTRUCTOR_SESSION_EDIT_PAGE)
                .withCourseId(course.getId())
                .withSessionName(feedbackSession.getFeedbackSessionName());

        return loginToPage(url, InstructorFeedbackEditPage.class, instructor.getGoogleId());
    }

    FeedbackSubmitPage loginToFeedbackSubmitPage() {
        AppUrl url = createFrontendUrl(Const.WebPageURIs.STUDENT_SESSION_SUBMISSION_PAGE)
                .withCourseId(student.getCourse())
                .withSessionName(feedbackSession.getFeedbackSessionName());

        return loginToPage(url, FeedbackSubmitPage.class, student.getGoogleId());
    }

    FeedbackSubmitPage getFeedbackSubmitPage() {
        AppUrl url = createFrontendUrl(Const.WebPageURIs.STUDENT_SESSION_SUBMISSION_PAGE)
                .withCourseId(student.getCourse())
                .withSessionName(feedbackSession.getFeedbackSessionName());

        return getNewPageInstance(url, FeedbackSubmitPage.class);
    }
}
