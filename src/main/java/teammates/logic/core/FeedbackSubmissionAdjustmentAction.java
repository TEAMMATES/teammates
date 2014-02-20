package teammates.logic.core;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.StudentEnrollDetails;
import teammates.common.util.Assumption;
import teammates.common.util.Utils;
import teammates.common.util.Const.ParamsNames;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

public class FeedbackSubmissionAdjustmentAction extends TaskQueueWorkerAction {

	public FeedbackSubmissionAdjustmentAction(
			HashMap<String, String> paramMap) {
		super(paramMap);
	}

	@SuppressWarnings("serial")
	@Override
	public boolean execute() {
		String courseId = paramMap.get(ParamsNames.COURSE_ID);
		Assumption.assertNotNull(courseId);
		
		String sessionName = paramMap.get(ParamsNames.FEEDBACK_SESSION_NAME);
		Assumption.assertNotNull(sessionName);
		
		String enrollmentDetails = paramMap.get(ParamsNames.ENROLLMENT_DETAILS);
		Assumption.assertNotNull(enrollmentDetails);
		
		Gson gsonParser = Utils.getTeammatesGson();
		Type enrollmentDetailsType = new TypeToken<ArrayList<StudentEnrollDetails>>(){}
									.getType();
		ArrayList<StudentEnrollDetails> enrollmentList = gsonParser
				.fromJson(enrollmentDetails, enrollmentDetailsType);
		
		log.info("Adjusting submissions for feedback session :" + sessionName +
				 "in course : " + courseId);
		
		FeedbackSessionAttributes feedbackSession = FeedbackSessionsLogic.inst()
				.getFeedbackSession(sessionName, courseId);
		StudentsLogic stLogic = StudentsLogic.inst();
		String errorString = "Error encountered while adjusting feedback session responses " +
				"of %s in course : %s : %s";
		
		if(feedbackSession != null) {
			List<FeedbackResponseAttributes> allResponses = FeedbackResponsesLogic.inst()
					.getFeedbackResponsesForSession(feedbackSession.feedbackSessionName,
							feedbackSession.courseId);
			
			for (FeedbackResponseAttributes response : allResponses) {
				try {
					stLogic.adjustFeedbackResponseForEnrollments(enrollmentList, response);
				} catch (Exception e) {
					log.severe(String.format(errorString, sessionName, courseId, e.getMessage()));
					return false;
				}
			}
			return true;
		}
		return false;
	}

}
