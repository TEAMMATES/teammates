package teammates.test.cases.ui.pagedata;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.test.cases.BaseTestCase;
import teammates.ui.controller.InstructorCourseEditPageData;

public class InstructorCourseEditPageDataTest extends BaseTestCase {
    private static DataBundle dataBundle = getTypicalDataBundle();
    
    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
    }
    
    @Test
    public void testAll() {
        ______TS("test typical case");
        
        AccountAttributes account = dataBundle.accounts.get("instructor1OfCourse1");
        InstructorCourseEditPageData pageData = new InstructorCourseEditPageData(account);
        
        CourseAttributes course = dataBundle.courses.get("typicalCourse1");
        
        List<InstructorAttributes> instructorList = new ArrayList<InstructorAttributes>();
        instructorList.add(dataBundle.instructors.get("instructor1OfCourse1"));
        instructorList.add(dataBundle.instructors.get("instructor2OfCourse1"));
        instructorList.add(dataBundle.instructors.get("helperOfCourse1"));
        
        InstructorAttributes currentInstructor = dataBundle.instructors.get("instructor1OfCourse1");
        
        int offset = -1;
        
        List<String> sectionNames = new ArrayList<String>();
        sectionNames.add("Section 1");
        sectionNames.add("Section 2");
        
        List<String> feedbackSessionNames = new ArrayList<String>();
        feedbackSessionNames.add("First feedback session");
        feedbackSessionNames.add("Second feedback session");
        feedbackSessionNames.add("Grace Period Session");
        feedbackSessionNames.add("Closed Session");
        feedbackSessionNames.add("Empty session");
        feedbackSessionNames.add("non visible session");
        
        pageData.init(course, instructorList, currentInstructor, offset, sectionNames, feedbackSessionNames);
        
        assertNotNull(pageData.deleteCourseButton);
        assertNotNull(pageData.addInstructorButton);
        
        assertNotNull(pageData.instructorPanelList);
        assertEquals(3, pageData.instructorPanelList.size());
        assertEquals(4, pageData.instructorPanelList.get(0).permissionInputGroup1.size());
        assertEquals(4, pageData.instructorPanelList.get(0).permissionInputGroup2.size());
        assertEquals(3, pageData.instructorPanelList.get(0).permissionInputGroup3.size());
        assertEquals(2, pageData.instructorPanelList.get(0).sectionRows.size());
        assertEquals(6, pageData.instructorPanelList.get(0).sectionRows.get(0).feedbackSessions.size());
        
        assertNotNull(pageData.addInstructorPanel);
        assertEquals(4, pageData.addInstructorPanel.permissionInputGroup1.size());
        assertEquals(4, pageData.addInstructorPanel.permissionInputGroup2.size());
        assertEquals(3, pageData.addInstructorPanel.permissionInputGroup3.size());
        assertEquals(2, pageData.addInstructorPanel.sectionRows.size());
        assertEquals(6, pageData.addInstructorPanel.sectionRows.get(0).feedbackSessions.size());
        
    }
}
