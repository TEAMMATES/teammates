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
                
                String actionsParam = "<a class=\"btn btn-default btn-xs\" id=\"t_course_unarchive" + idx + "\""
                                                + "href=\"" + getInstructorCourseArchiveLink(course.id, false, false) + "\""
                                      + ">Unarchive</a>";
                
                actionsParam += "<a class=\"btn btn-default btn-xs\" id=\"t_course_delete" + idx + "\""
                                        + "href=\"" + getInstructorCourseDeleteLink(course.id,false) + "\""
                                        + "onclick=\"return toggleDeleteCourseConfirmation('" + course.id + "');\""
                                        + "data-toggle=\"tooltip\" data-placement=\"top\" title=\"" + Const.Tooltips.COURSE_DELETE + "\""
                                        + (curInstructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE) ? ""
                                                                                                                                      : "disabled=\"disabled\"")
                                + ">Delete</a>";
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
                
                String actionsParam = "<a class=\"btn btn-default btn-xs t_course_enroll" + idx + "\""
                                                + "href=\"" + getInstructorCourseEnrollLink(courseBundle.course.id) + "\""
                                                + "data-toggle=\"tooltip\" data-placement=\"top\" title=\"" + Const.Tooltips.COURSE_ENROLL + "\""
                                                + (curInstructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT) ? ""
                                                                                                                                               : "disabled=\"disabled\"")
                                      + ">Enroll</a>";
                            
                actionsParam += "<a class=\"btn btn-default btn-xs t_course_view" + idx + "\""
                                    + "href=\"" + getInstructorCourseDetailsLink(courseBundle.course.id) + "\""
                                    + "data-toggle=\"tooltip\" data-placement=\"top\" title=\"" + Const.Tooltips.COURSE_DETAILS + "\""
                                + ">View</a>";
                        
                actionsParam += "<a class=\"btn btn-default btn-xs t_course_edit" + idx + "\""
                                    + "href=\"" + getInstructorCourseEditLink(courseBundle.course.id) + "\""
                                    + "data-toggle=\"tooltip\" data-placement=\"top\" title=\"" + Const.Tooltips.COURSE_EDIT + "\""
                                + ">Edit</a>";

                actionsParam += "<a class=\"btn btn-default btn-xs t_course_archive" + idx + "\""
                                    + "href=\"" + getInstructorCourseArchiveLink(courseBundle.course.id, true, false) + "\""
                                    + "data-toggle=\"tooltip\" data-placement=\"top\" title=\"" + Const.Tooltips.COURSE_ARCHIVE + "\""
                                + ">Archive</a>";

                actionsParam += "<a class=\"btn btn-default btn-xs t_course_delete" + idx + "\""
                                    + "href=\"" + getInstructorCourseDeleteLink(courseBundle.course.id, false) + "\""
                                    + "onclick=\"return toggleDeleteCourseConfirmation('" + courseBundle.course.id + "');\""
                                    + "data-toggle=\"tooltip\" data-placement=\"top\" title=\"" + Const.Tooltips.COURSE_DELETE + "\""
                                    + (curInstructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE) ? ""
                                                                                                                                  : "disabled=\"disabled\"")
                                + ">Delete</a>";

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
