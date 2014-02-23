package teammates.logic.core;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.datatransfer.StudentEnrollDetails;
import teammates.common.util.Assumption;
import teammates.common.util.Utils;
import teammates.common.util.Const.ParamsNames;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

public class EvaluationSubmissionAdjustmentAction extends TaskQueueWorkerAction {
	
	public EvaluationSubmissionAdjustmentAction(
			HashMap<String, String> paramMap) {
		super(paramMap);
	}

	@SuppressWarnings("serial")
	@Override
	public boolean execute() {
		String courseId = paramMap.get(ParamsNames.COURSE_ID); 
		Assumption.assertNotNull(courseId);
		
		String evalName = paramMap.get(ParamsNames.EVALUATION_NAME);
		Assumption.assertNotNull(evalName);
		
		String enrollmentDetails = paramMap.get(ParamsNames.ENROLLMENT_DETAILS);
		Assumption.assertNotNull(enrollmentDetails);
		
		Gson gsonParser = Utils.getTeammatesGson();
		Type enrollmentDetailsType = new TypeToken<ArrayList<StudentEnrollDetails>>(){}
									.getType();
		ArrayList<StudentEnrollDetails> enrollmentList = gsonParser
				.fromJson(enrollmentDetails, enrollmentDetailsType);
		
		log.info("Adjusting submissions for evaluation :" + evalName +
				 "in course : " + courseId);
		
		EvaluationAttributes evaluation = EvaluationsLogic.inst()
				.getEvaluation(courseId, evalName);
		StudentsLogic stLogic = StudentsLogic.inst();
		String errorString = "Error encountered while adjusting evaluation submission " +
				"of %s in course : %s : %s";
		
		if(evaluation != null) {
			try {
				stLogic.adjustSubmissionsForEnrollments(enrollmentList,evaluation);
				return true;
			} catch (Exception e) {
				log.severe(String.format(errorString, evalName, courseId, e.getMessage()));
				return false;
			}
		}
		return false;
	}

}
