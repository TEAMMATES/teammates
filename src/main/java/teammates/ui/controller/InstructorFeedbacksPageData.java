package teammates.ui.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.common.util.TimeHelper;
import teammates.ui.template.ElementTag;
import teammates.ui.template.FeedbackSessionRow;
import teammates.ui.template.FeedbackSessionsList;
import teammates.ui.template.FeedbackSessionsNewForm;

public class InstructorFeedbacksPageData extends PageData {
    
    public static final int MAX_CLOSED_SESSION_STATS = 5;
    
    public InstructorFeedbacksPageData(AccountAttributes account) {
        super(account);
        
    }

    
    // Flag for deciding if loading the sessions table, or the new sessions form.
    // if true -> loads the sessions table, else load the form
    public boolean isUsingAjax;
    
    public boolean isUsingAjax() {
        return isUsingAjax;
    }


    public FeedbackSessionsList fsList;


    public FeedbackSessionsNewForm newForm;
    
 

    /**
     * Initializes the PageData
     * @param courses                   courses that the user is an instructor of 
     * @param courseIdForNewSession     the course id to automatically select in the dropdown
     * @param existingFeedbackSessions  list of existing feedback sessions 
     * @param instructors               a map of courseId to the instructorAttributes for the current user
     * @param newFeedbackSession        the feedback session which values are used as the default values in the form
     * @param feedbackSessionType       "TEAMEVALUATION" or "STANDARD"
     * @param feedbackSessionNameForSessionList  the feedback session to highlight in the sessions table
     */
    public void init(List<CourseAttributes> courses, String courseIdForNewSession, 
                     List<FeedbackSessionAttributes> existingFeedbackSessions,
                     HashMap<String, InstructorAttributes> instructors,
                     FeedbackSessionAttributes newFeedbackSession, String feedbackSessionType, 
                     String feedbackSessionNameForSessionList) {

        
        buildNewForm(courses, courseIdForNewSession, 
                     instructors, newFeedbackSession, 
                     feedbackSessionType, feedbackSessionNameForSessionList);
        
        
        buildFsList(courseIdForNewSession, existingFeedbackSessions, instructors,
                                        feedbackSessionNameForSessionList);
        
    }

    private void buildFsList(String courseIdForNewSession,
                                    List<FeedbackSessionAttributes> existingFeedbackSessions,
                                    HashMap<String, InstructorAttributes> instructors,
                                    String feedbackSessionNameForSessionList) {
        FeedbackSessionAttributes.sortFeedbackSessionsByCreationTimeDescending(existingFeedbackSessions);
        List<FeedbackSessionRow> existingFeedbackSessionsRow = convertFeedbackSessionAttributesToSessionRows(existingFeedbackSessions,
                                        instructors, feedbackSessionNameForSessionList, courseIdForNewSession);
        fsList = new FeedbackSessionsList(existingFeedbackSessionsRow);
    }

    private void buildNewForm(List<CourseAttributes> courses, String courseIdForNewSession,
                                    HashMap<String, InstructorAttributes> instructors,
                                    FeedbackSessionAttributes newFeedbackSession, String feedbackSessionType,
                                    String feedbackSessionNameForSessionList) {
        
        List<String> courseIds = new ArrayList<String>();
        for (CourseAttributes course : courses) {
            courseIds.add(course.id);
        }
        
        newForm = new FeedbackSessionsNewForm(courseIdForNewSession, feedbackSessionType, 
                                              newFeedbackSession, courseIds);
        
        if (courses.isEmpty()) {
            newForm.formClasses = "form-group has-error";
            newForm.courseFieldClasses = "form-control text-color-red";
        }
        
        newForm.feedbackSessionNameForSessionList = feedbackSessionNameForSessionList;
        
        newForm.coursesSelectField = getCourseIdOptions(courses,  courseIdForNewSession, 
                                                        instructors, newFeedbackSession);
        
        newForm.timezoneSelectField = getTimeZoneOptionsAsHtml(newFeedbackSession);
        
        
        newForm.instructions = newFeedbackSession == null ?
                               "Please answer all the given questions." :
                               InstructorFeedbacksPageData.sanitizeForHtml(newFeedbackSession.instructions.getValue());
        
        newForm.fsStartDate = newFeedbackSession == null ?
                              TimeHelper.formatDate(TimeHelper.getNextHour()) :
                              TimeHelper.formatDate(newFeedbackSession.startTime);
        
        Date date;
        date = newFeedbackSession == null ? null : newFeedbackSession.startTime;
        newForm.fsStartTimeOptions = getTimeOptionsAsElementTags(date);
        
        newForm.fsEndDate = newFeedbackSession == null ?
                            "" : 
                            TimeHelper.formatDate(newFeedbackSession.endTime);
        date = newFeedbackSession == null ?
               null : newFeedbackSession.endTime;
        newForm.fsEndTimeOptions = getTimeOptionsAsElementTags(date);
        
        newForm.gracePeriodOptions = getGracePeriodOptionsAsElementTags(newFeedbackSession);
        
        boolean hasSessionVisibleDate = newFeedbackSession != null &&
                                        !TimeHelper.isSpecialTime(newFeedbackSession.sessionVisibleFromTime);
        newForm.sessionVisibleDateButtonCheckedAttribute = hasSessionVisibleDate ? "checked=\"checked\"" : "";
        newForm.sessionVisibleDateValue = hasSessionVisibleDate ? 
                                   TimeHelper.formatDate(newFeedbackSession.sessionVisibleFromTime) :
                                   "";
        newForm.sessionVisibleDateDisabledAttribute = hasSessionVisibleDate ? "" : "disabled=\"disabled\"";
        
        
        date = hasSessionVisibleDate ? newFeedbackSession.sessionVisibleFromTime : null;   
        
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
        
        date = hasResultVisibleDate ? newFeedbackSession.resultsVisibleFromTime :  null;
        newForm.responseVisibleTimeOptions = getTimeOptionsAsElementTags(date);
        
        newForm.responseVisibleImmediatelyCheckedAttribute 
            = (newFeedbackSession != null 
               && Const.TIME_REPRESENTS_FOLLOW_VISIBLE.equals(newFeedbackSession.resultsVisibleFromTime)) ?
                                                                    "checked=\"checked\"" : 
                                                                    "";
        newForm.responseVisiblePublishManuallyCheckedAttribute 
            = (newFeedbackSession == null 
               || Const.TIME_REPRESENTS_LATER.equals(newFeedbackSession.resultsVisibleFromTime) 
               || Const.TIME_REPRESENTS_NOW.equals(newFeedbackSession.resultsVisibleFromTime)) ?
                                                                     "checked=\"checked\"" :
                                                                      "";
        
        newForm.responseVisibleNeverCheckedAttribute = (newFeedbackSession != null 
                                                        && Const.TIME_REPRESENTS_NEVER
                                                           .equals(newFeedbackSession.resultsVisibleFromTime)) ?
                                                                        "checked=\"checked\"" : "";
                                
        newForm.submitButtonDisabledAttribute = courses.isEmpty() ? " disabled=\"disabled\"" : "";
   
    }
    
    
    List<FeedbackSessionRow> convertFeedbackSessionAttributesToSessionRows(List<FeedbackSessionAttributes> sessions, 
                                    HashMap<String, InstructorAttributes> instructors, String feedbackSessionNameForSessionList, String courseIdForNewSession) {

        
        List<FeedbackSessionRow> rows = new ArrayList<FeedbackSessionRow>();
        int displayedStatsCount = 0;
        
        for (FeedbackSessionAttributes session : sessions) {
            String courseId = session.courseId;
            String name = PageData.sanitizeForHtml(session.feedbackSessionName);
            String tooltip = PageData.getInstructorHoverMessageForFeedbackSession(session);
            String status = PageData.getInstructorStatusForFeedbackSession(session);
            String href = getFeedbackSessionStatsLink(session.courseId, session.feedbackSessionName);
            
            String recent = "";
            if (session.isOpened() || session.isWaitingToOpen()) {
                recent = " recent";
            } else if (displayedStatsCount < InstructorFeedbacksPageData.MAX_CLOSED_SESSION_STATS
                       && !TimeHelper.isOlderThanAYear(session.createdTime)) {
                recent = " recent";
                ++displayedStatsCount;
            }
            
            String actions = "";
            try {
                actions = getInstructorFeedbackSessionActions(session, false, instructors.get(courseId),
                                                getCourseIdSectionNamesMap(sessions).get(courseId));
            } catch (EntityDoesNotExistException e) {
                // nothing
            }
            
            ElementTag elementAttributes ;
            if (session.courseId.equals(courseIdForNewSession) && session.feedbackSessionName.equals(feedbackSessionNameForSessionList)) {
                elementAttributes = new ElementTag("class", "sessionsRow warning");
            } else {
                elementAttributes = new ElementTag("class", "sessionsRow");
            }
            
            rows.add(new FeedbackSessionRow(courseId, name, tooltip, status, href, recent, actions, elementAttributes));
        }
        
        return rows;
    }
    
    public FeedbackSessionsList getFsList() {
        return fsList;
    }
    
    public FeedbackSessionsNewForm getNewForm() {
        return newForm;
    }

    public ArrayList<ElementTag> getTimeZoneOptionsAsHtml(FeedbackSessionAttributes fs){
        return getTimeZoneOptionsAsElementTags(fs == null ? 
                                               Const.DOUBLE_UNINITIALIZED : 
                                               fs.timeZone);
    }


    public ArrayList<String> getGracePeriodOptionsAsHtml(FeedbackSessionAttributes fs){
        return getGracePeriodOptionsAsHtml(fs == null ? 
                                           Const.INT_UNINITIALIZED : 
                                           fs.gracePeriod);
    }
    
    public ArrayList<ElementTag> getGracePeriodOptionsAsElementTags(FeedbackSessionAttributes fs) {
        return getGracePeriodOptionsAsElementTags(fs == null ? 
                                                  Const.INT_UNINITIALIZED : 
                                                  fs.gracePeriod);
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
            

            if (instructors.get(course.id).isAllowedForPrivilege(
                    Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION)) {
                ElementTag option = createOption(course.id, course.id,  
                                                (isFilledFormForSessionInThisCourse || isEmptyFormForSessionInThisCourse));
                result.add(option);
            }
        }
        
        // Add "No active courses" option if there are no active courses
        if (result.isEmpty()) {
            ElementTag blankOption = createOption("No active courses!", "", true);
            result.add(blankOption);
        }
        
        return result;
    }
    
    private ElementTag createOption(String text, String value, boolean isSelected) {
        return isSelected ? 
               new ElementTag(text, "value", value, "selected", "selected") : 
               new ElementTag(text, "value", value);
    }


}
