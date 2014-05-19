package teammates.logic.automated;

import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.datatransfer.StudentEnrollDetails;
import teammates.common.util.Assumption;
import teammates.common.util.HttpRequestHelper;
import teammates.common.util.Utils;
import teammates.common.util.Const.ParamsNames;
import teammates.logic.core.EvaluationsLogic;
import teammates.logic.core.StudentsLogic;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class EvaluationSubmissionAdjustmentAction extends TaskQueueWorkerAction {
    private String courseId;
    private String evalName;
    private String enrollmentDetails;
    
    public EvaluationSubmissionAdjustmentAction(
            HttpServletRequest request) {
        super(request);
        
        this.courseId = HttpRequestHelper
                .getValueFromRequestParameterMap(request, ParamsNames.COURSE_ID); 
        Assumption.assertNotNull(courseId);
        
        this.evalName = HttpRequestHelper
                .getValueFromRequestParameterMap(request, ParamsNames.EVALUATION_NAME);
        Assumption.assertNotNull(evalName);
        
        this.enrollmentDetails = HttpRequestHelper
                .getValueFromRequestParameterMap(request,ParamsNames.ENROLLMENT_DETAILS);
        Assumption.assertNotNull(enrollmentDetails);
    }
    
    public EvaluationSubmissionAdjustmentAction(HashMap<String,String> paramMap) {    
        super(null);
        
        this.courseId = paramMap.get(ParamsNames.COURSE_ID); 
        Assumption.assertNotNull(courseId);
        
        this.evalName = paramMap.get(ParamsNames.EVALUATION_NAME);
        Assumption.assertNotNull(evalName);
        
        this.enrollmentDetails = paramMap.get(ParamsNames.ENROLLMENT_DETAILS);
        Assumption.assertNotNull(enrollmentDetails);
    }

    @Override
    public boolean execute() {
        
        Gson gsonParser = Utils.getTeammatesGson();
        ArrayList<StudentEnrollDetails> enrollmentList = gsonParser
                .fromJson(enrollmentDetails, new TypeToken<ArrayList<StudentEnrollDetails>>(){}
                .getType());
        
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
        } else {
            log.severe(String.format(errorString, evalName, courseId, "evaluation is null"));
            return false;
        }
    }

}
