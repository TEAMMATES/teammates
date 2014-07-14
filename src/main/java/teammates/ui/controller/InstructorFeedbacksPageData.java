package teammates.ui.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.util.Const;

public class InstructorFeedbacksPageData extends PageData {

    public InstructorFeedbacksPageData(AccountAttributes account) {
        super(account);
    }

    public String courseIdForNewSession;
    public FeedbackSessionAttributes newFeedbackSession;
    public List<CourseAttributes> courses;
    public List<EvaluationAttributes> existingEvalSessions;
    public List<FeedbackSessionAttributes> existingFeedbackSessions;        
    public HashMap<String, InstructorAttributes> instructors;
    
    public ArrayList<String> getTimeZoneOptionsAsHtml(){
        return getTimeZoneOptionsAsHtml(
                newFeedbackSession == null
                    ? Const.DOUBLE_UNINITIALIZED 
                    : newFeedbackSession.timeZone);
    }
    
    
    public ArrayList<String> getGracePeriodOptionsAsHtml(){
        return getGracePeriodOptionsAsHtml(
                newFeedbackSession == null 
                    ? Const.INT_UNINITIALIZED
                    : newFeedbackSession.gracePeriod);
    }
    
    public ArrayList<String> getCourseIdOptions() {
        ArrayList<String> result = new ArrayList<String>();

        for (CourseAttributes course : courses) {

            // True if this is a submission of the filled 'new session' form
            // for this course:
            boolean isFilledFormForSessionInThisCourse = (newFeedbackSession != null)
                    && course.id.equals(newFeedbackSession.courseId);

            // True if this is for displaying an empty form for creating a 
            // session for this course:
            boolean isEmptyFormForSessionInThisCourse = (courseIdForNewSession != null)
                    && course.id.equals(courseIdForNewSession);

            String selectedAttribute = isFilledFormForSessionInThisCourse
                    || isEmptyFormForSessionInThisCourse ? " selected=\"selected\""
                    : "";
            
            if (instructors.get(course.id).isAllowedForPrivilege(
                    Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION)) {
                result.add("<option value=\"" + course.id + "\""
                        + selectedAttribute + ">" + course.id + "</option>");
            }
        }
        return result;
    }
        

}
