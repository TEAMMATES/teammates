package teammates.ui.controller;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.exception.EnrollException;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;

/**
 * Action: saving the list of enrolled students for a course of an instructor
 */
public class InstructorCourseEnrollSaveAction extends Action {
    
    @Override
    public ActionResult execute() throws EntityDoesNotExistException {
        
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        Assumption.assertNotNull(courseId);
        String studentsInfo = getRequestParamValue(Const.ParamsNames.STUDENTS_ENROLLMENT_INFO);
        Assumption.assertPostParamNotNull(Const.ParamsNames.STUDENTS_ENROLLMENT_INFO, studentsInfo);
        
        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, account.googleId);
        new GateKeeper().verifyAccessible(
                instructor, logic.getCourse(courseId), Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT);
        
        /* Process enrollment list and setup data for page result */
        try {
            InstructorCourseEnrollResultPageData pageData = new InstructorCourseEnrollResultPageData(account);
            pageData.courseId = courseId;
            pageData.students = enrollAndProcessResultForDisplay(studentsInfo, courseId);
            pageData.hasSection = hasSections(pageData.students);
            pageData.enrollStudents = studentsInfo;
            statusToAdmin = "Students Enrolled in Course <span class=\"bold\">[" 
                    + courseId + "]:</span><br>" + (studentsInfo).replace("\n", "<br>");

            return createShowPageResult(Const.ViewURIs.INSTRUCTOR_COURSE_ENROLL_RESULT, pageData);
            
        } catch (EnrollException | InvalidParametersException e) {
            setStatusForException(e);
            statusToAdmin += "<br>Enrollment string entered by user:<br>" + (studentsInfo).replace("\n", "<br>");
            
            InstructorCourseEnrollPageData pageData = new InstructorCourseEnrollPageData(account);
            pageData.courseId = courseId;
            pageData.enrollStudents = studentsInfo;
            
            return createShowPageResult(Const.ViewURIs.INSTRUCTOR_COURSE_ENROLL, pageData);
        } catch (EntityAlreadyExistsException e) {
            setStatusForException(e);
            statusToUser.add("The enrollment failed, possibly because some students were re-enrolled before the previous "
                    + "enrollment action was still being processed by TEAMMATES database servers. "
                    + "Please try again after about 10 minutes. If the problem persists, please contact TEAMMATES support");
            
            InstructorCourseEnrollPageData pageData = new InstructorCourseEnrollPageData(account);
            pageData.courseId = courseId;
            pageData.enrollStudents = studentsInfo;
            
            log.severe("Entity already exists exception occurred when updating student: " + e.getMessage());
            return createShowPageResult(Const.ViewURIs.INSTRUCTOR_COURSE_ENROLL, pageData);
        }
    }

    private boolean hasSections(List<StudentAttributes>[] students){
        for(List<StudentAttributes> studentList: students){
            for(StudentAttributes student : studentList){
                if(!student.section.equals(Const.DEFAULT_SECTION)){
                    return true;
                }
            }
        }
        return false;
    }

    private List<StudentAttributes>[] enrollAndProcessResultForDisplay(String studentsInfo, String courseId)
            throws EnrollException, EntityDoesNotExistException, InvalidParametersException, EntityAlreadyExistsException {
        List<StudentAttributes> students = logic.enrollStudents(studentsInfo, courseId);
        Collections.sort(students, new Comparator<StudentAttributes>() {
            @Override
            public int compare(StudentAttributes o1, StudentAttributes o2) {
                return (o1.updateStatus.numericRepresentation - o2.updateStatus.numericRepresentation);
            }
        });

        return separateStudents(students);
    }

    /**
     * Separate the StudentData objects in the list into different categories based
     * on their updateStatus. Each category is put into a separate list.<br>
     * 
     * Precondition:<br>
     * * The list of StudentData objects passed in as argument has to be sorted in
     * ascending order of their updateStatus first
     * 
     * @return An array of lists of StudentData objects in which each list contains
     * student with the same updateStatus
     */
    @SuppressWarnings("unchecked")
    private List<StudentAttributes>[] separateStudents(List<StudentAttributes> students) {
    
        List<StudentAttributes>[] lists = new List[StudentAttributes.UpdateStatus.STATUS_COUNT];
        int prevIdx = 0;
        int nextIdx = 0;
        int id = 0;
        for (StudentAttributes student : students) {
            while (student.updateStatus.numericRepresentation > id) {
                lists[id++] = students.subList(prevIdx, nextIdx);
                StudentAttributes.sortByNameAndThenByEmail(lists[id - 1]);
                prevIdx = nextIdx;
            }
            nextIdx++;
        }
        while (id < StudentAttributes.UpdateStatus.STATUS_COUNT) {
            lists[id++] = students.subList(prevIdx, nextIdx);
            StudentAttributes.sortByNameAndThenByEmail(lists[id - 1]);
            prevIdx = nextIdx;
        }
        return lists;
    }

}
