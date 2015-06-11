package teammates.ui.controller;

import java.util.ArrayList;
import java.util.List;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.CourseDetailsBundle;
import teammates.common.util.Const;
import teammates.common.util.Sanitizer;
import teammates.logic.api.Logic;
import teammates.ui.template.ActiveCoursesTable;
import teammates.ui.template.ActiveCoursesTableRow;
import teammates.ui.template.ArchivedCoursesTable;
import teammates.ui.template.ArchivedCoursesTableRow;
import teammates.ui.template.ElementTag;

/**
 * This is the PageData object for the 'Courses' page 
 */
public class InstructorCoursesPageData extends PageData {
    private ArchivedCoursesTable archivedCourses;
    private ActiveCoursesTable activeCourses;
    private String courseIdToShow;
    private String courseNameToShow;
    
    public InstructorCoursesPageData(AccountAttributes account) {
        super(account);
    }
    
    public void init(ArrayList<CourseDetailsBundle> allCourses, String courseIdToShowParam, String courseNameToShowParam){
        activeCourses = extractActiveCourses(allCourses);
        archivedCourses = extractArchivedCourses(allCourses);
        courseIdToShow = courseIdToShowParam;
        courseNameToShow = courseNameToShowParam;
    }
    
    public String getCourseIdToShow() {
        return courseIdToShow;
    }
    
    public String getCourseNameToShow() {
        return courseNameToShow;
    }

    public ActiveCoursesTable getActiveCourses() {
        return activeCourses;
    }

    public ArchivedCoursesTable getArchivedCourses() {
        return archivedCourses;
    }
    
    private ArchivedCoursesTable extractArchivedCourses(List<CourseDetailsBundle> courseBundles) {
        ArchivedCoursesTable archivedCourses = new ArchivedCoursesTable();
        Logic logic = new Logic();

        CourseDetailsBundle.sortDetailedCoursesByCourseId(courseBundles);
        
        int idx = this.activeCourses.getRows().size() - 1;
        
        for (CourseDetailsBundle courseBundle : courseBundles) {
            CourseAttributes course = courseBundle.course;

            InstructorAttributes curInstructor = logic.getInstructorForGoogleId(course.id, account.googleId);

            if (Logic.isCourseArchived(course.id, curInstructor.googleId)) {
                idx++;
                
                List<ElementTag> actionsParam = new ArrayList<ElementTag>();
                
                ElementTag unarchivedButton = new ElementTag();               
                unarchivedButton.setContent("Unarchive");
                unarchivedButton.setAttribute("class", "btn btn-default btn-xs");
                unarchivedButton.setAttribute("id", "t_course_unarchive" + idx);
                unarchivedButton.setAttribute("href", getInstructorCourseArchiveLink(course.id, false, false));
                
                ElementTag deleteButton = new ElementTag();               
                deleteButton.setContent("Delete");
                deleteButton.setAttribute("class", "btn btn-default btn-xs");
                deleteButton.setAttribute("id", "t_course_delete" + idx);
                deleteButton.setAttribute("onclick", "return toggleDeleteCourseConfirmation('" + course.id + "');");
                deleteButton.setAttribute("href", getInstructorCourseDeleteLink(course.id, false));
                deleteButton.setAttribute("data-toggle", "tooltip");
                deleteButton.setAttribute("data-placement", "top");
                deleteButton.setAttribute("title", Const.Tooltips.COURSE_DELETE);
                if (!curInstructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE)) {
                    deleteButton.setAttribute("disabled", "disabled");
                }
                
                actionsParam.add(unarchivedButton);
                actionsParam.add(deleteButton);
                
                ArchivedCoursesTableRow row = new ArchivedCoursesTableRow(Sanitizer.sanitizeForHtml(course.id), 
                                                                          Sanitizer.sanitizeForHtml(course.name), actionsParam);
                archivedCourses.getRows().add(row);
            }
        }
        
        return archivedCourses;
    }
    
    private ActiveCoursesTable extractActiveCourses(List<CourseDetailsBundle> courseBundles) {
        ActiveCoursesTable activeCourses = new ActiveCoursesTable();
        Logic logic = new Logic();
        
        CourseDetailsBundle.sortDetailedCoursesByCourseId(courseBundles);
        
        int idx = -1;
        for (CourseDetailsBundle courseBundle : courseBundles) {
            CourseAttributes course = courseBundle.course;

            InstructorAttributes curInstructor = logic.getInstructorForGoogleId(course.id, account.googleId);

            if (!Logic.isCourseArchived(course.id, curInstructor.googleId)) {
                idx++;
                
                List<ElementTag> actionsParam = new ArrayList<ElementTag>();
                
                ElementTag enrollButton = new ElementTag();
                enrollButton.setContent("Enroll");
                enrollButton.setAttribute("class", "btn btn-default btn-xs t_course_enroll" + idx);
                enrollButton.setAttribute("href", getInstructorCourseEnrollLink(courseBundle.course.id));
                enrollButton.setAttribute("data-toggle", "tooltip");
                enrollButton.setAttribute("data-placement", "top");
                enrollButton.setAttribute("title", Const.Tooltips.COURSE_ENROLL);
                if (!curInstructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT)) {
                    enrollButton.setAttribute("disabled", "disabled");
                }
                
                ElementTag viewButton = new ElementTag();
                viewButton.setContent("View");
                viewButton.setAttribute("class", "btn btn-default btn-xs t_course_view" + idx);
                viewButton.setAttribute("href", getInstructorCourseDetailsLink(courseBundle.course.id));
                viewButton.setAttribute("data-toggle", "tooltip");
                viewButton.setAttribute("data-placement", "top");
                viewButton.setAttribute("title", Const.Tooltips.COURSE_DETAILS);
                
                ElementTag editButton = new ElementTag();
                editButton.setContent("Edit");
                editButton.setAttribute("class", "btn btn-default btn-xs t_course_edit" + idx);
                editButton.setAttribute("href", getInstructorCourseEditLink(courseBundle.course.id));
                editButton.setAttribute("data-toggle", "tooltip");
                editButton.setAttribute("data-placement", "top");
                editButton.setAttribute("title", Const.Tooltips.COURSE_EDIT);
                
                ElementTag archiveButton = new ElementTag();
                archiveButton.setContent("Archive");
                archiveButton.setAttribute("class", "btn btn-default btn-xs t_course_archive" + idx);
                archiveButton.setAttribute("href", getInstructorCourseArchiveLink(courseBundle.course.id, true, false));
                archiveButton.setAttribute("data-toggle", "tooltip");
                archiveButton.setAttribute("data-placement", "top");
                archiveButton.setAttribute("title", Const.Tooltips.COURSE_ARCHIVE);
                
                ElementTag deleteButton = new ElementTag();
                deleteButton.setContent("Delete");
                deleteButton.setAttribute("class", "btn btn-default btn-xs t_course_delete" + idx);
                deleteButton.setAttribute("onclick", "return toggleDeleteCourseConfirmation('" + courseBundle.course.id + "');");
                deleteButton.setAttribute("data-toggle", "tooltip");
                deleteButton.setAttribute("data-placement", "top");
                deleteButton.setAttribute("href", getInstructorCourseDeleteLink(courseBundle.course.id, false));
                deleteButton.setAttribute("title", Const.Tooltips.COURSE_DELETE);
                if (!(curInstructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE))) {
                    deleteButton.setAttribute("disabled", "disabled");
                }
                
                actionsParam.add(enrollButton);
                actionsParam.add(viewButton);
                actionsParam.add(editButton);
                actionsParam.add(archiveButton);
                actionsParam.add(deleteButton);
                
                ActiveCoursesTableRow row = new ActiveCoursesTableRow(Sanitizer.sanitizeForHtml(course.id), 
                                                                      Sanitizer.sanitizeForHtml(course.name), 
                                                                      courseBundle.stats.sectionsTotal,
                                                                      courseBundle.stats.teamsTotal, 
                                                                      courseBundle.stats.studentsTotal, 
                                                                      courseBundle.stats.unregisteredTotal, 
                                                                      actionsParam);
                activeCourses.getRows().add(row);
            }
        }
        
        return activeCourses;
    }
         
}
