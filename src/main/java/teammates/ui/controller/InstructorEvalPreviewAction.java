package teammates.ui.controller;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.SubmissionAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;

public class InstructorEvalPreviewAction extends Action {
    private StudentEvalSubmissionEditPageData data;
    
    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        Assumption.assertNotNull(courseId);
        String evalName = getRequestParamValue(Const.ParamsNames.EVALUATION_NAME);
        Assumption.assertNotNull(evalName);
        
        new GateKeeper().verifyAccessible(
                logic.getInstructorForGoogleId(courseId, account.googleId),
                logic.getEvaluation(courseId, evalName));
        
        List<StudentAttributes> studentList = logic.getStudentsForCourse(courseId);
        Collections.sort(studentList, new StudentComparator());
        
        StudentAttributes previewStudent;
        try {
            String paramStudentEmail = getRequestParamValue(Const.ParamsNames.PREVIEWAS);
            previewStudent = getPreviewStudent(paramStudentEmail, courseId, studentList);
        } catch (EntityDoesNotExistException e) {
            statusToUser.add("The course " + courseId 
                    + " has yet to have any students so sessions cannot be previewed.");
            isError = true;
            return createRedirectResult(Const.ActionURIs.INSTRUCTOR_EVALS_PAGE);
        }
        
        data = new StudentEvalSubmissionEditPageData(account);
        data.student = previewStudent;
        data.eval = logic.getEvaluation(courseId, evalName);
        Assumption.assertNotNull(data.eval);
        try{
            data.submissions = logic.getSubmissionsForEvaluationFromStudent(courseId, evalName, previewStudent.email);
            SubmissionAttributes.sortByReviewee(data.submissions);
            SubmissionAttributes.putSelfSubmissionFirst(data.submissions);
        } catch (InvalidParametersException e) {
            Assumption.fail("Invalid parameters not expected at this stage");
        }
        data.isPreview = true;
        data.studentList = studentList;
        
        statusToAdmin = "Preview evaluation as student (" + previewStudent.email + ")<br>" +
                "Session Name: " + evalName + "<br>" +
                "Course ID: " + courseId;
        
        ShowPageResult response = createShowPageResult(Const.ViewURIs.STUDENT_EVAL_SUBMISSION_EDIT, data);
        return response;
    }

    private StudentAttributes getPreviewStudent(String paramStudentEmail,
            String courseId, List<StudentAttributes> studentList)
            throws EntityDoesNotExistException {
        StudentAttributes previewStudent;
        if (paramStudentEmail != null) {
            previewStudent = logic.getStudentForEmail(courseId, paramStudentEmail);
        } else {
            if (!studentList.isEmpty()) {
                previewStudent = studentList.get(0);
            } else {
                throw new EntityDoesNotExistException(
                        "Trying to preview an evaluation in a course with no student.");
            }
        }
        return previewStudent;
    }
    
    private class StudentComparator implements Comparator<StudentAttributes> {
        @Override
        public int compare(StudentAttributes s1, StudentAttributes s2) {
            if (s1.team.equals(s2.team)) {
                return s1.name.compareToIgnoreCase(s2.name);
            }
            return s1.team.compareToIgnoreCase(s2.team);
        }    
    }
}
