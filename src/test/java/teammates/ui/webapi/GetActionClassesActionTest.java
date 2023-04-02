package teammates.ui.webapi;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.ui.output.ActionClasses;

/**
 * SUT: {@link GetActionClassesAction}.
 */
public class GetActionClassesActionTest extends BaseActionTest<GetActionClassesAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.ACTION_CLASS;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Test
    @Override
    protected void testExecute() {
        List<Class<? extends Action>> expectedActionClasses = Arrays.asList(
                DeleteFeedbackResponseCommentAction.class,
                CreateFeedbackResponseCommentAction.class,
                GetFeedbackResponseCommentAction.class,
                UpdateFeedbackResponseCommentAction.class,
                RestoreFeedbackSessionAction.class,
                BinFeedbackSessionAction.class,
                GetCoursesAction.class,
                GetRegkeyValidityAction.class,
                PutDataBundleAction.class,
                DeleteDataBundleAction.class,
                CreateFeedbackSessionLogAction.class,
                GetFeedbackSessionLogsAction.class,
                GetInstructorPrivilegeAction.class,
                UpdateInstructorPrivilegeAction.class,
                GetFeedbackSessionsAction.class,
                GenerateEmailAction.class,
                GetFeedbackQuestionsAction.class,
                GetOngoingSessionsAction.class,
                AdminExceptionTestAction.class,
                GetUserCookieAction.class,
                RemindFeedbackSessionResultAction.class,
                DeleteInstructorAction.class,
                CreateInstructorAction.class,
                GetInstructorAction.class,
                UpdateInstructorAction.class,
                ArchiveCourseAction.class,
                InstructorCourseJoinEmailWorkerAction.class,
                DeleteStudentAction.class,
                GetStudentAction.class,
                UpdateStudentAction.class,
                SearchStudentsAction.class,
                FeedbackSessionRemindEmailWorkerAction.class,
                DeleteFeedbackSessionAction.class,
                CreateFeedbackSessionAction.class,
                GetFeedbackSessionAction.class,
                UpdateFeedbackSessionAction.class,
                FeedbackSessionClosingRemindersAction.class,
                GetTimeZonesAction.class,
                FeedbackSessionRemindParticularUsersEmailWorkerAction.class,
                GetFeedbackResponsesAction.class,
                SubmitFeedbackResponsesAction.class,
                FeedbackSessionPublishedEmailWorkerAction.class,
                FeedbackSessionClosedRemindersAction.class,
                SendErrorReportAction.class,
                GetActionClassesAction.class,
                UnpublishFeedbackSessionAction.class,
                PublishFeedbackSessionAction.class,
                GetSessionResultsAction.class,
                GetHasResponsesAction.class,
                DatastoreBackupAction.class,
                RestoreCourseAction.class,
                BinCourseAction.class,
                DeleteAccountAction.class,
                CreateAccountAction.class,
                CreateAccountRequestAction.class,
                GetAccountRequestAction.class,
                DeleteAccountRequestAction.class,
                GetAccountAction.class,
                GetAccountsAction.class,
                FeedbackSessionPublishedRemindersAction.class,
                QueryLogsAction.class,
                SessionLinksRecoveryAction.class,
                SendJoinReminderEmailAction.class,
                RegenerateInstructorKeyAction.class,
                RegenerateStudentKeyAction.class,
                CompileLogsAction.class,
                GetAuthInfoAction.class,
                GetFeedbackSessionSubmittedGiverSetAction.class,
                GetCourseJoinStatusAction.class,
                JoinCourseAction.class,
                GetSessionResponseStatsAction.class,
                DeleteCourseAction.class,
                CreateCourseAction.class,
                GetCourseAction.class,
                UpdateCourseAction.class,
                GetFeedbackQuestionRecipientsAction.class,
                RemindFeedbackSessionSubmissionAction.class,
                FeedbackSessionUnpublishedEmailWorkerAction.class,
                SendEmailWorkerAction.class,
                GetInstructorsAction.class,
                PutDataBundleDocumentsAction.class,
                FeedbackSessionResendPublishedEmailWorkerAction.class,
                StudentCourseJoinEmailWorkerAction.class,
                SearchInstructorsAction.class,
                GetCourseSectionNamesAction.class,
                ResetAccountAction.class,
                FeedbackSessionOpeningRemindersAction.class,
                FeedbackSessionOpeningSoonRemindersAction.class,
                DeleteStudentsAction.class,
                GetStudentsAction.class,
                EnrollStudentsAction.class,
                DeleteFeedbackQuestionAction.class,
                CreateFeedbackQuestionAction.class,
                UpdateFeedbackQuestionAction.class,
                InstructorSearchIndexingWorkerAction.class,
                StudentSearchIndexingWorkerAction.class,
                AccountRequestSearchIndexingWorkerAction.class,
                SearchAccountRequestsAction.class,
                ResetAccountRequestAction.class,
                CalculateUsageStatisticsAction.class,
                GetUsageStatisticsAction.class,
                GetNotificationAction.class,
                CreateNotificationAction.class,
                UpdateNotificationAction.class,
                DeleteNotificationAction.class,
                GetNotificationsAction.class,
                MarkNotificationAsReadAction.class,
                GetReadNotificationsAction.class,
                GetDeadlineExtensionAction.class,
                SendLoginEmailAction.class
        );
        List<String> expectedActionClassesNames = expectedActionClasses.stream()
                .map(Class::getSimpleName)
                .sorted()
                .collect(Collectors.toList());

        GetActionClassesAction action = getAction();
        action.execute();
        JsonResult result = getJsonResult(action);
        ActionClasses data = (ActionClasses) result.getOutput();
        List<String> actualActionClasses = data.getActionClasses();
        Collections.sort(actualActionClasses);

        assertEquals(expectedActionClassesNames, actualActionClasses);
    }

    @Override
    @Test
    protected void testAccessControl() {
        verifyAccessibleForAdmin();
        verifyAccessibleForMaintainers();
        verifyInaccessibleForStudents();
        verifyInaccessibleForInstructors();
        verifyInaccessibleWithoutLogin();
        verifyInaccessibleForUnregisteredUsers();
    }

}
