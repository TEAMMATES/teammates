package teammates.ui.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.util.Const;
import teammates.ui.template.ElementTag;
import teammates.ui.template.FeedbackSessionsList;
import teammates.ui.template.FeedbackSessionsNewForm;

public class InstructorFeedbacksPageData extends PageData {

    public InstructorFeedbacksPageData(AccountAttributes account) {
        super(account);
    }

    
    // Flag for deciding if loading the sessions table, or the new sessions form.
    // if true -> loads the sessions table, else load the form
    public boolean isUsingAjax;
    
    public FeedbackSessionsList fsList;
    public FeedbackSessionsNewForm newForm;
    
    
    public void init(List<CourseAttributes> courses, String  courseIdForNewSession,
                                    HashMap<String, InstructorAttributes> instructors,
                                    FeedbackSessionAttributes newFeedbackSession) {
        
        if (courses.isEmpty()) {
            newForm.formClasses = "form-group has-error";
            newForm.courseFieldClasses = "form-group has-error";
        }
        
        newForm.coursesSelectField = getCourseIdOptions(courses, 
                                        courseIdForNewSession, instructors, newFeedbackSession);
        
    }

    public ArrayList<ElementTag> getTimeZoneOptionsAsHtml(){
        return getTimeZoneOptionsAsHtml(newForm.defaultFeedbackSession == null ? 
                                        Const.DOUBLE_UNINITIALIZED : 
                                        newForm.defaultFeedbackSession.timeZone);
    }


    public ArrayList<String> getGracePeriodOptionsAsHtml(){
        return getGracePeriodOptionsAsHtml(newForm.defaultFeedbackSession == null ? 
                                           Const.INT_UNINITIALIZED : 
                                           newForm.defaultFeedbackSession.gracePeriod);
    }

    public ArrayList<ElementTag> getCourseIdOptions(List<CourseAttributes> courses, String  courseIdForNewSession,
                                                HashMap<String, InstructorAttributes> instructors,
                                                FeedbackSessionAttributes newFeedbackSession) {
        ArrayList<ElementTag> result = new ArrayList<ElementTag>();

        for (CourseAttributes course : courses) {
            
            // True if this is a submission of the filled 'new session' form
            // for this course:
            boolean isFilledFormForSessionInThisCourse =
                    newFeedbackSession != null && course.id.equals(newFeedbackSession.courseId);

            // True if this is for displaying an empty form for creating a
            // session for this course:
            boolean isEmptyFormForSessionInThisCourse =
                    courseIdForNewSession != null && course.id.equals(courseIdForNewSession);
            
            if (instructors.get(course).isAllowedForPrivilege(
                    Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION)) {
                ElementTag option = createOption(course.id, course.id,  
                                                (isFilledFormForSessionInThisCourse || isEmptyFormForSessionInThisCourse));
                result.add(option);
            }
        }
        
        // Add option if there are no active courses
        if (result.isEmpty()) {
            ElementTag blankOption = createOption("No active courses!", "", true);
            result.add(blankOption);
        }
        
        return result;
    }
    
    private ElementTag createOption(String text, String value, boolean isSelected) {
        if (isSelected) {
            return new ElementTag(text, "value", value, "selected", "selected");
        } else {
            return new ElementTag(text, "value", value);
        }
    }


}
