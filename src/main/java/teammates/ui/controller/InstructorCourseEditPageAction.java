package teammates.ui.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.Utils;
import teammates.logic.api.GateKeeper;

/**
 * Action: showing the 'Edit' page for a course of an instructor
 */
public class InstructorCourseEditPageAction extends Action {
    
    //TODO: display privileges in the database properly
    @Override
    public ActionResult execute() throws EntityDoesNotExistException { 
                
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        Assumption.assertNotNull(courseId);
        String instructorEmail = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_EMAIL);
        String index = getRequestParamValue(Const.ParamsNames.COURSE_EDIT_MAIN_INDEX);

        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, account.googleId);
        CourseAttributes courseToEdit = logic.getCourse(courseId);
         
        new GateKeeper().verifyAccessible(instructor, courseToEdit);
        
        /* Setup page data for 'Edit' page of a course for an instructor */
        InstructorCourseEditPageData data = new InstructorCourseEditPageData(account);
        data.course = courseToEdit;
        if(instructorEmail == null) {
            data.instructorList = logic.getInstructorsForCourse(courseId);
            data.isAccessControlDisplayed = false;   
        } else {
            data.instructorList = new ArrayList<InstructorAttributes>();
            data.instructorList.add(logic.getInstructorForEmail(courseId, instructorEmail));
            data.index = Integer.parseInt(index);        
            data.isAccessControlDisplayed = true;
        }
        
        data.currentInstructor = instructor;
        data.sectionNames = logic.getSectionNamesForCourse(courseId);
        data.evalNames = new ArrayList<String>();
        data.feedbackNames = new ArrayList<String>();
        List<EvaluationAttributes> evaluations = logic.getEvaluationsForCourse(courseId);
        for (EvaluationAttributes eval : evaluations) {
            data.evalNames.add(eval.name);
        }
        List<FeedbackSessionAttributes> feedbacks = logic.getFeedbackSessionsForCourse(courseId);
        for (FeedbackSessionAttributes feedback : feedbacks) {
            data.feedbackNames.add(feedback.feedbackSessionName);
        }
        
        statusToAdmin = "instructorCourseEdit Page Load<br>"
                + "Editing information for Course <span class=\"bold\">["
                + courseId + "]</span>";
        
        ShowPageResult response = createShowPageResult(Const.ViewURIs.INSTRUCTOR_COURSE_EDIT, data);
        return response;
    }
}
