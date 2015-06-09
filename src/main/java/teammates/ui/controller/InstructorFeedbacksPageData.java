package teammates.ui.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.util.Const;
import teammates.common.util.TimeHelper;
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
        newForm.timezoneSelectField = getTimeZoneOptionsAsHtml();
        newForm.fsStartDate = newFeedbackSession == null ?
                              TimeHelper.formatDate(TimeHelper.getNextHour()) :
                              TimeHelper.formatDate(newFeedbackSession.startTime);
        
        Date date;
        date = newFeedbackSession == null ?
               null : newFeedbackSession.startTime;
        newForm.fsStartTimeOptions = getTimeOptionsAsElementTags(date);
        
        newForm.fsEndDate = newFeedbackSession == null ?
                            "" : 
                            TimeHelper.formatDate(newFeedbackSession.endTime);
        date = newFeedbackSession == null ?
               null : newFeedbackSession.endTime;
        newForm.fsEndTimeOptions = getTimeOptionsAsElementTags(date);
        
        
        boolean hasSessionVisibleDate = newFeedbackSession != null &&
                                        !TimeHelper.isSpecialTime(newFeedbackSession.sessionVisibleFromTime);
        newForm.sessionVisibleDateButtonCheckedAttribute = hasSessionVisibleDate ? "checked=\"checked\"" : "";
        newForm.sessionVisibleDateValue = hasSessionVisibleDate ? 
                                   TimeHelper.formatDate(newFeedbackSession.sessionVisibleFromTime) :
                                   "";
        newForm.sessionVisibleDateDisabledAttribute = hasSessionVisibleDate ? "" : "disabled=\"disabled\"";
        
        date = null;
        if (hasSessionVisibleDate) {
            date = newFeedbackSession.sessionVisibleFromTime;   
        }
        newForm.sessionVisibleTimeOptions = getTimeOptionsAsElementTags(date);
        
        newForm.sessionVisibleAtOpenCheckedAttribute = (newFeedbackSession == null ||
                                            Const.TIME_REPRESENTS_FOLLOW_OPENING
                                            .equals(newFeedbackSession.sessionVisibleFromTime)) ? 
                                        "checked=\"checked\"" : "";
        
        newForm.sessionVisiblePrivateCheckedAttribute = (newFeedbackSession != null &&
                                            Const.TIME_REPRESENTS_NEVER
                                            .equals(newFeedbackSession.sessionVisibleFromTime)) ?
                                        "checked=\"checked\"" : "";
                        
        boolean hasResultVisibleDate = newFeedbackSession != null &&
                                        !TimeHelper.isSpecialTime(newFeedbackSession.resultsVisibleFromTime);
        newForm.responseVisibleDateCheckedAttribute = hasResultVisibleDate ? "checked=\"checked\"" : "";
        newForm.responseVisibleDateValue = hasResultVisibleDate ?
                                        TimeHelper.formatDate(newFeedbackSession.resultsVisibleFromTime) : "";
        newForm.responseVisibleDisabledAttribute = hasResultVisibleDate ? "" : "disabled=\"disabled\"";
        
        date = null;
        if (hasResultVisibleDate) {
            date = newFeedbackSession.resultsVisibleFromTime;   
        }
        newForm.responseVisibleTimeOptions = getTimeOptionsAsElementTags(date);
        
        newForm.responseVisibleImmediatelyCheckedAttribute = (newFeedbackSession != null &&
                                                                Const.TIME_REPRESENTS_FOLLOW_VISIBLE
                                                                .equals(newFeedbackSession.resultsVisibleFromTime)) ?
                                                                        "checked=\"checked\"" : 
                                                                        "";
        newForm.responseVisiblePublishManuallyCheckedAttribute = (newFeedbackSession == null ||
                                        Const.TIME_REPRESENTS_LATER.equals(newFeedbackSession.resultsVisibleFromTime) ||
                                        Const.TIME_REPRESENTS_NOW.equals(newFeedbackSession.resultsVisibleFromTime)) ?
                                                   "checked=\"checked\"" : "";
        newForm.responseVisibleNeverCheckedAttribute = (newFeedbackSession != null &&
                                        Const.TIME_REPRESENTS_NEVER
                                                .equals(newFeedbackSession.resultsVisibleFromTime)) ?
                                     "checked=\"checked\"" : "";
                                
        newForm.submitButtonDisabledAttribute = courses.isEmpty() ? " disabled=\"disabled\"" : "";
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
    
    public ArrayList<ElementTag> getGracePeriodOptionsAsElementTags() {
        return getGracePeriodOptionsAsElementTags(newForm.defaultFeedbackSession == null ? 
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
