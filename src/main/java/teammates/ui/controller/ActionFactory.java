package teammates.ui.controller;

import static teammates.common.util.Const.ActionURIs.ADMIN_ACCOUNT_DELETE;
import static teammates.common.util.Const.ActionURIs.ADMIN_ACCOUNT_DETAILS_PAGE;
import static teammates.common.util.Const.ActionURIs.ADMIN_ACCOUNT_MANAGEMENT_PAGE;
import static teammates.common.util.Const.ActionURIs.ADMIN_ACTIVITY_LOG_PAGE;
import static teammates.common.util.Const.ActionURIs.ADMIN_EXCEPTION_TEST;
import static teammates.common.util.Const.ActionURIs.ADMIN_HOME_PAGE;
import static teammates.common.util.Const.ActionURIs.ADMIN_INSTRUCTORACCOUNT_ADD;
import static teammates.common.util.Const.ActionURIs.ADMIN_SEARCH_PAGE;
import static teammates.common.util.Const.ActionURIs.INSTRUCTOR_COURSES_PAGE;
import static teammates.common.util.Const.ActionURIs.INSTRUCTOR_COURSE_ADD;
import static teammates.common.util.Const.ActionURIs.INSTRUCTOR_COURSE_ARCHIVE;
import static teammates.common.util.Const.ActionURIs.INSTRUCTOR_COURSE_DELETE;
import static teammates.common.util.Const.ActionURIs.INSTRUCTOR_COURSE_DETAILS_PAGE;
import static teammates.common.util.Const.ActionURIs.INSTRUCTOR_COURSE_EDIT_PAGE;
import static teammates.common.util.Const.ActionURIs.INSTRUCTOR_COURSE_ENROLL_PAGE;
import static teammates.common.util.Const.ActionURIs.INSTRUCTOR_COURSE_ENROLL_SAVE;
import static teammates.common.util.Const.ActionURIs.INSTRUCTOR_COURSE_INSTRUCTOR_ADD;
import static teammates.common.util.Const.ActionURIs.INSTRUCTOR_COURSE_INSTRUCTOR_DELETE;
import static teammates.common.util.Const.ActionURIs.INSTRUCTOR_COURSE_INSTRUCTOR_EDIT_SAVE;
import static teammates.common.util.Const.ActionURIs.INSTRUCTOR_COURSE_REMIND;
import static teammates.common.util.Const.ActionURIs.INSTRUCTOR_COURSE_STUDENT_DELETE;
import static teammates.common.util.Const.ActionURIs.INSTRUCTOR_COURSE_STUDENT_DETAILS_EDIT;
import static teammates.common.util.Const.ActionURIs.INSTRUCTOR_COURSE_STUDENT_DETAILS_EDIT_SAVE;
import static teammates.common.util.Const.ActionURIs.INSTRUCTOR_COURSE_STUDENT_DETAILS_PAGE;
import static teammates.common.util.Const.ActionURIs.INSTRUCTOR_EVALS_PAGE;
import static teammates.common.util.Const.ActionURIs.INSTRUCTOR_EVAL_ADD;
import static teammates.common.util.Const.ActionURIs.INSTRUCTOR_EVAL_DELETE;
import static teammates.common.util.Const.ActionURIs.INSTRUCTOR_EVAL_EDIT_PAGE;
import static teammates.common.util.Const.ActionURIs.INSTRUCTOR_EVAL_EDIT_SAVE;
import static teammates.common.util.Const.ActionURIs.INSTRUCTOR_EVAL_PREVIEW;
import static teammates.common.util.Const.ActionURIs.INSTRUCTOR_EVAL_PUBLISH;
import static teammates.common.util.Const.ActionURIs.INSTRUCTOR_EVAL_REMIND;
import static teammates.common.util.Const.ActionURIs.INSTRUCTOR_EVAL_RESULTS_DOWNLOAD;
import static teammates.common.util.Const.ActionURIs.INSTRUCTOR_EVAL_RESULTS_PAGE;
import static teammates.common.util.Const.ActionURIs.INSTRUCTOR_EVAL_STATS_PAGE;
import static teammates.common.util.Const.ActionURIs.INSTRUCTOR_EVAL_SUBMISSION_EDIT;
import static teammates.common.util.Const.ActionURIs.INSTRUCTOR_EVAL_SUBMISSION_EDIT_SAVE;
import static teammates.common.util.Const.ActionURIs.INSTRUCTOR_EVAL_SUBMISSION_PAGE;
import static teammates.common.util.Const.ActionURIs.INSTRUCTOR_EVAL_UNPUBLISH;
import static teammates.common.util.Const.ActionURIs.INSTRUCTOR_FEEDBACKS_PAGE;
import static teammates.common.util.Const.ActionURIs.INSTRUCTOR_FEEDBACK_ADD;
import static teammates.common.util.Const.ActionURIs.INSTRUCTOR_FEEDBACK_DELETE;
import static teammates.common.util.Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE;
import static teammates.common.util.Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_SAVE;
import static teammates.common.util.Const.ActionURIs.INSTRUCTOR_FEEDBACK_PREVIEW_ASINSTRUCTOR;
import static teammates.common.util.Const.ActionURIs.INSTRUCTOR_FEEDBACK_PREVIEW_ASSTUDENT;
import static teammates.common.util.Const.ActionURIs.INSTRUCTOR_FEEDBACK_PUBLISH;
import static teammates.common.util.Const.ActionURIs.INSTRUCTOR_FEEDBACK_QUESTION_ADD;
import static teammates.common.util.Const.ActionURIs.INSTRUCTOR_FEEDBACK_QUESTION_EDIT;
import static teammates.common.util.Const.ActionURIs.INSTRUCTOR_FEEDBACK_QUESTION_SUBMISSION_EDIT_PAGE;
import static teammates.common.util.Const.ActionURIs.INSTRUCTOR_FEEDBACK_QUESTION_SUBMISSION_EDIT_SAVE;
import static teammates.common.util.Const.ActionURIs.INSTRUCTOR_FEEDBACK_REMIND;
import static teammates.common.util.Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESPONSE_COMMENT_ADD;
import static teammates.common.util.Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESULTS_DOWNLOAD;
import static teammates.common.util.Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESULTS_PAGE;
import static teammates.common.util.Const.ActionURIs.INSTRUCTOR_FEEDBACK_STATS_PAGE;
import static teammates.common.util.Const.ActionURIs.INSTRUCTOR_FEEDBACK_SUBMISSION_EDIT_PAGE;
import static teammates.common.util.Const.ActionURIs.INSTRUCTOR_FEEDBACK_SUBMISSION_EDIT_SAVE;
import static teammates.common.util.Const.ActionURIs.INSTRUCTOR_FEEDBACK_UNPUBLISH;
import static teammates.common.util.Const.ActionURIs.INSTRUCTOR_HOME_PAGE;
import static teammates.common.util.Const.ActionURIs.INSTRUCTOR_STUDENT_COMMENT_ADD;
import static teammates.common.util.Const.ActionURIs.INSTRUCTOR_STUDENT_COMMENT_EDIT;
import static teammates.common.util.Const.ActionURIs.INSTRUCTOR_STUDENT_LIST_PAGE;
import static teammates.common.util.Const.ActionURIs.INSTRUCTOR_STUDENT_RECORDS_PAGE;
import static teammates.common.util.Const.ActionURIs.STUDENT_COURSE_DETAILS_PAGE;
import static teammates.common.util.Const.ActionURIs.STUDENT_COURSE_JOIN;
import static teammates.common.util.Const.ActionURIs.STUDENT_COURSE_JOIN_AUTHENTICATED;
import static teammates.common.util.Const.ActionURIs.STUDENT_EVAL_RESULTS_PAGE;
import static teammates.common.util.Const.ActionURIs.STUDENT_EVAL_SUBMISSION_EDIT_PAGE;
import static teammates.common.util.Const.ActionURIs.STUDENT_EVAL_SUBMISSION_EDIT_SAVE;
import static teammates.common.util.Const.ActionURIs.STUDENT_FEEDBACK_QUESTION_SUBMISSION_EDIT_PAGE;
import static teammates.common.util.Const.ActionURIs.STUDENT_FEEDBACK_QUESTION_SUBMISSION_EDIT_SAVE;
import static teammates.common.util.Const.ActionURIs.STUDENT_FEEDBACK_RESULTS_PAGE;
import static teammates.common.util.Const.ActionURIs.STUDENT_FEEDBACK_SUBMISSION_EDIT_PAGE;
import static teammates.common.util.Const.ActionURIs.STUDENT_FEEDBACK_SUBMISSION_EDIT_SAVE;
import static teammates.common.util.Const.ActionURIs.STUDENT_HOME_PAGE;

import java.util.HashMap;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import teammates.common.util.Utils;

/**
 * Is used to generate the matching {@link Action} for a given URI.
 */
public class ActionFactory {
	protected static Logger log = Utils.getLogger();
	
	private static HashMap<String, Class<? extends Action>> actionMappings = new HashMap<String, Class<? extends Action>>();
	
	static{
		map(ADMIN_HOME_PAGE, AdminHomePageAction.class);
		map(ADMIN_ACCOUNT_DELETE, AdminAccountDeleteAction.class);
		map(ADMIN_ACTIVITY_LOG_PAGE, AdminActivityLogPageAction.class);
		map(ADMIN_ACCOUNT_DETAILS_PAGE, AdminAccountDetailsPageAction.class);
		map(ADMIN_ACCOUNT_MANAGEMENT_PAGE, AdminAccountManagementPageAction.class);
		map(ADMIN_EXCEPTION_TEST, AdminExceptionTestAction.class);
		map(ADMIN_INSTRUCTORACCOUNT_ADD, AdminInstructorAccountAddAction.class);
		map(ADMIN_SEARCH_PAGE, AdminSearchPageAction.class);
		
		map(INSTRUCTOR_COURSES_PAGE, InstructorCoursesPageAction.class);
		map(INSTRUCTOR_COURSE_ADD, InstructorCourseAddAction.class);
		map(INSTRUCTOR_COURSE_DELETE, InstructorCourseDeleteAction.class);
		map(INSTRUCTOR_COURSE_ARCHIVE, InstructorCourseArchiveAction.class);
		map(INSTRUCTOR_COURSE_DETAILS_PAGE, InstructorCourseDetailsPageAction.class);
		map(INSTRUCTOR_COURSE_REMIND, InstructorCourseRemindAction.class);
		map(INSTRUCTOR_COURSE_EDIT_PAGE, InstructorCourseEditPageAction.class);
		map(INSTRUCTOR_COURSE_INSTRUCTOR_ADD, InstructorCourseInstructorAddAction.class);
		map(INSTRUCTOR_COURSE_INSTRUCTOR_EDIT_SAVE, InstructorCourseInstructorEditSaveAction.class);
		map(INSTRUCTOR_COURSE_INSTRUCTOR_DELETE, InstructorCourseInstructorDeleteAction.class);
		map(INSTRUCTOR_COURSE_ENROLL_PAGE, InstructorCourseEnrollPageAction.class);
		map(INSTRUCTOR_COURSE_ENROLL_SAVE, InstructorCourseEnrollSaveAction.class);
		map(INSTRUCTOR_COURSE_STUDENT_DELETE, InstructorCourseStudentDeleteAction.class);
		map(INSTRUCTOR_COURSE_STUDENT_DETAILS_PAGE, InstructorCourseStudentDetailsPageAction.class);
		map(INSTRUCTOR_COURSE_STUDENT_DETAILS_EDIT, InstructorCourseStudentDetailsEditPageAction.class);
		map(INSTRUCTOR_COURSE_STUDENT_DETAILS_EDIT_SAVE, InstructorCourseStudentDetailsEditSaveAction.class);
		map(INSTRUCTOR_EVALS_PAGE, InstructorEvalsPageAction.class);
		map(INSTRUCTOR_EVAL_ADD, InstructorEvalAddAction.class);
		map(INSTRUCTOR_EVAL_DELETE, InstructorEvalDeleteAction.class);
		map(INSTRUCTOR_EVAL_RESULTS_DOWNLOAD, InstructorEvalResultsDownloadAction.class);
		map(INSTRUCTOR_EVAL_EDIT_PAGE, InstructorEvalEditPageAction.class);
		map(INSTRUCTOR_EVAL_EDIT_SAVE, InstructorEvalEditSaveAction.class);
		map(INSTRUCTOR_EVAL_PREVIEW, InstructorEvalPreviewAction.class);
		map(INSTRUCTOR_EVAL_PUBLISH, InstructorEvalPublishAction.class);
		map(INSTRUCTOR_EVAL_REMIND, InstructorEvalRemindAction.class);
		map(INSTRUCTOR_EVAL_RESULTS_PAGE, InstructorEvalResultsPageAction.class);
		map(INSTRUCTOR_EVAL_STATS_PAGE, EvaluationStatsPageAction.class);
		map(INSTRUCTOR_EVAL_SUBMISSION_EDIT, InstructorEvalSubmissionEditPageAction.class);
		map(INSTRUCTOR_EVAL_SUBMISSION_EDIT_SAVE, InstructorEvalSubmissionEditSaveAction.class);
		map(INSTRUCTOR_EVAL_SUBMISSION_PAGE, InstructorEvalSubmissionPageAction.class);
		map(INSTRUCTOR_EVAL_UNPUBLISH, InstructorEvalUnpublishAction.class);
		map(INSTRUCTOR_FEEDBACKS_PAGE, InstructorFeedbacksPageAction.class);
		map(INSTRUCTOR_FEEDBACK_ADD, InstructorFeedbackAddAction.class);
		map(INSTRUCTOR_FEEDBACK_DELETE, InstructorFeedbackDeleteAction.class);
		map(INSTRUCTOR_FEEDBACK_REMIND, InstructorFeedbackRemindAction.class);
		map(INSTRUCTOR_FEEDBACK_PUBLISH, InstructorFeedbackPublishAction.class);
		map(INSTRUCTOR_FEEDBACK_UNPUBLISH, InstructorFeedbackUnpublishAction.class);
		map(INSTRUCTOR_FEEDBACK_EDIT_PAGE, InstructorFeedbackEditPageAction.class);
		map(INSTRUCTOR_FEEDBACK_EDIT_SAVE, InstructorFeedbackEditSaveAction.class);
		map(INSTRUCTOR_FEEDBACK_QUESTION_ADD, InstructorFeedbackQuestionAddAction.class);
		map(INSTRUCTOR_FEEDBACK_QUESTION_EDIT, InstructorFeedbackQuestionEditAction.class);
		map(INSTRUCTOR_FEEDBACK_RESULTS_PAGE, InstructorFeedbackResultsPageAction.class);
		map(INSTRUCTOR_FEEDBACK_RESULTS_DOWNLOAD, InstructorFeedbackResultsDownloadAction.class);
		map(INSTRUCTOR_FEEDBACK_RESPONSE_COMMENT_ADD, InstructorFeedbackResponseCommentAddAction.class);
		map(INSTRUCTOR_FEEDBACK_PREVIEW_ASSTUDENT, InstructorFeedbackPreviewAsStudentAction.class);
		map(INSTRUCTOR_FEEDBACK_PREVIEW_ASINSTRUCTOR, InstructorFeedbackPreviewAsInstructorAction.class);
		map(INSTRUCTOR_FEEDBACK_STATS_PAGE, FeedbackSessionStatsPageAction.class);
		map(INSTRUCTOR_FEEDBACK_SUBMISSION_EDIT_PAGE, InstructorFeedbackSubmissionEditPageAction.class);
		map(INSTRUCTOR_FEEDBACK_SUBMISSION_EDIT_SAVE, InstructorFeedbackSubmissionEditSaveAction.class);
		map(INSTRUCTOR_FEEDBACK_QUESTION_SUBMISSION_EDIT_PAGE, InstructorFeedbackQuestionSubmissionEditPageAction.class);
		map(INSTRUCTOR_FEEDBACK_QUESTION_SUBMISSION_EDIT_SAVE, InstructorFeedbackQuestionSubmissionEditSaveAction.class);
		map(INSTRUCTOR_HOME_PAGE, InstructorHomePageAction.class);
		map(INSTRUCTOR_STUDENT_LIST_PAGE, InstructorStudentListPageAction.class);
		map(INSTRUCTOR_STUDENT_RECORDS_PAGE, InstructorStudentRecordsPageAction.class);
		
		map(INSTRUCTOR_STUDENT_COMMENT_ADD, InstructorStudentCommentAddAction.class);
		map(INSTRUCTOR_STUDENT_COMMENT_EDIT, InstructorStudentCommentEditAction.class);

		map(STUDENT_COURSE_DETAILS_PAGE, StudentCourseDetailsPageAction.class);
		map(STUDENT_COURSE_JOIN, StudentCourseJoinAction.class);
		map(STUDENT_COURSE_JOIN_AUTHENTICATED, StudentCourseJoinAuthenticatedAction.class);
		map(STUDENT_EVAL_SUBMISSION_EDIT_PAGE, StudentEvalSubmissionEditPageAction.class);
		map(STUDENT_EVAL_RESULTS_PAGE, StudentEvalResultsPageAction.class);
		map(STUDENT_EVAL_SUBMISSION_EDIT_SAVE, StudentEvalSubmissionEditSaveAction.class);
		map(STUDENT_FEEDBACK_RESULTS_PAGE, StudentFeedbackResultsPageAction.class);
		map(STUDENT_FEEDBACK_SUBMISSION_EDIT_PAGE, StudentFeedbackSubmissionEditPageAction.class);
		map(STUDENT_FEEDBACK_SUBMISSION_EDIT_SAVE, StudentFeedbackSubmissionEditSaveAction.class);
		map(STUDENT_FEEDBACK_QUESTION_SUBMISSION_EDIT_PAGE, StudentFeedbackQuestionSubmissionEditPageAction.class);
		map(STUDENT_FEEDBACK_QUESTION_SUBMISSION_EDIT_SAVE, StudentFeedbackQuestionSubmissionEditSaveAction.class);
		map(STUDENT_HOME_PAGE, StudentHomePageAction.class);
		
		//These are here for backward compatibility reasons. We used these URIs
		//  before V4.55 and some users have these URIs in emails they 
		//  received from TEAMMATES.
		map("/page/studentEvalResults", StudentEvalResultsPageAction.class);
		map("/page/studentEvalEdit", StudentEvalSubmissionEditPageAction.class);
	}



	/**
	 * @return the matching {@link Action} object for the URI in the {@code req}.
	 *   The returned {@code Action} is already initialized using the {@code req}.
	 */
	public static Action getAction(HttpServletRequest req) {
		
		String url = req.getRequestURL().toString();
		log.info("URL received :" + url);
		
		String uri = req.getRequestURI();
		Action c = getAction(uri);
		c.init(req);
		return c;
		
	}

	private static Action getAction(String uri) {
		Class<? extends Action> controllerClass = actionMappings.get(uri);
		
		if(controllerClass == null){
			return new NonExistentAction();
		}
		
		try {
			return controllerClass.newInstance();
		} catch (Exception e) {
			throw new RuntimeException("Could not create the action for :" + uri);
		}
		
	}



	private static void map(String actionUri, Class<? extends Action> actionClass) {
		actionMappings.put(actionUri, actionClass);
	}

}
