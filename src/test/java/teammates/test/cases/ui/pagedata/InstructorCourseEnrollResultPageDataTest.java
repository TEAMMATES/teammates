package teammates.test.cases.ui.pagedata;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;


import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.StudentAttributes.UpdateStatus;
import teammates.test.cases.BaseTestCase;
import teammates.ui.controller.InstructorCourseEnrollResultPageData;


public class InstructorCourseEnrollResultPageDataTest extends BaseTestCase {
private static DataBundle dataBundle = getTypicalDataBundle();
    
    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testAll() {
        ______TS("test typical case");
        AccountAttributes account = dataBundle.accounts.get("instructor1OfCourse1");
        String courseId = "idOfTypicalCourse1";
        
        List<StudentAttributes>[] students = new ArrayList[UpdateStatus.STATUS_COUNT];
        for (int i = 0; i < UpdateStatus.STATUS_COUNT; i++) {
            students[i] = new ArrayList<StudentAttributes>();
        }
        students[UpdateStatus.NEW.numericRepresentation].add(dataBundle.students.get("student1InCourse1"));
        students[UpdateStatus.NEW.numericRepresentation].add(dataBundle.students.get("student2InCourse1"));
        students[UpdateStatus.MODIFIED.numericRepresentation].add(dataBundle.students.get("student3InCourse1"));
        students[UpdateStatus.UNMODIFIED.numericRepresentation].add(dataBundle.students.get("student4InCourse1"));
        students[UpdateStatus.ERROR.numericRepresentation].add(dataBundle.students.get("student5InCourse1"));
        
        boolean hasSection = true;
        String enrollStudents = "enrollString";
        
        InstructorCourseEnrollResultPageData pageData = new InstructorCourseEnrollResultPageData(account, 
                                                                courseId, students, hasSection, enrollStudents);
        
        assertNotNull(pageData.getCourseId());
        assertEquals(courseId, pageData.getCourseId());
        
        assertNotNull(pageData.account);
        assertEquals(account.googleId, pageData.account.googleId);
        
        assertNotNull(pageData.getEnrollResultPanelList());
        assertEquals(students.length, pageData.getEnrollResultPanelList().size());
        assertEquals(students[UpdateStatus.NEW.numericRepresentation].size(),
                     pageData.getEnrollResultPanelList().get(UpdateStatus.NEW.numericRepresentation).getStudentList().size());
        assertEquals(students[UpdateStatus.ERROR.numericRepresentation].size(),
                     pageData.getEnrollResultPanelList().get(UpdateStatus.ERROR.numericRepresentation).getStudentList().size());
        assertEquals(students[UpdateStatus.MODIFIED.numericRepresentation].size(),
                     pageData.getEnrollResultPanelList().get(UpdateStatus.MODIFIED.numericRepresentation).getStudentList().size());
        assertEquals(students[UpdateStatus.NOT_IN_ENROLL_LIST.numericRepresentation].size(),
                     pageData.getEnrollResultPanelList().get(UpdateStatus.NOT_IN_ENROLL_LIST.numericRepresentation).getStudentList().size());
        assertEquals(students[UpdateStatus.UNKNOWN.numericRepresentation].size(),
                     pageData.getEnrollResultPanelList().get(UpdateStatus.UNKNOWN.numericRepresentation).getStudentList().size());
        assertEquals(students[UpdateStatus.UNMODIFIED.numericRepresentation].size(),
                     pageData.getEnrollResultPanelList().get(UpdateStatus.UNMODIFIED.numericRepresentation).getStudentList().size());
        
        assertTrue(pageData.isHasSection());
        assertEquals(enrollStudents, pageData.getEnrollStudents());
        
    }
}
