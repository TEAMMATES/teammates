package teammates.ui.controller;

import java.util.HashMap;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import teammates.common.Common;

/**
 * Is used to generate the matching {@link Action} for a given URI.
 */
public class ActionFactory {
	protected static Logger log = Common.getLogger();
	
	private static HashMap<String, Class<? extends Action>> actionMappings;
	
	static{
		//TODO: replace these literals with constants uses elsewhere
		actionMappings = new HashMap<String, Class<? extends Action>>();
		actionMappings.put("/admin/adminHome", AdminHomePageAction.class);
		actionMappings.put(Common.PAGE_ADMIN_ACCOUNT_DELETE, AdminAccountDeleteAction.class);
		actionMappings.put(Common.PAGE_ADMIN_ACTIVITY_LOG, AdminActivityLogPageAction.class);
		actionMappings.put("/admin/adminAccountDetails", AdminAccountDetailsPageAction.class);
		actionMappings.put("/admin/adminAccountManagement", AdminAccountManagementPageAction.class);
		actionMappings.put(Common.PAGE_ADMIN_EXCEPTION_TEST, AdminExceptionTestAction.class);
		actionMappings.put("/admin/adminInstructorAccountAdd", AdminInstructorAccountAddAction.class);
		actionMappings.put(Common.PAGE_ADMIN_SEARCH, AdminSearchPageAction.class);
		actionMappings.put("/page/instructorCourse", InstructorCoursePageAction.class);
		actionMappings.put("/page/instructorCourseAdd", InstructorCourseAddAction.class);
		actionMappings.put("/page/instructorCourseDelete", InstructorCourseDeleteAction.class);
		actionMappings.put("/page/instructorCourseDetails", InstructorCourseDetailsPageAction.class);
		actionMappings.put("/page/instructorCourseRemind", InstructorCourseRemindAction.class);
		actionMappings.put("/page/instructorCourseEdit", InstructorCourseEditPageAction.class);
		actionMappings.put("/page/instructorCourseEditSave", InstructorCourseEditSaveAction.class);
		actionMappings.put("/page/instructorCourseEnroll", InstructorCourseEnrollPageAction.class);
		actionMappings.put("/page/instructorCourseEnrollSave", InstructorCourseEnrollSaveAction.class);
		actionMappings.put("/page/instructorCourseStudentDelete", InstructorCourseStudentDeleteAction.class);
		actionMappings.put("/page/instructorCourseStudentDetails", InstructorCourseStudentDetailsPageAction.class);
		actionMappings.put("/page/instructorCourseStudentDetailsEdit", InstructorCourseStudentDetailsEditPageAction.class);
		actionMappings.put("/page/instructorCourseStudentDetailsEditSave", InstructorCourseStudentDetailsEditSaveAction.class);
		actionMappings.put("/page/instructorEval", InstructorEvalPageAction.class);
		actionMappings.put("/page/instructorEvalAdd", InstructorEvalAddAction.class);
		actionMappings.put("/page/instructorEvalDelete", InstructorEvalDeleteAction.class);
		actionMappings.put("/page/instructorEvalEdit", InstructorEvalEditPageAction.class);
		actionMappings.put("/page/instructorEvalEditSave", InstructorEvalEditSaveAction.class);
		actionMappings.put("/page/instructorEvalPublish", InstructorEvalPublishAction.class);
		actionMappings.put("/page/instructorEvalRemind", InstructorEvalRemindAction.class);
		actionMappings.put("/page/instructorEvalResults", InstructorEvalResultsPageAction.class);
		actionMappings.put("/page/instructorEvalSubmissionEdit", InstructorEvalSubmissionEditPageAction.class);
		actionMappings.put("/page/instructorEvalSubmissionEditHandler", InstructorEvalSubmissionEditSaveAction.class);
		actionMappings.put("/page/instructorEvalSubmissionView", InstructorEvalSubmissionViewPageAction.class);
		actionMappings.put("/page/instructorEvalUnpublish", InstructorEvalUnpublishAction.class);
		actionMappings.put("/page/instructorFeedback", InstructorFeedbackPageAction.class);
		actionMappings.put("/page/instructorFeedbackAdd", InstructorFeedbackAddAction.class);
		actionMappings.put("/page/instructorFeedbackDelete", InstructorFeedbackDeleteAction.class);
		actionMappings.put("/page/instructorFeedbackEdit", InstructorFeedbackEditPageAction.class);
		actionMappings.put("/page/instructorFeedbackEditSave", InstructorFeedbackEditSaveAction.class);
		actionMappings.put("/page/instructorFeedbackQuestionAdd", InstructorFeedbackQuestionAddAction.class);
		actionMappings.put("/page/instructorFeedbackQuestionEdit", InstructorFeedbackQuestionEditAction.class);
		actionMappings.put("/page/instructorFeedbackResults", InstructorFeedbackResultsPageAction.class);
		actionMappings.put("/page/instructorFeedbackResultsDownload", InstructorFeedbackResultsDownloadAction.class);
		actionMappings.put("/page/instructorHome", InstructorHomePageAction.class);

		actionMappings.put("/page/studentCourseDetails", StudentCourseDetailsPageAction.class);
		actionMappings.put("/page/studentCourseJoin", StudentCourseJoinAction.class);
		actionMappings.put("/page/studentEvalEdit", StudentEvalSubmissionEditPageAction.class);
		actionMappings.put("/page/studentEvalResults", StudentEvalResultsPageAction.class);
		actionMappings.put("/page/studentEvalEditHandler", StudentEvalSubmissionEditSaveAction.class);
		actionMappings.put("/page/studentFeedbackResults", StudentFeedbackResultsPageAction.class);
		actionMappings.put("/page/studentFeedbackSubmit", StudentFeedbackSubmitPageAction.class);
		actionMappings.put("/page/studentFeedbackSubmitSave", StudentFeedbackSubmitSaveAction.class);
		actionMappings.put("/page/studentHome", StudentHomePageAction.class);
	}
		
	/**
	 * 
	 * @param req
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
		
		Action c = null;
		
		try {
			c = (Action)controllerClass.newInstance();
		} catch (Exception e) {
			throw new RuntimeException("Could not create the action for :" + uri);
		}
		
		return c;
	}

}
