package teammates.sqlui.webapi;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.ui.output.ActionClasses;
import teammates.ui.webapi.AccountRequestSearchIndexingWorkerAction;
import teammates.ui.webapi.Action;
import teammates.ui.webapi.AdminExceptionTestAction;
import teammates.ui.webapi.ArchiveCourseAction;
import teammates.ui.webapi.BinCourseAction;
import teammates.ui.webapi.BinFeedbackSessionAction;
import teammates.ui.webapi.CalculateUsageStatisticsAction;
import teammates.ui.webapi.CompileLogsAction;
import teammates.ui.webapi.CreateAccountAction;
import teammates.ui.webapi.CreateAccountRequestAction;
import teammates.ui.webapi.CreateCourseAction;
import teammates.ui.webapi.CreateFeedbackQuestionAction;
import teammates.ui.webapi.CreateFeedbackResponseCommentAction;
import teammates.ui.webapi.CreateFeedbackSessionAction;
import teammates.ui.webapi.CreateFeedbackSessionLogAction;
import teammates.ui.webapi.CreateInstructorAction;
import teammates.ui.webapi.CreateNotificationAction;
import teammates.ui.webapi.DatastoreBackupAction;
import teammates.ui.webapi.DeleteAccountAction;
import teammates.ui.webapi.DeleteAccountRequestAction;
import teammates.ui.webapi.DeleteCourseAction;
import teammates.ui.webapi.DeleteDataBundleAction;
import teammates.ui.webapi.DeleteFeedbackQuestionAction;
import teammates.ui.webapi.DeleteFeedbackResponseCommentAction;
import teammates.ui.webapi.DeleteFeedbackSessionAction;
import teammates.ui.webapi.DeleteInstructorAction;
import teammates.ui.webapi.DeleteNotificationAction;
import teammates.ui.webapi.DeleteSqlDataBundleAction;
import teammates.ui.webapi.DeleteStudentAction;
import teammates.ui.webapi.DeleteStudentsAction;
import teammates.ui.webapi.EnrollStudentsAction;
import teammates.ui.webapi.FeedbackSessionClosedRemindersAction;
import teammates.ui.webapi.FeedbackSessionClosingSoonRemindersAction;
import teammates.ui.webapi.FeedbackSessionOpenedRemindersAction;
import teammates.ui.webapi.FeedbackSessionOpeningSoonRemindersAction;
import teammates.ui.webapi.FeedbackSessionPublishedEmailWorkerAction;
import teammates.ui.webapi.FeedbackSessionPublishedRemindersAction;
import teammates.ui.webapi.FeedbackSessionRemindEmailWorkerAction;
import teammates.ui.webapi.FeedbackSessionRemindParticularUsersEmailWorkerAction;
import teammates.ui.webapi.FeedbackSessionResendPublishedEmailWorkerAction;
import teammates.ui.webapi.FeedbackSessionUnpublishedEmailWorkerAction;
import teammates.ui.webapi.GenerateEmailAction;
import teammates.ui.webapi.GetAccountAction;
import teammates.ui.webapi.GetAccountRequestAction;
import teammates.ui.webapi.GetAccountRequestsAction;
import teammates.ui.webapi.GetAccountsAction;
import teammates.ui.webapi.GetActionClassesAction;
import teammates.ui.webapi.GetAuthInfoAction;
import teammates.ui.webapi.GetCourseAction;
import teammates.ui.webapi.GetCourseJoinStatusAction;
import teammates.ui.webapi.GetCourseSectionNamesAction;
import teammates.ui.webapi.GetCoursesAction;
import teammates.ui.webapi.GetDeadlineExtensionAction;
import teammates.ui.webapi.GetFeedbackQuestionRecipientsAction;
import teammates.ui.webapi.GetFeedbackQuestionsAction;
import teammates.ui.webapi.GetFeedbackResponseCommentAction;
import teammates.ui.webapi.GetFeedbackResponsesAction;
import teammates.ui.webapi.GetFeedbackSessionAction;
import teammates.ui.webapi.GetFeedbackSessionLogsAction;
import teammates.ui.webapi.GetFeedbackSessionSubmittedGiverSetAction;
import teammates.ui.webapi.GetFeedbackSessionsAction;
import teammates.ui.webapi.GetHasResponsesAction;
import teammates.ui.webapi.GetInstructorAction;
import teammates.ui.webapi.GetInstructorPrivilegeAction;
import teammates.ui.webapi.GetInstructorsAction;
import teammates.ui.webapi.GetNotificationAction;
import teammates.ui.webapi.GetNotificationsAction;
import teammates.ui.webapi.GetOngoingSessionsAction;
import teammates.ui.webapi.GetReadNotificationsAction;
import teammates.ui.webapi.GetRegkeyValidityAction;
import teammates.ui.webapi.GetSessionResponseStatsAction;
import teammates.ui.webapi.GetSessionResultsAction;
import teammates.ui.webapi.GetStudentAction;
import teammates.ui.webapi.GetStudentsAction;
import teammates.ui.webapi.GetTimeZonesAction;
import teammates.ui.webapi.GetUsageStatisticsAction;
import teammates.ui.webapi.GetUserCookieAction;
import teammates.ui.webapi.InstructorCourseJoinEmailWorkerAction;
import teammates.ui.webapi.InstructorSearchIndexingWorkerAction;
import teammates.ui.webapi.JoinCourseAction;
import teammates.ui.webapi.JsonResult;
import teammates.ui.webapi.MarkNotificationAsReadAction;
import teammates.ui.webapi.PublishFeedbackSessionAction;
import teammates.ui.webapi.PutDataBundleAction;
import teammates.ui.webapi.PutDataBundleDocumentsAction;
import teammates.ui.webapi.PutSqlDataBundleAction;
import teammates.ui.webapi.QueryLogsAction;
import teammates.ui.webapi.RegenerateInstructorKeyAction;
import teammates.ui.webapi.RegenerateStudentKeyAction;
import teammates.ui.webapi.RejectAccountRequestAction;
import teammates.ui.webapi.RemindFeedbackSessionResultAction;
import teammates.ui.webapi.RemindFeedbackSessionSubmissionAction;
import teammates.ui.webapi.ResetAccountAction;
import teammates.ui.webapi.ResetAccountRequestAction;
import teammates.ui.webapi.RestoreCourseAction;
import teammates.ui.webapi.RestoreFeedbackSessionAction;
import teammates.ui.webapi.SearchAccountRequestsAction;
import teammates.ui.webapi.SearchInstructorsAction;
import teammates.ui.webapi.SearchStudentsAction;
import teammates.ui.webapi.SendEmailWorkerAction;
import teammates.ui.webapi.SendErrorReportAction;
import teammates.ui.webapi.SendJoinReminderEmailAction;
import teammates.ui.webapi.SendLoginEmailAction;
import teammates.ui.webapi.SessionLinksRecoveryAction;
import teammates.ui.webapi.StudentCourseJoinEmailWorkerAction;
import teammates.ui.webapi.StudentSearchIndexingWorkerAction;
import teammates.ui.webapi.SubmitFeedbackResponsesAction;
import teammates.ui.webapi.UnpublishFeedbackSessionAction;
import teammates.ui.webapi.UpdateAccountRequestAction;
import teammates.ui.webapi.UpdateCourseAction;
import teammates.ui.webapi.UpdateFeedbackQuestionAction;
import teammates.ui.webapi.UpdateFeedbackResponseCommentAction;
import teammates.ui.webapi.UpdateFeedbackSessionAction;
import teammates.ui.webapi.UpdateFeedbackSessionLogsAction;
import teammates.ui.webapi.UpdateInstructorAction;
import teammates.ui.webapi.UpdateInstructorPrivilegeAction;
import teammates.ui.webapi.UpdateNotificationAction;
import teammates.ui.webapi.UpdateStudentAction;

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
    void testExecute() {
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
                FeedbackSessionClosingSoonRemindersAction.class,
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
                GetAccountRequestsAction.class,
                UpdateAccountRequestAction.class,
                RejectAccountRequestAction.class,
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
                FeedbackSessionOpenedRemindersAction.class,
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
                SendLoginEmailAction.class,
                PutSqlDataBundleAction.class,
                DeleteSqlDataBundleAction.class,
                UpdateFeedbackSessionLogsAction.class
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

    @Test
    void testAccessControl() {
        verifyOnlyAdminsCanAccess();
        verifyMaintainersCanAccess();
    }
}
